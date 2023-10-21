package scriptunseen.bassclef.tasks.active.overworld;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.PlaceBlockTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.Tag;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.ChatHelper;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import scriptunseen.bassclef.utils.helper.WorldScanner;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;

public class SetSpawnPointTask extends ActiveTask {

    private final long startTime;
    private BlockPos bed;
    private BlockPos pos;

    public SetSpawnPointTask() {
        this(null);
    }

    public SetSpawnPointTask(BlockPos pos) {
        this.startTime = System.currentTimeMillis();
        this.bed = null;
        this.pos = pos;
    }

    @Override
    public boolean activeTaskTick() {
        if (pos == null) {
            if (bed == null || getRunTime() % 20 == 0) {
                bed = WorldScanner.getNearestBlock(ctx, new BlockOptionalMetaLookup(Tag.getBlocks(Tag.BEDS)), 64, Math.max(5, ctx.playerFeet().y - 20), Math.min(256, ctx.playerFeet().y + 20));
                if (bed == null) {
                    if (InventoryHelper.isSelected(ctx, Tag.BEDS)) {
                        runSubTask(new PlaceBlockTask((Tag.BEDS)));
                        return true;
                    }
                    if (!InventoryHelper.equip(ctx, Tag.BEDS, SortInvTask.TEMPORARY, true)) {
                        fail("No bed nearby or in Inventory!");
                        return true;
                    }
                }
            } else {
                if (bed.equals(ctx.getSelectedBlock().orElse(null))) {
                    InteractionHelper.interactBlock(ctx);
                } else {
                    if (!Main.baritone.getCustomGoalProcess().isActive()) {
                        Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalNear(bed, 1));
                    }
                    Rotation rot = RotationUtils.calcRotationFromVec3d(ctx.playerHead(), new Vec3d(bed.getX() + 0.5, bed.getY() + 0.5, bed.getZ() + 0.5), ctx.playerRotations());
                    HitResult hr = RayTraceUtils.rayTraceTowards(ctx.player(), rot, ctx.playerController().getBlockReachDistance());
                    if (bed.equals(new BlockPos(hr.getPos()))) {
                        Main.baritone.getLookBehavior().updateTarget(rot, true);
                    }
                }
            }
            return true;
        }

        if (ctx.playerFeet().isWithinDistance(pos, 10)) {
            pos = null;
            Main.baritone.getPathingBehavior().forceCancel();
            return true;
        }

        if (!Main.baritone.getCustomGoalProcess().isActive()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(pos));
        }
        return true;
    }

    @Override
    protected boolean reachedGoal() {
        for (Map.Entry<Long, List<Text>> entry : ChatHelper.getChat().entrySet()) {
            if (startTime < entry.getKey()) {
                for (Text text : entry.getValue()) {
                    if (text.equals(new TranslatableText("block.minecraft.set_spawn"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
