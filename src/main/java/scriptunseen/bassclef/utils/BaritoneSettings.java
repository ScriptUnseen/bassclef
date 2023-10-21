package scriptunseen.bassclef.utils;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BaritoneSettings {
    public static void setting() {
        Settings s = BaritoneAPI.getSettings();
        s.allowInventory.value = true;
        s.allowParkour.value = true;
        s.freeLook.value = false;
        s.avoidance.value = true;
        s.maxFallHeightBucket.value = 256;
        s.blockReachDistance.reset();
        s.allowBreak.reset();
        s.allowPlace.reset();
        s.sprintInWater.reset();
        s.walkWhileBreaking.reset();
        s.randomLooking.reset();
        s.blockPlacementPenalty.reset();
        s.blocksToAvoid.reset();
        s.mobAvoidanceCoefficient.reset();
        s.mobAvoidanceRadius.reset();
        s.blocksToAvoid.value.addAll(Arrays.asList(Blocks.VINE, Blocks.SWEET_BERRY_BUSH));
        s.blocksToAvoidBreaking.value = new ArrayList<>();
        s.canWalkOn.value = new ArrayList<>(Collections.singletonList(Blocks.SOUL_SAND));
        s.acceptableThrowawayItems.reset();
        s.acceptableThrowawayItems.value.add(Registry.ITEM.get(new Identifier("minecraft:andesite")));
        s.acceptableThrowawayItems.value.add(Registry.ITEM.get(new Identifier("minecraft:diorite")));
        s.acceptableThrowawayItems.value.add(Registry.ITEM.get(new Identifier("minecraft:granite")));
        s.acceptableThrowawayItems.value.add(Registry.ITEM.get(new Identifier("minecraft:nether_bricks")));
        s.acceptableThrowawayItems.value.add(Registry.ITEM.get(new Identifier("minecraft:nether_brick_fence")));
    }

    public static void removeCobbleStoneFromThrowAway() {
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.remove(Registry.ITEM.get(new Identifier("minecraft:cobblestone")));
    }

    public static void addCobbleStoneFromThrowAway() {
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.add(Registry.ITEM.get(new Identifier("minecraft:cobblestone")));
    }

    public static void startEating() {
        BaritoneAPI.getSettings().allowPlace.value = false;
        BaritoneAPI.getSettings().allowParkour.value = false;
    }

    public static void stopEating() {
        BaritoneAPI.getSettings().allowPlace.value = true;
        BaritoneAPI.getSettings().allowParkour.value = true;
    }

    public static void enterEnd() {
        BaritoneAPI.getSettings().canWalkOn.value.add(Blocks.END_PORTAL_FRAME);
        BaritoneAPI.getSettings().canWalkOn.value.add(Blocks.END_PORTAL);
    }

    public static void startBuilding() {
        BaritoneAPI.getSettings().allowParkour.value = false;
    }

    public static void stopBuilding() {
        BaritoneAPI.getSettings().allowParkour.value = true;
    }
}
