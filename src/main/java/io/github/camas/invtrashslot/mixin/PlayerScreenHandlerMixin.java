package io.github.camas.invtrashslot.mixin;

import io.github.camas.invtrashslot.ITrashSlot;
import io.github.camas.invtrashslot.InvTrashSlot;
import io.github.camas.invtrashslot.SurvivalOnlySlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class PlayerScreenHandlerMixin extends ScreenHandler {

    public PlayerScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    /**
     * Adds the trash slot to the inventory screen
     */
    @Inject(method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V", at = @At("RETURN"))
    private void InvTrashSlot$constructor(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo callbackInfo) {
        // Add a gui slot containing the trash slot inventory
        Inventory inv = ((ITrashSlot) inventory.player).getTrashSlot().getInventory();
        addSlot(new SurvivalOnlySlot(inv, 0, InvTrashSlot.SLOT_X, InvTrashSlot.SLOT_Y));
    }
}
