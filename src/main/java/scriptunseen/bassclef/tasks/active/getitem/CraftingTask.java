package scriptunseen.bassclef.tasks.active.getitem;

import baritone.api.utils.BlockOptionalMetaLookup;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.GotoBlockTask;
import scriptunseen.bassclef.tasks.active.PlaceBlockTask;
import scriptunseen.bassclef.tasks.active.TaskQueue;
import scriptunseen.bassclef.tasks.active.getitem.utils.*;
import scriptunseen.bassclef.tasks.active.getitem.utils.*;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import scriptunseen.bassclef.utils.helper.WorldScanner;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

public class CraftingTask extends ActiveTask {

    private final Recipe recipe;
    private int state;
    private int count;

    public CraftingTask(Thing item) {
        this.state = 0;
        this.recipe = Recipe.getRecipe(item.getId());
        if (recipe != null) {
            this.count = (int) Math.ceil(item.getCount() * 1.0D / recipe.amount);
        } else {
            fail("No recipe found!");
        }
    }

    @Override
    public boolean activeTaskTick() {
        switch (state) {
            case 0:
                if (InventoryHelper.freeSlots(ctx) > 0) {
                    tryCraft();
                } else {
                    int id = SortInvTask.firstThingToRemove();
                    if (id == -1) {
                        tryCraft();
                    } else {
                        SortInvTask.removeItem(ctx, id);
                    }
                }
                break;
            case 1:
                InventoryHelper.clickSlot(ctx, 0, 0, SlotActionType.QUICK_MOVE);
                if (count > 64) {
                    count -= 64;
                    state = 0;
                } else {
                    state++;
                }
                break;
            case 2:
                if (ctx.player().currentScreenHandler.syncId == 0 && ctx.player().currentScreenHandler instanceof PlayerScreenHandler) {
                    System.out.println("screen is closed!");
                    taskState = TaskState.SUCCESS;
                }
                ctx.player().closeHandledScreen();
                break;
        }
        return false;
    }

    private void tryCraft() {
        if (readyToCraft()) {
            if (craft()) {
                state++;
            } else {
                ctx.player().closeHandledScreen();
                fail(1);
            }
        }
    }

    private boolean readyToCraft() {
        if (recipe.bigCrafting) {
            if (ctx.player().currentScreenHandler instanceof CraftingScreenHandler) {
                return true;
            } else {
                BlockPos ct = WorldScanner.getNearestBlock(ctx, new BlockOptionalMetaLookup("crafting_table"), 30, Math.max(ctx.playerFeet().getY() - 20, 0), Math.min(ctx.playerFeet().getY() + 10, 255));
                if (ct != null) {
                    runSubTask(new GotoBlockTask(ct));
                } else {
                    Inventory inv = new Inventory();
                    if (inv.contains(new Thing(InventoryHelper.getID("minecraft:crafting_table"), 1)) || inv.contains(new Thing(Tag.LOGS, 1)) || inv.contains(new Thing(Tag.PLANKS, 4))) {
                        placeTable();
                    } else {
                        ct = WorldScanner.getNearestBlock(ctx, new BlockOptionalMetaLookup("crafting_table"), 40, Math.max(ctx.playerFeet().getY() - 30, 0), Math.min(ctx.playerFeet().getY() + 20, 255));
                        if (ct != null) {
                            runSubTask(new GotoBlockTask(ct));
                        } else {
                            placeTable();
                        }
                    }
                }
            }
        } else if (ctx.player().currentScreenHandler instanceof PlayerScreenHandler) {
            return true;
        } else {
            ctx.player().closeHandledScreen();
        }
        return false;
    }

    private void placeTable() {
        TaskQueue tq = new TaskQueue();
        tq.add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:crafting_table"), 1), false));
        tq.add(new PlaceBlockTask(InventoryHelper.getID("minecraft:crafting_table")));
        runSubTask(tq);
    }

    private boolean craft() {
        int b = recipe.bigCrafting ? 1 : 0;
        int slot2 = -999;
        for (CraftingItem ci : recipe.recipe) {
            for (int s : ci.slots) {
                ItemStack is = null;
                for (int i = 0; i < Math.min(count, 64); i++) {
                    if (ctx.player().inventory.getCursorStack().isEmpty()) {
                        int slot = getSlotWithBiggestStack(ci.id);
                        if (slot == -1) {
                            System.out.println("out of mats at: " + i);
                            return false;
                        }
                        if (is == null) {
                            is = ctx.player().inventory.getStack(slot);
                        } else if (!is.getItem().equals(ctx.player().inventory.getStack(slot).getItem())) {
                            System.out.println("wrong mats");
                            return false;
                        }
                        slot2 = slot < 9 ? slot + 36 : slot;
                        slot2 += b;
                        InventoryHelper.clickSlot(ctx, slot2, 0, SlotActionType.PICKUP);
                    }
                    InventoryHelper.clickSlot(ctx, s, 1, SlotActionType.PICKUP);
                }
            }
            InventoryHelper.clickSlot(ctx, slot2, 0, SlotActionType.PICKUP);
            ctx.player().inventory.updateItems();
        }
        return true;
    }

    public int getSlotWithBiggestStack(int id) {
        int h = -1;
        Inventory inv = new Inventory();
        for (int i = 0; i < inv.size(); ++i) {
            if (inv.getThing(i) != null && id == inv.getThing(i).getId()) {
                if (h == -1 || inv.getThing(i).getCount() > inv.getThing(h).getCount()) {
                    h = i;
                }
            }
        }
        return h;
    }

    @Override
    public boolean allowedToCancel() {
        return false;
    }
}
