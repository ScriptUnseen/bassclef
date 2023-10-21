package scriptunseen.bassclef.tasks.active;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalInverted;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.pathing.movement.MovementHelper;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.getitem.GetItemTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import scriptunseen.bassclef.utils.helper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FillBucketTask extends GetSthTask {

    private final int id;
    private BlockPos toStand;
    private BlockPos fill;
    private boolean checkedFluid;

    public FillBucketTask(int id) {
        this.id = id;
        this.fill = null;
        this.checkedFluid = false;
    }

    @Override
    protected boolean reachedGoal() {
        if (fill == null) {
            return false;
        }

        int slot = new Inventory().getSlot((new Thing(id, 1)));
        if (slot == -1) {
            if (checkedFluid) {
                checkedFluid = false;
                if (ctx.player().inventory.getCursorStack().getItem().equals(Items.BUCKET)) {
                    InventoryHelper.clickSlot(Main.baritone().getPlayerContext(), SortInvTask.BUCKET + 36, 1, SlotActionType.PICKUP);
                }
            }
            return false;
        }

        if (!checkedFluid) {
            // update bucket to check if the fluid is real
            for (int i = 0; i < 2; i++) {
                InventoryHelper.clickSlot(Main.baritone().getPlayerContext(), slot < 9 ? slot + 36 : slot, 1, SlotActionType.PICKUP);
            }
            checkedFluid = true;
            return false;
        }
        return true;
    }

    @Override
    public void finish() {
        Main.baritone.getPathingBehavior().forceCancel();
    }

    @Override
    public boolean activeTaskTick() {
        if (getRunTime() % (20 * 4) == 0 || !(toStand == null || canStandAt(toStand))) {
            init();
        }

        Goal goal = Main.baritone.getCustomGoalProcess().getGoal();
        if (toStand == null) {
            if (!(goal instanceof GoalInverted)) {
                // find new spot
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalXZ(ctx.playerFeet())));
            }
            return true;
        }

        if (goal instanceof GoalInverted) {
            Main.baritone.getPathingBehavior().forceCancel();
            return true;
        }

        boolean onPos = toStand.equals(ctx.playerFeet());
        if (!(goal instanceof GoalBlock) && !onPos) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(toStand));
        }
        List<BlockPos> fluids = blockPos.stream().filter(this::inReach).collect(Collectors.toList());
        Rotation rot = null;
        Rotation breakRot = null;
        BlockHitResult bhr;
        BlockHitResult breakBhr = null;
        fill = null;
        for (BlockPos p : fluids) {
            Vec3d vec = new Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
            rot = RotationUtils.calcRotationFromVec3d(ctx.playerHead(), vec, ctx.playerRotations());
            HitResult hr = RayTraceUtils.rayTraceTowards(ctx.player(), rot, ctx.playerController().getBlockReachDistance());
            if (hr.getType() == HitResult.Type.MISS) {
                fill = p;
                break;
            } else if (hr.getType() == HitResult.Type.BLOCK) {
                bhr = (BlockHitResult) hr;
                if (p.equals(bhr.getBlockPos().offset(bhr.getSide()))) {
                    fill = p;
                    break;
                } else if (onPos) {
                    if (breakBhr == null && !bhr.getBlockPos().equals(WorldHelper.getActualBlockPosOfEntity(ctx.player()).offset(Direction.DOWN)) && !ctx.world().getBlockState(bhr.getBlockPos()).getBlock().equals(Blocks.BEDROCK)) {
                        breakBhr = bhr;
                        breakRot = rot;
                    }
                }
            }
        }
        if (fill != null) {
            Main.baritone.getPathingBehavior().cancelEverything();
            Main.baritone.getLookBehavior().updateTarget(rot, true);
            tryInteracting();
        } else if (onPos) {
            if (breakBhr != null) {
                Main.baritone.getLookBehavior().updateTarget(breakRot, true);
                MovementHelper.switchToBestToolFor(ctx, ctx.world().getBlockState(breakBhr.getBlockPos()));
                InputHandler.press(InputHandler.Input.LEFT_CLICK);
            } else {
                ChatHelper.displayChatMessage("Something went wrong!", true);
            }
        }
        return true;
    }

    private boolean canStandAt(BlockPos pos) {
        if (isValidBlock(pos.offset(Direction.DOWN))) {
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    for (int y = 0; y < 2; y++) {
                        if (!isValidBlock(pos.add(x, y, z))) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void init() {
        blockPos.clear();
        blockPos.addAll(WorldScanner.filterFluids(ctx, WorldScanner.scanChunkRadius(ctx, new BlockOptionalMetaLookup((id == 661) ? "water" : "lava"), 5, 100, 3, 300)));
        ArrayList<BlockPos> fluids = new ArrayList<>(blockPos);
        toStand = null;
        do {
            BlockPos n1 = WorldScanner.getNearest(ctx, fluids);
            if (n1 == null) {
                return;
            } else {
                fluids.remove(n1);
                toStand = getStand(n1);
            }
        } while (toStand == null);
    }

    private boolean inReach(BlockPos pos) {
        return ctx.playerHead().isInRange(new Vec3d(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5), ctx.playerController().getBlockReachDistance());
    }

    private void tryInteracting() {
        if (InventoryHelper.equip(ctx, 660, SortInvTask.BUCKET, true)) {
            System.out.println("bad click 2");
            InteractionHelper.interactItem(ctx);
        } else if (!(new Inventory()).contains(new Thing(id, 1))) {
            runSubTask(new GetItemTask(new ItemTexture(660, 1), false));
        }
    }

    private boolean isValidBlock(BlockPos pos) {
        BlockState blockState = ctx.world().getBlockState(pos);
        return !(blockState.getMaterial().isLiquid() || blockState.getBlock() instanceof FallingBlock);
    }

    private BlockPos getStand(BlockPos fluid) {
        if (isValidBlock(fluid.offset(Direction.UP))) {
            for (int i = 0; i < 1; i++) {
                fluid = fluid.offset(Direction.UP);
                // only x and z axis
                for (int j = 2; j < 6; j++) {
                    BlockPos stand = fluid.offset(Direction.byId(j));
                    if (canStandAt(stand)) return stand;
                }
            }
        }
        return null;
    }

    @Override
    public boolean allowedToCancel() {
        return false;
    }
}
