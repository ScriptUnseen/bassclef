package scriptunseen.bassclef.tasks.active.overworld;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalInverted;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.pathing.goals.GoalYLevel;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMetaLookup;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.getitem.GetItemTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;
import scriptunseen.bassclef.tasks.active.getitem.utils.Tag;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import scriptunseen.bassclef.utils.helper.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GetBedsTask extends ActiveTask {

    private final List<BlockPos> beds;
    private final List<ChunkPos> chunkPos;
    private final int count;
    private BetterBlockPos start;
    private int state;
    private int timer;


    public GetBedsTask(int count) {
        this.beds = new ArrayList<>();
        this.chunkPos = new ArrayList<>();
        this.state = 0;
        this.count = count;
        this.timer = 0;
    }

    @Override
    public boolean activeTaskTick() {
        if (start == null) {
            start = ctx.playerFeet();
            runSubTask(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:shears"), 1), false));
            return true;
        }

        if (ctx.playerFeet().y < 40 && !Main.baritone.getCustomGoalProcess().isActive()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalYLevel(64));
            return true;
        }

        int craftableBeds = craftableBeds();
        if (craftableBeds > 0) {
            runSubTask(new GetItemTask(new ItemTexture(Tag.BEDS, 1), true));
            return true;
        }

        Optional<Entity> entityItem = ctx.entitiesStream().filter(this::goodItem).min(Comparator.comparingDouble(value -> value.distanceTo(ctx.player())));
        if (entityItem.isPresent()) {
            if (state != 1) {
                Main.baritone.getPathingBehavior().forceCancel();
                state = 1;
            }
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(WorldHelper.getActualBlockPosOfEntity(entityItem.get())));
            return true;
        }

        Optional<Entity> entity = ctx.entitiesStream().filter(this::goodSheep).min(Comparator.comparingDouble(value -> value.distanceTo(ctx.player())));
        if (entity.isPresent()) {
            if (state != 2) {
                Main.baritone.getPathingBehavior().forceCancel();
                state = 2;
            }
            if (entity.get().distanceTo(ctx.player()) < 1) {
                if (!InventoryHelper.equip(ctx, InventoryHelper.getID("minecraft:shears"), SortInvTask.TEMPORARY, true)) {
                    runSubTask(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:shears"), 1), true));
                }
                InteractionHelper.lookAtEntity(ctx, entity.get());
                InteractionHelper.interactEntity(ctx, entity.get());
            } else {
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(WorldHelper.getActualBlockPosOfEntity(entity.get())));
            }
            return true;
        }

        scan(new BlockOptionalMetaLookup(Tag.getBlocks(Tag.BEDS)));
        if (beds.size() > 0) {
            if (!Main.baritone.getMineProcess().isActive()) {
                Main.baritone.getMineProcess().mine(new BlockOptionalMetaLookup(Tag.getBlocks(Tag.BEDS)));
            }
            state = 3;
            return true;
        }

        Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalXZ(start)));
        state = 0;
        return true;
    }

    @Override
    protected boolean reachedGoal() {
        return (new Inventory()).contains(new Thing(Tag.BEDS, count));
    }

    private int craftableBeds() {
        int count = 0;
        PlayerInventory inv = ctx.player().inventory;
        for (int i = 0; i < inv.main.size(); ++i) {
            int id = Registry.ITEM.getRawId(inv.main.get(i).getItem());
            if (id >= 95 && id <= 110) { // id range wool
                count += inv.main.get(i).getCount() / 3;
            }
        }
        return count;
    }

    private boolean goodItem(Entity entity) {
        List<Text> items = new ArrayList<>();
        for (int i = 0; i < Tag.TAGS[Tag.BEDS * -1].items.length; i++) {
            items.add(Registry.ITEM.get(Tag.TAGS[Tag.BEDS * -1].items[i]).getName());
        }
        for (int i = 0; i < Tag.TAGS[Tag.WOOL * -1].items.length; i++) {
            items.add(Registry.ITEM.get(Tag.TAGS[Tag.WOOL * -1].items[i]).getName());
        }
        return entity instanceof ItemEntity && items.contains(entity.getName());
    }

    private boolean goodSheep(Entity entity) {
        return entity instanceof SheepEntity && ((SheepEntity) entity).isShearable();
    }

    private void scan(BlockOptionalMetaLookup boml) {
        beds.addAll(WorldScanner.scanAllChunks(ctx, boml, 60, 150, chunkPos));
    }

    @Override
    public boolean cancelSubTask() {
        if (subTask instanceof GetItemTask) {
            if (craftableBeds() > 0) {
                timer = 0;
            } else {
                return ++timer > 20;
            }
        }
        return false;
    }
}
