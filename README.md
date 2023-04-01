

# SpaceInventoryFramework
Official inventory framework off all Spacetivity.net plugins which provides a lightweight api to enable a fast and bug free development process of guis.

```kotlin
package net.spacetivity.inventory.example

import net.spacetivity.inventory.api.CustomInventory
import net.spacetivity.inventory.api.InventoryController
import net.spacetivity.inventory.api.InventoryPagination
import net.spacetivity.inventory.api.InventorySize
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition
import net.spacetivity.inventory.utils.MathUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TestInventory(holder: Player) : CustomInventory(holder, InventorySize.of(3, 9), "Test Inv", true) {

    override fun setupInventory(holder: Player, controller: InventoryController) {

        val placeholder = InteractiveItem.placeholder(Material.BARRIER)

        // Fills the first row of the inventory
        controller.fill(InventoryController.FillType.ROW, placeholder, InventoryPosition.of(0, 0))

        // Fills a rectangular place in the inventory
        controller.fill(
            InventoryController.FillType.RECTANGLE, placeholder,
            InventoryPosition.of(0, 0), // Top left
            InventoryPosition.of(1, 1)
        ) // Bottom right

        // Types to fill several borders
        controller.fill(InventoryController.FillType.LEFT_BORDER, placeholder)
        controller.fill(InventoryController.FillType.RIGHT_BORDER, placeholder)
        controller.fill(InventoryController.FillType.TOP_BORDER, placeholder)
        controller.fill(InventoryController.FillType.BOTTOM_BORDER, placeholder)
        controller.fill(InventoryController.FillType.ALL_BORDERS, placeholder)

        // Sets an interactive item to a given position with an action when the player clicks on the item.
        controller.setItem(
            InventoryPosition.of(0, 1),
            InteractiveItem.of(ItemStack(Material.BARRIER)) { position, event ->
                event.inventory.close()
            })

        // Adds an item to the first free slot in the inventory
        controller.addItem(placeholder)

        // Adds an item to a random position
        controller.addItemToRandomPosition(placeholder)

        // Creates a pagination for the inventory
        val pagination: InventoryPagination = controller.createPagination()
        pagination.limitItemsPerPage(6)
        pagination.setPageSlotsFrom(1, 9)
        pagination.distributeItems(Material.values()
            .map { material -> InteractiveItem.of(ItemStack(material)) }
            .toMutableList())

        // Finds the first item with the given type
        val searchResult = controller.findFirstItemWithType(Material.LEATHER)

        // Gets back the bukkit inventory
        val bukkitInventory = controller.getRawInventory()

    }

    override fun setupUsableSlots(usableSlots: MutableList<InventoryPosition>) {
        val range = IntRange(0, 9) // iterates over all slots in the first row

        // Adds all slots of the first row. The end user can place or take items out and in the inventory (e.g. for a backpack)
        for (slot in range) {
            val position = MathUtils.slotToPosition(slot, 9)
            usableSlots.add(position)
        }
    }

}
```

```kotlin
package net.spacetivity.inventory.example

import net.spacetivity.inventory.SpaceInventoryBootstrap
import org.bukkit.plugin.java.JavaPlugin

class YourPlugin : JavaPlugin() {

    override fun onEnable() {
        SpaceInventoryBootstrap.instance.init(this)
    }
    
    override fun onDisable() {
        
    }
    
}
```