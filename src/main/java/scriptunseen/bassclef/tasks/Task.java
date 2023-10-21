package scriptunseen.bassclef.tasks;

import baritone.api.utils.IPlayerContext;
import scriptunseen.bassclef.Main;

public abstract class Task {

    protected final IPlayerContext ctx;
    public TaskState taskState = TaskState.RUNNING;
    private int runTime = 0;

    public Task() {
        this.ctx = Main.baritone.getPlayerContext();
    }

    public final boolean tick() {
        boolean b = taskTick();
        runTime++;
        return b;
    }

    public abstract boolean taskTick();

    public int getRunTime() {
        return runTime;
    }
}
