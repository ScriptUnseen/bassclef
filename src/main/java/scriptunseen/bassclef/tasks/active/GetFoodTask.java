package scriptunseen.bassclef.tasks.active;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalInverted;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BlockOptionalMetaLookup;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.getitem.CraftingTask;
import scriptunseen.bassclef.tasks.active.getitem.GetItemTask;
import scriptunseen.bassclef.tasks.active.getitem.SmeltItemTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;
import scriptunseen.bassclef.tasks.active.getitem.utils.SmeltItem;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import scriptunseen.bassclef.utils.helper.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class GetFoodTask extends GetSthTask {

    private final int foodLevel;
    private int state;

    public GetFoodTask(int foodLevel) {
        this.foodLevel = foodLevel;
        this.state = 0;
    }

    @Override
    public boolean activeTaskTick() {
        if (start == null) {
            start = ctx.playerFeet();
        }
        scan(new BlockOptionalMetaLookup("hay_block"), 60, 150);
        updateBlocks();
        Inventory inv = new Inventory();

        int wheat = inv.getCount(InventoryHelper.getID("minecraft:wheat"));
        if (wheat >= 3) {
            runSubTask(new CraftingTask(new Thing(InventoryHelper.getID("minecraft:bread"), wheat/3)));
            return true;
        }

        int hay = inv.getCount(InventoryHelper.getID("minecraft:hay_block"));
        if (blockPos.size() > 0 && (hay * 3 + inv.getCount(InventoryHelper.getID("minecraft:bread"))) * 6 < foodLevel) {
            if (!Main.baritone.getMineProcess().isActive()) {
                Main.baritone.getMineProcess().mine(new BlockOptionalMetaLookup("hay_block"));
            }
            return true;
        }

        if (Main.baritone.getMineProcess().isActive()) {
            Main.baritone.getMineProcess().cancel();
        }

        if (hay > 0) {
            runSubTask(new CraftingTask(new Thing(InventoryHelper.getID("minecraft:wheat"), hay * 3)));
            return true;
        }

        if (hungerIfSmelted() > foodLevel || (InventoryHelper.countHunger(true) < 10 && countUncooked() >= 7)) {
            runSubTask(new SmeltItemTask(getUncooked()));
            return true;
        }

        if (ctx.entitiesStream().anyMatch(this::foodItem)) {
            Optional<Entity> entity = ctx.entitiesStream().filter(this::foodItem).min(Comparator.comparingDouble(value -> value.distanceTo(ctx.player())));
            entity.ifPresent(value -> {
                if (state != 1) Main.baritone.getPathingBehavior().forceCancel();
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(WorldHelper.getActualBlockPosOfEntity(value)));
                state = 1;
            });
            return true;
        }

        if (ctx.entitiesStream().anyMatch(this::livingFood)) {
            Optional<Entity> entity = ctx.entitiesStream().filter(this::livingFood).min(Comparator.comparingDouble(value -> value.distanceTo(ctx.player())));
            entity.ifPresent(sheep -> {
                if (sheep.distanceTo(ctx.player()) < ctx.playerController().getBlockReachDistance()) {
                    if (ctx.player().isOnGround()) {
                        InputHandler.press(InputHandler.Input.JUMP);
                        return;
                    }
                    if (ctx.player().fallDistance == 0.0F) {
                        return;
                    }
                    if (InventoryHelper.isSaveToEquip(ctx)) {
                        if (InventoryHelper.equipWeapon(ctx, false)) {
                            if (!ctx.player().isUsingItem() && ctx.player().getAttackCooldownProgress(0.0F) == 1.0F) {
                                InteractionHelper.attackEntity(ctx, sheep, true);
                            }
                        } else {
                            runSubTask(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:stone_axe"), 1), true));
                            return;
                        }
                    }
                } else if (sheep.distanceTo(ctx.player()) < 7) {
                    if (InventoryHelper.isSaveToEquip(ctx)) {
                        InventoryHelper.equipWeapon(ctx, false);
                    }
                }
                if (state != 2) {
                    Main.baritone.getPathingBehavior().forceCancel();
                }
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(sheep.getBlockPos()));
                state = 2;
            });
            return true;
        }

        if (!Main.baritone.getCustomGoalProcess().isActive()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalXZ(start)));
        }
        return true;
    }

    private void updateBlocks() {
        for (int i = blockPos.size() - 1; i >= 0; i--) {
            if (!ctx.world().getBlockState(blockPos.get(i)).getBlock().equals(Blocks.HAY_BLOCK)) {
                blockPos.remove(i);
            }
        }
    }

    private boolean livingFood(Entity entity) {
        return (entity instanceof AnimalEntity && !((AnimalEntity) entity).isBaby()) && entity.isAlive() && (entity instanceof CowEntity || entity instanceof PigEntity || entity instanceof SheepEntity || entity instanceof ChickenEntity || entity instanceof RabbitEntity);
    }

    private boolean foodItem(Entity entity) {
        if (entity instanceof ItemEntity) {
            Item item = ((ItemEntity) entity).getStack().getItem();
            return item.isFood() && SortInvTask.contains(Registry.ITEM.getRawId(item)) && !ctx.world().getBlockState(entity.getBlockPos()).getMaterial().isLiquid();
        }
        return false;
    }

    private List<Thing> getUncooked() {
        Inventory inv = new Inventory();
        List<Thing> uncooked = new ArrayList<>();
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getThing(i) != null) {
                SmeltItem si = SmeltItem.getSmeltItemFromInp(inv.getThing(i).getId());
                if (si != null && Registry.ITEM.get(si.out).isFood()) {
                    uncooked.add(new Thing(si.out, inv.getThing(i).getCount()));
                }
            }
        }
        return uncooked;
    }

    private int countUncooked() {
        Inventory inv = new Inventory();
        int count = 0;
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getThing(i) != null && Registry.ITEM.get(inv.getThing(i).getId()).isFood() && SmeltItem.getSmeltItemFromInp(inv.getThing(i).getId()) != null) {
                count += inv.getThing(i).getCount();
            }
        }
        return count;
    }

    @SuppressWarnings("ConstantConditions")
    private int hungerIfSmelted() {
        List<Thing> uncooked = getUncooked();
        int hunger = InventoryHelper.countHunger(true);
        for (Thing thing : uncooked) {
            hunger += Registry.ITEM.get(thing.getId()).getFoodComponent().getHunger() * thing.getCount();
        }
        return hunger;
    }

    @Override
    protected boolean reachedGoal() {
        return InventoryHelper.countHunger(true) >= foodLevel;
    }
}
