package scriptunseen.bassclef.tasks.active.overworld.portal.utils;

import scriptunseen.bassclef.tasks.active.building.utils.FluidSchematic;
import scriptunseen.bassclef.tasks.active.building.utils.FluidType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;

public class PortalFluidSchematic extends FluidSchematic {

    public PortalFluidSchematic() {
        super.stand = new Vec3i(2, 2, 0);
        super.fluids = new ArrayList<>();

        //water
        fluids.add(new Fluid(new Vec3i(0, 4, 2), Direction.EAST, FluidType.WATER));

        //bottom
        fluids.add(new Fluid(new Vec3i(1, 0, 2), Direction.NORTH, FluidType.LAVA));
        fluids.add(new Fluid(new Vec3i(2, 0, 2), Direction.NORTH, FluidType.LAVA));

        //right
        fluids.add(new Fluid(new Vec3i(0, 1, 2), Direction.NORTH, FluidType.LAVA));
        fluids.add(new Fluid(new Vec3i(0, 2, 2), Direction.NORTH, FluidType.LAVA));
        fluids.add(new Fluid(new Vec3i(0, 3, 2), Direction.NORTH, FluidType.LAVA));

        //left
        fluids.add(new Fluid(new Vec3i(3, 1, 2), Direction.NORTH, FluidType.LAVA));
        fluids.add(new Fluid(new Vec3i(3, 1, 1), Direction.UP, FluidType.LAVA));
        fluids.add(new Fluid(new Vec3i(3, 2, 1), Direction.UP, FluidType.LAVA));

        //top
        fluids.add(new Fluid(new Vec3i(0, 4, 1), Direction.EAST, FluidType.LAVA));
        fluids.add(new Fluid(new Vec3i(1, 4, 1), Direction.EAST, FluidType.LAVA));

        fluids.add(new Fluid(new Vec3i(0, 4, 2), new Vec3d(1, 0.15, 0.85), FluidType.EMPTY));
    }
}
