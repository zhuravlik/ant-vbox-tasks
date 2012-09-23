/*
   Copyright (C) 2012 Anton Lobov <zhuravlik> <ahmad200512[at]yandex.ru>

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General
   Public License along with this library; if not, write to the
   Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA 02110-1301 USA
 */

package zhuravlik.ant.vbox.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import zhuravlik.ant.vbox.VboxAction;
import zhuravlik.ant.vbox.VboxTask;

import java.io.File;

import static zhuravlik.ant.vbox.reflection.Fields.*;
import static zhuravlik.ant.vbox.reflection.Methods.*;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 29.01.12
 * Time: 10:13
 * To change this template use File | Settings | File Templates.
 */
public class GetFile extends VboxAction {
    String path;
    String destination;
    int timeout;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private void getObject(Object guest, String path, String dest) throws Exception {
        // implementation of fileExists in VB is ugly: it depends on trailing slash
        // to distinguish between file and directory; and depending on it, it checks
        // for directory existence or file existence
        //
        // we only need to check whether our object is file, so work around it
        if (!path.endsWith("\\")
                && !path.endsWith("/")
                &&
                (
                    VboxTask.versionPrefix.contains("4_1") ?
                    (Boolean)fileExistsMethod.invoke(guest, path, VboxTask.username, VboxTask.password) :
                       (Boolean)fileExistsMethod.invoke(VboxTask.session, path)
                )) {
            Object p =

                    VboxTask.versionPrefix.contains("4_1") ?
                    copyFromGuestMethod.invoke(guest, path, dest,
                    VboxTask.username, VboxTask.password, (long)0) :
                    copyFromGuestMethod.invoke(VboxTask.session, path, dest, copyFileFlagNone.get(null)) ;
            waitForCompletionMethod.invoke(p, -1);
        }
        else {
            File f = new File(dest);

            if (f.exists() && !f.isDirectory())
                throw new BuildException("Trying to overwrite existing non-directory with directory, that " +
                        "fails by definition");
            else if (!f.exists())
                f.mkdirs();


            if (VboxTask.versionPrefix.contains("4_1")) {
                Long directoryHandle = (Long)directoryOpenMethod.invoke(guest, path, "", directoryOpenFlagNone.get(null),
                    VboxTask.username, VboxTask.password);

                Object entry;
                while ((entry = directoryReadMethod.invoke(guest, directoryHandle)) != null) {
                    String entryName = (String) getDirectoryEntryName.invoke(entry);

                    String guestSeparator = path.indexOf('\\') >= 0 ? "\\" : "/"; //let's employ some simple heuristic
                    getObject(guest, path + guestSeparator + entryName, dest + File.separator + entryName);
                }
            }
            else {
                Object directory = directoryOpenMethod.invoke(VboxTask.session, path, "", directoryOpenFlagNone.get(null));
                Object entry;
                while ((entry = directoryReadMethod.invoke(directory)) != null) {
                    String entryName = (String) getDirectoryEntryName.invoke(entry);

                    String guestSeparator = path.indexOf('\\') >= 0 ? "\\" : "/"; //let's employ some simple heuristic
                    getObject(guest, path + guestSeparator + entryName, dest + File.separator + entryName);
                }
            }

        }
    }

    @Override
    public void executeAction(Object machine, Object session) {
        try {
            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null))
                lockMachineMethod.invoke(machine, session, sharedLockField.get(null));

            Object console = getConsoleMethod.invoke(session);
            Object guest = getGuestMethod.invoke(console);

            // there is a need to parse directory contents, because
            // copyFromGuest method was too unstable during tests for directories,
            // and was stable only for single files
            //
            // that's why it is safer to iterate over directory tree and get each single file,
            // creating intermediate directories by the way
            getObject(guest, path, destination);

            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
        

        /*if (session.getState() == SessionState.Unlocked)
            machine.lockMachine(session, LockType.Shared);

        IProgress p = session.getConsole().getGuest().copyFromGuest(path, destination, VboxTask.username, VboxTask.password, (long)0);
        p.waitForCompletion(-1);



        if (session.getState() == SessionState.Locked)
            session.unlockMachine();    */
    }
}
