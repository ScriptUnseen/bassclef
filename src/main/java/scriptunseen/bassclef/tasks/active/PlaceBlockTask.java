package scriptunseen.bassclef.tasks.active;

import baritone.api.pathing.goals.GoalInverted;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.RotationUtils;
import baritone.pathing.movement.MovementHelper;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.getitem.utils.Tag;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.InputHandler;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class PlaceBlockTask extends ActiveTask {

    private final int block;

    public PlaceBlockTask(int block) {
        this.block = block;
    }

    @Override
    public boolean activeTaskTick() {
        BlockPos pos = scanBlocks(block == Tag.BEDS);
        if (pos == null) {
            if (!Main.baritone.getCustomGoalProcess().isActive()) {
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalXZ(new BetterBlockPos(new BlockPos(ctx.playerFeet())))));
            }
            return true;
        }
        if (ctx.player().isOnGround()){
            Main.baritone.getPathingBehavior().forceCancel();
            Main.baritone.getLookBehavior().updateTarget(RotationUtils.calcRotationFromVec3d(ctx.playerHead(), new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5), ctx.playerRotations()), true);
            Optional<BlockPos> look = ctx.getSelectedBlock();
            if (look.isPresent()) {
                BlockPos lPos = look.get();
                if (lPos.equals(pos.down())) {
                    InputHandler.press(InputHandler.Input.SNEAK);
                    if (ctx.player().currentScreenHandler.syncId != 0) {
                        ctx.player().closeHandledScreen();
                        return true;
                    }

                    if (InventoryHelper.isSelected(ctx, block)) {
                        if (InteractionHelper.interactBlock(ctx)) {
                            taskState = TaskState.SUCCESS;
                        }
                        return true;
                    }

                    if (!InventoryHelper.equip(ctx, block, SortInvTask.TEMPORARY, true)) {
                        fail("I don't have this block!");
                        return true;
                    }

                    return true;
                } else {
                    MovementHelper.switchToBestToolFor(ctx, ctx.world().getBlockState(ctx.getSelectedBlock().orElse(null)));
                    InputHandler.press(InputHandler.Input.LEFT_CLICK);
                }
            }
        }
        return true;
    }

    public BlockPos scanBlocks(boolean twoBlocks) {
        for (int i = 2; i < 6; i++) {
            Direction direction = Direction.byId(i);
            int sign = (i % 2 == 0 ? -1 : 1);
            BlockPos pos = (new BlockPos(ctx.playerFeetAsVec().add((i < 4 ? 0 : 0.3) * sign, 0, (i < 4 ? 0.3 : 0) * sign))).offset(direction);
            if (ctx.world().getBlockState(pos).getMaterial().isReplaceable() && ctx.world().getBlockState(pos.offset(Direction.DOWN)).getMaterial().isSolid()) {
                if (!twoBlocks || ctx.world().getBlockState(pos.offset(direction)).getMaterial().isReplaceable()) {
                    return pos;
                }
            }
        }
        return null;
    }
}
