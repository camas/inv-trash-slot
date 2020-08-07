package io.github.camas.invtrashslot.mixin;

import io.github.camas.invtrashslot.InvTrashSlot;
import io.github.camas.invtrashslot.InvTrashSlotClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
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

    /**
     * Deletes item when delete button pressed
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // From GameRenderer
        int mouseX = (int) (this.client.mouse.getX() * (double) this.client.getWindow().getScaledWidth() / (double) this.client.getWindow().getWidth());
        int mouseY = (int) (this.client.mouse.getY() * (double) this.client.getWindow().getScaledHeight() / (double) this.client.getWindow().getHeight());
        if (InvTrashSlotClient.deleteKeybind.matchesKey(keyCode, scanCode)) {
            for (Slot slot : this.handler.slots) {
                if (isPointWithinBounds(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                    if (slot.getStack().isEmpty()) {
                        break;
                    }
                    PacketByteBuf packetData = new PacketByteBuf(Unpooled.buffer());
                    packetData.writeInt(slot.id);
                    ClientSidePacketRegistry.INSTANCE.sendToServer(InvTrashSlot.TRASH_SLOT_PACKET_ID, packetData);
                    slot.setStack(ItemStack.EMPTY);
                    break;
                }
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
