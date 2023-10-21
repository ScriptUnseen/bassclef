package scriptunseen.bassclef.tasks.active.building.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class Lake {
    public List<BlockPos> pos;

    public Lake() {
        this.pos = new ArrayList<>();
    }

    public static Lake getLake(List<BlockPos> fluid, BlockPos pos) {
        Lake lake = new Lake();
        lake.pos.add(pos);
        findLake(fluid, pos, lake);
        return lake;
    }

    public static void findLake(List<BlockPos> lava, BlockPos pos, Lake lake) {
        lava.remove(pos);
        for (Direction direction : Direction.values()) {
            if (lava.contains(pos.offset(direction))) {
                lake.pos.add(pos);
                findLake(lava, pos.offset(direction), lake);
            }
        }
    }

    public BlockPos getSouthest() {
        List<BlockPos> surface = getSurfacePos();
        if (surface.size() > 0) {
            int south = 0;
            for (int i = 1; i < surface.size(); i++) {
                if (surface.get(south).getZ() < surface.get(i).getZ()) south = i;
            }
            return surface.get(south);
        }
        return null;
    }

    public List<BlockPos> getSurfacePos() {
        List<BlockPos> surface = new ArrayList<>();
        int y = getHighestY();
        if (y != -1) {
            for (BlockPos p : pos) {
                if (p.getY() == y) surface.add(p);
            }
        }
        return surface;
    }

    public int getHighestY() {
        int y = -1;
        for (BlockPos position : pos) {
            if (position.getY() > y) y = position.getY();
        }
        return y;
    }
}
