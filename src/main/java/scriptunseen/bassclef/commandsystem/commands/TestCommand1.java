package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.active.end.CheerTask;
import scriptunseen.bassclef.tasks.frames.FrameTaskManager;

public class TestCommand1 implements ICommand {
    @Override
    public boolean onCommand(String[] args) {
        FrameTaskManager.setFrameTask(null);
        TaskManager.addPrimaryTaskAndRun(new CheerTask());
        return true;
    }
}