package scriptunseen.bassclef.tasks.active;

import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalNear;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.TaskManager;
import scriptunseen.bassclef.tasks.TaskState;
import scriptunseen.bassclef.tasks.active.end.CheerTask;
import scriptunseen.bassclef.tasks.active.end.PrepareOneCycleTask;
import scriptunseen.bassclef.tasks.active.getitem.GetItemTask;
import scriptunseen.bassclef.tasks.active.getitem.utils.ItemTexture;
import scriptunseen.bassclef.tasks.active.getitem.utils.Tag;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.tasks.active.nether.FindFortressTask;
import scriptunseen.bassclef.tasks.active.nether.PiglinTradeTask;
import scriptunseen.bassclef.tasks.active.overworld.GetBedsTask;
import scriptunseen.bassclef.tasks.active.overworld.SearchPortalTask;
import scriptunseen.bassclef.tasks.active.overworld.portal.BuildPortalTask;
import scriptunseen.bassclef.tasks.active.util.ShowTimeTask;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import scriptunseen.bassclef.utils.*;
import scriptunseen.bassclef.utils.BaritoneSettings;
import scriptunseen.bassclef.utils.Positions;
import scriptunseen.bassclef.utils.helper.ChatHelper;
import scriptunseen.bassclef.utils.helper.InteractionHelper;
import scriptunseen.bassclef.utils.helper.Inventory;
import scriptunseen.bassclef.utils.helper.InventoryHelper;
import net.minecraft.block.Material;

public class SpeedrunTask extends TaskQueue {

    public SpeedrunTask() {
        this(4);
    }

    public SpeedrunTask(int start) {
        Main.positions = new Positions();

        // default speedrun Settings
        BaritoneSettings.setting();
        BaritoneSettings.removeCobbleStoneFromThrowAway();

        SortInvTask.initToKeep();
        TaskManager.cancelEverything();

        switch (start) {
            case 4:
                doFirstOverworldPart();
            case 3:
                doNetherPart();
            case 2:
                doSecondOverworldPart();
            case 1:
                doEndPart();
        }
    }

    private void doFirstOverworldPart() {
        add(new GetItemTask(new ItemTexture(Tag.LOGS, 3), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:wooden_pickaxe"), 1), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:stone_pickaxe"), 1), false));
        // Get coal first to automatically farm cobblestone
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:coal"), 2), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:cobblestone"), 8 + 3 + 2), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:stone_hoe"), 1), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:stone_axe"), 1), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:furnace"), 1), true));
        // Get spare wood
        add(new GetItemTask(new ItemTexture(Tag.LOGS, 8), false));
        // add Cobblestone because its no longer needed for crafting
        add(new ActiveTask() {
            @Override
            public boolean activeTaskTick() {
                BaritoneSettings.addCobbleStoneFromThrowAway();
                taskState = TaskState.SUCCESS;
                return true;
            }
        });

        add(new GetFoodTask(230)); // around 230/6 bread

        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:iron_ingot"), 3 + 3 + 1), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:iron_pickaxe"), 1), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:water_bucket"), 1), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:diamond_pickaxe"), 1), true));

        add(new GetArmorTask());
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:diamond"), 2 + 1 + 3), true));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:diamond_sword"), 1), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:diamond_shovel"), 1), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:diamond_pickaxe"), 1), true));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:flint_and_steel"), 1), false));

        add(new RemoveItemsToKeepTask("minecraft:flint", "minecraft:diamond"));

        add(new BuildPortalTask());
    }

    private void doNetherPart() {
        add(new ActiveTask() {
            @Override
            public boolean activeTaskTick() {
                Main.positions.portal = ctx.playerFeet();
                taskState = TaskState.SUCCESS;
                return false;
            }
        });

        add(new RemoveItemsToKeepTask("minecraft:flint_and_steel"));

        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:golden_boots"), 1), true));
        add(new PutArmorPieceOnTask(InventoryHelper.getID("minecraft:golden_boots")));
        add(new PiglinTradeTask(new Thing(InventoryHelper.getID("minecraft:ender_pearl"), 13)));

        add(new PutArmorPieceOnTask(InventoryHelper.getID("minecraft:diamond_boots")));

        // Craft string into wool
        add(new ActiveTask() {
            @Override
            public boolean activeTaskTick() {
                int count = (new Inventory()).getCount(InventoryHelper.getID("minecraft:string"));
                if (count / 4 > 0) {
                    runSubTask(new GetItemTask(new ItemTexture(Tag.WOOL, count / 4), true));
                } else {
                    taskState = TaskState.SUCCESS;
                }
                return false;
            }

            @Override
            public boolean cancelSubTask() {
                return getRunTime() % 20 == 0 && (new Inventory()).getCount(InventoryHelper.getID("minecraft:string")) / 4 == 0;
            }
        });

        add(new RemoveItemsToKeepTask("minecraft:string", "minecraft:golden_boots", "minecraft:gold_ingot", "minecraft:gold_nugget"));

        add(new FindFortressTask());

        add(new ActiveTask() {
            @Override
            public boolean activeTaskTick() {
                Goal goal = new GoalNear(Main.positions.portal, 50);
                if (goal.isInGoal(Main.baritone.getPlayerContext().playerFeet())) {
                    taskState = TaskState.SUCCESS;
                } else if (!Main.baritone.getCustomGoalProcess().isActive()) {
                    Main.baritone.getCustomGoalProcess().setGoalAndPath(goal);
                }
                return true;
            }
        });

        // leave Nether
        add(new ActiveTask() {
            @Override
            public boolean activeTaskTick() {
                if (Main.baritone.getPlayerContext().world().getDimension().isBedWorking()) {
                    taskState = TaskState.SUCCESS;
                } else if (!(Main.baritone.getCustomGoalProcess().getGoal() instanceof GoalBlock && (((GoalBlock) Main.baritone.getCustomGoalProcess().getGoal()).getGoalPos().equals(Main.positions.portal))) && !Main.baritone.getPlayerContext().playerFeet().equals(Main.positions.portal)) {
                    ChatHelper.displayChatMessage("Leaving Nether!");
                    Main.baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(Main.positions.portal));
                }
                return true;
            }
        });
    }

    private void doSecondOverworldPart() {
        // destroy portal to get obi
        add(new ActiveTask() {
            @Override
            public boolean activeTaskTick() {
                if (ctx.world().getBlockState(ctx.playerFeet()).getMaterial().equals(Material.PORTAL)) {
                    InventoryHelper.equip(ctx, InventoryHelper.getID("minecraft:water_bucket"), SortInvTask.BUCKET, true);
                    if (ctx.player().pitch == 90) {
                        InteractionHelper.interactItem(ctx);
                    } else {
                        ctx.player().pitch = 90;
                    }
                } else if (!new Inventory().contains(new Thing(InventoryHelper.getID("minecraft:water_bucket"), 1))) {
                    runSubTask(new FillBucketTask(InventoryHelper.getID("minecraft:water_bucket")));
                } else {
                    taskState = TaskState.SUCCESS;
                }
                return false;
            }
        });

        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:obsidian"), 1), false));
        add(new GetItemTask(new ItemTexture(InventoryHelper.getID("minecraft:shears"), 1), false));

        add(new RemoveItemsToKeepTask("minecraft:iron_ore"));

        add(new GetFoodTask(100));

        add(new GetBedsTask(6));
    }

    private void doEndPart() {
        add(new SearchPortalTask());
        add(new PrepareOneCycleTask());
        add(new CheerTask());
        add(new ShowTimeTask(this));
    }
}
