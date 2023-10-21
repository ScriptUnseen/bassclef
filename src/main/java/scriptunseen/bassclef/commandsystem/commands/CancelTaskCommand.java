package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.frames.FrameTaskManager;
import scriptunseen.bassclef.tasks.passive.RestoreFromDeathTask;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.ChatHelper;

public class CancelTaskCommand implements ICommand {

    @Override
    public boolean onCommand(String[] args) {
        if (TaskManager.getRunningTask() != null) {
            ChatHelper.displayChatMessage("Canceled everything! (subtask: " + TaskManager.getRunningTask().getSubtaskName() + ")");
        }
        TaskManager.cancelEverything();
        TaskManager.disablePassiveTasks();
        RestoreFromDeathTask.reset();
        FrameTaskManager.setFrameTask(null);
        SortInvTask.initToKeep();
        return true;
    }
}
