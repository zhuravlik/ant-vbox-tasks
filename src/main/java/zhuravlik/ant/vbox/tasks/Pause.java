package zhuravlik.ant.vbox.tasks;

import org.apache.tools.ant.BuildException;
import zhuravlik.ant.vbox.VboxAction;

import static zhuravlik.ant.vbox.reflection.Fields.lockedStateField;
import static zhuravlik.ant.vbox.reflection.Fields.sharedLockField;
import static zhuravlik.ant.vbox.reflection.Fields.unlockedStateField;
import static zhuravlik.ant.vbox.reflection.Methods.*;

/**
 * Created with IntelliJ IDEA.
 * User: anton
 * Date: 7/12/12
 * Time: 11:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pause extends VboxAction {

    @Override
    public void executeAction(Object machine, Object session) {
        try {
            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null))
                lockMachineMethod.invoke(machine, session, sharedLockField.get(null));

            Object console = getConsoleMethod.invoke(session);
            pauseVMMethod.invoke(console);

            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
