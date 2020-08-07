package io.github.camas.invtrashslot.mixin;

import io.github.camas.invtrashslot.InvTrashSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {

    private static final Identifier InvTrashSlot$BACKGROUND_TEXTURE = new Identifier(InvTrashSlot.MOD_ID, "textures/trashslotbackground.png");

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    /**
     * Adds trash slot to bounds checking
     */
    @Inject(method = "isClickOutsideBounds(DDIII)Z", at = @At("HEAD"), cancellable = true)
    private void InvTrashSlot$isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button, CallbackInfoReturnable<Boolean> callback) {
        // If within 1 of trash slot return false, ignoring usual bounds checking
        if (isPointWithinBounds(InvTrashSlot.SLOT_X, InvTrashSlot.SLOT_Y, 16, 16, mouseX, mouseY)) {
            callback.setReturnValue(false);
        }
    }

    /**
     * Draws the trash slot background
     */
    @Inject(method = "drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V", at = @At("TAIL"))
    private void InvTrashSlot$drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo callback) {
        this.client.getTextureManager().bindTexture(InvTrashSlot$BACKGROUND_TEXTURE);
        drawTexture(matrices, this.x + InvTrashSlot.SLOT_X - 5, this.y + InvTrashSlot.SLOT_Y - 2, 0, 0, 26, 23, 26, 23);
    }
}
