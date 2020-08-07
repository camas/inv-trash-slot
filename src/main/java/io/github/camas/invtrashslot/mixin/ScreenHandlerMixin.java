package io.github.camas.invtrashslot.mixin;

import io.github.camas.invtrashslot.TrashSlotInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Shadow
    public List<Slot> slots;

    @Shadow
    public abstract ItemStack transferSlot(PlayerEntity player, int index);

    @Inject(method = "onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true)
    private void slotClicked(int i, int j, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> callbackInfo) {
        // Return early if not a real index
        if (i < 0) {
            return;
        }

        // Get slot from ScreenHandler.slots
        Slot slot = slots.get(i);
        // Check slot is a trash slot
        if (slot.inventory instanceof TrashSlotInventory) {
            ItemStack toReturn = slot.getStack().copy();
            // Get item under mouse cursor
            ItemStack cursorItem = playerEntity.inventory.getCursorStack();
            // Return if nothing under mouse or slot empty
            if (cursorItem.isEmpty()) {
                return;
            }

            if (j == 1) {
                // Right click
                ItemStack toTrash = cursorItem.split(1);
                if (ScreenHandler.canStacksCombine(toTrash, slot.getStack())) {
                    int newCount = slot.getStack().getCount() + 1;
                    slot.getStack().setCount(Math.min(newCount, slot.getStack().getMaxCount()));
                } else {
                    slot.setStack(toTrash);
                }
            } else {
                // Assume left click
                // Set trash slot to cursor item and set cursor item to empty
                if (ScreenHandler.canStacksCombine(cursorItem, slot.getStack())) {
                    int newCount = slot.getStack().getCount() + cursorItem.getCount();
                    slot.getStack().setCount(Math.min(newCount, slot.getStack().getMaxCount()));
                } else {
                    slot.setStack(cursorItem);
                }
                playerEntity.inventory.setCursorStack(ItemStack.EMPTY);
            }
            // Return injected method early
            callbackInfo.setReturnValue(toReturn);
        }
    }
}
