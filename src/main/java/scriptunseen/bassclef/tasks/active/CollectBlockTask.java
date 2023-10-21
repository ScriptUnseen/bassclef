package scriptunseen.bassclef.tasks.active;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.IPlayerContext;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.utils.helper.WorldHelper;
import scriptunseen.bassclef.utils.helper.WorldScanner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class CollectBlockTask extends ActiveTask {

    private final int block;
    private final BlockOptionalMetaLookup boml;
    private final IPlayerContext ctx;
    private BlockPos pos;
    private boolean b;

    public CollectBlockTask(int block) {
        this.block = block;
        this.ctx = Main.baritone.getPlayerContext();
        this.boml = new BlockOptionalMetaLookup(Registry.BLOCK.get(Registry.ITEM.getId(Registry.ITEM.get(block))));
        this.b = true;
    }

    @Override
    public boolean activeTaskTick() {
        Optional<Entity> item = ctx.entitiesStream().filter(this::itemToCollect).findFirst();
        if (item.isPresent()) {
            b = false;
            if (Main.baritone.getMineProcess().isActive()) {
                Main.baritone.getPathingBehavior().forceCancel();
            }
            if (!Main.baritone.getCustomGoalProcess().isActive()) {
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(WorldHelper.getActualBlockPosOfEntity(item.get())));
            }
            return true;
        }

        if (b) {
            if (pos == null || getRunTime() % 20 == 0) {
                pos = WorldScanner.getNearestBlock(ctx, boml, 32, 0, 256);
                if (pos == null || !WorldScanner.isValidBlock(ctx, pos)) {
                    fail();
                    return true;
                }
            }
            if (!Main.baritone.getMineProcess().isActive()) {
                Main.baritone.getMineProcess().mine(boml);
            }
            return true;
        }

        taskState = TaskState.SUCCESS;
        return true;
    }

    private boolean itemToCollect(Entity entity) {
        if (entity instanceof ItemEntity) {
            Item item = ((ItemEntity) entity).getStack().getItem();
            return Registry.ITEM.get(block).equals(item);
        }
        return false;
    }

    @Override
    public void finish() {
        if (Main.baritone.getPathingBehavior().isPathing()) {
            Main.baritone.getPathingBehavior().forceCancel();
        }
    }
}
