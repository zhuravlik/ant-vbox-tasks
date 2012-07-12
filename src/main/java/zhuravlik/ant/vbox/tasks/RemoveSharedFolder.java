package zhuravlik.ant.vbox.tasks;

import org.apache.tools.ant.BuildException;
import zhuravlik.ant.vbox.VboxAction;

import static zhuravlik.ant.vbox.reflection.Fields.lockedStateField;
import static zhuravlik.ant.vbox.reflection.Fields.unlockedStateField;
import static zhuravlik.ant.vbox.reflection.Fields.writeLockField;
import static zhuravlik.ant.vbox.reflection.Methods.*;

/**
 * Created with IntelliJ IDEA.
 * User: anton
 * Date: 7/12/12
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoveSharedFolder extends VboxAction {
    String shareName;

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    @Override
    public void executeAction(Object machine, Object session) {
        try {
            if (getSessionStateMethod.invoke(session) == unlockedStateField.get(null))
                lockMachineMethod.invoke(machine, session, writeLockField.get(null));

            Object console = getConsoleMethod.invoke(session);
            Object guest = getGuestMethod.invoke(console);

            removeSharedFolderMethod.invoke(console, shareName);

            if (getSessionStateMethod.invoke(session) == lockedStateField.get(null))
                unlockMachineMethod.invoke(session);
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
