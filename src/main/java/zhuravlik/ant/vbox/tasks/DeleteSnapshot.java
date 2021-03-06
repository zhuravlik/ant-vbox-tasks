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
 * Date: 29.01.12
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class DeleteSnapshot extends VboxAction {

    String name;
    boolean withChildren = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWithChildren() {
        return withChildren;
    }

    public void setWithChildren(boolean withChildren) {
        this.withChildren = withChildren;
    }

    @Override
    public void executeAction(Object machine, Object session) {
        try {
            Object snapshot = findSnapshotMethod.invoke(machine, name);

            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null))
                lockMachineMethod.invoke(machine, session, sharedLockField.get(null));

            Object console = getConsoleMethod.invoke(session);

            String id = (String) getSnapshotIdMethod.invoke(snapshot);

            Object p = withChildren ? deleteSnapshotAndAllChildrenMethod.invoke(console, id)
                    : deleteSnapshotMethod.invoke(console, id);

            waitForCompletionMethod.invoke(p, -1);

            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);

        }
        catch (Exception e) {
            throw new BuildException(e);
        }
        
        /*ISnapshot snapshot = machine.findSnapshot(name);

        if (session.getState() == SessionState.Unlocked)
            machine.lockMachine(session, LockType.Shared);

        IProgress p = withChildren ? session.getConsole().deleteSnapshotAndAllChildren(snapshot.getId())
                : session.getConsole().deleteSnapshot(snapshot.getId());
        p.waitForCompletion(-1);

        if (session.getState() == SessionState.Locked)
            session.unlockMachine(); */
    }
}

