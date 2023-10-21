package scriptunseen.bassclef.tasks.passive;

import baritone.api.utils.input.Input;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.utils.BaritoneSettings;
import scriptunseen.bassclef.utils.helper.InputHandler;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class EatingTask extends PassiveTask {
    private int timer;
    private boolean eating;

    public EatingTask() {
        this.eating = false;
        this.timer = 0;
    }

    @Override
    public boolean taskTick() {
        if (eating) {
            InputHandler.press(InputHandler.Input.RIGHT_CLICK);
        }
        if (timer++ >= 10) {
            if (ctx.player().getHungerManager().isNotFull()) {
                if (eating) {
                    if (TaskManager.isRunning() && isForced()) {
                        TaskManager.pause();
                    }
                    if (canEat()) {
                        if (ctx.getSelectedBlock().isPresent()) {
                            Block block = ctx.world().getBlockState(ctx.getSelectedBlock().get()).getBlock();
                            if (block instanceof BlockWithEntity || block instanceof CraftingTableBlock) {
                                stopEating();
                                return false;
                            }
                        }
                        selectFood();
                        InputHandler.press(InputHandler.Input.RIGHT_CLICK);
                    } else {
                        stopEating();
                    }
                } else {
                    if (canEat()) {
                        startEating();
                    } else {
                        timer = 0;
                    }
                }
            } else {
                if (eating) {
                    stopEating();
                } else {
                    timer = 0;
                }
            }
        }
        return false;
    }

    private boolean isForced() {
        return ctx.player().getHungerManager().getFoodLevel() < 14 || ctx.player().getHealth() < ctx.player().getMaxHealth();
    }

    private boolean canEat() {
        boolean force = isForced();
        if (hasFood() && (ctx.world().getBlockState(ctx.playerFeet().offset(Direction.DOWN)).getMaterial().isSolid() || force) && ctx.player().currentScreenHandler.syncId == 0 && (force || !Main.baritone.getInputOverrideHandler().isInputForcedDown(Input.CLICK_LEFT))) {
            Optional<BlockPos> pos = ctx.getSelectedBlock();
            if (pos.isPresent()) {
                Block b = ctx.world().getBlockState(pos.get()).getBlock();
                return !(b instanceof HorizontalFacingBlock || b instanceof BlockWithEntity || b instanceof CraftingTableBlock);
            } else {
                return true;
            }
        }
        return false;
    }

    private void stopEating() {
        eating = false;
        timer = 0;
        BaritoneSettings.stopEating();
        if (!TaskManager.isRunning() && !RunAwayTask.isRunning()) TaskManager.resume();
    }

    public void selectFood() {
        if (!ctx.player().inventory.getMainHandStack().isFood()) {
            int best = -1; // checking for the food with the lowest count to save inventory space
            for (int i = 0; i < ctx.player().inventory.size(); i++) {
                ItemStack current = ctx.player().inventory.getStack(i);
                if (current.isFood() && !current.getItem().equals(Items.CHICKEN)) {
                    if (best == -1 || current.getCount() < ctx.player().inventory.getStack(best).getCount()) {
                        best = i;
                    }
                }
            }
            if (best > -1 && ctx.player().currentScreenHandler.syncId == 0) {
                int slot = best;
                if (slot <= 8) slot += 36;
                InventoryHelper.clickSlot(ctx, slot, SortInvTask.FOOD, SlotActionType.SWAP);
                ctx.player().inventory.selectedSlot = SortInvTask.FOOD;
            }
        }
    }

    private void startEating() {
        eating = true;
        BaritoneSettings.startEating();
    }

    private boolean hasFood() {
        for (int i = 0; i < ctx.player().inventory.size(); i++) {
            if (ctx.player().inventory.getStack(i).getItem().isFood()) return true;
        }
        return false;
    }
}
