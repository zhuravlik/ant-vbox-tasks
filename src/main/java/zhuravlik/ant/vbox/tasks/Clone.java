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

import java.util.ArrayList;
import java.util.UUID;
import org.apache.tools.ant.BuildException;
import zhuravlik.ant.vbox.VboxAction;

import static zhuravlik.ant.vbox.reflection.Fields.*;
import static zhuravlik.ant.vbox.reflection.Methods.*;

/**
 *
 * @author anton
 */
public class Clone extends VboxAction {
    
    String snapshot = null;
    String destination;
    String name;
    String cloneMode;
    String cloneType;
    String osType;
    String keep;

    public String getCloneMode() {
        return cloneMode;
    }

    public void setCloneMode(String cloneMode) {
        this.cloneMode = cloneMode;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getCloneType() {
        return cloneType;
    }

    public void setCloneType(String cloneType) {
        this.cloneType = cloneType;
    }

    public String getKeep() {
        return keep;
    }

    public void setKeep(String keep) {
        this.keep = keep;
    }        

    @Override
    public void executeAction(Object machine, Object session) {
        
        if (cloneMode == null)
            cloneMode = "current";
        
        if (!cloneMode.equals("current") || !cloneMode.equals("withchildren") || !cloneMode.equals("full")) 
            throw new BuildException("Invalid clone mode " + cloneMode + ", should be either current, withchildren or full");
        
        if (cloneType == null)
            cloneType = "linked";
        
        if (!cloneType.equals("linked") || !cloneType.equals("full")) 
            throw new BuildException("Invalid clone type " + cloneType + ", should be either linked or full");
        
        try {
            Object vbm = managerCreateInstanceMethod.invoke(null, new Object[] {null});
            Object box = managerGetVBoxMethod.invoke(vbm);
            
            Object newMachine = createMachineMethod.invoke(box, destination, name, osType, UUID.randomUUID(), false);
            
            Object cloneTypeObj = cloneMode.equals("current") ? cloneModeMachineState.get(null) :
                    (                    
                        cloneMode.equals("withchildren") ? cloneModeMachineAndChildStates.get(null) :
                            cloneModeAllStates.get(null)
                    );                        
            
            ArrayList<Object> opts = new ArrayList<Object>();
            
            if (cloneType.equals("linked"))
                opts.add(cloneOptionLink.get(null));
            
            if (keep != null && keep.contains("allmac"))
                opts.add(cloneOptionKeepAllMACs.get(null));
            
            if (keep != null && keep.contains("natmac"))
                opts.add(cloneOptionKeepNATMACs.get(null));
            
            if (keep != null && keep.contains("diskname"))
                opts.add(cloneOptionKeepDiskNames.get(null));              
            
            Object[] optsArr = opts.toArray();
            
            Object progress = cloneToMethod.invoke(machine, newMachine, cloneTypeObj, optsArr);                                                
            waitForCompletionMethod.invoke(progress, -1);
            
            machineSaveSettingsMethod.invoke(newMachine);
            
            registerMachineMethod.invoke(box);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
    
}
