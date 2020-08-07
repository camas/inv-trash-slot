package io.github.camas.invtrashslot.mixin;

import io.github.camas.invtrashslot.ITrashSlot;
import io.github.camas.invtrashslot.PlayerTrashSlot;
import net.minecraft.entity.player.PlayerEntity;
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
    // Try to make name unique
    PlayerTrashSlot InvTrashSlot$playerTrashSlot = new PlayerTrashSlot();

    /**
     * Get the player' trash slot
     */
    @NotNull
    @Override
    public PlayerTrashSlot getTrashSlot() {
        return InvTrashSlot$playerTrashSlot;
    }

    // Reads trash slot data after other player data has been read
    @Inject(method = "readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void readNBT(CompoundTag tag, CallbackInfo info) {
        getTrashSlot().readFromTag(tag);
    }

    // Writes trash slot data after other player data has been written
    @Inject(method = "writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void writeNBT(CompoundTag tag, CallbackInfo info) {
        getTrashSlot().writeToTag(tag);
    }
}
