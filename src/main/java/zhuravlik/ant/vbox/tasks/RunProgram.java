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

import java.util.ArrayList;
import java.util.Arrays;

import static zhuravlik.ant.vbox.reflection.Classes.holderClass;
import static zhuravlik.ant.vbox.reflection.Fields.*;
import static zhuravlik.ant.vbox.reflection.Methods.*;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 29.01.12
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
public class RunProgram extends VboxAction {

    String path;
    String args;
    String env;
    int timeout;
    boolean returnImmediately = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isReturnImmediately() {
        return returnImmediately;
    }

    public void setReturnImmediately(boolean returnImmediately) {
        this.returnImmediately = returnImmediately;
    }

    @Override
    public void executeAction(Object machine, Object session) {

        try {
            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null))
                lockMachineMethod.invoke(machine, session, sharedLockField.get(null));

            Object console = getConsoleMethod.invoke(session);
            Object guest = getGuestMethod.invoke(console);



            if (VboxTask.versionPrefix.contains("4_1")) {
                Object hld = holderClass.newInstance();

                long flag = returnImmediately ?
                        (Long)executeProcessFlagEnumValueMethod.invoke(
                                waitForProcessStartOnlyField.get(null)
                        ) :
                        (Long)executeProcessFlagEnumValueMethod.invoke(
                                waitForNoneField.get(null)
                        );

                Object p = executeProcessMethod.invoke(guest, path,
                        flag,
                        Arrays.asList(args.split(" ")), Arrays.asList(env.split(";")),
                        VboxTask.username, VboxTask.password, (long)timeout, hld);
                waitForCompletionMethod.invoke(p, -1);
            }
            else {
                ArrayList<Object> flags = new ArrayList<Object>();

                flags.add(returnImmediately ? waitForProcessStartOnlyField.get(null) : waitForNoneField.get(null));

                executeProcessMethod.invoke(VboxTask.session, path,
                        Arrays.asList(args.split(" ")), Arrays.asList(env.split(";")),
                        flags, (long)timeout);
            }

            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
        
        /*if (session.getState() == SessionState.Unlocked)
            machine.lockMachine(session, LockType.Shared);

        Holder<Long> hld = new Holder<Long>();
        IProgress p = session.getConsole().getGuest().executeProcess(path,
                returnImmediately ? (long)ExecuteProcessFlag.WaitForProcessStartOnly.value() : (long)ExecuteProcessFlag.None.value(),
                Arrays.asList(args.split(" ")), Arrays.asList(env.split(";")),
                VboxTask.username, VboxTask.password, (long)timeout, hld);
        
        p.waitForCompletion(timeout);

        if (session.getState() == SessionState.Locked)
            session.unlockMachine();    */
    }
}
