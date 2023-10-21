package scriptunseen.bassclef.tasks.active.getitem.utils;

import scriptunseen.bassclef.utils.helper.InventoryHelper;

public class Thing {
    private final int id;
    private int count;

    public Thing(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public Thing(String name, int count) {
        this.id = InventoryHelper.getID(name);
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
