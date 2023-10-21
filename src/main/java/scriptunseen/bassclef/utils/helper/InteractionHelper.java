package scriptunseen.bassclef.utils.helper;

import baritone.api.utils.IPlayerContext;
import baritone.api.utils.RotationUtils;
import scriptunseen.bassclef.Main;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;

public interface InteractionHelper {
    static void attackEntity(IPlayerContext ctx, Entity target) {
        attackEntity(ctx, target, false);
    }

    static void attackEntity(IPlayerContext ctx, Entity target, boolean lookAt) {
        if (lookAt) {
            lookAtEntity(ctx, target);
        }
        Main.getInteractionManager().attackEntity(ctx.player(), target);
    }

    static void lookAtEntity(IPlayerContext ctx, Entity entity) {
        Main.baritone.getLookBehavior().updateTarget(RotationUtils.calcRotationFromVec3d(ctx.playerHead(), entity.getPos(), ctx.playerRotations()), false);
    }

    static void lookAt(IPlayerContext ctx, Vec3d vec) {
        Main.baritone.getLookBehavior().updateTarget(RotationUtils.calcRotationFromVec3d(ctx.playerHead(), vec, ctx.playerRotations()), true);
    }

    static boolean interactBlock(IPlayerContext ctx) {
        if (Main.client.crosshairTarget instanceof BlockHitResult) {
            for (Hand hand : Hand.values()) {
                ItemStack itemStack = ctx.player().getStackInHand(hand);
                BlockHitResult blockHitResult = (BlockHitResult) Main.client.crosshairTarget;
                int i = itemStack.getCount();
                ActionResult actionResult2 = Main.getInteractionManager().interactBlock(ctx.player(), Main.client.world, hand, blockHitResult);
                if (actionResult2.isAccepted()) {
                    if (actionResult2.shouldSwingHand()) {
                        ctx.player().swingHand(hand);
                        if (!itemStack.isEmpty() && (itemStack.getCount() != i || Main.getInteractionManager().hasCreativeInventory())) {
                            Main.client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                        }
                    }
                    return true;
                }

                if (actionResult2 == ActionResult.FAIL) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    static void interactItem(IPlayerContext ctx) {
        System.out.println("interacting:");
        for (Hand hand : Hand.values()) {
            ItemStack itemStack = ctx.player().getStackInHand(hand);
            if (!itemStack.isEmpty()) {
                ActionResult actionResult3 = Main.getInteractionManager().interactItem(ctx.player(), ctx.world(), hand);
                if (actionResult3.isAccepted()) {
                    if (actionResult3.shouldSwingHand()) {
                        ctx.player().swingHand(hand);
                    }

                    Main.client.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                    return;
                }
            }
        }
    }

    static void interactEntity(IPlayerContext ctx, Entity entity) {
        Main.getInteractionManager().interactEntity(ctx.player(), entity, Hand.MAIN_HAND);
    }

    static boolean isLookingAtEnderman(IPlayerContext ctx, Entity enderman) {
        if (enderman instanceof EndermanEntity) {
            ItemStack itemStack = ctx.player().inventory.armor.get(3);
            if (itemStack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
                return false;
            } else {
                Vec3d vec3d = ctx.player().getRotationVec(1.0F).normalize();
                Vec3d vec3d2 = new Vec3d(enderman.getX() - ctx.player().getX(), enderman.getEyeY() - ctx.player().getEyeY(), enderman.getZ() - ctx.player().getZ());
                double d = vec3d2.length();
                vec3d2 = vec3d2.normalize();
                double e = vec3d.dotProduct(vec3d2);
                return e > 1.0D - 0.025D / d && ctx.player().canSee(enderman);
            }
        }
        return false;
    }
}
