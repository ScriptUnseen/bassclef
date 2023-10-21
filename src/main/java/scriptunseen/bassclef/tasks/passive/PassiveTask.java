package scriptunseen.bassclef.tasks.passive;

import scriptunseen.bassclef.tasks.Task;
import scriptunseen.bassclef.tasks.TaskState;

public abstract class PassiveTask extends Task {
    protected boolean runWithNullWorld = false;

    public PassiveTask() {
        taskState = TaskState.OFF;
    }

    public abstract boolean taskTick();

    public boolean isRunWithNullWorld() {
        return runWithNullWorld;
    }
}
