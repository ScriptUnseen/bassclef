package scriptunseen.bassclef.tasks.active.nether;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalInverted;
import baritone.api.pathing.goals.GoalNear;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.tasks.passive.FightMobTask;
import scriptunseen.bassclef.utils.helper.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import scriptunseen.bassclef.utils.helper.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GetRodsTask extends ActiveTask {

    private final BlockPos spawner;

    public GetRodsTask(BlockPos spawner) {
        this.spawner = spawner;
        if (WorldScanner.getSpawnerEntity(ctx, spawner) instanceof BlazeEntity) {
            disableAutoFight();
        } else {
            fail();
        }
    }

    private void disableAutoFight() {
        TaskManager.disableTask(FightMobTask.class);
    }

    @Override
    public boolean activeTaskTick() {
        List<Entity> monster = ctx.entitiesStream().filter(this::monster).sorted(Comparator.comparingDouble(entity -> entity.distanceTo(ctx.player()))).collect(Collectors.toList());
        List<Entity> toClose = getEntitiesInDist(monster, 1.5);
        if (InventoryHelper.isSaveToEquip(ctx) && (monster.size() > 0 && monster.get(0).distanceTo(ctx.player()) < 6)) {
            InventoryHelper.equipWeapon(ctx, false, nearbyZombiePiglin());
        }
        Entity enemy = getTarget(monster);
        if (enemy != null) {
            runAway(enemy);
            if (toClose.size() > 0) {
                toClose.forEach(entity -> InteractionHelper.attackEntity(ctx, entity));
            } else {
                List<Entity> toHit = getEntitiesInDist(monster, ctx.playerController().getBlockReachDistance());
                if (toHit.size() > 0) {
                    if (ctx.player().isOnGround() && InventoryHelper.isSaveToEquip(ctx) && InventoryHelper.equipWeapon(ctx, true, nearbyZombiePiglin()) && ctx.player().getAttackCooldownProgress(0.0F) == 1.0F) {
                        InteractionHelper.attackEntity(ctx, enemy, true);
                    }
                } else {
                    findTarget();
                }
            }
        } else {
            findTarget();
        }
        return true;
    }

    private boolean monster(Entity entity) {
        return entity.isAttackable() && entity.isAlive() && (entity instanceof MobEntity && !(entity instanceof EndermanEntity) && !(entity instanceof ZombifiedPiglinEntity && !((ZombifiedPiglinEntity) entity).isAngryAt(ctx.player())) && !((entity instanceof PiglinEntity) && FightMobTask.hasGoldenArmor()));
    }

    private List<Entity> getEntitiesInDist(List<Entity> list, double dist) {
        List<Entity> inDist = new ArrayList<>();
        for (Entity e : list) {
            if (e.distanceTo(ctx.player()) < dist) {
                inDist.add(e);
            }
        }
        return inDist;
    }

    private boolean nearbyZombiePiglin() {
        return ctx.entitiesStream().anyMatch(entity -> entity instanceof ZombifiedPiglinEntity && entity.distanceTo(ctx.player()) < ctx.playerController().getBlockReachDistance());
    }

    private Entity getTarget(List<Entity> list) {
        return list.size() > 0 ? list.get(0) : null;
    }

    private void runAway(Entity enemy) {
        Goal goal = Main.baritone.getCustomGoalProcess().getGoal();
        double dist = enemy.distanceTo(ctx.player());
        if (goal instanceof GoalInverted) {
            if (dist > ctx.playerController().getBlockReachDistance() && ctx.player().isOnGround()) {
                Main.baritone.getPathingBehavior().forceCancel();
            }
        } else if (dist < 1.5) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalBlock(enemy.getBlockPos())));
        } else if (dist < ctx.playerController().getBlockReachDistance()){
            if (goal != null && ctx.player().isOnGround()) {
                Main.baritone.getPathingBehavior().forceCancel();
            }
        }
    }

    private void findTarget() {
        List<Entity> targets = ctx.entitiesStream().filter(this::target).sorted(Comparator.comparingDouble(value -> value.distanceTo(ctx.player()))).collect(Collectors.toList());
        Entity target = getTarget(targets);
        if (target != null && !(Main.baritone.getCustomGoalProcess().getGoal() instanceof GoalInverted)) {
            if (target instanceof BlazeEntity) {
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalNear(target.getBlockPos(), 3));
            } else if (target instanceof ItemEntity) {
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(WorldHelper.getActualBlockPosOfEntity(target)));
            }
        } else if (!Main.baritone.getCustomGoalProcess().isActive() && !ctx.playerFeet().isWithinDistance(spawner, 14) && ctx.player().getHealth() >= ctx.player().getMaxHealth()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalNear(spawner, 10));
        }
    }

    private boolean target(Entity entity) {
        return blazeRod(entity) || nearbyBlaze(entity);
    }

    private boolean blazeRod(Entity entity) {
        if (entity != null) {
            return entity instanceof ItemEntity && entity.getName().equals(Registry.ITEM.get(new Identifier("blaze_rod")).getName());
        }
        return false;
    }

    private boolean nearbyBlaze(Entity entity) {
        if (entity != null) {
            return entity.isAlive() && entity instanceof BlazeEntity && (entity.getPos().distanceTo(ctx.playerFeetAsVec()) < 30 || (spawner != null && spawner.isWithinDistance(entity.getPos(), 30))) && WorldScanner.getAirBlocks(ctx, entity.getBlockPos()) < 20;
        }
        return false;
    }

    @Override
    protected boolean reachedGoal() {
        Inventory inv = new Inventory();
        return inv.contains(new Thing(InventoryHelper.getID("minecraft:blaze_rod"), 7));
    }

    @Override
    public void finish() {
        ChatHelper.displayChatMessage("Collected the rods!");
        enableAutoFight();
    }

    private void enableAutoFight() {
        TaskManager.enableTask(FightMobTask.class);
    }
}
