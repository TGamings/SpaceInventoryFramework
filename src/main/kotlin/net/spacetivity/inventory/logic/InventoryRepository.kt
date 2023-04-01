package net.spacetivity.inventory.logic

import net.spacetivity.inventory.api.CustomInventory
import java.util.*

class InventoryRepository {

    val playerInventories: MutableMap<UUID, CustomInventory> = mutableMapOf()

     fun unregister(uniqueId: UUID) {
        this.playerInventories.remove(uniqueId)
    }

}