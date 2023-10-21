package scriptunseen.bassclef.tasks.passive;

import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.active.overworld.GetDownInFluidTask;
import scriptunseen.bassclef.utils.helper.InputHandler;

public class SaveFromLavaTask extends PassiveTask {

    private boolean goingDown = false;

    @Override
    public boolean taskTick() {
        if (ctx.world().getDimension().isUltrawarm()) {
            if (goingDown) {
                if (TaskManager.getRunningTask() == null) {
                    goingDown = false;
                    TaskManager.runTask(0);
                }
            } else {
                if (ctx.player().isInLava() && !Main.baritone.getPathingBehavior().isPathing()) {
                    Main.baritone.getPathingBehavior().forceCancel();
                    TaskManager.addPrimaryTaskAndRun(new GetDownInFluidTask());
                    goingDown = true;
                }
                if (ctx.player().isSubmergedInWater() || (ctx.player().isTouchingWater() && !Main.baritone.getCustomGoalProcess().isActive())) {
                    InputHandler.press(InputHandler.Input.JUMP);
                }
            }
        }
        return false;
    }
}
