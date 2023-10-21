package scriptunseen.bassclef.tasks.frames;

import baritone.api.utils.IPlayerContext;
import scriptunseen.bassclef.Main;

public abstract class FrameTask {
    protected IPlayerContext ctx;

    public FrameTask() {
        this.ctx = Main.baritone.getPlayerContext();
    }

    public abstract boolean tick(float tickDelta, long startTime, boolean tick);
}
