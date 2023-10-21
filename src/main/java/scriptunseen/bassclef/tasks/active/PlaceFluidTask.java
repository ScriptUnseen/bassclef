package scriptunseen.bassclef.tasks.active;

import baritone.api.pathing.goals.GoalBlock;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.pathing.movement.MovementHelper;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.helper.InputHandler;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PlaceFluidTask extends ActiveTask {

    private final BlockPos pos;
    private final BlockPos stand;
    private final int id;
    private final Direction direction;
    private final Vec3d goal;
    // wait to check if the water/lava is real
    private int timer = 0;

    public PlaceFluidTask(BlockPos stand, BlockPos pos, Direction direction, int id) {
        this.pos = pos;
        this.goal = Vec3d.ofCenter(pos).add(direction.getOffsetX() * 0.5, direction.getOffsetY() * 0.5, direction.getOffsetZ() * 0.5);
        this.stand = stand;
        this.id = id;
        this.direction = direction;
    }

    public PlaceFluidTask(BlockPos stand, BlockPos pos, Vec3d goal, int id) {
        this.pos = pos;
        this.direction = null;
        this.goal = goal.add(pos.getX(), pos.getY(), pos.getZ());
        this.stand = stand;
        this.id = id;
    }

    @Override
    protected boolean reachedGoal() {
        if (id == 660) {
            return ctx.world().getBlockState(new BlockPos((int) Math.floor(goal.x), (int) Math.floor(goal.y), (int) Math.floor(goal.z))).isAir();
        }

        Block block = ctx.world().getBlockState(pos.offset(direction)).getBlock();

        if (id == 661) {
            if (block.equals(Blocks.STONE)) {
                return true;
            }
            if (block.equals(Blocks.WATER)) {
                return ++timer > 20;
            }
            timer = 0;
            return false;
        }

        if (id == 662) {
            if (block.equals(Blocks.OBSIDIAN)) {
                return true;
            }
            if (block.equals(Blocks.LAVA)) {
                return ++timer > 20;
            }
            timer = 0;
            return false;
        }
        fail("Something went wrong in PlaceFluidTask!");
        return false;
    }

    @Override
    public boolean activeTaskTick() {
        if ((new Inventory()).contains(new Thing(id, 1))) {
            if (!Main.baritone.getCustomGoalProcess().isActive()) {
                Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(stand));
            }
            click();
        } else if (timer == 0) {
            runSubTask(new FillBucketTask(id));
        }
        return true;
    }

    private void click() {
        if (ctx.playerHead().isInRange(goal, ctx.playerController().getBlockReachDistance())) {
            boolean onPos = ctx.playerFeet().equals(stand);
            if (Main.client.crosshairTarget instanceof BlockHitResult) {
                BlockHitResult bhr = (BlockHitResult) Main.client.crosshairTarget;
                if (isLookingAtRightSpot(bhr)) {
                    if (InventoryHelper.equip(ctx, id, SortInvTask.BUCKET, true)) {
                        System.out.println("bad click");
                        InteractionHelper.interactItem(ctx);
                    }
                } else if (onPos) {
                    MovementHelper.switchToBestToolFor(ctx, ctx.world().getBlockState(bhr.getBlockPos()));
                    InputHandler.press(InputHandler.Input.LEFT_CLICK);
                }
            }

            Rotation rot = RotationUtils.calcRotationFromVec3d(ctx.playerHead(), goal, ctx.playerRotations());
            HitResult hr = RayTraceUtils.rayTraceTowards(ctx.player(), rot, ctx.playerController().getBlockReachDistance());
            if (onPos || (hr instanceof BlockHitResult && isLookingAtRightSpot((BlockHitResult) hr))) {
                Main.baritone.getLookBehavior().updateTarget(rot, true);
            }
        }
    }

    private boolean isLookingAtRightSpot(BlockHitResult bhr) {
        return bhr.getBlockPos().equals(pos) && (direction == null || bhr.getSide().equals(direction));
    }

    @Override
    public void finish() {
        Main.baritone.getPathingBehavior().cancelEverything();
    }
}
