package scriptunseen.bassclef.tasks.active.end;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BetterBlockPos;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.Tag;
import scriptunseen.bassclef.tasks.frames.FrameTaskManager;
import scriptunseen.bassclef.tasks.frames.OneCycleFrameTask;
import scriptunseen.bassclef.tasks.passive.DontLookAtEnderman;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.ChatHelper;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.LandingPhase;
import net.minecraft.entity.boss.dragon.phase.SittingScanningPhase;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class OneCycleTask extends ActiveTask {

    private final BlockPos pillar;
    private final BlockPos obi;
    private int lastBed;
    private boolean firstBed;
    private int toStand;

    public OneCycleTask(BlockPos pillar) {
        this.pillar = pillar;
        this.obi = pillar.add(-1, 1, 0);
        this.lastBed = 20;
        this.firstBed = true;
    }

    @Override
    public boolean activeTaskTick() {
        if (!ctx.player().isAlive()) {
            return true;
        }
        Optional<Entity> e = ctx.entitiesStream().filter(entity -> entity instanceof EnderDragonEntity).findFirst();
        EnderDragonEntity dragon;
        if (e.orElse(null) instanceof EnderDragonEntity && (dragon = (EnderDragonEntity) e.get()).isAlive()) {
            if (ctx.playerFeet().x * ctx.playerFeet().x + ctx.playerFeet().z * ctx.playerFeet().z > 7 * 7) {
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalXZ(new BetterBlockPos(-1, 0, 0)));
            }
            if (dragon.getPhaseManager().getCurrent() instanceof LandingPhase) {
                if (dragon.getBlockPos().getY() > pillar.getY() + 10) {
                    if (dragon.partHead.getPos().getX() > 10 || ctx.playerFeet().equals(pillar.add(0, -3, Integer.signum(toStand += dragon.partHead.getBlockPos().getZ()) * 2))) {
                        return false;
                    }
                    //toStand += dragon.partHead.getBlockPos().getZ();
                    if (!(Main.baritone.getCustomGoalProcess().getGoal() instanceof GoalBlock && ((GoalBlock) Main.baritone.getCustomGoalProcess().getGoal()).getGoalPos().equals(pillar.add(0, -3, Integer.signum(toStand) * 2)))) {
                        Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(new BetterBlockPos(pillar.add(0, -3, Integer.signum(toStand) * 2))));
                    }
                } else {
                    if (FrameTaskManager.hasTask()) {
                        TaskManager.disableTask(DontLookAtEnderman.class);
                        if (!Registry.BLOCK.getId(ctx.world().getBlockState(pillar.offset(Direction.UP)).getBlock()).toString().contains("bed")) {
                            if (Main.client.crosshairTarget != null && Main.client.crosshairTarget.getType() == HitResult.Type.BLOCK && ((BlockHitResult) Main.client.crosshairTarget).getBlockPos().offset(((BlockHitResult) Main.client.crosshairTarget).getSide()).equals(obi.offset(Direction.EAST))) {
                                InventoryHelper.equip(ctx, Tag.BEDS, SortInvTask.TEMPORARY, true);
                                InteractionHelper.interactBlock(ctx);
                            }
                        }
                        if (!Main.baritone.getPathingBehavior().isPathing()) {
                            InteractionHelper.lookAt(ctx, new Vec3d(obi.getX() + 1, obi.getY() + 0.5, obi.getZ() + Integer.signum(toStand) < 0 ? 0.1 : 0.9));
                        }
                    } else {
                        FrameTaskManager.setFrameTask(new OneCycleFrameTask(this, pillar));
                    }
                }
            } else if ((dragon.getPhaseManager().getCurrent() instanceof LandingPhase && Main.baritone.getPathingBehavior().isPathing()) || dragon.getPhaseManager().getCurrent() instanceof SittingScanningPhase) {
                FrameTaskManager.setFrameTask(null);
                fail("failed killing dragon! Trying again!");
            }
        } else if (ctx.world().getDimension().hasEnderDragonFight()) {
            end();
        }
        lastBed++;
        return false;
    }

    private void end() {
        TaskManager.enableTask(DontLookAtEnderman.class);
        FrameTaskManager.setFrameTask(null);
        ChatHelper.displayChatMessage("killed the dragon!");
        taskState = TaskState.SUCCESS;
    }

    public boolean isFirstBed() {
        return firstBed;
    }

    public void setFirstBed(boolean b) {
        firstBed = b;
    }

    public int getLastBed() {
        return lastBed;
    }

    public void resetLastBed() {
        lastBed = 0;
    }
}
