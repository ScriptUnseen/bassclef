package scriptunseen.bassclef.tasks.passive;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalInverted;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.utils.helper.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.util.math.Direction;
import scriptunseen.bassclef.utils.helper.*;

import java.util.Comparator;
import java.util.Optional;

public class RunAwayTask extends PassiveTask {

    private static int running = 0;

    private int fireTimer = 0;
    private static int fireRes = 0;
    private boolean pausedTaskManager = false;


    @Override
    public boolean taskTick() {
        if (ctx.player().currentScreenHandler.syncId != 0 || ctx.player().isDead()) {
            return true;
        }

        if (ctx.player().isOnFire() && !ctx.player().hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            if (fireRes == 0) {
                fireRes = InventoryHelper.getFireRes(ctx);
            }
            switch (fireRes) {
                case 1:
                    if (ctx.player().isOnGround()) {
                        if (fireTimer++ == 0 || fireTimer > 5) {
                            if (ctx.playerRotations().getPitch() == 90 && ctx.player().inventory.getMainHandStack().getItem() instanceof SplashPotionItem) {
                                InteractionHelper.interactItem(ctx);
                                return true;
                            } else {
                                ctx.player().pitch = 90;
                                InventoryHelper.equip(ctx, InventoryHelper.getID("splash_potion"), SortInvTask.TEMPORARY, true);
                            }
                        }
                    }
                    break;
                case 2:
                    if (fireTimer == 0 || ++fireTimer > 5) {
                        if (ctx.player().inventory.getMainHandStack().getItem() instanceof PotionItem) {
                            InputHandler.press(InputHandler.Input.RIGHT_CLICK);
                            return true;
                        } else {
                            InventoryHelper.equip(ctx, InventoryHelper.getID("potion"), SortInvTask.TEMPORARY, true);
                        }
                    }
                    break;
            }
        } else {
            fireTimer = 0;
            fireRes = 0;
        }

        if (ctx.player().getHealth() >= ctx.player().getMaxHealth() && pausedTaskManager) {
            TaskManager.resume();
            pausedTaskManager = false;
        }

        if (ctx.player().getHealth() < 12) {
            if (!pausedTaskManager) {
                pausedTaskManager = true;
                TaskManager.pause();
            }

            Optional<Entity> enemy = getNearestMob();
            enemy.ifPresent(entity -> {
                if (entity.distanceTo(ctx.player()) < 5) {
                    System.out.println(Main.baritone.getCustomGoalProcess().getGoal());
                    if (!(Main.baritone.getCustomGoalProcess().getGoal() instanceof GoalInverted)) {
                        if (Main.baritone.getCustomGoalProcess().getGoal() != null) {
                            Main.baritone.getPathingBehavior().forceCancel();
                            ChatHelper.displayChatMessage("canceled");
                        }
                        ChatHelper.displayChatMessage("started running!");
                        Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalBlock(enemy.get().getBlockPos())));
                    }
                } else if (entity.distanceTo(ctx.player()) < 15) {
                    if (!(ctx.player().isTouchingWater() || WorldHelper.inHole(ctx.player()))) {
                        if (Main.baritone.getCustomGoalProcess().getGoal() == null) {
                            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(ctx.playerFeet().offset(Direction.DOWN, 3)));
                        }
                    }
                }
            });
        }
        return true;
    }



    public boolean taskTick2() {
        if (ctx.player().currentScreenHandler.syncId != 0 || ctx.player().isDead()) {
            return true;
        }

        if (!isRunning()) {
            if (ctx.player().isOnFire() && !ctx.player().hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                running = 1;
            } else if (ctx.player().getHealth() < 10) {
                Optional<Entity> enemy = getNearestMob(5);
                TaskManager.pause();

                if (enemy.isPresent()) {
                    ChatHelper.displayChatMessage("started running!");
                    Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalBlock(enemy.get().getBlockPos())));
                    running = 2;
                    return true;
                }

                if (getNearestMob(15).isPresent()) {
                    Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(ctx.playerFeet().offset(Direction.DOWN, 3)));
                    running = 3;
                    return true;
                }

                running = 4;
            }
        } else if (ctx.player().getHealth() >= ctx.player().getMaxHealth()) {
            ChatHelper.displayChatMessage("stopped running!");
            stopRunning();
        } else {
            switch (running) {
                case 1:
                    if (!ctx.player().isOnFire() || ctx.player().hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                        stopRunning();
                    } else {
                        usePotion();
                    }
                    break;
                case 2:
                    if (!getNearestMob(5).isPresent()) {
                        ChatHelper.displayChatMessage("stopped running!");
                        stopRunning();
                    }
                    break;
                case 3:
                    if (!(ctx.player().isTouchingWater() || WorldHelper.inHole(ctx.player()))) {
                        if (!Main.baritone.getPathingBehavior().isPathing()) {
                            ChatHelper.displayChatMessage("started digging!");
                            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(ctx.playerFeet().offset(Direction.DOWN, 3)));
                        }
                    }
                    break;
                case 4:
                    break;
            }
        }
        return true;
    }

    public static void resetRunning() {
        running = 0;
    }

    public static boolean isRunning() {
        return running != 0;
    }

    private Optional<Entity> getNearestMob() {
        return ctx.entitiesStream().filter(entity -> entity instanceof HostileEntity).min(Comparator.comparingDouble(value -> value.distanceTo(ctx.player())));
    }

    private Optional<Entity> getNearestMob(int dist) {
        return ctx.entitiesStream().filter(entity -> entity instanceof HostileEntity).filter(entity -> entity.distanceTo(ctx.player()) < dist).min(Comparator.comparingDouble(value -> value.distanceTo(ctx.player())));
    }

    public void stopRunning() {
        Main.baritone.getPathingBehavior().forceCancel();
        TaskManager.resume();
        running = 0;
    }

    private void usePotion() {
        switch (InventoryHelper.getFireRes(ctx)) {
            case 0:
                if (!ctx.world().getDimension().isUltrawarm()) {
                    TaskManager.pause();
                    Main.baritone.getGetToBlockProcess().getToBlock(Blocks.WATER);
                }
                break;
            case 1:
                if (ctx.player().isOnGround()) {
                    if (ctx.playerRotations().getPitch() == 90 && ctx.player().inventory.getMainHandStack().getItem() instanceof SplashPotionItem) {
                        InteractionHelper.interactItem(ctx);
                        stopRunning();
                    } else {
                        ctx.player().pitch = 90;
                        InventoryHelper.equip(ctx, InventoryHelper.getID("splash_potion"), SortInvTask.TEMPORARY, true);
                    }
                }
                break;
            case 2:
                if (ctx.player().inventory.getMainHandStack().getItem() instanceof PotionItem) {
                    InputHandler.press(InputHandler.Input.RIGHT_CLICK);
                } else {
                    InventoryHelper.equip(ctx, InventoryHelper.getID("potion"), SortInvTask.TEMPORARY, true);
                }
                break;
        }
    }

    public static int getFireRes() {
        return fireRes;
    }
}
