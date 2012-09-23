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

import java.util.List;

import static zhuravlik.ant.vbox.reflection.Fields.*;
import static zhuravlik.ant.vbox.reflection.Methods.*;

/**
 *
 * @author anton
 */
public class Delete extends VboxAction {
    
    boolean deleteFiles = true;

    public boolean isDeleteFiles() {
        return deleteFiles;
    }

    public void setDeleteFiles(boolean deleteFiles) {
        this.deleteFiles = deleteFiles;
    }
    
    @Override
    public void executeAction(Object machine, Object session) {
        try {
            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null))
                lockMachineMethod.invoke(machine, session, writeLockField.get(null));            
            
            Object v1 = cleanupModeUnregisterOnly.get(null);
            Object v2 = cleanupModeDetachAllReturnHardDisksOnly.get(null);
            
            
            List media = (List) unregisterMachineMethod.invoke(machine, deleteFiles ? v2 : v1);
            
            if (deleteFiles)
                machineDeleteMediaMethod.invoke(machine, media);
            
            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
