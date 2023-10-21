package scriptunseen.bassclef.tasks.active;

import scriptunseen.bassclef.tasks.Task;
import scriptunseen.bassclef.tasks.TaskState;

import java.util.ArrayList;
import java.util.List;

public class TaskQueue extends ActiveTask {

    private final List<ActiveTask> tasks;

    public TaskQueue() {
        tasks = new ArrayList<>();
    }

    @Override
    public boolean activeTaskTick() {
        if (tasks.size() > 0) {
            Task task = tasks.get(0);
            if (task.taskState == TaskState.RUNNING) {
                return task.tick();
            } else {
                tasks.remove(0);
                if (tasks.size() > 0) {
                    System.out.println(tasks.get(0).getClass().getSimpleName());
                }
            }
        }
        if (tasks.size() == 0) {
            taskState = TaskState.SUCCESS;
        }
        return true;
    }

    @Override
    public boolean hasTask(Class<? extends ActiveTask> task) {
        return (tasks.size() > 0 && tasks.get(0).hasTask(task));
    }

    @Override
    public String getSubtaskName() {
        if (tasks.size() > 0) {
            return tasks.get(0).getSubtaskName();
        } else {
            return getClass().getSimpleName();
        }
    }

    public List<ActiveTask> getTasks() {
        return tasks;
    }

    public void add(ActiveTask task) {
        task.run();
        tasks.add(task);
    }

    @Override
    public StringBuilder getTaskTree(int depth) {
        StringBuilder tree = new StringBuilder();
        tree.append(getClass().getSimpleName());
        if (tasks.size() > 0) {
            tree.append("\n");
            for (int i = 1; i <= depth; i++) {
                tree.append("    ");
            }
            tree.append("-> ").append(tasks.get(0).getTaskTree(depth + 1).toString());
        }
        return tree;
    }
}
