package scriptunseen.bassclef.tasks.passive;

import baritone.api.BaritoneAPI;
import baritone.api.utils.RotationUtils;
import baritone.pathing.movement.MovementHelper;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.utils.helper.ChatHelper;
import scriptunseen.bassclef.utils.helper.InputHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AntiStuckTask extends PassiveTask {

    // FIXME in water trying to break block

    private BlockPos pos;
    private int timer;
    private int count;
    private int bigCount;
    private boolean inBlock;
    private boolean inPortal;
    private BlockPos onEdge;

    public AntiStuckTask() {
        reset();
    }

    @Override
    public boolean taskTick() {
        if (inPortal) {
            if (ctx.world().getBlockState(ctx.playerFeet()).getBlock().equals(Blocks.NETHER_PORTAL)) {
                InputHandler.press(InputHandler.Input.MOVE_FORWARD);
            } else {
                inPortal = false;
            }
        } else if (inBlock) {
            BlockState bs;
            bs = ctx.world().getBlockState(ctx.playerFeet().up());
            if (bs.getMaterial().blocksMovement()) {
                Main.baritone.getLookBehavior().updateTarget(RotationUtils.calcRotationFromVec3d(ctx.playerHead(), Vec3d.of(ctx.playerFeet()), ctx.playerRotations()), true);
                MovementHelper.switchToBestToolFor(ctx, bs);
            } else {
                inBlock = false;
            }
        } else if (onEdge != null) {
            if (onEdge.equals(ctx.playerFeet())) {
                onEdge = null;
            } else {
                Main.baritone.getLookBehavior().updateTarget(RotationUtils.calcRotationFromVec3d(ctx.playerHead(), new Vec3d(onEdge.getX() + .5, onEdge.getY() + 1.5, onEdge.getZ() + .5), ctx.playerRotations()), false);
                InputHandler.press(InputHandler.Input.MOVE_FORWARD);
            }
        } else if (Main.baritone.getPathingBehavior().getGoal() != null && !Main.baritone.getBuilderProcess().isActive()) {
            if (timer++ >= 20) {
                if (ctx.playerFeet().isWithinDistance(pos, 2)) {
                    if (++count > 16) {
                        count = 0;
                        if (ctx.world().getBlockState(ctx.playerFeet().up()).getMaterial().blocksMovement() || ctx.world().getBlockState(ctx.playerFeet()).getMaterial().blocksMovement()) {
                            inBlock = true;
                        } else {
                            BlockPos pos = Main.baritone.getPathingBehavior().pathStart();
                            if (Main.baritone.getPathingBehavior().getGoal().isInGoal(pos)) {
                                onEdge = pos;
                                ChatHelper.displayChatMessage("AntiStuckTask: 1", true);
                                return false;
                            }
                            if (ctx.world().getBlockState(ctx.playerFeet()).getBlock().equals(Blocks.NETHER_PORTAL)) {
                                inPortal = true;
                                ChatHelper.displayChatMessage("AntiStuckTask: 2", true);
                                return false;
                            }
                            if (++bigCount > 5) {
                                ChatHelper.displayChatMessage("Bot seems still stuck!\nTaking tougher actions!", true);
                                BaritoneAPI.getSettings().walkWhileBreaking.value = false;
                                TaskManager.disableTask(AvoidMonsterTask.class);
                                ctx.player().closeHandledScreen();
                            } else {
                                ChatHelper.displayChatMessage("Bot is probably stuck!\nCanceling pathing of baritone!\nThis can cause errors!", true);
                            }
                            Main.baritone.getPathingBehavior().forceCancel();
                        }
                    }
                } else {
                    count = 0;
                    if (bigCount > 0) {
                        TaskManager.enableTask(AvoidMonsterTask.class);
                        BaritoneAPI.getSettings().walkWhileBreaking.value = true;
                        bigCount = 0;
                    }
                    pos = new BlockPos(ctx.playerFeet());
                }
                timer = 0;
            }
        } else {
            timer = 0;
        }
        return false;
    }

    public void reset() {
        this.timer = 0;
        this.count = 0;
        this.bigCount = 0;
        this.inBlock = false;
        this.inPortal = false;
        this.pos = new BlockPos(0, 0, 0);
        this.onEdge = null;
    }

    public int getBigCount() {
        return bigCount;
    }
}
