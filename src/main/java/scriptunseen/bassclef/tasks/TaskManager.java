package scriptunseen.bassclef.tasks;

import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.passive.*;
import org.jetbrains.annotations.Nullable;
import scriptunseen.bassclef.tasks.passive.*;

import java.util.ArrayList;
import java.util.List;


public class TaskManager {
    private static final List<ActiveTask> primaryTasks = new ArrayList<>();
    private static int runningTask = -1;
    private static PassiveTask[] tasks = new PassiveTask[0];
    private static boolean pActive = false;

    public static void runTask(int i) {
        runningTask = i;
    }

    public static void addPrimaryTaskAndRun(ActiveTask task) {
        addPrimaryTask(task);
        runningTask = primaryTasks.size() - 1;
    }

    public static void addPrimaryTask(ActiveTask task) {
        primaryTasks.add(task);
    }

    public static void removeTask(int i) {
        primaryTasks.remove(i);
    }

    public static void tick() {
        if (Main.client.world != null) {
            //run primary task
            boolean allowPassive = true;
            ActiveTask primaryTask = getRunningTask();
            if (primaryTask != null) {
                if (primaryTask.taskState == TaskState.RUNNING) {
                    allowPassive = primaryTask.tick();
                } else if (primaryTask.taskState == TaskState.FAIL || primaryTask.taskState == TaskState.SUCCESS) {
                    primaryTasks.remove(runningTask);
                }
            }

            //run passive tasks
            for (PassiveTask task : tasks) {
                if (task.taskState == TaskState.RUNNING) {
                    task.tick();
                }
            }
        } else {
            for (PassiveTask task : tasks) {
                if (task.taskState == TaskState.RUNNING && task.isRunWithNullWorld()) {
                    task.tick();
                }
            }
        }
    }

    public static @Nullable ActiveTask getRunningTask() {
        if (runningTask >= 0 && primaryTasks.size() > runningTask) {
            return primaryTasks.get(runningTask);
        }
        return null;
    }

    public static void disablePassiveTasks() {
        for (PassiveTask pt : tasks) {
            pt.taskState = TaskState.PAUSE;
        }
        pActive = false;
    }

    public static void disableTask(Class<? extends PassiveTask> task) {
        for (PassiveTask passiveTask : tasks) {
            if (passiveTask.getClass() == task) {
                passiveTask.taskState = TaskState.PAUSE;
                return;
            }
        }
    }

    public static void enableTask(Class<? extends PassiveTask> task) {
        for (PassiveTask passiveTask : tasks) {
            if (passiveTask.getClass() == task) {
                passiveTask.taskState = TaskState.RUNNING;
                return;
            }
        }
    }

    public static void cancelPrimaryTask() {
        runningTask = -1;
        primaryTasks.remove(getRunningTask());
        Main.baritone.getMineProcess().cancel();
        Main.baritone.getPathingBehavior().forceCancel();
    }

    public static void cancelEverything() {
        runningTask = -1;
        primaryTasks.clear();
        Main.baritone.getMineProcess().cancel();
        Main.baritone.getPathingBehavior().forceCancel();
    }

    public static boolean isRunning() {
        return runningTask != -1;
    }

    public static void pause() {
        runningTask = -1;
        if (Main.baritone.getBuilderProcess().isActive()) {
            Main.baritone.getBuilderProcess().pause();
        } else {
            Main.baritone.getPathingBehavior().forceCancel();
        }
    }

    public static void resume() {
        if (primaryTasks.size() > 0) resume(0);
    }

    public static void resume(int task) {
        runningTask = task;
        if (Main.baritone.getBuilderProcess().isPaused()) {
            Main.baritone.getBuilderProcess().resume();
        }
    }

    public static PassiveTask getPassiveTask(Class<? extends PassiveTask> task) {
        for (PassiveTask passiveTask : tasks) {
            if (passiveTask.getClass() == task) {
                return passiveTask;
            }
        }
        return null;
    }

    public static void initPassives() {
        tasks = new PassiveTask[]{new AntiStuckTask(), new AvoidMonsterTask(), new EatingTask(), new FightMobTask(), new RunAwayTask(), new SortInvTask(), new DontLookAtEnderman(), new WaterMLGTask(), new RestoreFromDeathTask(), new SaveFromLavaTask()};
        if (pActive) {
            enablePassiveTasks();
        }
    }

    public static void enablePassiveTasks() {
        for (PassiveTask pt : tasks) {
            pt.taskState = TaskState.RUNNING;
        }
        pActive = true;
    }

    public static int getRunning() {
        return runningTask;
    }

    public static int taskLength() {
        return primaryTasks.size();
    }
}
