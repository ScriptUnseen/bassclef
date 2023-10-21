package scriptunseen.bassclef.tasks.active.util;

import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.passive.SortInvTask;

public class AddItemsToKeepTask extends ActiveTask {

    private final SortInvTask.ItemToRemove[] toAdd;

    public AddItemsToKeepTask(SortInvTask.ItemToRemove... toRemove) {
        this.toAdd = toRemove;
    }

    @Override
    public boolean activeTaskTick() {
        for (SortInvTask.ItemToRemove item : toAdd) {
            SortInvTask.add(item.getId(), item.getValue(), item.getMax(), item.getMin());
        }
        taskState = TaskState.SUCCESS;
        return true;
    }
}
