package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.active.GetFoodTask;

public class GetFoodCommand implements ICommand {
    @Override
    public boolean onCommand(String[] args) {
        int foodLevel = 200;
        if (args.length == 2) {
            foodLevel = Integer.parseInt(args[1]);
        }
        TaskManager.addPrimaryTaskAndRun(new GetFoodTask(foodLevel));
        return false;
    }
}
