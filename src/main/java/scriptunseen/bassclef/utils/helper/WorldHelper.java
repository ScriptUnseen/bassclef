package scriptunseen.bassclef.utils.helper;

import baritone.api.utils.IPlayerContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface WorldHelper {
    static BlockPos getActualBlockPosOfEntity(Entity entity) {
        BlockPos pos = entity.getBlockPos();
        if (entity.getEntityWorld().getBlockState(pos.down()).getMaterial().blocksMovement()) {
            return pos;
        } else {
            double s = entity.getWidth() / 2.0D;
            int xOff = getOffset(s, entity.getX(), entity.getBlockPos().getX());
            int zOff = getOffset(s, entity.getZ(), entity.getBlockPos().getZ());
            return pos.add(xOff, 0, zOff);
        }
    }

    static int getOffset(double s, double d, int i) {
        if (Math.floor(d + s) != i) {
            return 1;
        }
        if (Math.floor(d - s) != i) {
            return -1;
        }
        return 0;
    }

    static boolean isNotSave(IPlayerContext ctx) {
        BlockPos pos = ctx.playerFeet();
        int offs = 0;
        int scan;
        for (scan = scanBlocks(ctx.world(), pos.offset(Direction.UP, offs)); scan == 1; scan = scanBlocks(ctx.world(), pos.offset(Direction.UP, offs))) {
            offs++;
        }
        return offs > 2 && scan == 0;
    }

    static int scanBlocks(World world, BlockPos pos) {
        if (world.getBlockState(pos).getMaterial().isSolid()) {
            return 2;
        }
        return surrounded(world, pos) ? 1 : 0;
    }

    static boolean inHole(Entity entity) {
        for (int i = 0; i < 2; i++) {
            if (!surrounded(entity.world, entity.getBlockPos().offset(Direction.UP, i))) {
                return false;
            }
        }
        return true;
    }

    static boolean surrounded(World world, BlockPos pos) {
        for (int i = 2; i < 6; i++) {
            if (!world.getBlockState(pos.offset(Direction.byId(i))).getMaterial().isSolid()) {
                return false;
            }
        }
        return true;
    }
}
