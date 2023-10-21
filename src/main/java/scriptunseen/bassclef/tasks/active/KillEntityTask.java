package scriptunseen.bassclef.tasks.active;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalInverted;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class KillEntityTask extends ActiveTask {

    private final UUID uuid;

    public KillEntityTask(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean activeTaskTick() {
        Optional<Entity> optionalEntity = ctx.entitiesStream().filter(entity -> entity.getUuid().equals(uuid)).findFirst();
        if (optionalEntity.isPresent()) {
            Entity entity = optionalEntity.get();
            if (entity.isAttackable()) {
                if (ctx.playerFeetAsVec().isInRange(entity.getPos(), ctx.playerController().getBlockReachDistance() * 2) && InventoryHelper.isSaveToEquip(ctx)) {
                    InventoryHelper.equipWeapon(ctx, false);
                    if (ctx.playerFeetAsVec().isInRange(entity.getPos(), ctx.playerController().getBlockReachDistance())) {
                        if (InventoryHelper.equipWeapon(ctx, false) && ctx.player().getAttackCooldownProgress(0.0F) == 1.0F) {
                            InteractionHelper.attackEntity(ctx, entity, true);
                        }
                        if (ctx.playerFeetAsVec().isInRange(entity.getPos(), 2)) {
                            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalBlock(entity.getBlockPos())));
                        }
                    } else {
                        Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(entity.getBlockPos()));
                    }
                }
                return true;
            }
        }
        Main.baritone.getPathingBehavior().forceCancel();
        taskState = TaskState.SUCCESS;
        return true;
    }
}
