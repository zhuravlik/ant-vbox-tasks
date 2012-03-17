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

package zhuravlik.ant.vbox;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import zhuravlik.ant.vbox.reflection.Classes;
import zhuravlik.ant.vbox.reflection.Fields;
import zhuravlik.ant.vbox.reflection.Methods;
import zhuravlik.ant.vbox.tasks.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: anton
 * Date: 29.01.12
 * Time: 9:19
 * To change this template use File | Settings | File Templates.
 */
public class VboxTask extends Task {
    
    public static String username;
    public static String password;
    public static String versionPrefix;
    
    private List<VboxAction> actions = new ArrayList<VboxAction>();
    
    String name;
    String api_version;
	String vbox_home;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi_version() {
        return api_version;
    }

    public void setApi_version(String api_version) {
        this.api_version = api_version;
    }

	public String getVbox_home() {
		return vbox_home;
	}

	public void setVbox_home(String home) {
		this.vbox_home = home;
	}

    public void addCreateDirectory(CreateDirectory createDirectory) {
        actions.add(createDirectory);
    }
    
    public void addDeleteSnapshot(DeleteSnapshot deleteSnapshot) {
        actions.add(deleteSnapshot);
    }  
    
    public void addGetFile(GetFile getFile) {
        actions.add(getFile);
    }
    
    public void addLogin(Login login) {
        actions.add(login);
    }
    
    public void addLogout(Logout logout) {
        actions.add(logout);
    }
    
    public void addPowerOff(PowerOff powerOff) {
        actions.add(powerOff);
    }
    
    public void addPowerOn(PowerOn powerOn) {
        actions.add(powerOn);
    }
    
    public void addPutFile(PutFile putFile) {
        actions.add(putFile);
    }
    
    public void addRevertToSnapshot(RevertToSnapshot revertToSnapshot) {
        actions.add(revertToSnapshot);
    }

    public void addRunProgram(RunProgram runProgram) {
        actions.add(runProgram);
    }
    
    public void addTakeSnapshot(TakeSnapshot takeSnapshot) {
        actions.add(takeSnapshot);
    }

    public void addWaitForTools(WaitForTools waitForTools) {
        actions.add(waitForTools);
    }
    
    public void execute() throws BuildException {

		if (vbox_home != null)
			System.setProperty("vbox.home", vbox_home);
        
        versionPrefix = "org.virtualbox_" + api_version.replaceAll("\\.", "_");
        Classes.initialize();
        Methods.initialize();
        Fields.initialize();
        
        try {
            Class managerClass = Class.forName(versionPrefix + ".VirtualBoxManager");
            Class boxInterface = Class.forName(versionPrefix + ".IVirtualBox");
            Class sessionInterface = Class.forName(versionPrefix + ".ISession");
            
            Method createMethod = managerClass.getMethod("createInstance", String.class);
            Object vbm = createMethod.invoke(null, new Object[] {null});
            Method getVBMethod = managerClass.getMethod("getVBox");
            Method getSessionMethod = managerClass.getMethod("getSessionObject");
            
            Object box = getVBMethod.invoke(vbm);
            Object session = getSessionMethod.invoke(vbm);            

            Method getMachinesMethod = boxInterface.getMethod("getMachines");
            List vms = (List)getMachinesMethod.invoke(box);
            
            Class machineInterface = Class.forName(versionPrefix + ".IMachine");
            Method getMachineNameMethod = machineInterface.getMethod("getName");
            
            Object neededVM = null;
            for (Object vm: vms) {
                String nm = (String) getMachineNameMethod.invoke(vm);
                if (name.equals(nm)) {
                    neededVM = vm;
                }
            }
            
            if (neededVM == null)
                throw new BuildException("Virtual machine [" + name + "] cannot be found");

            for (VboxAction action: actions) {
                action.executeAction(neededVM, session);
            }
            
            Class sessionStateEnum = Class.forName(versionPrefix + ".SessionState");
            Field lockedStateField = sessionStateEnum.getField("Locked");
            
            Method getSessionStateMethod = sessionInterface.getMethod("getState");
            Method unlockMachineMethod = sessionInterface.getMethod("unlockMachine");

            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }

        /*VirtualBoxManager vbm = VirtualBoxManager.createInstance(null);

        IVirtualBox box = vbm.getVBox();
        ISession session = vbm.getSessionObject();

        List<IMachine> vms = box.getMachines();
        IMachine neededVM = null;
        for (IMachine vm: vms) {
            if (vm.getName().equals(name)) {
                neededVM = vm;
            }
        }

        if (neededVM == null)
            throw new BuildException("Virtual machine [" + name + "] cannot be found");

        for (VboxAction action: actions) {
            action.executeAction(neededVM, session);
        }

        if (session.getState() == SessionState.Locked)
            session.unlockMachine();    */
    }
}
