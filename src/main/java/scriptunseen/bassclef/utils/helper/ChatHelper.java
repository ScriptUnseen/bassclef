package scriptunseen.bassclef.utils.helper;

import scriptunseen.bassclef.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ChatHelper {
    Map<Long, List<Text>> messages = new HashMap<>();

    static void displayChatMessage(String message) {
        displayChatMessage(Main.NAME, message, false);
    }

    static void displayChatMessage(String message, boolean warning) {
        displayChatMessage(Main.NAME, message, warning);
    }

    static void displayChatMessage(String sender, String message, boolean warning) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText("§2[" + sender + "] " + (warning ? "§c" : "§f") + message));
    }

    static Map<Long, List<Text>> getChat() {
        return messages;
    }

    static void addMessage(Text message) {
        messages.computeIfAbsent(System.currentTimeMillis(), k -> new ArrayList<>()).add(message);
    }
}
