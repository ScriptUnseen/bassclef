package scriptunseen.bassclef.tasks.passive;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.movement.IMovement;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.WorldHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class AvoidMonsterTask extends PassiveTask {

    @Override
    public boolean taskTick() {
        if (getRunTime() % 20 == 0) {
            calcMobAvoidanceCoefficient();
        }
        if (Main.baritone.getPathingBehavior().isPathing()) {
            if (isGoingDown() && WorldHelper.isNotSave(ctx)) {
                placeBlock();
            }
        }
        return true;
    }

    private void calcMobAvoidanceCoefficient() {
        BaritoneAPI.getSettings().mobAvoidanceCoefficient.value = 30D / ctx.player().getHealth();
        //BaritoneAPI.getSettings().mobAvoidanceRadius.value = (int) (ctx.player().getMaxHealth() - ctx.player().getHealth());
    }

    private boolean isGoingDown() {
        if (Main.baritone.getPathingBehavior().getPath().isPresent()) {
            List<IMovement> path = Main.baritone.getPathingBehavior().getPath().get().movements();
            int pos = Main.baritone.getPathingBehavior().getCurrent().getPosition();
            if (pos - 1 >= 0 && pos < path.size()) {
                return path.get(pos - 1).getSrc().y > path.get(pos).getSrc().y;
            }
        }
        return false;
    }

    private void placeBlock() {
        if (BaritoneAPI.getSettings().acceptableThrowawayItems.value.contains(ctx.player().inventory.getStack(SortInvTask.BLOCKS).getItem())) {
            ctx.player().inventory.selectedSlot = SortInvTask.BLOCKS;
            InteractionHelper.lookAt(ctx, Vec3d.ofCenter(ctx.playerFeet().offset(Direction.UP, 2)).add(0.5, 0, 0));
            InteractionHelper.interactBlock(ctx);
        }
    }
}
