package scriptunseen.bassclef.tasks.frames;

import baritone.api.utils.IPlayerContext;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.end.OneCycleTask;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class OneCycleFrameTask extends FrameTask {

    public static double m = -1.6;
    public static double c = 7.3;

    private final BlockPos pillar;
    private final OneCycleTask otc;

    public OneCycleFrameTask(OneCycleTask oct, BlockPos pillar) {
        this.otc = oct;
        this.pillar = pillar;
    }

    @Override
    public boolean tick(float tickDelta, long startTime, boolean tick) {
        IPlayerContext ctx = Main.baritone.getPlayerContext();
        Optional<Entity> entity = ctx.entitiesStream().filter(e -> e instanceof EnderDragonEntity).findFirst();
        if (entity.isPresent()) {
            EnderDragonEntity dragon = (EnderDragonEntity) entity.get();
            if (dragon.isAlive()) {
                EnderDragonPart head = dragon.partHead;
                Vec3d pos = new Vec3d(MathHelper.lerp(tickDelta, head.lastRenderX, head.getX()), MathHelper.lerp(tickDelta, head.lastRenderY, head.getY()), MathHelper.lerp(tickDelta, head.lastRenderZ, head.getZ()));
                if (Registry.BLOCK.getId(ctx.world().getBlockState(pillar.offset(Direction.UP)).getBlock()).toString().contains("bed")) {
                    if (pos.getY() < pillar.getY() + 9) {
                        if (getDist(pos, ctx.playerFeet().z) < (otc.isFirstBed() ? shouldExplode(pos) : 1) && otc.getLastBed() > 20) {
                            explode();
                        }
                    }
                }
            }
        }
        return false;
    }

    private double getDist(Vec3d pos, int p) {
        return (pos.x - 0.5) * (pos.x - 0.5) + (pos.z - 0.5 - p / 4.0) * (pos.z - 0.5 - p / 4.0);
    }

    private double shouldExplode(Vec3d pos) {
        double height = getHeight(pos);
        //double m = -1.6;
        //double c = 7.3;
        return height < 7.2 ? m * height + c * -m : 0;
    }

    private void explode() {
        InteractionHelper.interactBlock(ctx); //
        // can't click normal, because the click must be instant
        //clickBlock(pillar.getX(), pillar.getY() + 1, pillar.getZ());
        otc.resetLastBed();
        otc.setFirstBed(false);
    }

    private double getHeight(Vec3d posDragon) {
        return posDragon.getY() - pillar.getY();
    }

    public void clickBlock(int x, int y, int z) {
        ItemStack itemStack = ctx.player().getStackInHand(Hand.MAIN_HAND);
        BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(0, 0, 0), Direction.UP, new BlockPos(x, y, z), true);
        int i = itemStack.getCount();
        if (Main.client.interactionManager != null) {
            ActionResult actionResult2 = Main.client.interactionManager.interactBlock(ctx.player(), ctx.player().clientWorld, Hand.MAIN_HAND, blockHitResult);
            if (actionResult2.isAccepted()) {
                if (actionResult2.shouldSwingHand()) {
                    ctx.player().swingHand(Hand.MAIN_HAND);
                    if (!itemStack.isEmpty() && (itemStack.getCount() != i || Main.client.interactionManager.hasCreativeInventory())) {
                        Main.client.gameRenderer.firstPersonRenderer.resetEquipProgress(Hand.MAIN_HAND);
                    }
                }
            }
        }
    }
}
