package scriptunseen.bassclef.tasks.passive;

import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.KillEntityTask;
import scriptunseen.bassclef.tasks.active.nether.PiglinTradeTask;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FightMobTask extends PassiveTask {

    private static boolean hasGoldenArmor = false;
    private boolean fighting = false;

    public static boolean hasGoldenArmor() {
        return hasGoldenArmor;
    }

    @Override
    public boolean taskTick() {
        if (fighting) {
            if (TaskManager.getRunningTask() == null) {
                fighting = false;
                TaskManager.runTask(0);
            }
        } else {
            if (getRunTime() % 20 == 0) {
                ActiveTask task = TaskManager.getRunningTask();
                if (task != null && task.hasTask(PiglinTradeTask.class)) {
                    ctx.entitiesStream().filter(entity -> {
                        if (entity instanceof PiglinEntity && ctx.playerFeet().isWithinDistance(entity.getPos(), 5)) {
                            PiglinEntity.Activity activity = ((PiglinEntity) entity).getActivity();
                            return activity == PiglinEntity.Activity.ATTACKING_WITH_MELEE_WEAPON;
                        }
                        return false;
                    }).min(Comparator.comparingDouble(entity -> entity.distanceTo(ctx.player()))).ifPresent(entity -> {
                        Main.baritone.getPathingBehavior().forceCancel();
                        TaskManager.addPrimaryTaskAndRun(new KillEntityTask(entity.getUuid()));
                        fighting = true;
                    });
                }
            }
            hasGoldenArmor = InventoryHelper.hasGoldArmor(ctx);
            List<Entity> monster = ctx.entitiesStream().filter(this::monster).collect(Collectors.toList());
            if (monster.size() > 0) {
                ItemStack main = ctx.player().inventory.getMainHandStack();
                if (main.getDamage()*1.0 / main.getMaxDamage() < 0.3) {
                    InventoryHelper.equipWeapon(ctx, false);
                }
                monster.forEach(entity -> InteractionHelper.attackEntity(ctx, entity));
            }
        }
        return true;
    }



    private boolean monster(Entity entity) {
        if (entity.isAttackable() && entity.isAlive() && (entity instanceof HostileEntity || entity instanceof SlimeEntity || entity instanceof HoglinEntity || entity instanceof FireballEntity) && !(entity instanceof ZombifiedPiglinEntity) && !(entity instanceof EndermanEntity) && !(entity instanceof PiglinEntity && hasGoldenArmor)) {
            return entity.getPos().distanceTo(ctx.player().getPos()) < ctx.playerController().getBlockReachDistance();
        }
        return false;
    }
}