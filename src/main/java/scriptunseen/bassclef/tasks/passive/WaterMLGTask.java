package scriptunseen.bassclef.tasks.passive;

import baritone.api.utils.Rotation;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.utils.helper.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import scriptunseen.bassclef.utils.helper.*;

public class WaterMLGTask extends PassiveTask {

    private boolean mlg;
    private double height;

    public WaterMLGTask() {
        this.mlg = false;
        this.height = -1;
    }

    @Override
    public boolean taskTick() {
        if (ctx.world().getDimension().hasEnderDragonFight()) {
            if (mlg) {
                if (WorldScanner.getAirBlocks(ctx, ctx.playerFeet()) < ctx.playerController().getBlockReachDistance() * 2) {
                    Main.baritone.getLookBehavior().updateTarget(new Rotation(ctx.playerRotations().getYaw(), 90.0f), true);
                    if (isEmpty()) {
                        if (ctx.player().isTouchingWater()) {
                            interact();
                            mlg = false;
                        }
                    } else {
                        if (!ctx.player().isOnGround() && !ctx.player().isTouchingWater() && ctx.player().inventory.getStack(SortInvTask.BUCKET).getItem().equals(Registry.ITEM.get(new Identifier("minecraft:water_bucket")))) {
                            interact();
                        }
                    }
                }
                if (ctx.player().isOnGround()) mlg = false;
            } else if (!ctx.player().isOnGround()) {
                if (height == -1) {
                    height = ctx.playerFeetAsVec().y;
                } else if (height - ctx.playerFeetAsVec().y > 3) {

                    // Is only needed if he gets knocked away from the dragon
                    LivingEntity attacker = ctx.player().getAttacker();
                    if (attacker instanceof EnderDragonEntity) {
                        ChatHelper.displayChatMessage("Preparing for mlg!", true);
                        mlg = true;
                    }
                }
            } else {
                height = -1;
            }
        }
        return false;
    }

    private boolean isEmpty() {
        return (new Inventory()).contains(new Thing(InventoryHelper.getID("minecraft:bucket"), 1));
    }

    private void interact() {
        ctx.player().inventory.selectedSlot = SortInvTask.BUCKET;
        InteractionHelper.interactItem(ctx);
    }
}
