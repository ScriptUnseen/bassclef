package scriptunseen.bassclef.mixin;

import scriptunseen.bassclef.tasks.frames.FrameTaskManager;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GameRenderer.class)
public class RenderMixin {
    @Inject(at = @At("HEAD"), method = "render")
    private void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        FrameTaskManager.tick(tickDelta, startTime, tick);
    }
}
