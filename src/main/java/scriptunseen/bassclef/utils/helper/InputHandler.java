package scriptunseen.bassclef.utils.helper;

import scriptunseen.bassclef.Main;
import net.minecraft.client.options.KeyBinding;

public class InputHandler {

    public static void clearKeys() {
        for (Input input : Input.values()) {
            if (input.pressed) {
                if (input == Input.LEFT_CLICK) {
                    Main.baritone.getInputOverrideHandler().clearAllKeys();
                }
                input.pressed = false;
                input.key.setPressed(false);
            }
        }
    }

    public static void pressKeys() {
        for (Input input : Input.values()) {
            if (input.pressed) {
                if (input == Input.LEFT_CLICK) {
                    Main.baritone.getInputOverrideHandler().setInputForceState(baritone.api.utils.input.Input.CLICK_LEFT, true);
                } /*else if (input == Input.RIGHT_CLICK) {
                    Main.client.interactionManager.interactBlock()
                }*/ else {
                    input.key.setPressed(true);
                }
            }
        }
    }

    public static void press(Input input) {
        input.pressed = true;
    }

    public enum Input {
        RIGHT_CLICK(Main.client.options.keyUse),
        LEFT_CLICK(Main.client.options.keyAttack),
        MOVE_RIGHT(Main.client.options.keyRight),
        MOVE_LEFT(Main.client.options.keyLeft),
        MOVE_FORWARD(Main.client.options.keyForward),
        MOVE_BACKWARD(Main.client.options.keyBack),
        SPRINT(Main.client.options.keySprint),
        SNEAK(Main.client.options.keySneak),
        JUMP(Main.client.options.keyJump);

        private final KeyBinding key;
        private boolean pressed;

        Input(KeyBinding key) {
            this.pressed = false;
            this.key = key;
        }

        public boolean isPressed() {
            return pressed;
        }

    }
}
