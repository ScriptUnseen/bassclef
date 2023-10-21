package scriptunseen.bassclef.tasks.active.end;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BetterBlockPos;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.Tag;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.WorldScanner;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.LandingApproachPhase;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;

import java.util.Optional;

public class PrepareOneCycleTask extends ActiveTask {

    private BlockPos middlePillar;
    private boolean up;
    private int yLevel;
    int toStand;
    private boolean prepared;

    public PrepareOneCycleTask() {
        up = false;
        prepared = false;
        toStand = -48;
    }

    @Override
    public boolean activeTaskTick() {
        if (!new Inventory().contains(new Thing(Tag.BEDS, 5))) {
            fail("No beds!");
            return true;
        }
        if (prepared) {
            Optional<Entity> e = ctx.entitiesStream().filter(entity -> entity instanceof EnderDragonEntity).findFirst();
            EnderDragonEntity dragon;
            if (e.isPresent()) {
                dragon = (EnderDragonEntity) e.get();
                if (dragon.isAlive()) {
                    if (dragon.getPhaseManager().getCurrent() instanceof LandingApproachPhase && !(toLate(dragon) || toDangerous())) {
                        Main.baritone.getPathingBehavior().forceCancel();
                        runSubTask(new OneCycleTask(middlePillar));
                    } else if (ctx.playerFeet().x == toStand && ctx.playerFeet().z == -1) {
                        if (ctx.entitiesStream().anyMatch(entity -> entity instanceof DragonFireballEntity)) {
                            if (!up) {
                                up = true;
                                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(ctx.playerFeet().offset(Direction.UP, 6)));
                            }
                            return false;
                        } else if (isNearBreath(false)) {
                            up = true;
                            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(ctx.playerFeet().offset(Direction.UP, 6)));
                        } else if (up) {
                            if (ctx.player().isOnGround()) {
                                up = false;
                                Main.baritone.getPathingBehavior().forceCancel();
                            }
                        } else if (!isNearBreath(true)) {
                            if (!Main.baritone.getCustomGoalProcess().isActive() && ctx.playerFeet().getY() > yLevel) {
                                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(ctx.playerFeet().offset(Direction.DOWN)));
                            }
                        }
                    } else if (!Main.baritone.getCustomGoalProcess().isActive()) {
                        Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(new BetterBlockPos(toStand, yLevel, -1)));
                    }
                } else if (ctx.world().getDimension().hasEnderDragonFight()) {
                    taskState = TaskState.SUCCESS;
                }
            } else if (ctx.world().getDimension().hasEnderDragonFight()) {
                taskState = TaskState.SUCCESS;
            }
        } else {
            yLevel = getYLevel();
            middlePillar = getMiddlePillar();
            if (middlePillar != null) {
                prepared = true;
                runSubTask(new BuildPreparationTask(middlePillar));
            }
        }
        return true;
    }

    private boolean toLate(EnderDragonEntity dragon) {
        return ctx.world().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(-40, 0, 0)).getY() + 15 > dragon.getBlockPos().getY() && dragon.getBlockPos().getX() > 30;
    }

    private boolean toDangerous() {
        return ctx.entitiesStream().anyMatch(entity -> entity instanceof AreaEffectCloudEntity && (entity.getPos().x + 40) * (entity.getPos().x + 40) + (entity.getPos().z * entity.getPos().z) < 30 * 30);
    }

    private boolean isNearBreath(boolean b) {
        return ctx.entitiesStream().anyMatch(entity -> entity instanceof AreaEffectCloudEntity && ctx.playerHead().getY() - (b ? 1 : 0) - entity.getPos().getY() < 3 && Math.abs(ctx.playerFeetAsVec().x - entity.getPos().x) < 5 && Math.abs(ctx.playerFeetAsVec().z - entity.getPos().z) < 5);
    }

    private int getYLevel() {
        return 255 - WorldScanner.getAirBlocks(ctx, new BlockPos(toStand, 255, -1));
    }

    private BlockPos getMiddlePillar() {
        if (ctx.world().isChunkLoaded(0, 0)) {
            for (int i = 0; i < 255; i++) {
                if (ctx.world().getBlockState(new BlockPos(0, i, 0)).getBlock().equals(Blocks.BEDROCK) && !ctx.world().getBlockState(new BlockPos(0, i + 1, 0)).getBlock().equals(Blocks.BEDROCK))
                    return new BlockPos(0, i, 0);
            }
        }
        return null;
    }
}
