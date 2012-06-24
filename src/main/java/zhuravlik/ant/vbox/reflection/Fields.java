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

import java.lang.reflect.Field;

import static zhuravlik.ant.vbox.reflection.Classes.*;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.03.12
 * Time: 20:34
 * To change this template use File | Settings | File Templates.
 */
public class Fields {

    public static Field lockedStateField;
    public static Field unlockedStateField;
    public static Field sharedLockField;
    public static Field writeLockField;
    public static Field parentsFlagField;
    public static Field waitForProcessStartOnlyField;
    public static Field waitForNoneField;
    public static Field additionsRunLevelDesktopField;
    public static Field consoleDisplayField;
    public static Field holderValueField;
    
    public static Field cloneModeMachineState;
    public static Field cloneModeMachineAndChildStates;
    public static Field cloneModeAllStates;
    
    public static Field cloneOptionLink;
    public static Field cloneOptionKeepAllMACs;
    public static Field cloneOptionKeepNATMACs;
    public static Field cloneOptionKeepDiskNames;

    public static void initialize() throws BuildException {
        try {
            lockedStateField = sessionStateEnum.getField("Locked");
            unlockedStateField = sessionStateEnum.getField("Unlocked");
            sharedLockField = lockTypeEnum.getField("Shared");
            writeLockField = lockTypeEnum.getField("Write");
            parentsFlagField = directoryCreateFlagEnum.getField("Parents");
            waitForProcessStartOnlyField = executeProcessFlagEnum.getField("WaitForProcessStartOnly");
            waitForNoneField = executeProcessFlagEnum.getField("None");
            additionsRunLevelDesktopField = additionsRunLevelTypeEnum.getField("Desktop");
            consoleDisplayField = consoleInterface.getField("display");
            holderValueField = holderClass.getField("value");
            
           cloneModeMachineState = cloneModeEnum.getField("MachineState");
           cloneModeMachineAndChildStates = cloneModeEnum.getField("MachineAndChildStates");
           cloneModeAllStates = cloneModeEnum.getField("AllStates");
           
           cloneOptionLink = cloneOptionsEnum.getField("Link");
           cloneOptionKeepAllMACs = cloneOptionsEnum.getField("KeepAllMACs");
           cloneOptionKeepNATMACs = cloneOptionsEnum.getField("KeepNATMACs");
           cloneOptionKeepDiskNames = cloneOptionsEnum.getField("KeepDiskNames");
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
