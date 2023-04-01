package net.spacetivity.inventory

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.spacetivity.inventory.api.CustomInventory
import net.spacetivity.inventory.api.InventoryController
import net.spacetivity.inventory.api.InventorySize
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TestInventory(holder: Player) : CustomInventory(holder, InventorySize.of(3, 9), "Test Inventory", true) {

    override fun setupInventory(holder: Player, controller: InventoryController) {
        val placeholder = InteractiveItem.placeholder(Material.BLUE_STAINED_GLASS_PANE)

        controller.fill(
            InventoryController.FillType.RECTANGLE, placeholder,
            InventoryPosition.of(1, 7),
            InventoryPosition.of(2, 8)
        )

        controller.addItemToRandomPosition(placeholder)

        controller.addItem(InteractiveItem.of(ItemStack(Material.SADDLE)) { pos, event ->
            val player = event.whoClicked as Player
            val currentItem = controller.getItem(pos)!!

            currentItem.update(controller, InteractiveItem.Modification.DISPLAY_NAME, "<red>New Name!")
            currentItem.update(controller, InteractiveItem.Modification.INCREMENT, 5)

            player.sendMessage("clicked > ${PlainTextComponentSerializer.plainText().serialize(currentItem.item.itemMeta.displayName()!!)}")

        })

    }

    override fun setupUsableSlots(usableSlots: MutableList<InventoryPosition>) {
        // usableSlots.add(InventoryPosition.of(0, 1)) 1
    }

}