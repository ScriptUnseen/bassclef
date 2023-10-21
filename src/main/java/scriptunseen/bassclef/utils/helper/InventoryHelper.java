package scriptunseen.bassclef.utils.helper;

import baritone.api.BaritoneAPI;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.input.Input;
import com.google.common.collect.Multimap;
import scriptunseen.bassclef.Main;
import scriptunseen.bassclef.tasks.active.getitem.utils.SmeltItem;
import scriptunseen.bassclef.tasks.active.getitem.utils.Tag;
import scriptunseen.bassclef.tasks.active.getitem.utils.Thing;
import scriptunseen.bassclef.tasks.passive.RunAwayTask;
import scriptunseen.bassclef.tasks.passive.SortInvTask;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface InventoryHelper {

    static boolean equipWeapon(IPlayerContext ctx, boolean checkOnly) {
        return equipWeapon(ctx, checkOnly, false);
    }

    static boolean equipWeapon(IPlayerContext ctx, boolean checkOnly, boolean noSword) {
        int max = -1;
        for (int i = 0; i < ctx.player().inventory.size(); i++) {
            ItemStack item = ctx.player().inventory.getStack(i);
            if ((noSword && item.getItem() instanceof MiningToolItem) || (!noSword && (item.getItem() instanceof SwordItem || item.getItem() instanceof MiningToolItem))) {
                double damage = getAttackDamagePerSek(ctx, item);
                if (damage > 0) {
                    if (max == -1 || damage > getAttackDamagePerSek(ctx, ctx.player().inventory.getStack(max))) {
                        max = i;
                    }
                }
            }
        }
        if (max == -1) {
            System.out.println("cant find item to fight");
            return false;
        } else {
            Item item = ctx.player().inventory.getStack(max).getItem();
            return checkOnly ? ctx.player().inventory.getMainHandStack().getItem().equals(item) : equip(ctx, Registry.ITEM.getRawId(item), item instanceof SwordItem ? SortInvTask.SWORD : SortInvTask.AXE, true);
        }
    }

    static double getAttackDamagePerSek(IPlayerContext ctx, ItemStack item) {
        final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
        final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

        double damage = 0;
        double speed = 0;
        Multimap<EntityAttribute, EntityAttributeModifier> multimap = item.getAttributeModifiers(EquipmentSlot.MAINHAND);
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entityAttributeEntityAttributeModifierEntry : multimap.entries()) {
            EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier) ((Map.Entry<?, ?>) entityAttributeEntityAttributeModifierEntry).getValue();
            double d = entityAttributeModifier.getValue();
            if (ctx.player() != null) {
                if (entityAttributeModifier.getId().equals(ATTACK_DAMAGE_MODIFIER_ID)) {
                    d += ctx.player().getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    d += EnchantmentHelper.getAttackDamage(item, EntityGroup.DEFAULT);
                    damage = d;
                } else if (entityAttributeModifier.getId().equals(ATTACK_SPEED_MODIFIER_ID)) {
                    d += ctx.player().getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
                    speed = d;
                    break;
                }
            }
        }
        return damage * speed;
    }

    static boolean equip(IPlayerContext ctx, int id, int slot, boolean select) {
        if (ctx.player().currentScreenHandler.syncId != 0) {
            ChatHelper.displayChatMessage("Bot is currently in another screen", true);
            return false;
        }

        int slot2 = -1;
        for (int i = 0; i < ctx.player().inventory.size(); i++) {
            if (id > 0) {
                if (Registry.ITEM.getRawId(ctx.player().inventory.getStack(i).getItem()) == id) {
                    slot2 = i;
                    break;
                }
            } else {
                if (-id < Tag.TAGS.length) {
                    int finalI = i;
                    if (Arrays.stream(Tag.TAGS[-id].items).anyMatch(identifier -> Registry.ITEM.get(identifier).equals(ctx.player().inventory.getStack(finalI).getItem()))) {
                        slot2 = i;
                        break;
                    }
                }
            }
        }

        if (slot2 == -1) {
            return false;
        }

        if (slot2 < 9) {
            if (select) {
                ctx.player().inventory.selectedSlot = slot2;
                ctx.player().inventory.updateItems();
                return true;
            }
            slot2 += 36;
        }

        clickSlot(ctx, slot2, slot, SlotActionType.SWAP);
        if (select) {
            ctx.player().inventory.selectedSlot = slot;
        }
        ctx.player().inventory.updateItems();
        return true;
    }

    static void clickSlot(IPlayerContext ctx, int slotId, int clickData, SlotActionType slotActionType) {
        ctx.playerController().windowClick(ctx.player().currentScreenHandler.syncId, slotId, clickData, slotActionType, ctx.player());
    }

    static boolean isSaveToEquip(IPlayerContext ctx) {
        return !ctx.player().isUsingItem() && !Main.baritone.getInputOverrideHandler().isInputForcedDown(Input.CLICK_RIGHT) && !InputHandler.Input.RIGHT_CLICK.isPressed() && RunAwayTask.getFireRes() == 0;
    }

    static boolean isSelected(IPlayerContext ctx, int id) {
        int id2 = Registry.ITEM.getRawId(ctx.player().inventory.getMainHandStack().getItem());
        if (id > 0) {
            return id2 == id;
        } else {
            for (Identifier identifier : Tag.TAGS[-1 * id].items) {
                if (Registry.ITEM.get(identifier).equals(ctx.player().inventory.getMainHandStack().getItem())) {
                    return true;
                }
            }
            return false;
        }
    }

    static boolean hasGoldArmor(IPlayerContext ctx) {
        for (ItemStack itemStack : ctx.player().getArmorItems()) {
            if (Registry.ITEM.getId(itemStack.getItem()).toString().startsWith("minecraft:golden")) {
                return true;
            }
        }
        return false;
    }

    static int countHunger(boolean onlyCooked) {
        int hunger = 0;
        Inventory inv = new Inventory();
        for (int i = 0; i < inv.size(); i++) {
            Thing thing = inv.getThing(i);
            if (thing != null) {
                if (!onlyCooked || SmeltItem.getSmeltItemFromInp(thing.getId()) == null) {
                    FoodComponent fc = Registry.ITEM.get(thing.getId()).getFoodComponent();
                    if (fc != null) {
                        hunger += fc.getHunger() * thing.getCount();
                    }
                }
            }
        }
        return hunger;
    }

    static int freeSlots(IPlayerContext ctx) {
        return (int) ctx.player().inventory.main.stream().filter(ItemStack::isEmpty).count();
    }

    static boolean contains(IPlayerContext ctx, Thing thing) {
        int count = 0;
        PlayerInventory inv = ctx.player().inventory;
        if (thing.getId() > 0) {
            Item item = Item.byRawId(thing.getId());
            for (int i = 0; i < inv.size(); i++) {
                if (item == inv.getStack(i).getItem()) {
                    count += inv.getStack(i).getCount();
                }
            }
        } else {
            for (int i = 0; i < inv.size(); i++) {
                for (Identifier item : Tag.TAGS[thing.getId()].items) {
                    if (Registry.ITEM.get(item).equals(inv.getStack(i).getItem())) {
                        count += inv.getStack(i).getCount();
                    }
                }
            }
        }
        return count > thing.getCount();
    }

    static int getBlocksToPlaceCount(IPlayerContext ctx) {
        int count = 0;
        for (ItemStack stack : ctx.player().inventory.main) {
            if (BaritoneAPI.getSettings().acceptableThrowawayItems.value.contains(stack.getItem())) {
                count += stack.getCount();
            }
        }
        return count;
    }

    static int getFireRes(IPlayerContext ctx) {
        int res = 0;
        for (ItemStack stack : ctx.player().inventory.main) {
            if (stack.getItem() instanceof PotionItem) {
                if (isFireRes(stack)) {
                    if (stack.getItem() instanceof SplashPotionItem) {
                        return 1;
                    } else {
                        res = 2;
                    }
                }
            }
        }
        return res;
    }

    static boolean isFireRes(ItemStack stack) {
        List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(stack);
        for (StatusEffectInstance instance : effects) {
            if (instance.getEffectType().equals(StatusEffects.FIRE_RESISTANCE)) {
                return true;
            }
        }
        return false;
    }

    static int getID(String id) {
        return Registry.ITEM.getRawId(Registry.ITEM.get(new Identifier(id)));
    }

}
