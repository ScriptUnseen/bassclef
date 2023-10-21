package scriptunseen.bassclef.tasks.active.nether;

import baritone.api.pathing.goals.GoalInverted;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.BlockOptionalMetaLookup;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.utils.helper.ChatHelper;
import scriptunseen.bassclef.utils.helper.WorldScanner;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class FindFortressTask extends ActiveTask {

    private final BetterBlockPos origin;
    private final List<ChunkPos> chunkPosFort;
    private final List<ChunkPos> chunkPosSpawner;
    private boolean foundSpawner;
    private BlockPos fort;

    public FindFortressTask() {
        this.fort = null;
        this.foundSpawner = false;
        this.origin = ctx.playerFeet();
        this.chunkPosFort = new ArrayList<>();
        this.chunkPosSpawner = new ArrayList<>();
    }

    @Override
    public boolean activeTaskTick() {
        if (foundSpawner) {
            taskState = TaskState.SUCCESS;
        } else {
            if (fort != null) {
                scanForSpawner();
            } else {
                scanForFortress();
            }
        }
        return true;
    }

    private void scanForSpawner() {
        List<BlockPos> spawner = WorldScanner.scanAllChunks(ctx, new BlockOptionalMetaLookup("spawner"), 50, 90, chunkPosSpawner);
        BlockPos blazeSpawner = blazeSpawner(spawner);
        if (blazeSpawner != null) {
            foundSpawner = true;
            Main.positions.spawner = blazeSpawner;
            ChatHelper.displayChatMessage("Found spawner at " + blazeSpawner.toShortString());
            runSubTask(new GetRodsTask(blazeSpawner));
        } else if (new GoalXZ(fort.getX(), fort.getZ()).isInGoal(ctx.playerFeet())) {
            ChatHelper.displayChatMessage("Cant find Spawner!", true);
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalXZ(origin)));
        } else if (!Main.baritone.getCustomGoalProcess().isActive()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalXZ(fort.getX(), fort.getZ()));
        }
    }

    private void scanForFortress() {
        List<BlockPos> bricks = WorldScanner.scanAllChunks(ctx, new BlockOptionalMetaLookup("nether_bricks"), 50, 80, chunkPosFort);
        if (bricks.size() > 0) {
            fort = WorldScanner.getNearest(ctx, bricks);
            ChatHelper.displayChatMessage("Found fortress!");
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalXZ(fort.getX(), fort.getZ()));
        } else if (!Main.baritone.getCustomGoalProcess().isActive()) {
            Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalInverted(new GoalXZ(origin)));
        }
    }

    private BlockPos blazeSpawner(List<BlockPos> positions) {
        List<BlockPos> spawner = new ArrayList<>();
        for (BlockPos pos : positions) {
            if (WorldScanner.getSpawnerEntity(ctx, pos) instanceof BlazeEntity) spawner.add(pos);
        }
        return WorldScanner.getNearest(ctx, spawner);
    }
}
