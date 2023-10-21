package scriptunseen.bassclef.tasks.passive;

import baritone.api.utils.Rotation;
import baritone.api.utils.input.Input;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;


public class DontLookAtEnderman extends PassiveTask {

    private int timer;

    public DontLookAtEnderman() {
        timer = 0;
    }

    @Override
    public boolean taskTick() {
        if (Main.client.crosshairTarget != null && Main.client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            EntityHitResult result = (EntityHitResult) Main.client.crosshairTarget;
            if (result.getEntity() instanceof EndermanEntity) {
                ctx.player().pitch = 45;
            }
        }
        if (ctx.entitiesStream().anyMatch(entity -> InteractionHelper.isLookingAtEnderman(ctx, entity))) {
            ctx.player().pitch = 90;
        }
        if (timer++ > 10 && ctx.world().getDimension().hasEnderDragonFight() && ctx.playerRotations().getPitch() < 45.0F) {
            timer = 0;
            if (!Main.baritone.getInputOverrideHandler().isInputForcedDown(Input.CLICK_LEFT))
                Main.baritone.getLookBehavior().updateTarget(new Rotation(ctx.playerRotations().getYaw(), 45.0F), false);
        }
        return false;
    }


}