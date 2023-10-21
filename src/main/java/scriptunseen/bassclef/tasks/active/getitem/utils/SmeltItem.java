package scriptunseen.bassclef.tasks.active.getitem.utils;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SmeltItem {
    public static final SmeltItem[] SMELT_ITEMS = new SmeltItem[]{
            new SmeltItem(getID("minecraft:iron_ore"), getID("minecraft:iron_ingot")),
            new SmeltItem(getID("minecraft:gold_ore"), getID("minecraft:gold_ingot")),
            new SmeltItem(getID("minecraft:porkchop"), getID("minecraft:cooked_porkchop")),
            new SmeltItem(getID("minecraft:beef"), getID("minecraft:cooked_beef")),
            new SmeltItem(getID("minecraft:chicken"), getID("minecraft:cooked_chicken")),
            new SmeltItem(getID("minecraft:mutton"), getID("minecraft:cooked_mutton")),
            new SmeltItem(getID("minecraft:rabbit"), getID("minecraft:cooked_rabbit"))
    };

    public int inp;
    public int out;

    public SmeltItem(int inp, int out) {
        this.inp = inp;
        this.out = out;
    }

    public static SmeltItem getSmeltItemFromItem(int item) {
        for (SmeltItem si : SMELT_ITEMS) {
            if (si.inp == item || si.out == item) return si;
        }
        return null;
    }

    public static SmeltItem getSmeltItemFromInp(int item) {
        for (SmeltItem si : SMELT_ITEMS) {
            if (si.inp == item) return si;
        }
        return null;
    }

    public static int getID(String id) {
        return Registry.ITEM.getRawId(Registry.ITEM.get(new Identifier(id)));
    }
}