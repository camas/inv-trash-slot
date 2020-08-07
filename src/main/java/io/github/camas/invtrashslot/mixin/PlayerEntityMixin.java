package io.github.camas.invtrashslot.mixin;

import io.github.camas.invtrashslot.ITrashSlot;
import io.github.camas.invtrashslot.PlayerTrashSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixins for PlayerEntity
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements ITrashSlot {
    // Adds a trash slot to every player entity on creation
    PlayerTrashSlot InvTrashSlot$playerTrashSlot = new PlayerTrashSlot();

    /**
     * Get the player's trash slot
     */
    @NotNull
    @Override
    public PlayerTrashSlot getTrashSlot() {
        return InvTrashSlot$playerTrashSlot;
    }

    /**
     * Reads trash slot data after other player data has been read
     */
    @Inject(method = "readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void InvTrashSlot$readCustomDataFromTag(CompoundTag tag, CallbackInfo info) {
        getTrashSlot().readFromTag(tag);
    }

    /**
     * Writes trash slot data after other player data has been written
     */
    @Inject(method = "writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void InvTrashSlot$writeCustomDataToTag(CompoundTag tag, CallbackInfo info) {
        getTrashSlot().writeToTag(tag);
    }

    /**
     * Empties the trash slot on death
     */
    @Inject(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("TAIL"))
    private void InvTrashSlot$onDeath(DamageSource source, CallbackInfo callback) {
        this.InvTrashSlot$playerTrashSlot.getInventory().setStack(0, ItemStack.EMPTY);
    }
}
