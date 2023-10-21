package scriptunseen.bassclef.tasks.active.getitem;

import baritone.api.pathing.goals.GoalInverted;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMeta;
import baritone.api.utils.BlockOptionalMetaLookup;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.CollectBlockTask;
import scriptunseen.bassclef.tasks.active.FillBucketTask;
import scriptunseen.bassclef.tasks.active.GetSthTask;
import scriptunseen.bassclef.tasks.active.TaskQueue;
import scriptunseen.bassclef.tasks.active.getitem.utils.*;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.utils.helper.WorldScanner;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import scriptunseen.bassclef.tasks.active.getitem.utils.*;

import java.util.ArrayList;

public class GetItemTask extends GetSthTask {

    private final ItemTexture item;
    private final boolean topItem;
    private final BlockPos startingPos;
    private boolean firstTick;
    private BlockOptionalMetaLookup blocks;
    private int ccount;
    private boolean collectedTable;
    private int timer;

    public GetItemTask(ItemTexture item, boolean topItem) {
        this.item = item;
        this.topItem = topItem;
        this.ccount = -1;
        this.startingPos = ctx.playerFeet();
        this.firstTick = true;
        this.collectedTable = true;
        this.timer = 0;
    }

    @Override
    public boolean activeTaskTick() {
        if (ccount == -1) {
            ccount = topItem ? (new Inventory()).getCount(item.item) : 0;
        }
        if (reachedSubGoal()) {
            if (collectedTable) {
                taskState = TaskState.SUCCESS;
                if (Main.baritone.getMineProcess().isActive()) {
                    Main.baritone.getMineProcess().cancel();
                }
            } else {
                collectedTable = true;
                BlockPos pos = WorldScanner.getNearestBlock(ctx, new BlockOptionalMetaLookup("crafting_table"), 32);
                if (pos != null && ctx.world().getBlockState(pos.offset(Direction.DOWN)).getMaterial().isSolid()) {
                    runSubTask(new CollectBlockTask(InventoryHelper.getID("minecraft:crafting_table")));
                }
            }
            return true;
        }
        if (firstTick) {
            chunkPos.clear();
            System.out.println("Item: " + item.item + " count: " + item.count);
            if (needsCraftingTable()) {
                runSubTask(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:crafting_table"), 1), false));
                return true;
            }
            switch (item.type) {
                case SMELT_ITEM: {
                    int smeltItem = getSmeltItem(item.item);
                    TaskQueue tq = new TaskQueue();
                    if (needsFurnace()) {
                        tq.add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:furnace"), 1), false));
                    }
                    tq.add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:coal"), 1), false));
                    tq.add(new GetItemTask(new ItemTexture(SmeltItem.SMELT_ITEMS[smeltItem].inp, item.count), false));
                    runSubTask(tq);
                    break;
                }
                case CRAFTING: {
                    collectedTable = !topItem;
                    TaskQueue tq = new TaskQueue();
                    for (int i = 0; i < item.size(); i++) {
                        tq.add(new GetItemTask(item.get(i), false));
                    }
                    runSubTask(tq);
                    break;
                }
                case FLUID:
                    runSubTask(new GetItemTask(new ItemTexture(660, item.count), false));
                    break;
                default: {
                    int blockItem = getBlockItem(item.item);
                    int smeltItem = getSmeltItem(item.item);
                    int id;
                    if (blockItem != -1) {
                        id = BlockItem.BLOCK_ITEMS[blockItem].block;
                    } else if (smeltItem != -1) {
                        id = SmeltItem.SMELT_ITEMS[smeltItem].inp;
                    } else {
                        id = item.item;
                    }
                    initBlocks(id);
                    Main.baritone.getMineProcess().mine(blocks);
                    break;
                }
            }
            firstTick = false;
            return true;
        }
        switch (item.type) {
            case CRAFTING:
                if (hasCraftingItems()) {
                    runSubTask(new CraftingTask(new Thing(item.item, item.count)));
                } else {
                    firstTick = true;
                }
                break;
            case SMELT_ITEM:
                if (hasSmeltItem()) {
                    ArrayList<Thing> list = new ArrayList<>();
                    list.add((new Thing(item.item, item.count)));
                    runSubTask(new SmeltItemTask(list));
                } else {
                    firstTick = true;
                }
                break;
            case FLUID:
                runSubTask(new FillBucketTask(item.item));
                break;
            default:
                if (timer++ > 20) {
                    timer = 0;
                    checkPick();
                }
                if (!Main.baritone.getMineProcess().isActive()) {
                    scanChunks();
                }
                break;
        }
        return true;
    }

    private boolean reachedSubGoal() {
        return new Inventory().contains(new Thing(item.item, item.count + ccount));
    }

    private boolean needsCraftingTable() {
        return this.item.hasBigCrafting() && hasNoThing("minecraft:crafting_table") && WorldScanner.getNearestBlock(ctx, new BlockOptionalMetaLookup("crafting_table"), 30, Math.max(ctx.playerFeet().getY() - 20, 0), Math.min(Main.baritone.getPlayerContext().playerFeet().getY() + 10, 255)) == null;
    }

    private int getSmeltItem(int smeltItem) {
        for (int i = 0; i < SmeltItem.SMELT_ITEMS.length; i++) {
            if (smeltItem == SmeltItem.SMELT_ITEMS[i].out) return i;
        }
        return -1;
    }

    private boolean needsFurnace() {
        return this.item.needsFurnace() && hasNoThing("minecraft:furnace") && WorldScanner.getNearestBlock(ctx, new BlockOptionalMetaLookup("furnace"), 30, Math.max(ctx.playerFeet().getY() - 20, 0), Math.min(ctx.playerFeet().getY() + 10, 255)) == null;
    }

    private int getBlockItem(int blockItem) {
        for (int i = 0; i < BlockItem.BLOCK_ITEMS.length; i++) {
            if (blockItem == BlockItem.BLOCK_ITEMS[i].item) return i;
        }
        return -1;
    }

    private void initBlocks(int id) {
        if (id > 0) {
            Block block = Registry.BLOCK.get(Registry.ITEM.getId(Registry.ITEM.get(id)));
            if (!block.equals(Blocks.AIR)) {
                blocks = new BlockOptionalMetaLookup(block);
            } else {
                fail("This Thing is not mineable!");
            }
        } else {
            Tag tag = Tag.TAGS[id * -1];
            BlockOptionalMeta[] boms = new BlockOptionalMeta[tag.items.length];
            for (int i = 0; i < boms.length; i++) {
                boms[i] = new BlockOptionalMeta(tag.items[i].toString());
            }
            blocks = new BlockOptionalMetaLookup(boms);
        }
    }

    private boolean hasCraftingItems() {
        Inventory inv = new Inventory();
        for (int i = 0; i < item.size(); i++) {
            if (!inv.contains(new Thing(item.get(i).item, item.get(i).count))) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean hasSmeltItem() {
        Inventory inv = new Inventory();
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getThing(i) != null) {
                if (inv.getThing(i).getId() == SmeltItem.getSmeltItemFromItem(item.item).inp && inv.getThing(i).getCount() >= item.count) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkPick() {
        // check if we need a pickaxe
        boolean b1 = false;
        for (BlockOptionalMeta bom : blocks.blocks()) {
            if (bom.getAnyBlockState().isToolRequired()) b1 = true;
        }
        if (b1) {
            int pick = SortInvTask.getHighest(SortInvTask.pickaxes);
            PickaxeItem item = null;
            // Do we have the pickaxe already?
            boolean b2 = false;
            if (pick != -1) {
                item = (PickaxeItem) Registry.ITEM.get(pick);
                for (BlockOptionalMeta bom : blocks.blocks()) {
                    if (!item.isEffectiveOn(bom.getAnyBlockState())) {
                        b2 = true;
                        break;
                    }
                }
            }
            if (item == null || b2) {
                runSubTask(new GetItemTask(new ItemTexture(bestPick(), 1), true));
            }
        }
    }

    private void scanChunks() {
        if (scan(blocks).size() > 0) {
            Main.baritone.getPathingBehavior().forceCancel();
            firstTick = true;
        } else {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalXZ(new BetterBlockPos(startingPos))));
        }
    }

    private boolean hasNoThing(String name) {
        Inventory inv = new Inventory();
        int crTable = Registry.ITEM.getRawId(Registry.ITEM.get(new Identifier(name)));
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getThing(i) != null) {
                if (inv.getThing(i).getId() == crTable) {
                    return false;
                }
            }
        }
        return true;
    }

    private int bestPick() {
        for (int id : SortInvTask.pickaxes) {
            boolean b = true;
            PickaxeItem pick = (PickaxeItem) Registry.ITEM.get(id);
            for (BlockOptionalMeta bom : blocks.blocks()) {
                if (!pick.isEffectiveOn(bom.getAnyBlockState()) && bom.getAnyBlockState().isToolRequired()) {
                    b = false;
                    break;
                }
            }
            if (b) {
                return id;
            }
        }
        fail("No pickaxe capable of destroying this Block!");
        return -1;
    }

    @Override
    public boolean cancelSubTask() {
        if (getRunTime() % 20 == 0) {
            return collectedTable && reachedSubGoal() && subTask.allowedToCancel();
        }
        return false;
    }

    public ItemTexture getItem() {
        return item;
    }
}