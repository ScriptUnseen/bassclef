package scriptunseen.bassclef.tasks.active;

import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMetaLookup;
import scriptunseen.bassclef.utils.helper.WorldScanner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public abstract class GetSthTask extends ActiveTask {
    public final List<BlockPos> blockPos;
    public final List<ChunkPos> chunkPos;
    public BetterBlockPos start;

    public GetSthTask() {
        this.blockPos = new ArrayList<>();
        this.chunkPos = new ArrayList<>();
    }

    public List<BlockPos> scan(BlockOptionalMetaLookup boml) {
        return scan(boml, 1, 256);
    }

    public List<BlockPos> scan(BlockOptionalMetaLookup boml, int yLevelTresholdBottom, int yLevelTresholdTop) {
        for (BlockPos pos : WorldScanner.scanAllChunks(ctx, boml, yLevelTresholdBottom, yLevelTresholdTop, chunkPos)) {
            if (WorldScanner.isValidBlock(ctx, pos)) {
                blockPos.add(pos);
            }
        }
        return blockPos;
    }
}
