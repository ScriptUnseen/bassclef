package scriptunseen.bassclef.tasks.active;

import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.utils.helper.InventoryHelper;

public class GetArmorTask extends ActiveTask {
    private boolean firstTick;

    public GetArmorTask() {
        this.firstTick = true;
    }

    @Override
    public boolean activeTaskTick() {
        if (firstTick) {
            firstTick = false;
            TaskQueue tq = new TaskQueue();
            tq.add(new PutArmorPieceOnTask(InventoryHelper.getID("minecraft:diamond_chestplate")));
            tq.add(new PutArmorPieceOnTask(InventoryHelper.getID("minecraft:diamond_leggings")));
            tq.add(new PutArmorPieceOnTask(InventoryHelper.getID("minecraft:diamond_boots")));
            tq.add(new PutArmorPieceOnTask(InventoryHelper.getID("minecraft:diamond_helmet")));
            runSubTask(tq);
        } else {
            taskState = TaskState.SUCCESS;
        }
        return true;
    }
}
