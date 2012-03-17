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
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import zhuravlik.ant.vbox.VboxAction;

import static zhuravlik.ant.vbox.reflection.Methods.launchVMProcessMethod;
import static zhuravlik.ant.vbox.reflection.Methods.waitForCompletionMethod;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 29.01.12
 * Time: 9:43
 * To change this template use File | Settings | File Templates.
 */
public class PowerOn extends VboxAction {
    
    int timeout = 10000;
    String type;
    String env;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void executeAction(Object machine, Object session) throws BuildException {
        if (machine == null)
            throw new BuildException("No machine associated with session found");

        try {

            System.err.println(type == null);
            System.err.println(env == null);
            System.err.println(launchVMProcessMethod == null);

            Object p = launchVMProcessMethod.invoke(machine, session, type, env);
            waitForCompletionMethod.invoke(p, -1);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }



        /*IProgress p = machine.launchVMProcess(session, type, env);

        //IProgress p = console.powerUp();
        p.waitForCompletion(-1);

        //machine.lockMachine(session, LockType.Shared);*/
    }
}
