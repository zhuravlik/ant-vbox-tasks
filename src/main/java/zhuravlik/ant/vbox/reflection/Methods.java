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

package zhuravlik.ant.vbox.reflection;

import org.apache.tools.ant.BuildException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static zhuravlik.ant.vbox.reflection.Classes.*;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.03.12
 * Time: 20:36
 * To change this template use File | Settings | File Templates.
 */
public class Methods {
    
    public static Method getSessionStateMethod;
    public static Method unlockMachineMethod;
    public static Method getConsoleMethod;
    public static Method lockMachineMethod;
    public static Method getGuestMethod;
    public static Method setCredentialsMethod;
    public static Method waitForCompletionMethod;
    public static Method copyFromGuestMethod;
    public static Method copyToGuestMethod;
    public static Method findSnapshotMethod;
    public static Method deleteSnapshotAndAllChildrenMethod;
    public static Method deleteSnapshotMethod;
    public static Method getSnapshotIdMethod;
    public static Method directoryCreateMethod;
    public static Method directoryCreateFlagEnumValueMethod;
    public static Method powerDownMethod;
    public static Method launchVMProcessMethod;
    public static Method restoreSnapshotMethod;
    public static Method executeProcessMethod;
    public static Method takeSnapshotMethod;
    public static Method executeProcessFlagEnumValueMethod;
    public static Method createSharedFolderMethod;
    public static Method takeScreenShotPNGToArrayMethod;
    public static Method getScreenResolutionMethod;
    public static Method cloneToMethod;
    public static Method managerCreateInstanceMethod;
    public static Method managerGetVBoxMethod;
    public static Method managerGetSessionObjectMethod;            
    public static Method getMachinesMethod;            
    public static Method getMachineNameMethod;
    public static Method createMachineMethod;
    public static Method machineSaveSettingsMethod;
    public static Method registerMachineMethod;
    public static Method unregisterMachineMethod;
    public static Method machineDeleteMediaMethod;
    public static Method getAdditionsStatusMethod;
    
    public static void initialize() throws BuildException {
        try {
            getSessionStateMethod = sessionInterface.getMethod("getState");
            unlockMachineMethod = sessionInterface.getMethod("unlockMachine");
            getConsoleMethod = sessionInterface.getMethod("getConsole");
            lockMachineMethod = machineInterface.getMethod("lockMachine", sessionInterface, lockTypeEnum);
            getGuestMethod = consoleInterface.getMethod("getGuest");
            setCredentialsMethod = guestInterface.getMethod("setCredentials", String.class, String.class, String.class, Boolean.class);
            waitForCompletionMethod = progressInterface.getMethod("waitForCompletion", Integer.class);
            copyFromGuestMethod = guestInterface.getMethod("copyFromGuest",
                    String.class, String.class, String.class, String.class, Long.class);
            copyToGuestMethod = guestInterface.getMethod("copyToGuest",
                    String.class, String.class, String.class, String.class, Long.class);
            findSnapshotMethod = machineInterface.getMethod("findSnapshot", String.class);

            deleteSnapshotAndAllChildrenMethod = consoleInterface.getMethod("deleteSnapshotAndAllChildren",
                    String.class);

            deleteSnapshotMethod = consoleInterface.getMethod("deleteSnapshot",
                    String.class);

            getSnapshotIdMethod = snapshotInterface.getMethod("getId");

            directoryCreateMethod = guestInterface.getMethod("directoryCreate",
                    String.class, String.class, String.class, Long.class, Long.class);

            directoryCreateFlagEnumValueMethod = directoryCreateFlagEnum.getMethod("value");
            
            powerDownMethod = consoleInterface.getMethod("powerDown");
            
            launchVMProcessMethod = machineInterface.getMethod("launchVMProcess", sessionInterface, String.class,
                    String.class);

            restoreSnapshotMethod = consoleInterface.getMethod("restoreSnapshot", snapshotInterface);
            
            executeProcessMethod = guestInterface.getMethod("executeProcess", String.class,
                    Long.class, List.class, List.class, String.class, String.class,
                    Long.class, holderClass);
            
            takeSnapshotMethod = consoleInterface.getMethod("takeSnapshot", String.class, String.class);

            executeProcessFlagEnumValueMethod = executeProcessFlagEnum.getMethod("value");

            getAdditionsStatusMethod = guestInterface.getMethod("getAdditionsStatus", additionsRunLevelTypeEnum);
            
            createSharedFolderMethod = consoleInterface.getMethod("createSharedFolder", String.class, String.class, 
                    Boolean.class, Boolean.class);            
            
            takeScreenShotPNGToArrayMethod = displayInterface.getMethod("takeScreenShotPNGToArray", Long.class, Long.class, Long.class);
            getScreenResolutionMethod = displayInterface.getMethod("getScreenResolution", Long.class, holderClass, holderClass, holderClass);
            
            cloneToMethod = machineInterface.getMethod("cloneTo", machineInterface, cloneModeEnum, cloneOptionsEnumArray);
            createMachineMethod = virtualBoxInterface.getMethod("createMachine", String.class, String.class, String.class, UUID.class, Boolean.class);
            
            managerCreateInstanceMethod = virtualBoxManagerClass.getMethod("createInstance", String.class);
            managerGetVBoxMethod = virtualBoxManagerClass.getMethod("getVBox");
            managerGetSessionObjectMethod = virtualBoxManagerClass.getMethod("getSessionObject");
            
            getMachinesMethod = virtualBoxInterface.getMethod("getMachines");
            
            getMachineNameMethod = machineInterface.getMethod("getName");
            
            machineSaveSettingsMethod = machineInterface.getMethod("saveSettings");
            
            machineDeleteMediaMethod = machineInterface.getMethod("delete", mediumArray);
            
            registerMachineMethod = virtualBoxInterface.getMethod("registerMachine", machineInterface);
            
            unregisterMachineMethod = machineInterface.getMethod("unregister", cleanupOptionsEnum);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
