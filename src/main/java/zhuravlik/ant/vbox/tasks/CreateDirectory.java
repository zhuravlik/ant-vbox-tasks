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

import org.virtualbox_4_1.DirectoryCreateFlag;
import org.virtualbox_4_1.IConsole;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import zhuravlik.ant.vbox.VboxAction;
import zhuravlik.ant.vbox.VboxTask;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 29.01.12
 * Time: 10:45
 * To change this template use File | Settings | File Templates.
 */
public class CreateDirectory extends VboxAction {
    
    String path;
    int mode = 777;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public void executeAction(IMachine machine, ISession session) {

        session.getConsole().getGuest().directoryCreate(path, VboxTask.username, VboxTask.password, (long) mode, (long) DirectoryCreateFlag.Parents.value());
    }
}
