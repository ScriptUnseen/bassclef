package scriptunseen.bassclef.tasks.active.overworld;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.BlockOptionalMeta;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class GoInPortalTask extends ActiveTask {

    private final BlockPos pos;
    int timer;
    private int phase;

    public GoInPortalTask(BlockPos pos) {
        this.pos = pos;
        this.phase = 0;
        this.timer = 20 * 5;
    }

    @Override
    public boolean activeTaskTick() {
        switch (phase) {
            case 0:
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(pos));
                phase = 1;
                break;
            case 1:
                if (!Main.baritone.getCustomGoalProcess().isActive()) {
                    if (isFASEquipped()) {
                        if (lightPortal()) phase = 2;
                    } else {
                        InventoryHelper.equip(ctx, 575, SortInvTask.TEMPORARY, true);
                        phase = 0;
                    }
                }
                break;
            case 2:
                if (!Main.baritone.getCustomGoalProcess().isActive()) {
                    Main.baritone.getGetToBlockProcess().getToBlock(new BlockOptionalMeta(Blocks.NETHER_PORTAL));
                    phase = 3;
                }
                break;
            case 3:
                if (timer-- <= 0) {
                    Main.positions.portal = Main.baritone.getPlayerContext().playerFeet();
                    taskState = TaskState.SUCCESS;
                }
                break;
        }
        return false;
    }

    private boolean isFASEquipped() {
        return Main.client.player != null && Main.client.player.inventory.getStack(Main.client.player.inventory.selectedSlot).getItem().equals(Registry.ITEM.get(575));
    }

    private boolean lightPortal() {
        IPlayerContext ctx = Main.baritone.getPlayerContext();
        Optional<Rotation> reachable = RotationUtils.reachable(ctx.player(), pos.offset(Direction.DOWN), ctx.playerController().getBlockReachDistance());
        if (Main.client.interactionManager != null && reachable.isPresent()) {
            Main.baritone.getLookBehavior().updateTarget(reachable.get(), true);
            if (pos.offset(Direction.DOWN).equals(ctx.getSelectedBlock().orElse(null))) {
                BlockHitResult bhr = (BlockHitResult) Main.client.crosshairTarget;
                Main.client.interactionManager.interactBlock(Main.client.player, Main.client.world, Hand.MAIN_HAND, bhr);
                return true;
            }
        }
        return false;
    }
}
