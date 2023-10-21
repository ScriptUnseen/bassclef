package scriptunseen.bassclef.tasks.active.end;

import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.utils.helper.Dimension;
import scriptunseen.bassclef.utils.helper.WorldScanner;

public class CheerTask extends ActiveTask {

    private boolean firstTick = true;

    @Override
    public boolean activeTaskTick() {
        if (WorldScanner.getDimension(ctx.world()) == Dimension.OVERWORLD) {
            taskState = TaskState.SUCCESS;
            return true;
        }
        if (firstTick) {
            firstTick = false;
            Main.client.options.perspective = 2;
        }
        if (ctx.player().pitch == -70) {
            ctx.player().yaw = (ctx.player().yaw + 1) % 360.0F;
        } else {
            ctx.player().pitch = -70;
        }
        if (getRunTime() % 2 == 0) {
            Main.client.options.keySneak.setPressed(!Main.client.options.keySneak.isPressed());
        }
        return false;
    }
}
