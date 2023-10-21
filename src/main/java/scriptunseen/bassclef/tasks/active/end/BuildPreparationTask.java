package scriptunseen.bassclef.tasks.active.end;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.RotationUtils;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BuildPreparationTask extends ActiveTask {

    private final BlockPos pillar;
    private final BlockPos temp;
    private final BlockPos obi;

    public BuildPreparationTask(BlockPos pillar) {
        this.pillar = pillar;
        this.temp = pillar.offset(Direction.WEST);
        this.obi = temp.offset(Direction.UP);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean activeTaskTick() {
        if (ctx.playerFeet().equals(pillar.add(-2, 0, 2))) {
            if (ctx.world().getBlockState(temp).isAir()) {
                if (Main.client.crosshairTarget.getType() == HitResult.Type.BLOCK && ((BlockHitResult) Main.client.crosshairTarget).getBlockPos().offset(((BlockHitResult) Main.client.crosshairTarget).getSide()).equals(temp)) {
                    ctx.player().inventory.selectedSlot = SortInvTask.BLOCKS;
                    InteractionHelper.interactBlock(ctx);
                } else {
                    Main.baritone.getLookBehavior().updateTarget(RotationUtils.calcRotationFromVec3d(ctx.playerHead(), new Vec3d(pillar.getX(), pillar.getY() + 0.5, pillar.getZ() + 0.5), ctx.playerRotations()), true);
                }
                return false;
            } else if (ctx.world().getBlockState(obi).isAir()) {
                if (Main.client.crosshairTarget.getType() == HitResult.Type.BLOCK && ((BlockHitResult) Main.client.crosshairTarget).getBlockPos().offset(((BlockHitResult) Main.client.crosshairTarget).getSide()).equals(obi)) {
                    InventoryHelper.equip(ctx, InventoryHelper.getID("minecraft:obsidian"), SortInvTask.TEMPORARY, true);
                    InteractionHelper.interactBlock(ctx);
                } else {
                    Main.baritone.getLookBehavior().updateTarget(RotationUtils.calcRotationFromVec3d(ctx.playerHead(), new Vec3d(pillar.getX() - 0.5, pillar.getY() + 1, pillar.getZ() + 0.5), ctx.playerRotations()), true);
                }
                return false;
            } else {
                taskState = TaskState.SUCCESS;
                return false;
            }
        } else if (!Main.baritone.getCustomGoalProcess().isActive()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(new BetterBlockPos(pillar.add(-2, 0, 2))));
        }
        return true;
    }
}
