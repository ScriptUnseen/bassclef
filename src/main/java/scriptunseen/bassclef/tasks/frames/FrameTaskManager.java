package scriptunseen.bassclef.tasks.frames;

import scriptunseen.bassclef.Main;
import org.jetbrains.annotations.Nullable;

public class FrameTaskManager {

    private static FrameTask frameTask;

    public static void tick(float tickDelta, long startTime, boolean tick) {
        if (Main.client.world != null && frameTask != null) {
            frameTask.tick(tickDelta, startTime, tick);
        }
    }

    public static void setFrameTask(@Nullable FrameTask fTask) {
        frameTask = fTask;
    }

    public static boolean hasTask() {
        return frameTask != null;
    }

}
