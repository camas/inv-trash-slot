package io.github.camas.invtrashslot.mixin;

import io.github.camas.invtrashslot.InvTrashSlot;
import io.github.camas.invtrashslot.InvTrashSlotClient;
import io.github.camas.invtrashslot.TrashSlotInventory;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Final
    @Shadow
    protected ScreenHandler handler;

    @Shadow
    protected abstract boolean isPointWithinBounds(int xPosition, int yPosition, int width, int height, double pointX, double pointY);

    /**
     * Trashes item when delete button pressed
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void InvTrashSlot$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callback) {
        // Ignore if not InventoryScreen
        if (!((Object) this instanceof InventoryScreen)) {
            return;
        }

        // Ignore if Delete key not pressed
        if (!InvTrashSlotClient.deleteKeybind.matchesKey(keyCode, scanCode)) {
            return;
        }

        // Copied from GameRenderer
        int mouseX = (int) (this.client.mouse.getX() * (double) this.client.getWindow().getScaledWidth() / (double) this.client.getWindow().getWidth());
        int mouseY = (int) (this.client.mouse.getY() * (double) this.client.getWindow().getScaledHeight() / (double) this.client.getWindow().getHeight());


        for (Slot slot : this.handler.slots) {
            if (isPointWithinBounds(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                if (slot.getStack().isEmpty() || slot.inventory instanceof TrashSlotInventory) {
                    break;
                }
                PacketByteBuf packetData = new PacketByteBuf(Unpooled.buffer());
                packetData.writeInt(slot.id);
                ClientSidePacketRegistry.INSTANCE.sendToServer(InvTrashSlot.TRASH_SLOT_PACKET_ID, packetData);
                slot.setStack(ItemStack.EMPTY);
                break;
            }

        }
        callback.setReturnValue(true);
    }
}
