package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.active.overworld.portal.BuildPortalTask;

public class BuildPortalCommand implements ICommand {
    @Override
    public boolean onCommand(String[] args) {
        TaskManager.addPrimaryTaskAndRun(new BuildPortalTask());
        return true;
    }
}
