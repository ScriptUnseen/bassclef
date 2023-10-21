package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.active.SpeedrunTask;

public class SpeedrunCommand implements ICommand {
    @Override
    public boolean onCommand(String[] args) {
        TaskManager.addPrimaryTaskAndRun(new SpeedrunTask(args.length == 2 ? Integer.parseInt(args[1]) : 4));
        TaskManager.enablePassiveTasks();
        return true;
    }
}
