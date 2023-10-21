package scriptunseen.bassclef.tasks.active.building;

import baritone.api.schematic.ISchematic;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.PlaceFluidTask;
import scriptunseen.bassclef.tasks.active.TaskQueue;
import scriptunseen.bassclef.tasks.active.building.utils.FluidSchematic;
import scriptunseen.bassclef.tasks.active.overworld.GoInPortalTask;
import scriptunseen.bassclef.tasks.active.overworld.portal.utils.PortalSchematic;
import scriptunseen.bassclef.tasks.passive.AntiStuckTask;
import scriptunseen.bassclef.utils.BaritoneSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class BuildTask extends ActiveTask {

    private final ISchematic schematic;
    private final FluidSchematic fluidSchematic;
    private BlockPos pos;
    private int phase;
    private int timer;

    public BuildTask(BlockPos pos, ISchematic schematic, FluidSchematic fluidSchematic) {
        this.phase = 1;
        this.pos = pos;
        this.schematic = schematic;
        this.fluidSchematic = fluidSchematic;
        this.timer = 0;
    }

    @Override
    public boolean activeTaskTick() {
        switch (phase) {
            case 1:
                pos = pos.add(calcOffset());
                Main.baritone.getBuilderProcess().build("structure", schematic, pos);
                phase++;
                break;
            case 2:
                if (!(Main.baritone.getBuilderProcess().isActive() || Main.baritone.getBuilderProcess().isPaused())) {
                    BaritoneSettings.startBuilding();
                    TaskQueue tq = new TaskQueue();
                    for (FluidSchematic.Fluid fluid : fluidSchematic.fluids) {
                        switch (fluid.type) {
                            case EMPTY:
                                tq.add(new PlaceFluidTask(pos.add(fluidSchematic.stand), pos.add(fluid.pos), fluid.block, 660));
                                break;
                            case WATER:
                                tq.add(new PlaceFluidTask(pos.add(fluidSchematic.stand), pos.add(fluid.pos), fluid.direction, 661));
                                break;
                            case LAVA:
                                tq.add(new PlaceFluidTask(pos.add(fluidSchematic.stand), pos.add(fluid.pos), fluid.direction, 662));
                                break;
                        }
                    }
                    runSubTask(tq);
                    phase++;
                }
                break;
            case 3:
                BaritoneSettings.stopBuilding();
                if (schematic instanceof PortalSchematic) {
                    if (timer++ >= 20 * 6 + 10) {
                        runSubTask(new GoInPortalTask(pos.add(2, 1, 1)));
                        phase++;
                    }
                    break;
                }
            case 4:
                taskState = TaskState.SUCCESS;
                break;
        }
        return true;
    }

    private Vec3i calcOffset() {
        Vec3i offs = new Vec3i(0, 0, 0);
        while (getBadBlocksInBuild(offs) > 0) {
            offs = getOffset(offs);
        }
        return offs;
    }

    private int getBadBlocksInBuild(Vec3i offs) {
        int count = 0;
        for (int y = -1; y < schematic.heightY() + 1; y++) {
            for (int x = -1; x < schematic.widthX() + 1; x++) {
                for (int z = -1; z < schematic.lengthZ() + 1; z++) {
                    if (isBadBlock(pos.add(x, y, z).add(offs))) {
                        count++;
                    } else if (y == schematic.heightY()) {
                        if (ctx.world().getBlockState(pos.add(x, y, z).add(offs)).getBlock() instanceof FallingBlock)
                            count++;
                    }
                }
            }
        }
        return count;
    }

    private Vec3i getOffset(Vec3i offs) {
        int[] min = {0, Integer.MAX_VALUE};
        for (int i = 0; i < 3; i++) {
            Direction direction = Direction.byId(i * 2 + 1);
            int count = getBadBlocksInBuild(offs.offset(direction, 1)) * (direction == Direction.UP ? 2 : 1);
            if (count < min[1]) {
                min[0] = i;
                min[1] = count;
            }
        }
        return offs.offset(Direction.byId(min[0] * 2 + 1), 1);
    }

    private boolean isBadBlock(BlockPos pos) {
        Block block = ctx.world().getBlockState(pos).getBlock();
        return block.equals(Blocks.LAVA) || block.equals(Blocks.WATER) || block.equals(Blocks.BEDROCK);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean cancelSubTask() {
        if (getRunTime() % 20 == 0) {
            AntiStuckTask ast = ((AntiStuckTask) TaskManager.getPassiveTask(AntiStuckTask.class));
            if (ast.getBigCount() >= 2) {
                fail("0");
                ast.reset();
                return true;
            }
        }
        return false;
    }
}
