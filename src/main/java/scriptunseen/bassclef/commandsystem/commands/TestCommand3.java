package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.tasks.frames.OneCycleFrameTask;

public class TestCommand3 implements ICommand {

    @Override
    public boolean onCommand(String[] args) {
        OneCycleFrameTask.c = Integer.parseInt(args[1]);
        return false;
    }
}
