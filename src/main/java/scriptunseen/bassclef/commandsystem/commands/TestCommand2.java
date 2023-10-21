package scriptunseen.bassclef.commandsystem.commands;

import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.commandsystem.ICommand;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.screen.slot.SlotActionType;

public class TestCommand2 implements ICommand {

    @Override
    public boolean onCommand(String[] args) {
        InventoryHelper.clickSlot(Main.baritone().getPlayerContext(), 6+36, 1, SlotActionType.PICKUP);
        System.out.println(Main.baritone().getPlayerContext().player().inventory.getCursorStack().getItem());
        InventoryHelper.clickSlot(Main.baritone().getPlayerContext(), 6+36, 1, SlotActionType.PICKUP);
        System.out.println(Main.baritone().getPlayerContext().player().inventory.getCursorStack().getItem());
        System.out.print(Main.baritone().getPlayerContext().player().inventory.getStack(6).getItem());
        return true;
    }
}