package io.github.camas.invtrashslot

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

/**
 * Mod entry point
 */
@Suppress()
fun init() {
    println("InvTrashSlot ${InvTrashSlot.VERSION} by Camas")

    // Register packets
    ServerSidePacketRegistry.INSTANCE.register(InvTrashSlot.TRASH_SLOT_PACKET_ID)
    { context, buffer ->
        val slotId = buffer.readInt()
        val toTrashSlot = context.player.currentScreenHandler.getSlot(slotId)
        val toTrash = toTrashSlot.stack
        toTrashSlot.stack = ItemStack.EMPTY
        if (toTrash.isEmpty) {
            return@register
        }
        val trashInv = (context.player as ITrashSlot).getTrashSlot().inventory
        val currentStack = trashInv.getStack(0)
        // Set trash slot to cursor item and set cursor item to empty
        if (ScreenHandler.canStacksCombine(currentStack, toTrash)) {
            val newCount = currentStack.count + toTrash.count
            currentStack.count = newCount.coerceAtMost(currentStack.maxCount)
        } else {
            trashInv.setStack(0, toTrash)
        }

    }
}

/**
 * Client-only entry point
 */
fun initClient() {
    KeyBindingHelper.registerKeyBinding(InvTrashSlotClient.deleteKeybind)
}

object InvTrashSlot {
    const val VERSION = "1.0.0"
    const val MOD_ID = "invtrashslot"
    const val SLOT_X = 148
    const val SLOT_Y = 164

    @JvmField
    val TRASH_SLOT_PACKET_ID = Identifier(MOD_ID, "trash_slot_packet");


}

object InvTrashSlotClient {
    @JvmField
    val deleteKeybind = KeyBinding(
            "key.${InvTrashSlot.MOD_ID}.delete",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DELETE,
            "category.${InvTrashSlot.MOD_ID}.main");
}