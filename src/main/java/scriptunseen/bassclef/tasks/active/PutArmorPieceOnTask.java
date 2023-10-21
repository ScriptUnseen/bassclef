package scriptunseen.bassclef.tasks.active;

import scriptunseen.bassclef.tasks.active.getitem.GetItemTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.registry.Registry;

public class PutArmorPieceOnTask extends ActiveTask {

    private final Item armor;
    private final int armorSlot;

    public PutArmorPieceOnTask(int id) {
        this.armor = Registry.ITEM.get(id);
        this.armorSlot = getArmorSlot();
    }

    private int getArmorSlot() {
        String name = Registry.ITEM.getId(armor).toString();
        if (name.endsWith("helmet")) {
            return 5;
        } else if (name.endsWith("chestplate")) {
            return 6;
        } else if (name.endsWith("leggings")) {
            return 7;
        } else if (name.endsWith("boots")) {
            return 8;
        } else {
            return -1;
        }
    }

    @Override
    protected boolean reachedGoal() {
        Thing thing = (new Inventory()).getThing(44 - armorSlot); // 3 - (armorSlot-5) + 36
        return thing != null && thing.getId() == Registry.ITEM.getRawId(armor);
    }

    @Override
    public boolean activeTaskTick() {
        if (ctx.player().currentScreenHandler.syncId == 0) {
            Inventory inv = new Inventory();
            int slot = inv.getSlot(new Thing(Registry.ITEM.getRawId(armor), 1));
            if (slot != -1) {
                SortInvTask.removeItems(ctx);
                InventoryHelper.clickSlot(ctx, armorSlot, 0, SlotActionType.QUICK_MOVE);
                InventoryHelper.clickSlot(ctx, slot < 9 ? slot + 36 : slot, 0, SlotActionType.QUICK_MOVE);
            } else {
                runSubTask(new GetItemTask(new ItemTexture(Registry.ITEM.getRawId(armor), 1), true));
            }
        } else {
            ctx.player().closeHandledScreen();
        }
        return true;
    }
}
