package scriptunseen.bassclef.mixin;

import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.utils.helper.InputHandler;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ClientMixin {

    @Inject(at = @At("HEAD"), method = "handleInputEvents")
    private void handleInputEvents(CallbackInfo ci) {
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;world:Lnet/minecraft/client/world/ClientWorld;", ordinal = 4, shift = At.Shift.BEFORE), method = "tick")
    private void tick(CallbackInfo ci) {
        tasks();
    }

    private void tasks() {
        if (!Main.client.isPaused()) {
            InputHandler.clearKeys();
            TaskManager.tick();
            InputHandler.pressKeys();
        }
    }
}
