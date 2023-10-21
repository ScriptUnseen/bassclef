package scriptunseen.bassclef.tasks.active.util;

import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.InventoryHelper;

public class SetItemsToKeepTask extends ActiveTask {

    private final int id;
    private final int value;
    private final int max;
    private final int min;

    public SetItemsToKeepTask(String name, int value) {
        this.id = InventoryHelper.getID(name);
        this.value = value;
        this.max = -1;
        this.min = -1;
    }

    public SetItemsToKeepTask(String name, int max, int min) {
        this.id = InventoryHelper.getID(name);
        this.value = -1;
        this.max = max;
        this.min = min;
    }

    @Override
    public boolean activeTaskTick() {
        SortInvTask.ItemToRemove item = SortInvTask.get(id);
        if (item != null) {
            if (value != -1) item.setValue(value);
            if (min != -1) item.setMin(min);
            if (max != -1) item.setMax(max);
        }
        return false;
    }
}
