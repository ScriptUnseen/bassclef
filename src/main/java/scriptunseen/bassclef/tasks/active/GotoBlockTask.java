package scriptunseen.bassclef.tasks.active;

import baritone.api.pathing.goals.GoalNear;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class GotoBlockTask extends ActiveTask {

    private final BlockPos pos;

    // I know this Goal is included in baritone, but it causes massive lag
    public GotoBlockTask(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public boolean activeTaskTick() {
        if (rightClickContainer()) {
            Main.baritone.getPathingBehavior().forceCancel();
            taskState = TaskState.SUCCESS;
            return false;
        } else if (!Main.baritone.getGetToBlockProcess().isActive()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalNear(pos, 1));
        }
        return true;
    }

    private boolean rightClickContainer() {
        IPlayerContext ctx = Main.baritone.getPlayerContext();
        Optional<Rotation> reachable = RotationUtils.reachable(ctx.player(), pos, ctx.playerController().getBlockReachDistance());
        if (reachable.isPresent()) {
            Main.baritone.getPathingBehavior().forceCancel();
            Main.baritone.getLookBehavior().updateTarget(reachable.get(), true);
            if (Main.client.interactionManager != null && pos.equals(ctx.getSelectedBlock().orElse(null)) && Main.client.crosshairTarget != null && Main.client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                BlockHitResult bhr = (BlockHitResult) Main.client.crosshairTarget;
                Main.client.interactionManager.interactBlock(Main.client.player, Main.client.world, Hand.MAIN_HAND, bhr);
                return !(ctx.player().currentScreenHandler instanceof PlayerScreenHandler);
            }
            return false;
        }
        return false;
    }
}