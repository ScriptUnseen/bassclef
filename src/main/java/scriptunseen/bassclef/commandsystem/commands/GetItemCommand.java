package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.active.getitem.GetItemTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;

public class GetItemCommand implements ICommand {
    @Override
    public boolean onCommand(String[] args) {
        if (args.length == 3) {
            TaskManager.addPrimaryTaskAndRun(new GetItemTask(new ItemTexture(Integer.parseInt(args[1]), Integer.parseInt(args[2])), true));
            return true;
        }
        return false;
    }
}