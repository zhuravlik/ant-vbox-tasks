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
import zhuravlik.ant.vbox.VboxAction;
import zhuravlik.ant.vbox.VboxTask;

import java.io.File;
import java.util.ArrayList;

import static zhuravlik.ant.vbox.reflection.Fields.*;
import static zhuravlik.ant.vbox.reflection.Methods.*;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 29.01.12
 * Time: 10:17
 * To change this template use File | Settings | File Templates.
 */
public class PutFile extends VboxAction {

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

    private void putObject(Object guest, String path, String dest) throws Exception {
        File f = new File(path);

        if (!f.exists())
            throw new BuildException("Trying to put nonexisting file");
        else if (f.isFile()) {
            Object p =
                    VboxTask.versionPrefix.contains("4_1") ?
                    copyToGuestMethod.invoke(guest, path, dest, VboxTask.username, VboxTask.password, (long)0) :
                    copyToGuestMethod.invoke(VboxTask.session, path, dest, copyFileFlagNone.get(null));
            waitForCompletionMethod.invoke(p, -1);
        }
        else if (f.isDirectory()) {
            File[] fs = f.listFiles();
            String gsep = dest.indexOf('\\') >= 0 ? "\\" : "/";

            if (!dest.endsWith("\\")
                    && !dest.endsWith("/")
                    && (
                       VboxTask.versionPrefix.contains("4_1") ?
                            (Boolean)fileExistsMethod.invoke(guest, dest, VboxTask.username, VboxTask.password) :
                            (Boolean)fileExistsMethod.invoke(VboxTask.session, dest)
                    )
            ) {
                throw new BuildException("Trying to put directory in place of existing file, it fails by definition");
            }
            else {
                if (!dest.endsWith(gsep))
                    dest = dest + gsep;

                if (!(
                        VboxTask.versionPrefix.contains("4_1") ?
                                (Boolean)fileExistsMethod.invoke(guest, dest, VboxTask.username, VboxTask.password) :
                                (Boolean)fileExistsMethod.invoke(VboxTask.session, dest)
                ))   {
                    if (VboxTask.versionPrefix.contains("4_1"))
                        directoryCreateMethod.invoke(guest, dest);
                    else {
                        ArrayList<Object> flags = new ArrayList<Object>();
                        flags.add(directoryCreateFlagParents.get(null));
                        directoryCreateMethod.invoke(VboxTask.session, dest, (long)777, flags);
                    }
                }
            }

            for (File fl: fs) {
                putObject(guest, path + File.separator + fl.getName(), dest + gsep + fl.getName());
            }
        }

    }

    @Override
    public void executeAction(Object machine, Object session) {

        try {
            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null))
                lockMachineMethod.invoke(machine, session, writeLockField.get(null));

            Object console = getConsoleMethod.invoke(session);
            Object guest = getGuestMethod.invoke(console);

            /*Object p = copyToGuestMethod.invoke(guest, path, destination, VboxTask.username, VboxTask.password, (long)0);
            waitForCompletionMethod.invoke(p, -1);*/

            putObject(guest, path, destination);


            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }

        /*if (session.getState() == SessionState.Unlocked)
            machine.lockMachine(session, LockType.Shared);

        IProgress p = session.getConsole().getGuest().copyToGuest(path, destination, VboxTask.username, VboxTask.password, (long)0);
        p.waitForCompletion(-1);

        if (session.getState() == SessionState.Locked)
            session.unlockMachine();*/
    }
}
