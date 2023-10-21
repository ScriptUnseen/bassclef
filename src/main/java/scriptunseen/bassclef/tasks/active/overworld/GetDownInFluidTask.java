package scriptunseen.bassclef.tasks.active.overworld;

import baritone.api.BaritoneAPI;
import baritone.pathing.movement.MovementHelper;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.InputHandler;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class GetDownInFluidTask extends ActiveTask {
    public boolean activeTaskTick() {
        ctx.player().yaw = 0;
        double s = ctx.player().getWidth() / 2;
        if (WorldHelper.getOffset(s, ctx.playerFeetAsVec().x, ctx.playerFeet().x) != 0) {
            InputHandler.press(InputHandler.Input.MOVE_LEFT);
        }
        if (WorldHelper.getOffset(s, ctx.playerFeetAsVec().z, ctx.playerFeet().z) != 0) {
            InputHandler.press(InputHandler.Input.MOVE_FORWARD);
        }
        if (ctx.player().isOnGround()) {
            BlockState bs = ctx.world().getBlockState(ctx.playerFeet().up());
            if (bs.getMaterial().isLiquid() && bs.get(FluidBlock.LEVEL) != 0 && WorldHelper.isNotSave(ctx)) {
                placeBlock();
                taskState = TaskState.SUCCESS;
            } else {
                ctx.player().pitch = 90f;
                if (Main.client.crosshairTarget != null && Main.client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult bhr = (BlockHitResult) Main.client.crosshairTarget;
                    MovementHelper.switchToBestToolFor(ctx, ctx.world().getBlockState(bhr.getBlockPos()));
                    InputHandler.press(InputHandler.Input.LEFT_CLICK);
                }
            }
        }
        InputHandler.press(InputHandler.Input.SNEAK);
        return false;
    }

    private void placeBlock() {
        if (BaritoneAPI.getSettings().acceptableThrowawayItems.value.contains(ctx.player().inventory.getStack(SortInvTask.BLOCKS).getItem())) {
            ctx.player().inventory.selectedSlot = SortInvTask.BLOCKS;
            InteractionHelper.lookAt(ctx, Vec3d.ofCenter(ctx.playerFeet().offset(Direction.UP, 2)).add(0.5, 0, 0));
            InteractionHelper.interactBlock(ctx);
        }
    }

    @Override
    protected boolean reachedGoal() {
        return !(ctx.player().isTouchingWater() || ctx.player().isInLava());
    }
}
