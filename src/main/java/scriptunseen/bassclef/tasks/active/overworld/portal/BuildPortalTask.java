package scriptunseen.bassclef.tasks.active.overworld.portal;

import baritone.api.utils.BlockOptionalMetaLookup;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.ActiveTask;
import scriptunseen.bassclef.tasks.active.building.BuildTask;
import scriptunseen.bassclef.tasks.active.overworld.portal.utils.PortalFluidSchematic;
import scriptunseen.bassclef.tasks.active.overworld.portal.utils.PortalSchematic;
import scriptunseen.bassclef.utils.helper.ChatHelper;
import scriptunseen.bassclef.tasks.active.building.utils.Lake;
import scriptunseen.bassclef.utils.helper.WorldScanner;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class BuildPortalTask extends ActiveTask {

    @Override
    public boolean activeTaskTick() {
        if (!ctx.world().getDimension().isUltrawarm()) {
            List<BlockPos> lava = WorldScanner.filterFluids(ctx, WorldScanner.scanChunkRadius(ctx, new BlockOptionalMetaLookup("lava"), 4, 100, 10, 1000));
            BlockPos n1;
            for (; (n1 = WorldScanner.getNearest(ctx, lava)) != null; lava.remove(n1)) {
                Lake lake = Lake.getLake(lava, n1);
                if (lake.pos.size() >= 10) {
                    BlockPos stand = lake.getSouthest().add(0, 0, 1);
                    ChatHelper.displayChatMessage("started");
                    runSubTask(new BuildTask(new BlockPos(stand.getX(), Math.max(stand.getY() - 1, 9), stand.getZ()), new PortalSchematic(), new PortalFluidSchematic()));
                    return true;
                } else {
                    ChatHelper.displayChatMessage("Not enough lava!", true);
                }
            }
            ChatHelper.displayChatMessage("Could not find valid lava lake!", true);
            fail();
        } else {
            taskState = TaskState.SUCCESS;
        }
        return true;
    }
}
