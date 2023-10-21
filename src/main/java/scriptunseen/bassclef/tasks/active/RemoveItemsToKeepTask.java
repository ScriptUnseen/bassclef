package scriptunseen.bassclef.tasks.active;

import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.passive.SortInvTask;

public class RemoveItemsToKeepTask extends ActiveTask {

    private final String[] toRemove;

    public RemoveItemsToKeepTask(String... toRemove) {
        this.toRemove = toRemove;
    }

    @Override
    public boolean activeTaskTick() {
        for (String string : toRemove) {
            SortInvTask.remove(string);
        }
        taskState = TaskState.SUCCESS;
        return true;
    }
}
