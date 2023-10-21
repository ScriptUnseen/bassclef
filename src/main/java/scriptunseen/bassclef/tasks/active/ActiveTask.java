package scriptunseen.bassclef.tasks.active;

import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.Task;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.utils.helper.ChatHelper;

public abstract class ActiveTask extends Task {

    protected ActiveTask subTask;

    public ActiveTask() {
        this.subTask = null;
    }

    @Override
    public final boolean taskTick() {
        if (subTask == null) {
            if (reachedGoal()) {
                finish();
                taskState = TaskState.SUCCESS;
            } else {
                return activeTaskTick();
            }
        } else {
            if (cancelSubTask()) {
                subTask = null;
                Main.baritone.getPathingBehavior().forceCancel();
                return true;
            }

            if (subTask.taskState == TaskState.RUNNING) {
                return subTask.tick();
            }

            subTask = null;
        }
        return true;
    }

    protected boolean reachedGoal() {
        return false;
    }

    public void finish() {
    }

    public abstract boolean activeTaskTick();

    public boolean cancelSubTask() {
        return false;
    }

    public void runSubTask(ActiveTask task) {
        System.out.println(task.getClass().getSimpleName());
        subTask = task;
        subTask.run();
    }

    public void run() {
        if ((taskState == TaskState.OFF || taskState == TaskState.PAUSE)) {
            taskState = TaskState.RUNNING;
        }
    }

    public void fail() {
        ChatHelper.displayChatMessage("Task " + this.getClass().getSimpleName() + " failed!", true);
        taskState = TaskState.FAIL;
    }

    public void fail(String message) {
        ChatHelper.displayChatMessage(message, true);
        taskState = TaskState.FAIL;
    }

    public void fail(int code) {
        ChatHelper.displayChatMessage("Task " + this.getClass().getSimpleName() + " failed! error: " + code, true);
        taskState = TaskState.FAIL;
    }

    public boolean hasTask(Class<? extends ActiveTask> task) {
        return this.getClass() == task || (subTask != null && subTask.hasTask(task));
    }

    public boolean allowedToCancel() {
        if (subTask == null) {
            return true;
        } else {
            return subTask.allowedToCancel();
        }
    }

    public String getSubtaskName() {
        if (subTask == null) {
            return getClass().getSimpleName();
        } else {
            return subTask.getSubtaskName();
        }
    }

    public String getTaskTree() {
        return getTaskTree(1).toString();
    }

    public StringBuilder getTaskTree(int depth) {
        StringBuilder tree = new StringBuilder();
        String name = getClass().getSimpleName();
        tree.append(name.equals("") ? "Anonymous" : name);
        if (subTask != null) {
            tree.append("\n");
            for (int i = 1; i <= depth; i++) {
                tree.append("   ");
            }
            tree.append("-> ").append(subTask.getTaskTree(depth + 1).toString());
        }
        return tree;
    }
}
