package scriptunseen.bassclef.tasks.active.building.utils;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public abstract class FluidSchematic {

    public List<Fluid> fluids;
    public Vec3i stand;

    public static class Fluid {
        public Vec3i pos;
        public Direction direction;
        public FluidType type;
        public Vec3d block;

        public Fluid(Vec3i pos, Direction direction, FluidType type) {
            this.pos = pos;
            this.direction = direction;
            this.type = type;
        }

        public Fluid(Vec3i pos, Vec3d block, FluidType type) {
            this.pos = pos;
            this.block = block;
            this.type = type;
        }
    }
}
