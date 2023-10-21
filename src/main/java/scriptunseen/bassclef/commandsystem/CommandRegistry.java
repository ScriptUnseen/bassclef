package scriptunseen.bassclef.commandsystem;

import java.util.HashMap;
import java.util.Map;

public final class CommandRegistry {
    private static final Map<String, ICommand> COMMANDS = new HashMap<>();
    private static String prefix = ".";

    public static boolean runCommand(String message) {
        String[] args = message.split(" ");
        if (args.length > 0) {
            if (args[0].startsWith(prefix)) {
                for (Map.Entry<String, ICommand> entry : COMMANDS.entrySet()) {
                    if (args[0].substring(1).equals(entry.getKey())) {
                        entry.getValue().onCommand(args);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void setPrefix(String s) {
        prefix = s;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static void addCommand(String name, ICommand command) {
        COMMANDS.put(name, command);
    }
}
