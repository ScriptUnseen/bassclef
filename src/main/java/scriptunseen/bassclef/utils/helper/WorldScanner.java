package scriptunseen.bassclef.utils.helper;

import baritone.api.utils.BlockOptionalMetaLookup;
import baritone.api.utils.IPlayerContext;
import scriptunseen.bassclef.Main;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldScanner {

    public static List<BlockPos> scanAllChunks(IPlayerContext ctx, BlockOptionalMetaLookup boml, int yLevelTresholdBottom, int yLevelTresholdTop, List<ChunkPos> chunkPos) {
        List<BlockPos> blocks = new ArrayList<>();
        int maxRadius = Main.client.options.viewDistance / 2 + 1;
        int chunkX = (ctx.playerFeet().x >> 4) - maxRadius;
        int chunkZ = (ctx.playerFeet().z >> 4) - maxRadius;

        for (int xoff = 0; xoff < maxRadius * 2; xoff++) {
            for (int zoff = 0; zoff < maxRadius * 2; zoff++) {
                int x = chunkX + xoff;
                int z = chunkZ + zoff;
                if (ctx.world().isChunkLoaded(x, z) && !chunkPos.contains(new ChunkPos(x, z))) {
                    blocks.addAll(WorldScanner.scanChunk(ctx, new ChunkPos(x, z), boml, yLevelTresholdBottom, yLevelTresholdTop));
                    chunkPos.add(new ChunkPos(x, z));
                }
            }
        }
        return blocks;
    }

    public static List<BlockPos> scanChunk(IPlayerContext ctx, ChunkPos chunkPos, BlockOptionalMetaLookup bom, int yLevelTresholdBottom, int yLevelTresholdTop) {
        List<BlockPos> blocks = new ArrayList<>();
        if (!ctx.world().isChunkLoaded(chunkPos.x, chunkPos.z)) {
            return blocks;
        }
        WorldChunk wc = ctx.world().getChunk(chunkPos.x, chunkPos.z);
        for (int y = yLevelTresholdBottom; y < yLevelTresholdTop; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos newPos = new BlockPos(x, y, z);
                    BlockState bs = wc.getBlockState(newPos);
                    //if (bs.getMaterial().isLiquid() && !(bs.getBlock() instanceof FluidBlock)) continue;
                    if (bom.has(wc.getBlockState(newPos).getBlock())) {
                        blocks.add(new BlockPos((chunkPos.x << 4) | x, y, chunkPos.z << 4 | z));
                    }
                }
            }
        }
        return blocks;
    }

    public static BlockPos getNearestBlock(IPlayerContext ctx, BlockOptionalMetaLookup boml, int radius) {
        return getNearestBlock(ctx, boml, radius, ctx.playerFeet().y - radius / 2, ctx.playerFeet().y + radius / 2);
    }

    public static BlockPos getNearestBlock(IPlayerContext ctx, BlockOptionalMetaLookup boml, int radius, int yLevelTresholdBottom, int yLevelTresholdTop) {
        List<BlockPos> blocks = scanChunkRadius(ctx, boml, yLevelTresholdBottom, yLevelTresholdTop, (radius >> 4) + 1, 100);
        BlockPos pos = null;
        for (BlockPos p : blocks) {
            if (pos == null || ctx.playerFeet().getSquaredDistance(p) < ctx.playerFeet().getSquaredDistance(pos)) {
                pos = p;
            }
        }
        return pos;
    }

    public static List<BlockPos> scanChunkRadius(IPlayerContext ctx, BlockOptionalMetaLookup bom, int yLevelTresholdBottom, int yLevelTresholdTop, int maxRadius, int maxNum) {
        List<BlockPos> blocks = new ArrayList<>();
        int chunkX = (ctx.playerFeet().x >> 4);
        int chunkZ = (ctx.playerFeet().z >> 4);

        for (int i = 0; i < maxRadius; i++) {
            if (i == 0) {
                blocks.addAll((scanChunk(ctx, new ChunkPos(chunkX, chunkZ), bom, yLevelTresholdBottom, yLevelTresholdTop)));
            } else {
                if (blocks.size() < maxNum) {
                    for (int j = 0; j < i + i; j++) {
                        blocks.addAll((scanChunk(ctx, new ChunkPos(chunkX + i, chunkZ - i + j), bom, yLevelTresholdBottom, yLevelTresholdTop)));
                        blocks.addAll((scanChunk(ctx, new ChunkPos(chunkX + i - j, chunkZ + i), bom, yLevelTresholdBottom, yLevelTresholdTop)));
                        blocks.addAll((scanChunk(ctx, new ChunkPos(chunkX - i, chunkZ + i - j), bom, yLevelTresholdBottom, yLevelTresholdTop)));
                        blocks.addAll((scanChunk(ctx, new ChunkPos(chunkX - i + j, chunkZ - i), bom, yLevelTresholdBottom, yLevelTresholdTop)));

                    }
                } else {
                    return blocks;
                }
            }
        }
        return blocks;
    }

    public static boolean isValidBlock(IPlayerContext ctx, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (ctx.world().getBlockState(pos.offset(direction)).getMaterial().isLiquid()) return false;
        }
        return true;
    }

    @Nullable
    public static BlockPos getNearest(IPlayerContext ctx, List<BlockPos> pos) {
        if (pos.size() > 0) {
            int min = 0;
            for (int i = 1; i < pos.size(); i++) {
                if (pos.get(i).getSquaredDistance(ctx.player().getBlockPos()) < pos.get(min).getSquaredDistance(ctx.player().getBlockPos()))
                    min = i;
            }
            return pos.get(min);
        } else {
            return null;
        }
    }

    public static Entity getSpawnerEntity(IPlayerContext ctx, BlockPos pos) {
        BlockEntity be = ctx.world().getBlockEntity(pos);
        if (be instanceof MobSpawnerBlockEntity) {
            return ((MobSpawnerBlockEntity) be).getLogic().getRenderedEntity();
        }
        return null;
    }

    public static int getAirBlocks(IPlayerContext ctx, BlockPos pos) {
        for (int i = 0; i < pos.getY(); i++) {
            if (ctx.world().getBlockState(pos.offset(Direction.DOWN, i)).getMaterial().blocksMovement()) {
                return i - 1;
            }
        }
        return -1;
    }

    public static List<BlockPos> filterFluids(IPlayerContext ctx, List<BlockPos> pos) {
        List<BlockPos> fPos = new ArrayList<>();
        for (BlockPos p : pos) {
            if (ctx.world().getBlockState(p).get(FluidBlock.LEVEL) == 0) fPos.add(p);
        }
        return fPos;
    }

    public static Dimension getDimension(World world) {
        if (world.getDimension().isUltrawarm()) {
            return Dimension.NETHER;
        }
        if (world.getDimension().hasEnderDragonFight()) {
            return Dimension.END;
        }
        return Dimension.OVERWORLD;
    }
}
