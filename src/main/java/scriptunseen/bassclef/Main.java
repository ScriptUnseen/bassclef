package scriptunseen.bassclef;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import scriptunseen.bassclef.commandsystem.CommandRegistry;
import scriptunseen.bassclef.commandsystem.commands.*;
import scriptunseen.bassclef.commandsystem.commands.*;
import scriptunseen.bassclef.utils.Positions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;


@Environment(EnvType.CLIENT)
public class Main implements ClientModInitializer {

    public static final String NAME = "bassclef";
    public static MinecraftClient client;
    public static Baritone baritone;
    public static Positions positions = new Positions();

    public static Baritone baritone() {
        return (Baritone) BaritoneAPI.getProvider().getBaritoneForPlayer(client.player);
    }

    public static ClientPlayerInteractionManager getInteractionManager() {
        return client.interactionManager;
    }

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        initCommands();

    }

    private void initCommands() {
        CommandRegistry.addCommand("run", new SpeedrunCommand());
        CommandRegistry.addCommand("get", new GetItemCommand());
        CommandRegistry.addCommand("build", new BuildPortalCommand());
        CommandRegistry.addCommand("food", new GetFoodCommand());
        CommandRegistry.addCommand("cancel", new CancelTaskCommand());
        CommandRegistry.addCommand("tasktree", new TaskTreeCommand());
        CommandRegistry.addCommand("t1", new TestCommand1());
        CommandRegistry.addCommand("t2", new TestCommand2());
        CommandRegistry.addCommand("t3", new TestCommand3());
        CommandRegistry.addCommand("passive", new ActivatePassiveTasksCommand());
    }
}