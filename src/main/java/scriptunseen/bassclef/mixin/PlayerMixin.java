package scriptunseen.bassclef.mixin;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.commandsystem.CommandRegistry;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.utils.helper.InputHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class PlayerMixin {
    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    private void sendChatMessage(String message, CallbackInfo ci) {
        if (CommandRegistry.runCommand(message)) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci) {
    }

    private void tasks() {
        InputHandler.clearKeys();
        TaskManager.tick();
        InputHandler.pressKeys();
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(MinecraftClient minecraftClient, ClientWorld clientWorld, ClientPlayNetworkHandler clientPlayNetworkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean bl, boolean bl2, CallbackInfo ci) {
        Main.baritone = (Baritone) BaritoneAPI.getProvider().getBaritoneForPlayer(MinecraftClient.getInstance().player);
        TaskManager.initPassives();
    }
}
