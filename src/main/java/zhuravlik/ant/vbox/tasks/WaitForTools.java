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


import static zhuravlik.ant.vbox.reflection.Fields.*;
import static zhuravlik.ant.vbox.reflection.Methods.*;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.03.12
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public class WaitForTools extends VboxAction {
    
    int timeout;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void executeAction(Object machine, Object session) {

        try {
            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null))
                lockMachineMethod.invoke(machine, session, sharedLockField.get(null));

            Object console = getConsoleMethod.invoke(session);
            Object guest = getGuestMethod.invoke(console);

            int i = 0;
            
            while (!(Boolean)getAdditionsStatusMethod.invoke(guest, additionsRunLevelDesktopField.get(null))
                && i < timeout) {
                try {Thread.sleep(1000);} catch (Exception e) {
                    if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                        unlockMachineMethod.invoke(session);
                    throw new BuildException(e.getMessage());
                }
                i++;
            }

            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }


        /*if (session.getState() == SessionState.Unlocked)
            machine.lockMachine(session, LockType.Shared);


        int i = 0;
        while (!session.getConsole().getGuest().getAdditionsStatus(AdditionsRunLevelType.Desktop)
                && i < timeout) {
            try {Thread.sleep(1000);} catch (Exception e) {
                if (session.getState() == SessionState.Locked)
                    session.unlockMachine();
                throw new BuildException(e.getMessage());
            }
            i++;
        }


        if (session.getState() == SessionState.Locked)
            session.unlockMachine();   */
    }
}
