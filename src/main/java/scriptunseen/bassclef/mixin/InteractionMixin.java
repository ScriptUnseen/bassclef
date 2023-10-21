package scriptunseen.bassclef.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class InteractionMixin {

    @Inject(at = @At("HEAD"), method = "clickSlot")
    private void clickSlot(int syncId, int slotId, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        //printClick(slotId, clickData, actionType);
    }

    private void printClick(int slotId, int clickData, SlotActionType actionType) {
        System.out.println("-------------------------------------");
        System.out.println("SlotId: " + slotId);
        System.out.println("clickData: " + clickData);
        System.out.println("actiontype: " + actionType.toString());
        System.out.println("-------------------------------------");
    }

    @Inject(at = @At("HEAD"), method = "interactItem")
    private void interactItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        System.out.println("InteractionItem");
    }

    @Inject(at = @At("HEAD"), method = "breakBlock")
    private void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        /*BlockState blockState = Main.client.world.getBlockState(pos);
        if(CollectTableTask.active == 1 && blockState.getBlock().asItem().equals(Registry.ITEM.get(new Identifier("minecraft:crafting_table")))){
            CollectTableTask.active = 2;
        }*/
    }
}
