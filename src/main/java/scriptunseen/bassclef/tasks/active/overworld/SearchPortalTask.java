package scriptunseen.bassclef.tasks.active.overworld;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.pathing.goals.GoalYLevel;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMetaLookup;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.GetSthTask;
import scriptunseen.bassclef.tasks.active.getitem.GetItemTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.*;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import scriptunseen.bassclef.utils.BaritoneSettings;

import java.util.Optional;

public class SearchPortalTask extends GetSthTask {

    private int phase;
    private int timer;
    private Vec3d pPos;
    private double x, z;
    private BetterBlockPos goal;
    private int counter;
    private BlockPos toStand;
    private BlockPos toPlace;
    private boolean b;

    public SearchPortalTask() {
        phase = -1;
        timer = 0;
        counter = 0;
        b = false;
        toPlace = null;
    }

    @Override
    public boolean activeTaskTick() {
        switch (phase) {
            case -1:
                if (ctx.playerFeet().y > 60) {
                    Main.baritone.getPathingBehavior().forceCancel();
                    phase++;
                } else if (!Main.baritone.getCustomGoalProcess().isActive()) {
                    Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalYLevel(64));
                }
                break;
            case 0:
                if ((new Inventory()).contains(new Thing(InventoryHelper.getID("minecraft:ender_eye"), 13))) {
                    InventoryHelper.equip(ctx, InventoryHelper.getID("minecraft:ender_eye"), SortInvTask.TEMPORARY, true);
                    phase++;
                } else {
                    runSubTask(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:ender_eye"), 13), false));
                }
                break;
            case 1:
                if (InventoryHelper.isSelected(ctx, InventoryHelper.getID("ender_eye"))) {
                    InteractionHelper.interactItem(ctx);
                    pPos = ctx.playerFeetAsVec();
                    phase++;
                } else {
                    InventoryHelper.equip(ctx, InventoryHelper.getID("ender_eye"), SortInvTask.TEMPORARY, true);
                }
                break;
            case 2:
                if (++timer > 20) {
                    timer = 0;
                    phase++;
                }
                break;
            case 3:
                Optional<Entity> eye = ctx.entitiesStream().filter(entity -> entity instanceof EyeOfEnderEntity).findFirst();
                if (eye.isPresent() && !ctx.playerFeet().isWithinDistance(eye.get().getPos(), 1)) {
                    Vec3d ePos = eye.get().getPos();
                    x = (ePos.x - pPos.x) * 20;
                    z = (ePos.z - pPos.z) * 20;
                    goal = new BetterBlockPos(ctx.playerFeet().add((int) x, 0, (int) z));
                    phase++;
                } else if (++timer > 2 * 20) {
                    timer = 0;
                    phase = 1;
                }
                break;
            case 4:
                if (Math.pow(ctx.playerFeet().x - goal.x, 2) + Math.pow(ctx.playerFeet().z - goal.z, 2) < 10 * 10) {
                    counter++;
                    goal = new BetterBlockPos(new BlockPos(pPos.add(x * counter, 0, z * counter)));
                    Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalXZ(goal));
                } else if (!Main.baritone.getCustomGoalProcess().isActive()) {
                    Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalXZ(goal));
                }
                scanChunks();
                break;
            case 5:
                int sumX = 0;
                int sumZ = 0;
                for (BlockPos p : blockPos) {
                    sumX += p.getX();
                    sumZ += p.getZ();
                }
                toStand = new BlockPos(sumX / 12 + (b ? -2 : 2), blockPos.get(0).getY(), sumZ / 12 + (b ? -2 : 2));
                phase++;
            case 6:
                if (!Main.baritone.getCustomGoalProcess().isActive()) {
                    if (ctx.player().isOnGround() && ctx.playerFeet().equals(toStand)) {
                        phase++;
                        counter = 0;
                        timer = 0;
                    } else if (ctx.player().isInsideWaterOrBubbleColumn() && ctx.world().getBiome(ctx.playerFeet()).getCategory() == Biome.Category.OCEAN) {
                        runSubTask(new GetDownInFluidTask());
                    } else {
                        Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(toStand));
                    }
                }
                break;
            case 7:
                if (counter < 12) {
                    clickOnPortalFrame();
                } else if (b) {
                    phase++;
                    toStand = toStand.offset(Direction.UP);
                } else {
                    b = true;
                    phase = 5;
                }
                break;
            case 8:
                if (!Main.baritone.getCustomGoalProcess().isActive()) {
                    if (ctx.playerFeet().equals(toStand)) {
                        phase++;
                    } else {
                        Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(toStand));
                    }
                }
                break;
            case 9:
                runSubTask(new SetSpawnPointTask());
                phase++;
                break;
            case 10:
                if (Main.positions.endPortal == null) {
                    Main.positions.endPortal = toStand.add(2, 0, 2);
                    BaritoneSettings.enterEnd();
                }
                if (ctx.world().getDimension().hasEnderDragonFight()) {
                    Main.baritone.getPathingBehavior().forceCancel();
                    taskState = TaskState.SUCCESS;
                } else if (!Main.baritone.getCustomGoalProcess().isActive()) {
                    Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(Main.positions.endPortal));
                }
                break;
        }
        return true;
    }

    private void scanChunks() {
        if (scan(new BlockOptionalMetaLookup(Blocks.END_PORTAL_FRAME), 10, 50).size() == 12) {
            Main.baritone.getPathingBehavior().forceCancel();
            phase++;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void clickOnPortalFrame() {
        if (counter % 2 == 0) {
            InventoryHelper.equip(ctx, InventoryHelper.getID("minecraft:ender_eye"), SortInvTask.TEMPORARY, true);
            if (counter < 6) {
                toPlace = toStand.offset(b ? Direction.SOUTH : Direction.NORTH, 3 - (counter / 2));
            } else {
                toPlace = toStand.offset(b ? Direction.EAST : Direction.WEST, 3 - (counter - 6) / 2);
            }
            InteractionHelper.lookAt(ctx, new Vec3d(toPlace.getX() + .5, toPlace.getY() + 1 - 3.0 / 16, toPlace.getZ() + .5));
            counter++;
        } else if (ctx.isLookingAt(toPlace) && InventoryHelper.isSelected(ctx, InventoryHelper.getID("minecraft:ender_eye"))) {
            Main.client.interactionManager.interactBlock(ctx.player(), ctx.player().clientWorld, Hand.MAIN_HAND, (BlockHitResult) Main.client.crosshairTarget);
            counter++;
        } else {
            counter--;
        }
    }
}
