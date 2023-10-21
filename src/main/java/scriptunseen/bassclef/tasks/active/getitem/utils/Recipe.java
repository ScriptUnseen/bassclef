package scriptunseen.bassclef.tasks.active.getitem.utils;

import scriptunseen.bassclef.utils.helper.InventoryHelper;
import org.jetbrains.annotations.Nullable;

public enum Recipe {
    PLANKS(Tag.PLANKS, 4, new CraftingItem[]{new CraftingItem(Tag.LOGS, new int[]{1})}, false),
    STICKS(InventoryHelper.getID("minecraft:stick"), 4, new CraftingItem[]{new CraftingItem(Tag.PLANKS, new int[]{1, 3})}, false),
    TABLE(InventoryHelper.getID("minecraft:crafting_table"), 1, new CraftingItem[]{new CraftingItem(Tag.PLANKS, new int[]{1, 2, 3, 4})}, false),
    FURNACE(InventoryHelper.getID("minecraft:furnace"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:cobblestone"), new int[]{1, 2, 3, 4, 6, 7, 8, 9})}, true),
    BUCKET(InventoryHelper.getID("minecraft:bucket"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:iron_ingot"), new int[]{4, 6, 8})}, true),
    FAS(InventoryHelper.getID("minecraft:flint_and_steel"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:iron_ingot"), new int[]{1}), new CraftingItem(InventoryHelper.getID("minecraft:flint"), new int[]{3})}, false),
    SHEARS(InventoryHelper.getID("minecraft:shears"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:iron_ingot"), new int[]{1, 4})}, false),
    TORCHES(InventoryHelper.getID("minecraft:torch"), 4, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:stick"), new int[]{3}), new CraftingItem(InventoryHelper.getID("minecraft:coal"), new int[]{1})}, false),
    STONE_HOE(InventoryHelper.getID("minecraft:stone_hoe"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:stick"), new int[]{5, 8}), new CraftingItem(InventoryHelper.getID("minecraft:cobblestone"), new int[]{1, 2})}, true),
    DIAMOND_SWORD(InventoryHelper.getID("minecraft:diamond_sword"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:stick"), new int[]{8}), new CraftingItem(InventoryHelper.getID("diamond"), new int[]{2, 5})}, true),
    DIAMOND_SHOVEL(InventoryHelper.getID("minecraft:diamond_shovel"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:stick"), new int[]{5, 8}), new CraftingItem(InventoryHelper.getID("minecraft:diamond"), new int[]{2})}, true),
    WOODEN_PICKAXE(InventoryHelper.getID("minecraft:wooden_pickaxe"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:stick"), new int[]{5, 8}), new CraftingItem(Tag.PLANKS, new int[]{1, 2, 3})}, true),
    STONE_PICKAXE(InventoryHelper.getID("minecraft:stone_pickaxe"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:stick"), new int[]{5, 8}), new CraftingItem(InventoryHelper.getID("minecraft:cobblestone"), new int[]{1, 2, 3})}, true),
    STONE_AXE(InventoryHelper.getID("minecraft:stone_axe"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:stick"), new int[]{5, 8}), new CraftingItem(InventoryHelper.getID("minecraft:cobblestone"), new int[]{1, 2, 4})}, true),
    IRON_PICKAXE(InventoryHelper.getID("minecraft:iron_pickaxe"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:stick"), new int[]{5, 8}), new CraftingItem(InventoryHelper.getID("minecraft:iron_ingot"), new int[]{1, 2, 3})}, true),
    DIAMOND_PICKAXE(InventoryHelper.getID("minecraft:diamond_pickaxe"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:stick"), new int[]{5, 8}), new CraftingItem(InventoryHelper.getID("minecraft:diamond"), new int[]{1, 2, 3})}, true),
    DIAMOND_BOOTS(InventoryHelper.getID("minecraft:diamond_boots"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:diamond"), new int[]{4, 6, 7, 9})}, true),
    GOLDEN_BOOTS(InventoryHelper.getID("minecraft:golden_boots"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:gold_ingot"), new int[]{1, 3, 4, 6})}, true),
    DIAMOND_LEGGINGS(InventoryHelper.getID("minecraft:diamond_leggings"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:diamond"), new int[]{1, 2, 3, 4, 6, 7, 9})}, true),
    DIAMOND_CHESTPLATE(InventoryHelper.getID("minecraft:diamond_chestplate"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:diamond"), new int[]{1, 3, 4, 5, 6, 7, 8, 9})}, true),
    DIAMOND_HELMET(InventoryHelper.getID("minecraft:diamond_helmet"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:diamond"), new int[]{1, 2, 3, 4, 6})}, true),
    WHEAT(InventoryHelper.getID("minecraft:wheat"), 9, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:hay_block"), new int[]{1})}, false),
    BREAD(InventoryHelper.getID("minecraft:bread"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:wheat"), new int[]{1, 2, 3})}, true),
    GOLD_INGOT(InventoryHelper.getID("minecraft:gold_ingot"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:gold_nugget"), new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9})}, true),
    BLAZE_POWDER(InventoryHelper.getID("minecraft:blaze_powder"), 2, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:blaze_rod"), new int[]{1})}, false),
    ENDER_EYE(InventoryHelper.getID("minecraft:ender_eye"), 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:ender_pearl"), new int[]{1}), new CraftingItem(InventoryHelper.getID("minecraft:blaze_powder"), new int[]{2})}, false),
    WOOL(Tag.WOOL, 1, new CraftingItem[]{new CraftingItem(InventoryHelper.getID("minecraft:string"), new int[]{1, 2, 3, 4})}, false),
    BED(Tag.BEDS, 1, new CraftingItem[]{new CraftingItem(Tag.WOOL, new int[]{1, 2, 3}), new CraftingItem(Tag.PLANKS, new int[]{4, 5, 6})}, true);

    public final int output;
    public final int amount;
    public final CraftingItem[] recipe;
    public final boolean bigCrafting;

    Recipe(int output, int amount, CraftingItem[] recipe, boolean bigCrafting) {
        this.output = output;
        this.amount = amount;
        this.recipe = recipe;
        this.bigCrafting = bigCrafting;
    }

    public static @Nullable Recipe getRecipe(int id) {
        for (Recipe recipe : Recipe.values()) {
            if (recipe.output == id) return recipe;
        }
        return null;
    }
}
