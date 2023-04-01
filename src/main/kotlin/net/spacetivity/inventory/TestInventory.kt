package net.spacetivity.inventory

import net.spacetivity.inventory.api.CustomInventory
import net.spacetivity.inventory.api.InventoryController
import net.spacetivity.inventory.api.InventorySize
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TestInventory(holder: Player) : CustomInventory(holder, InventorySize.of(3, 9), "Test Inventory", false) {

    override fun setupInventory(holder: Player, controller: InventoryController) {
        val placeholder = InteractiveItem.placeholder(Material.BLUE_STAINED_GLASS_PANE)

        controller.fill(
            InventoryController.FillType.RECTANGLE, placeholder,
            InventoryPosition.of(0, 1),
            InventoryPosition.of(1, 1)
        )

        //controller.addItemToRandomPosition(placeholder)

        controller.addItem(InteractiveItem.of(ItemStack(Material.SADDLE)) { pos, event ->
            val player = event.whoClicked as Player
            val currentItem = controller.getItem(pos)!!
            currentItem.update(InteractiveItem.Modification.DISPLAY_NAME, "<red>New Name!")
            player.closeInventory()
        })

    }

}