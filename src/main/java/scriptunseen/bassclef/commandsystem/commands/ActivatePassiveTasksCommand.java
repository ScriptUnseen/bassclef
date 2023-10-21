package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.passive.SortInvTask;

public class ActivatePassiveTasksCommand implements ICommand {
    @Override
    public boolean onCommand(String[] args) {
        SortInvTask.initToKeep();
        TaskManager.enablePassiveTasks();
        return true;
    }
}
