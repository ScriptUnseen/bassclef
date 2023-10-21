package scriptunseen.bassclef.utils.helper;

import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.getitem.utils.Tag;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Inventory {

    public ClientPlayerEntity player;
    private Thing[] inventory;

    public Inventory() {
        player = Main.client.player;
        update();
    }

    public void update() {
        player.inventory.updateItems();
        inventory = new Thing[player.inventory.size()];
        for (int i = 0; i < player.inventory.size(); i++) {
            ItemStack item = player.inventory.getStack(i);
            int tag = getTag(Registry.ITEM.getId(item.getItem()));
            if (tag == -1) {
                int id = Registry.ITEM.getRawId(item.getItem());
                if (id != 0) inventory[i] = new Thing(id, item.getCount());
            } else {
                inventory[i] = new Thing(tag * -1, item.getCount());
            }
        }
    }

    private int getTag(Identifier id) {
        for (int i = 0; i < Tag.TAGS.length; i++) {
            for (Identifier item : Tag.TAGS[i].items) {
                if (item.equals(id)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void add(Thing thing) {
        for (Thing value : inventory) {
            if (value != null) {
                if (value.getId() == thing.getId()) {
                    value.setCount(value.getCount() + thing.getCount());
                    return;
                }
            }
        }
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                inventory[i] = new Thing(thing.getId(), thing.getCount());
                return;
            }
        }
    }

    public int getSlot(Thing thing) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && thing.getId() == inventory[i].getId()) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(Thing item) {
        int count = 0;
        for (Thing thing : inventory) {
            if (thing != null && item.getId() == thing.getId()) {
                count += thing.getCount();
            }
        }
        return count >= item.getCount();
    }

    public int getCount(int id) {
        int count = 0;
        for (Thing thing : inventory) {
            if (thing != null && id == thing.getId()) {
                count += thing.getCount();
            }
        }
        return count;
    }

    public int countStacks(int id) {
        int count = 0;
        for (Thing thing : inventory) {
            if (thing != null && id == thing.getId()) {
                count++;
            }
        }
        return count;
    }

    public Thing getThing(int index) {
        return inventory[index];
    }

    public int size() {
        return inventory.length;
    }
}
