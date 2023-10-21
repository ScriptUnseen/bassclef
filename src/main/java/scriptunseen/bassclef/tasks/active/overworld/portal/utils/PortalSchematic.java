package scriptunseen.bassclef.tasks.active.overworld.portal.utils;

import baritone.utils.schematic.StaticSchematic;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;

import java.util.List;

public class PortalSchematic extends StaticSchematic {

    public PortalSchematic() {
        super.x = 5;
        super.y = 5;
        super.z = 5;
        super.states = new BlockState[x][y][z];
        initPortalState();
    }

    private void initPortalState() {
        //first row
        super.states[1][0][0] = Blocks.COBBLESTONE.getDefaultState();
        super.states[2][0][0] = Blocks.COBBLESTONE.getDefaultState();
        super.states[3][0][0] = Blocks.COBBLESTONE.getDefaultState();

        super.states[0][0][1] = Blocks.COBBLESTONE.getDefaultState();
        super.states[1][0][1] = Blocks.AIR.getDefaultState();
        super.states[2][0][1] = Blocks.AIR.getDefaultState();
        super.states[3][0][1] = Blocks.COBBLESTONE.getDefaultState();

        super.states[0][0][2] = Blocks.COBBLESTONE.getDefaultState();
        super.states[1][0][2] = Blocks.COBBLESTONE.getDefaultState();
        super.states[2][0][2] = Blocks.COBBLESTONE.getDefaultState();
        super.states[3][0][2] = Blocks.COBBLESTONE.getDefaultState();

        //second row
        super.states[0][1][0] = Blocks.COBBLESTONE.getDefaultState();
        super.states[1][1][0] = Blocks.COBBLESTONE.getDefaultState();
        super.states[2][1][0] = Blocks.COBBLESTONE.getDefaultState();
        super.states[3][1][0] = Blocks.COBBLESTONE.getDefaultState();

        super.states[0][1][1] = Blocks.AIR.getDefaultState();
        super.states[1][1][1] = Blocks.AIR.getDefaultState();
        super.states[2][1][1] = Blocks.AIR.getDefaultState();
        super.states[3][1][1] = Blocks.AIR.getDefaultState();
        super.states[4][1][1] = Blocks.COBBLESTONE.getDefaultState();

        super.states[0][1][2] = Blocks.COBBLESTONE.getDefaultState();
        super.states[3][1][2] = Blocks.COBBLESTONE.getDefaultState();

        super.states[0][1][3] = Blocks.COBBLESTONE.getDefaultState();
        super.states[1][1][3] = Blocks.AIR.getDefaultState();
        super.states[2][1][3] = Blocks.COBBLESTONE.getDefaultState();
        super.states[3][1][3] = Blocks.COBBLESTONE.getDefaultState();

        super.states[1][1][4] = Blocks.COBBLESTONE.getDefaultState();
        super.states[2][1][4] = Blocks.COBBLESTONE.getDefaultState();

        //third row
        super.states[0][2][0] = Blocks.AIR.getDefaultState();
        super.states[1][2][0] = Blocks.AIR.getDefaultState();
        super.states[2][2][0] = Blocks.AIR.getDefaultState();
        super.states[3][2][0] = Blocks.AIR.getDefaultState();

        super.states[0][2][1] = Blocks.AIR.getDefaultState();
        super.states[1][2][1] = Blocks.AIR.getDefaultState();
        super.states[2][2][1] = Blocks.AIR.getDefaultState();
        super.states[3][2][1] = Blocks.AIR.getDefaultState();

        super.states[0][2][2] = Blocks.COBBLESTONE.getDefaultState();
        super.states[1][2][2] = Blocks.AIR.getDefaultState();
        super.states[2][2][2] = Blocks.COBBLESTONE.getDefaultState();
        super.states[3][2][2] = Blocks.COBBLESTONE.getDefaultState();

        //fourth row
        super.states[1][3][0] = Blocks.AIR.getDefaultState();
        super.states[2][3][0] = Blocks.AIR.getDefaultState();
        super.states[3][3][0] = Blocks.AIR.getDefaultState();

        super.states[0][3][1] = Blocks.AIR.getDefaultState();
        super.states[1][3][1] = Blocks.AIR.getDefaultState();
        super.states[2][3][1] = Blocks.AIR.getDefaultState();
        super.states[3][3][1] = Blocks.AIR.getDefaultState();

        super.states[0][3][2] = Blocks.COBBLESTONE.getDefaultState();
        super.states[1][3][2] = Blocks.AIR.getDefaultState();
        super.states[2][3][2] = Blocks.AIR.getDefaultState();
        super.states[3][3][2] = Blocks.AIR.getDefaultState(); // ?

        //fifth row
        super.states[1][4][0] = Blocks.AIR.getDefaultState();
        super.states[2][4][0] = Blocks.AIR.getDefaultState();

        super.states[0][4][1] = Blocks.COBBLESTONE.getDefaultState();
        super.states[1][4][1] = Blocks.AIR.getDefaultState();
        super.states[2][4][1] = Blocks.AIR.getDefaultState();
        super.states[3][4][1] = Blocks.AIR.getDefaultState();

        super.states[0][4][2] = Blocks.COBBLESTONE.getDefaultState();
        super.states[1][4][2] = Blocks.AIR.getDefaultState();
        super.states[2][4][2] = Blocks.AIR.getDefaultState();
        super.states[3][4][2] = Blocks.AIR.getDefaultState();
    }

    @Override
    public BlockState desiredState(int x, int y, int z, BlockState current, List<BlockState> approxPlaceable) {
        BlockState bs = states[x][y][z];
        if (bs == null) return current;
        if (bs.getMaterial().isSolid()) {
            if (current.getMaterial().isSolid()) return current;
            for (BlockState blockState : approxPlaceable) {
                if (blockState.getMaterial().isSolid() && !(blockState.getBlock() instanceof FallingBlock || blockState.getBlock() instanceof BlockWithEntity))
                    return blockState;
            }
        }
        return bs;
    }
}
