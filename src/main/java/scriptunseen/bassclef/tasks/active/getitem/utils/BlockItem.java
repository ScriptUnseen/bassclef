package scriptunseen.bassclef.tasks.active.getitem.utils;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockItem {
    public static final BlockItem[] BLOCK_ITEMS = new BlockItem[]{
            new BlockItem(getID("minecraft:cobblestone"), getID("minecraft:stone"), 1),
            new BlockItem(getID("minecraft:coal"), getID("minecraft:coal_ore"), 1),
            new BlockItem(getID("minecraft:flint"), getID("minecraft:gravel"), 0),
            new BlockItem(getID("minecraft:lapis_lazuli"), getID("minecraft:lapis_ore"), 3),
            new BlockItem(getID("minecraft:gold_nugget"), getID("minecraft:nether_gold_ore"), 1),
            new BlockItem(getID("minecraft:diamond"), getID("minecraft:diamond_ore"), 3),
            new BlockItem(getID("minecraft:iron_ore"), getID("minecraft:iron_ore"), 3),
            new BlockItem(getID("minecraft:gold_ore"), getID("minecraft:gold_ore"), 3)
    };

    public int item;
    public int block;
    public int hardness;

    public BlockItem(int item, int block, int hardness) {
        this.item = item;
        this.block = block;
        this.hardness = hardness;
    }

    public static int getID(String id) {
        return Registry.ITEM.getRawId(Registry.ITEM.get(new Identifier(id)));
    }
}
