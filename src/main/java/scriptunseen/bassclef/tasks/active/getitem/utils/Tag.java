package scriptunseen.bassclef.tasks.active.getitem.utils;


import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class Tag {
    public static final int LOGS = -0;
    public static final int PLANKS = -1;
    public static final int BEDS = -2;
    public static final int WOOL = -3;
    public static final Tag[] TAGS = new Tag[]{
            new Tag(
                    new Identifier[]{new Identifier("minecraft:oak_log"), new Identifier("spruce_log"), new Identifier("minecraft:birch_log"), new Identifier("minecraft:jungle_log"), new Identifier("minecraft:acacia_log"), new Identifier("minecraft:dark_oak_log"), new Identifier("minecraft:crimson_stem"), new Identifier("minecraft:warped_stem")}
            ),
            new Tag(
                    new Identifier[]{new Identifier("minecraft:oak_planks"), new Identifier("spruce_planks"), new Identifier("minecraft:birch_planks"), new Identifier("minecraft:jungle_planks"), new Identifier("minecraft:acacia_planks"), new Identifier("minecraft:dark_oak_planks"), new Identifier("minecraft:crimson_planks"), new Identifier("minecraft:warped_planks")}
            ),
            new Tag(
                    new Identifier[]{new Identifier("minecraft:white_bed"), new Identifier("minecraft:orange_bed"), new Identifier("minecraft:magenta_bed"), new Identifier("minecraft:light_blue_bed"), new Identifier("minecraft:yellow_bed"), new Identifier("minecraft:lime_bed"), new Identifier("minecraft:pink_bed"), new Identifier("minecraft:gray_bed"), new Identifier("minecraft:light_gray_bed"), new Identifier("minecraft:cyan_bed"), new Identifier("minecraft:purple_bed"), new Identifier("minecraft:blue_bed"), new Identifier("minecraft:brown_bed"), new Identifier("minecraft:green_bed"), new Identifier("minecraft:red_bed"), new Identifier("minecraft:black_bed")}
            ),
            new Tag(
                    new Identifier[]{new Identifier("minecraft:white_wool"), new Identifier("minecraft:orange_wool"), new Identifier("minecraft:magenta_wool"), new Identifier("minecraft:light_blue_wool"), new Identifier("minecraft:yellow_wool"), new Identifier("minecraft:lime_wool"), new Identifier("minecraft:pink_wool"), new Identifier("minecraft:gray_wool"), new Identifier("minecraft:light_gray_wool"), new Identifier("minecraft:cyan_wool"), new Identifier("minecraft:purple_wool"), new Identifier("minecraft:blue_wool"), new Identifier("minecraft:brown_wool"), new Identifier("minecraft:green_wool"), new Identifier("minecraft:red_wool"), new Identifier("minecraft:black_wool")}
            )
    };
    public Identifier[] items;

    public Tag(Identifier[] items) {
        this.items = items;
    }

    public static List<Block> getBlocks(int tag) {
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < Tag.TAGS[tag * -1].items.length; i++) {
            blocks.add(Registry.BLOCK.get(Tag.TAGS[tag * -1].items[i]));
        }
        return blocks;
    }
}
