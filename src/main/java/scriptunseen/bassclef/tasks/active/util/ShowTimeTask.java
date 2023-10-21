package scriptunseen.bassclef.tasks.active.util;

import scriptunseen.bassclef.tasks.Task;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.utils.helper.ChatHelper;

public class ShowTimeTask extends ActiveTask {

    private final Task task;

    public ShowTimeTask(Task task) {
        this.task = task;
    }

    @Override
    public boolean activeTaskTick() {
        int ticks = task.getRunTime();
        int t = ticks % 20;
        int s = (ticks = ticks/20) % (60);
        int m = (ticks = ticks/60) % (60);
        int h = (ticks/60) % 60;
        ChatHelper.displayChatMessage("Time: " + h + "h " + m + "m " + s + "s " + t + "t");
        taskState = TaskState.SUCCESS;
        return true;
    }
}
