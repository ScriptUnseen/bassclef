package scriptunseen.bassclef.tasks.active.nether;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalInverted;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BetterBlockPos;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.PutArmorPieceOnTask;
import scriptunseen.bassclef.tasks.active.getitem.GetItemTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.registry.Registry;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PiglinTradeTask extends ActiveTask {

    private final Thing trade;
    private final BetterBlockPos origin;

    public PiglinTradeTask(Thing trade) {
        this.trade = trade;
        this.origin = ctx.playerFeet();
    }

    @Override
    public boolean activeTaskTick() {
        if (!InventoryHelper.hasGoldArmor(ctx)) {
            runSubTask(new PutArmorPieceOnTask(InventoryHelper.getID("minecraft:golden_boots")));
            return true;
        }

        Entity item = getNearest(ctx.entitiesStream().filter(this::isThing).collect(Collectors.toList()));
        if (item instanceof ItemEntity) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(item.getBlockPos()));
            return true;
        }

        PiglinEntity follow = (PiglinEntity) getNearest(ctx.entitiesStream().filter(this::isPiglin).sorted(Comparator.comparingDouble(value -> value.distanceTo(ctx.player()))).collect(Collectors.toList()));
        if (!hasGold()) {
            if (follow == null || follow.getActivity() != PiglinEntity.Activity.ADMIRING_ITEM){
                runSubTask(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:gold_ingot"), 64), true));
            }
            return true;
        }

        if (follow != null) {
            if (follow.distanceTo(ctx.player()) < ctx.playerController().getBlockReachDistance()) {
                if (Main.baritone.getCustomGoalProcess().isActive() && ctx.player().isOnGround()) {
                    Main.baritone.getPathingBehavior().forceCancel();
                }
                if (follow.getActivity() == PiglinEntity.Activity.ADMIRING_ITEM) {
                    return true;
                }
                if (InventoryHelper.equip(ctx, InventoryHelper.getID("gold_ingot"), SortInvTask.TEMPORARY, true)) {
                    InteractionHelper.lookAtEntity(ctx, follow);
                    InteractionHelper.interactEntity(ctx, follow);
                } else {
                    runSubTask(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:gold_ingot"), 64), true));
                }
            } else {
                Goal goal = Main.baritone.getCustomGoalProcess().getGoal();
                // if the piglinRun is too far away it sometimes changes the position before the path is calculated,
                // so the bot should only change the goal if the piglin is too far away from the previous pos
                if (!(goal instanceof GoalBlock) || !((GoalBlock) goal).getGoalPos().isWithinDistance(follow.getBlockPos(), 2)) {
                    Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(follow.getBlockPos()));
                }
            }
            return true;
        }

        if (!Main.baritone.getCustomGoalProcess().isActive()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalXZ(origin)));
            return true;
        }
        return true;
    }

    @Override
    protected boolean reachedGoal() {
        return (new Inventory()).contains(new Thing(trade.getId(), trade.getCount()));
    }

    @Override
    public void finish() {
        Main.baritone.getPathingBehavior().forceCancel();
    }

    private boolean hasGold() {
        return (new Inventory()).contains(new Thing(InventoryHelper.getID("minecraft:gold_ingot"), 1));
    }

    private Entity getNearest(List<Entity> entities) {
        return entities.size() > 0 ? entities.get(0) : null;
    }

    private boolean isThing(Entity entity) {
        if (entity instanceof ItemEntity) {
            return Registry.ITEM.getRawId(((ItemEntity) entity).getStack().getItem()) == trade.getId();
        }
        return false;
    }

    private boolean isPiglin(Entity entity) {
        if (entity instanceof PiglinEntity) {
            return ((PiglinEntity) entity).isAdult();
        }
        return false;
    }
}
