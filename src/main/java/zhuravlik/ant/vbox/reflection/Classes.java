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
import zhuravlik.ant.vbox.VboxTask;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 14.03.12
 * Time: 20:28
 * To change this template use File | Settings | File Templates.
 */
public class Classes {

    public static Class machineInterface;
    public static Class sessionInterface;
    public static Class consoleInterface;
    public static Class guestInterface;
    public static Class progressInterface;
    public static Class snapshotInterface;
    public static Class sessionStateEnum;
    public static Class lockTypeEnum;
    public static Class directoryCreateFlagEnum;
    public static Class holderClass;
    public static Class executeProcessFlagEnum;
    public static Class additionsRunLevelTypeEnum;


    public static void initialize() throws BuildException {
        try {
            machineInterface = Class.forName(VboxTask.versionPrefix + ".IMachine");
            sessionInterface = Class.forName(VboxTask.versionPrefix + ".ISession");
            consoleInterface = Class.forName(VboxTask.versionPrefix + ".IConsole");
            guestInterface = Class.forName(VboxTask.versionPrefix + ".IGuest");
            progressInterface = Class.forName(VboxTask.versionPrefix + ".IProgress");
            snapshotInterface = Class.forName(VboxTask.versionPrefix + ".ISnapshot");

            sessionStateEnum = Class.forName(VboxTask.versionPrefix + ".SessionState");
            lockTypeEnum = Class.forName(VboxTask.versionPrefix + ".LockType");
            directoryCreateFlagEnum = Class.forName(VboxTask.versionPrefix + ".DirectoryCreateFlag");
            executeProcessFlagEnum = Class.forName(VboxTask.versionPrefix + ".ExecuteProcessFlag");
            additionsRunLevelTypeEnum = Class.forName(VboxTask.versionPrefix + ".AdditionsRunLevelType");

            holderClass = Class.forName(VboxTask.versionPrefix + ".Holder");
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
}