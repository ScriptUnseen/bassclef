package scriptunseen.bassclef.tasks.active.getitem;

import baritone.api.utils.BlockOptionalMetaLookup;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.*;
import scriptunseen.bassclef.tasks.active.*;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;
import scriptunseen.bassclef.tasks.active.getitem.utils.SmeltItem;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.utils.helper.ChatHelper;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import scriptunseen.bassclef.utils.helper.WorldScanner;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class SmeltItemTask extends ActiveTask {

    private final List<Thing> out;
    private final int count;
    private int state;

    public SmeltItemTask(List<Thing> out) {
        this.out = out;
        this.state = 0;
        this.count = getCount();
    }

    private int getCount() {
        int c = 0;
        for (Thing thing : out) {
            c += thing.getCount();
        }
        // can't put new items in and old items out of the furnace in the same tick
        if (out.size() > 1) c++;
        return (int) Math.ceil(c / 8.0);
    }

    @Override
    public boolean activeTaskTick() {
        switch (state) {
            case 0:
                TaskQueue tq = new TaskQueue();
                if (!(new Inventory()).contains(new Thing(InventoryHelper.getID("minecraft:coal"), count)))
                    tq.add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:coal"), count), false));
                BlockPos furnace = WorldScanner.getNearestBlock(ctx, new BlockOptionalMetaLookup("furnace"), 30, Math.max(Main.baritone.getPlayerContext().playerFeet().getY() - 20, 0), Math.min(ctx.playerFeet().getY() + 10, 255));
                if (furnace == null) {
                    tq.add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:furnace"), 1), false));
                    tq.add(new PlaceBlockTask(InventoryHelper.getID("minecraft:furnace")));
                }
                if (tq.getTasks().size() > 0) {
                    runSubTask(tq);
                } else {
                    if (Main.client.currentScreen instanceof FurnaceScreen) {
                        state++;
                    } else {
                        runSubTask(new GotoBlockTask(furnace));
                    }
                }
                break;
            case 1:
                quickMove(InventoryHelper.getID("minecraft:coal"));
                state++;
                break;
            case 2:
                int item = getSmeltItem();
                if (item == -1) {
                    fail();
                    return true;
                } else {
                    quickMove(item);
                }
                state++;
                break;
            case 3:
                if (doneSmelting()) state++;
                break;
            case 4:
                InventoryHelper.clickSlot(ctx, 2, 0, SlotActionType.QUICK_MOVE);
                state++;
                break;
            case 5:
                InventoryHelper.clickSlot(ctx, 0, 0, SlotActionType.QUICK_MOVE);
                out.remove(0);
                if (out.size() == 0) {
                    state++;
                } else {
                    state = 2;
                }
                break;
            case 6:
                ctx.player().closeHandledScreen();
                if (ctx.player().currentScreenHandler.syncId == 0) state++;
                break;
            case 7:
                BlockPos pos = WorldScanner.getNearestBlock(ctx, new BlockOptionalMetaLookup("furnace"), 32);
                if (pos != null && ctx.world().getBlockState(pos.offset(Direction.DOWN)).getMaterial().isSolid()) {
                    runSubTask(new CollectBlockTask(InventoryHelper.getID("minecraft:furnace")));
                }
                state++;
                break;
            case 8:
                ChatHelper.displayChatMessage("finished");
                taskState = TaskState.SUCCESS;
                break;
        }
        return true;
    }

    private void quickMove(int id) {
        int slot = ctx.player().inventory.getSlotWithStack(getStack(id));
        InventoryHelper.clickSlot(ctx, (slot < 9 ? slot + 36 : slot) - 6, 0, SlotActionType.QUICK_MOVE);
    }

    private int getSmeltItem() {
        if (out.size() > 0) {
            for (int i = 0; i < SmeltItem.SMELT_ITEMS.length; i++) {
                if (out.get(0).getId() == SmeltItem.SMELT_ITEMS[i].out) return SmeltItem.SMELT_ITEMS[i].inp;
            }
        }
        return -1;
    }

    private boolean doneSmelting() {
        if (Main.client.currentScreen instanceof FurnaceScreen && out.size() > 0) {
            FurnaceScreenHandler fsh = ((FurnaceScreen) Main.client.currentScreen).getScreenHandler();
            ItemStack is = fsh.slots.get(0).getStack();
            return is.isEmpty();
        } else {
            fail();
        }
        return false;
    }

    private ItemStack getStack(int id) {
        return new ItemStack(Registry.ITEM.get(id));
    }

    @Override
    public boolean allowedToCancel() {
        return false;
    }
}
