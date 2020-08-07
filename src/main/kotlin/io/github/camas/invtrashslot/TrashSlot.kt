package io.github.camas.invtrashslot

import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag

const val TAG_KEY = "inv-trash-slot"
const val INVENTORY_TYPE = 10

/**
 * Main trash slot logic
 */
class PlayerTrashSlot {
    var inventory: TrashSlotInventory = TrashSlotInventory()

    /**
     * Reads slot data from a [CompoundTag]
     */
    fun readFromTag(tag: CompoundTag) {
        inventory.readTags(tag.getList(TAG_KEY, INVENTORY_TYPE))
    }

    /**
     * Writes slot data to a a [CompoundTag]
     */
    fun writeToTag(tag: CompoundTag) {
        tag.put(TAG_KEY, inventory.tags)
    }
}

/**
 * A single slot inventory that can always be inserted into
 */
class TrashSlotInventory : SimpleInventory(1) {
    override fun canInsert(stack: ItemStack?): Boolean {
        return true
    }
}

/**
 * Interface solely used to add a PlayerTrashSlot to PlayerEntity
 */
interface ITrashSlot {
    fun getTrashSlot(): PlayerTrashSlot
}
