package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.utils.helper.ChatHelper;

public class TaskTreeCommand implements ICommand {

    @Override
    public boolean onCommand(String[] args) {
        if (TaskManager.getRunningTask() != null) {
            ChatHelper.displayChatMessage(TaskManager.getRunningTask().getTaskTree());
        }
        return true;
    }
}
