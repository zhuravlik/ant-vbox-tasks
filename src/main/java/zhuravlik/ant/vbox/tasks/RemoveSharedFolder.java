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

import static zhuravlik.ant.vbox.reflection.Fields.lockedStateField;
import static zhuravlik.ant.vbox.reflection.Fields.unlockedStateField;
import static zhuravlik.ant.vbox.reflection.Fields.writeLockField;
import static zhuravlik.ant.vbox.reflection.Methods.*;

/**
 * Created with IntelliJ IDEA.
 * User: anton
 * Date: 7/12/12
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoveSharedFolder extends VboxAction {
    String shareName;

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    @Override
    public void executeAction(Object machine, Object session) {
        try {
            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null))
                lockMachineMethod.invoke(machine, session, writeLockField.get(null));

            Object console = getConsoleMethod.invoke(session);
            Object guest = getGuestMethod.invoke(console);

            removeSharedFolderMethod.invoke(console, shareName);

            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
