package net.spacetivity.inventory

import net.spacetivity.inventory.api.CustomInventory
import net.spacetivity.inventory.api.InventoryController
import net.spacetivity.inventory.api.InventorySize
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.InventoryPosition
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TestInventory(holder: Player) : CustomInventory(holder, InventorySize.of(3, 9), "Test Inventory", true) {

    override fun setupInventory(holder: Player, controller: InventoryController) {
        val placeholder = InteractiveItem.placeholder(Material.BLUE_STAINED_GLASS_PANE)

        controller.fill(InventoryController.FillType.TOP_BORDER, placeholder)

        val pagination = controller.createPagination()
        pagination.limitItemsPerPage(6)
        pagination.setPageSlotsFrom(1, 0)

        val items = Material.values().toList()
            .filter { material -> !material.isAir && material != Material.WATER }
            .map { material -> InteractiveItem.of(ItemStack(material)) }.toMutableList()
        pagination.distributeItems(items)

        controller.setItem(0, 3, InteractiveItem.of(ItemStack(Material.SLIME_BALL)) { pos, event ->
            pagination.toPreviousPage()
            holder.playSound(holder.location, Sound.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, 1F, 1F)
        })

        controller.setItem(0, 5, InteractiveItem.of(ItemStack(Material.REDSTONE)) { pos, event ->
            pagination.toNextPage()
            holder.playSound(holder.location, Sound.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, 1F, 1F)
        })
    }

    override fun setupUsableSlots(usableSlots: MutableList<InventoryPosition>) {
        // usableSlots.add(InventoryPosition.of(0, 1)) 1
    }

}