package scriptunseen.bassclef.tasks.active.getitem.utils;

import scriptunseen.bassclef.utils.helper.InventoryHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemTexture {
    private final List<ItemTexture> textures;
    public int item;
    public int count;
    public ItemType type;

    public ItemTexture(int item, int count) {
        this.item = item;
        this.count = count;
        this.textures = new ArrayList<>();
        initTexture();
    }

    private void initTexture() {
        if (count == 0) {
            return;
        }
        Recipe recipe = Recipe.getRecipe(item);
        if (recipe != null) {
            for (CraftingItem craftingItem : recipe.recipe) {
                int c = (int) Math.ceil((count * 1.0 / recipe.amount)) * craftingItem.slots.length;
                textures.add(new ItemTexture(craftingItem.id, c));
            }
            type = ItemType.CRAFTING;
        } else if (isSmeltItem()) {
            type = ItemType.SMELT_ITEM;
        } else if (item == InventoryHelper.getID("water_bucket") || item == InventoryHelper.getID("lava_bucket")) {
            type = ItemType.FLUID;
        } else {
            type = ItemType.BLOCK;
        }
    }

    private boolean isSmeltItem() {
        for (SmeltItem si : SmeltItem.SMELT_ITEMS) {
            if (si.out == item) return true;
        }
        return false;
    }

    public boolean hasBigCrafting() {
        Recipe recipe = Recipe.getRecipe(item);
        if (recipe == null) {
            for (ItemTexture it : textures) {
                return it.hasBigCrafting();
            }
            return false;
        } else {
            return recipe.bigCrafting;
        }
    }

    public boolean needsFurnace() {
        boolean smeltItem = isSmeltItem();
        if (smeltItem) {
            return true;
        } else {
            for (ItemTexture it : textures) {
                return it.needsFurnace();
            }
            return false;
        }
    }

    public int size() {
        return textures.size();
    }

    public ItemTexture get(int index) {
        return textures.get(index);
    }
}
