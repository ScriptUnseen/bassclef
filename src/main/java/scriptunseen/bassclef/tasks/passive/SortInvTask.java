package scriptunseen.bassclef.tasks.passive;

import baritone.api.utils.IPlayerContext;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.getitem.utils.Tag;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class SortInvTask extends PassiveTask {
    public static final int[] pickaxes = {InventoryHelper.getID("minecraft:wooden_pickaxe"), InventoryHelper.getID("minecraft:golden_pickaxe"), InventoryHelper.getID("minecraft:stone_pickaxe"), InventoryHelper.getID("minecraft:iron_pickaxe"), InventoryHelper.getID("minecraft:diamond_pickaxe"),};
    public static final int[] shovels = {InventoryHelper.getID("minecraft:wooden_shovel"), InventoryHelper.getID("minecraft:golden_shovel"), InventoryHelper.getID("minecraft:stone_shovel"), InventoryHelper.getID("minecraft:iron_shovel"), InventoryHelper.getID("minecraft:diamond_shovel")};
    public static final int[] axes = {InventoryHelper.getID("minecraft:wooden_axe"), InventoryHelper.getID("minecraft:golden_axe"), InventoryHelper.getID("minecraft:stone_axe"), InventoryHelper.getID("minecraft:iron_axe"), InventoryHelper.getID("minecraft:diamond_axe")};
    public static final int[] swords = {InventoryHelper.getID("minecraft:wooden_sword"), InventoryHelper.getID("minecraft:golden_sword"), InventoryHelper.getID("minecraft:stone_sword"), InventoryHelper.getID("minecraft:iron_sword"), InventoryHelper.getID("minecraft:diamond_sword")};
    public static final int[] hoes = {InventoryHelper.getID("minecraft:wooden_hoe"), InventoryHelper.getID("minecraft:golden_hoe"), InventoryHelper.getID("minecraft:stone_hoe"), InventoryHelper.getID("minecraft:iron_hoe"), InventoryHelper.getID("minecraft:diamond_hoe")};

    public static final int PICKAXE = 0;
    public static final int SWORD = 1;
    public static final int AXE = 2;
    public static final int SHOVEL = 3;
    public static final int HOE = 4;
    public static final int FOOD = 5;
    public static final int BUCKET = 6;
    public static final int TEMPORARY = 7;
    public static final int BLOCKS = 8;

    private static final List<ItemToRemove> toKeep = new ArrayList<>();

    public static void initToKeep() {
        toKeep.clear();
        add(Tag.LOGS, 6, 36, 0);
        add(Tag.PLANKS, 7, 36, 0);
        add(Tag.BEDS, 1, 36, 0);
        add(Tag.WOOL, 10, 36, 1);

        add("minecraft:wheat", 5, 36, 0);
        add("minecraft:flint_and_steel", 0, 1, 1);
        add("minecraft:blaze_rod", 0, 1, 1);
        add("minecraft:blaze_powder", 0, 1, 1);
        add("minecraft:ender_pearl", 0, 2, 1);
        add("minecraft:ender_eye", 0, 1, 1);

        add("minecraft:stick", 15, 1, 0);

        add("minecraft:string", 7, 36, 0);

        add("minecraft:crafting_table", 2);
        add("minecraft:furnace", 15, 1, 0);

        add("minecraft:coal", 10, 36, 0);
        add("minecraft:flint", 2, 36, 0);
        add("minecraft:iron_ingot", 2, 36, 0);
        add("minecraft:gold_nugget", 2, 36, 0);
        add("minecraft:gold_ingot", 2, 36, 0);
        add("minecraft:diamond", 2, 36, 0);

        add("minecraft:iron_ore", 10, 36, 0);

        add("minecraft:cobblestone", 2, true);
        add("minecraft:dirt", 5, true);
        add("minecraft:andesite", 5, true);
        add("minecraft:granite", 5, true);
        add("minecraft:diorite", 5, true);
        add("minecraft:netherrack", 5, true);

        add("minecraft:hay_block", 1, 36, 0);
        add("minecraft:boat", 20);

        add("minecraft:bread", 1, 36, 0);
        add("minecraft:apple", 1, 36, 0);
        add("minecraft:chicken", 1, 36, 0);
        add("minecraft:cooked_chicken", 1, 36, 0);
        add("minecraft:beef", 1, 36, 0);
        add("minecraft:cooked_beef", 1, 36, 0);
        add("minecraft:mutton", 1, 36, 0);
        add("minecraft:cooked_mutton", 1, 36, 0);
        add("minecraft:porkchop", 1, 36, 0);
        add("minecraft:cooked_porkchop", 1, 36, 0);
        add("minecraft:rabbit", 1, 36, 0);
        add("minecraft:cooked_rabbit", 1, 36, 0);

        add("minecraft:bucket", 0);
        add("minecraft:water_bucket", 0);
        add("minecraft:lava_bucket", 0);

        add("minecraft:potion", 0, 3, 2);
        add("minecraft:splash_potion", 0, 3, 2);

        add("minecraft:shears", 0);
        add("minecraft:wooden_pickaxe", 0);
        add("minecraft:stone_pickaxe", 0);
        add("minecraft:iron_pickaxe", 0);
        add("minecraft:diamond_pickaxe", 0, 2, 1);
        add("minecraft:stone_axe", 0);
        add("minecraft:stone_hoe", 2);
        add("minecraft:diamond_shovel", 0);
        add("minecraft:diamond_sword", 0);
        add("minecraft:diamond_helmet", 0);
        add("minecraft:diamond_chestplate", 0);
        add("minecraft:diamond_leggings", 0);
        add("minecraft:golden_boots", 0);
        add("minecraft:diamond_boots", 0);

        add("minecraft:obsidian", 0);
    }

    public static void add(String name, int value, int max, int min) {
        add(InventoryHelper.getID(name), value, max, min);
    }

    public static void add(int id, int value, int max, int min) {
        toKeep.add(new ItemToRemove(id, value, max, min));
    }

    public static void add(String name, int value) {
        add(InventoryHelper.getID(name), value);
    }

    public static void add(String name, int value, boolean block) {
        add(InventoryHelper.getID(name), value, block);
    }

    public static void add(int id, int value) {
        toKeep.add(new ItemToRemove(id, value));
    }

    public static void add(int id, int value, boolean block) {
        toKeep.add(new ItemToRemove(id, value, block));
    }

    public static void remove(String name) {
        remove(InventoryHelper.getID(name));
    }

    public static void remove(int id) {
        for (int i = toKeep.size() - 1; i >= 0; i--) {
            if (toKeep.get(i).id == id) {
                toKeep.remove(i);
            }
        }
    }

    public static boolean contains(int id) {
        for (int i = toKeep.size() - 1; i >= 0; i--) {
            if (toKeep.get(i).id == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean taskTick() {
        if (getRunTime() % 20 == 0 && saveToThrowOut() && !RestoreFromDeathTask.isRestoring()) {
            removeTools();
            removeItems(ctx);
            equipTools();
        }
        return false;
    }

    private boolean saveToThrowOut() {
        return (!Main.baritone.getMineProcess().isActive() || Main.baritone.getPlayerContext().player().inventory.getMainHandStack().getItem() instanceof ToolItem) && ctx.player().currentScreenHandler.syncId == 0 && !ctx.player().isCreative();
    }

    private void removeTools() {
        removeTool(pickaxes);
        removeTool(shovels);
        removeTool(axes);
        removeTool(swords);
        removeTool(hoes);
    }

    public static void removeItems(IPlayerContext ctx) {
        int freeSlots = 0;
        Inventory inv = new Inventory();
        for (int i = 0; i < inv.size(); i++) {
            Thing thing = inv.getThing(i);
            if (thing != null) {
                ItemToRemove item = get(thing.getId());
                if (item == null) {
                    removeItem(ctx, thing.getId());
                } else if (item.max < inv.countStacks(thing.getId())) {
                    removeItem(ctx, thing.getId());
                    break; // Don't remove more than one stack of an item
                }
            } else {
                freeSlots++;
            }
        }
        if (freeSlots <= 2) {
            if (removeBlocks(ctx)) {
                Item item = null;
                for (int i = 0; i < inv.size(); i++) {
                    Thing thing = inv.getThing(i);
                    if (thing != null) {
                        ItemToRemove toRemove = get(thing.getId());
                        if (toRemove != null) {
                            Item newItem = new Item(i, toRemove, inv.countStacks(thing.getId()));
                            if ((item == null || item.value > item.value) && newItem.toRemove.min < newItem.count) {
                                item = newItem;
                            }
                        }
                    }
                }
                if (item != null) {
                    removeItem(ctx, item.toRemove.id);
                }
            }
        }
    }

    public static int firstThingToRemove() {
        Inventory inv = new Inventory();
        for (int i = 0; i < inv.size(); i++) {
            Thing thing = inv.getThing(i);
            if (thing != null) {
                ItemToRemove item = get(thing.getId());
                if (item == null) {
                    return thing.getId();
                } else if (item.max < inv.countStacks(thing.getId())) {
                    return item.id;
                }
            }
        }
        return -1;
    }

    private void equipTools() {
        int pick = getHighest(pickaxes);
        int shovel = getHighest(shovels);
        int sword = getHighest(swords);
        int axe = getHighest(axes);
        int hoe = getHighest(hoes);

        if (shovel != -1) {
            InventoryHelper.equip(ctx, shovel, SortInvTask.SHOVEL, false);
        }
        if (sword != -1) {
            InventoryHelper.equip(ctx, sword, SortInvTask.SWORD, false);
        }
        if (axe != -1) {
            InventoryHelper.equip(ctx, axe, SortInvTask.AXE, false);
        }
        if (hoe != -1) {
            InventoryHelper.equip(ctx, hoe, SortInvTask.HOE, false);
        }
        if (pick != -1) {
            InventoryHelper.equip(ctx, pick, SortInvTask.PICKAXE, false);
        }
    }

    private void removeTool(int[] tools) {
        int maxTool = getHighestTool(tools);
        if (maxTool >= 1) {
            for (int i = 0; i < maxTool; i++) {
                removeItem(ctx, tools[i]);
            }
        }
    }

    public static ItemToRemove get(int id) {
        for (ItemToRemove item : toKeep) {
            if (item.id == id) {
                return item;
            }
        }
        return null;
    }

    public static void removeItem(IPlayerContext ctx, int id) {
        DefaultedList<ItemStack> main = ctx.player().inventory.main;
        int slot = -1;
        for (int i = 0; i < main.size(); i++) {
            if (Registry.ITEM.getRawId(main.get(i).getItem()) == id) {
                if (slot == -1 || main.get(i).getCount() < main.get(slot).getCount()) {
                    slot = i;
                }
            }
        }
        if (slot != -1) {
            int slot2 = slot + ((slot <= 8) ? 36 : 0);
            InventoryHelper.clickSlot(Main.baritone.getPlayerContext(), slot2, 1, SlotActionType.THROW);
        }
    }

    private static boolean removeBlocks(IPlayerContext ctx) {
        Thing toRemove = null;
        Inventory inv = new Inventory();
        for (int i = 0; i < inv.size(); i++) {
            Thing thing = inv.getThing(i);
            if (thing != null) {
                ItemToRemove item = get(thing.getId());
                if (item != null && item.block) {
                    if (toRemove == null || toRemove.getCount() > thing.getCount()) toRemove = thing;
                }
            }
        }
        if (toRemove == null) {
            return true;
        } else {
            removeItem(ctx, toRemove.getId());
            return false;
        }
    }

    public static int getHighest(int[] tools) {
        int highest = getHighestTool(tools);
        return (highest == -1) ? -1 : tools[highest];
    }

    private static int getHighestTool(int[] tools) {
        Inventory inv = new Inventory();
        for (int i = tools.length - 1; i >= 0; i--) {
            if (inv.contains(new Thing(tools[i], 1))) {
                return i;
            }
        }
        return -1;
    }

    public static class ItemToRemove {
        private final int id;
        private final boolean block;
        private int value;
        private int max;
        private int min;

        public ItemToRemove(int id, int value) {
            this(id, value, 1, 1);
        }

        public ItemToRemove(int id, int value, int max, int min) {
            this.id = id;
            this.value = value;
            this.max = max;
            this.min = min;
            this.block = false;
        }

        public ItemToRemove(int id, int value, boolean block) {
            this.id = id;
            this.value = value;
            this.max = block ? 36 : 1;
            this.min = block ? 0 : 1;
            this.block = block;
        }

        public int getId() {
            return id;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public boolean isBlock() {
            return block;
        }
    }

    public static class Item {
        int slot;
        ItemToRemove toRemove;
        int count;
        double value;

        public Item(int slot, ItemToRemove toRemove, int count) {
            this.slot = slot;
            this.toRemove = toRemove;
            this.count = count;
            this.value = getValue();
        }

        public double getValue() {
            return toRemove.value * 1.0 / count;
        }
    }
}
