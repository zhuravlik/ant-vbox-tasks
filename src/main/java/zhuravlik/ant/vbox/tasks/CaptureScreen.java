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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import zhuravlik.ant.vbox.VboxAction;

import static zhuravlik.ant.vbox.reflection.Fields.*;
import static zhuravlik.ant.vbox.reflection.Methods.*;
import static zhuravlik.ant.vbox.reflection.Classes.*;

/**
 *
 * @author anton
 */
public class CaptureScreen extends VboxAction {

    String path;
    int screenId = 0;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getScreenId() {
        return screenId;
    }

    public void setScreenId(int screenId) {
        this.screenId = screenId;
    }

    @Override
    public void executeAction(Object machine, Object session) {
        try {
            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null)) {
                lockMachineMethod.invoke(machine, session, writeLockField.get(null));
            }

            Object console = getConsoleMethod.invoke(session);
            Object display = consoleDisplayField.get(console);

            Object wHolder = holderClass.newInstance();
            Object hHolder = holderClass.newInstance();
            Object dHolder = holderClass.newInstance();
            Long scrId = (long) screenId;

            getScreenResolutionMethod.invoke(display, scrId, wHolder, hHolder, dHolder);
            
            Long width = (Long)holderValueField.get(wHolder);
            Long height = (Long)holderValueField.get(hHolder);            

            byte[] bdata = (byte[])takeScreenShotPNGToArrayMethod.invoke(display, scrId, width, height);

            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null)) {
                unlockMachineMethod.invoke(session);
            }

            try {
                new File(path).createNewFile();
                FileOutputStream fos = new FileOutputStream(new File(path));
                fos.write(bdata);
                fos.close();
            } catch (IOException e) {
                throw new BuildException("Unable to write screenshot to file " + path + ": " + e.getMessage());
            }
            
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
