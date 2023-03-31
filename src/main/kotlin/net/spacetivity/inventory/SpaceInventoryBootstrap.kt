package net.spacetivity.inventory

import net.spacetivity.inventory.api.InventoryController
import net.spacetivity.inventory.item.InteractiveItem
import net.spacetivity.inventory.item.ItemEnchantment
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SpaceInventoryBootstrap {



    fun init(controller: InventoryController) {
        val itemStack = ItemStack(Material.SADDLE)

        val item: InteractiveItem = InteractiveItem.of(itemStack) {position, event ->
            val player = event.whoClicked as Player
            player.closeInventory()
        }

        item.update(InteractiveItem.Modification.DISPLAY_NAME, "<green>Neuer Name!")
        item.update(InteractiveItem.Modification.LORE, arrayOf("First Line", "Second Line"))
        item.update(InteractiveItem.Modification.AMOUNT, 10)
        item.update(InteractiveItem.Modification.GLOWING)
        item.update(InteractiveItem.Modification.ENCHANTMENTS, ItemEnchantment.of(Enchantment.DURABILITY, 1, true))


    }

}