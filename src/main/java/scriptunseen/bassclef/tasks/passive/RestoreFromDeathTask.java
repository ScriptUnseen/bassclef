package scriptunseen.bassclef.tasks.passive;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BetterBlockPos;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.getitem.GetItemTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.utils.helper.*;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.registry.Registry;
import scriptunseen.bassclef.utils.helper.*;

import java.util.Comparator;
import java.util.Optional;


public class RestoreFromDeathTask extends PassiveTask {

    private static BetterBlockPos death = null;
    private static Dimension dimension = null;

    public RestoreFromDeathTask() {
        runWithNullWorld = true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean taskTick() {
        if (Main.client.currentScreen instanceof DeathScreen) {
            if (death == null) {
                death = ctx.playerFeet();
                RunAwayTask.resetRunning();
                ChatHelper.displayChatMessage("Death at " + death.toShortString());
                dimension = WorldScanner.getDimension(ctx.world());
                ChatHelper.displayChatMessage(dimension + "");
                ChatHelper.displayChatMessage("Searching for stuff!");
                TaskManager.pause();
            }
            System.out.println("trying to respawn");
            Main.client.player.requestRespawn();
            return true;
        }
        // wait 2 sec for everything to load
        if (death == null || ctx.world() == null || getRunTime() < 20 * 2) {
            return true;
        }

        switch (dimension) {
            case OVERWORLD:
                if (SortInvTask.getHighest(SortInvTask.pickaxes) == -1) {
                    ActiveTask task = TaskManager.getRunningTask();
                    if (!(task instanceof GetItemTask && ((GetItemTask) task).getItem().item == InventoryHelper.getID("minecraft:wooden_pickaxe"))) {
                        TaskManager.addPrimaryTaskAndRun(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:wooden_pickaxe"), 1), false));
                    }
                } else {
                    searchStuff();
                }
                break;
            case NETHER:
                ChatHelper.displayChatMessage("Walking in the Nether is too dangerous! You should head to the next world!", true);
                reset();
                TaskManager.cancelPrimaryTask();
                break;
            case END:
                if (dimension == WorldScanner.getDimension(ctx.world())) {
                    searchStuff();
                } else if (Main.positions.endPortal != null) {
                    Goal goal = Main.baritone.getCustomGoalProcess().getGoal();
                    if (!(goal instanceof GoalBlock) || !((GoalBlock) goal).getGoalPos().equals(Main.positions.endPortal)) {
                        Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(Main.positions.endPortal));
                    }
                } else {
                    reset();
                    ChatHelper.displayChatMessage("I don't know where the portal is!", true);
                }
                break;
        }
        return false;
    }

    private void searchStuff() {
        if (ctx.playerFeet().isWithinDistance(death, 20)) {
            Optional<Entity> entity = ctx.entitiesStream().filter(this::pickUpItem).min(Comparator.comparingDouble(value -> value.distanceTo(ctx.player())));
            if (entity.isPresent()) {
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(WorldHelper.getActualBlockPosOfEntity(entity.get())));
            } else {
                restored();
            }
        } else if (!Main.baritone.getCustomGoalProcess().isActive()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(death));
        }
    }

    public static void reset() {
        death = null;
        dimension = null;
        if (TaskManager.taskLength() > 1) {
            TaskManager.removeTask(TaskManager.taskLength() - 1);
            TaskManager.runTask(0);
        }
    }

    private boolean pickUpItem(Entity entity) {
        if (entity instanceof ItemEntity && death.isWithinDistance(entity.getPos(), 10)) {
            int id = Registry.ITEM.getRawId(((ItemEntity) entity).getStack().getItem());
            return SortInvTask.contains(id);
        }
        return false;
    }

    private void restored() {
        ChatHelper.displayChatMessage("Collected all items!");
        putArmorOn();
        reset();
    }

    private void putArmorOn() {
        Inventory inv = new Inventory();
        for (int i = 0; i < inv.size(); i++) {
            Thing thing = inv.getThing(i);
            if (thing != null) {
                if (Registry.ITEM.get(thing.getId()) instanceof ArmorItem) {
                    InventoryHelper.clickSlot(ctx, i < 9 ? i + 36 : i, 0, SlotActionType.QUICK_MOVE);
                }
            }
        }
    }

    public static boolean isRestoring() {
        return death != null;
    }
}
