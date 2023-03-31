package net.spacetivity.inventory.item

import net.kyori.adventure.text.Component
import net.spacetivity.inventory.item.InteractiveItem.Modification.*
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

data class InteractiveItem(val item: ItemStack, val action: (InventoryPosition, InventoryClickEvent) -> Unit) {

    fun runAction(position: InventoryPosition, event: InventoryClickEvent) = this.action.invoke(position, event)

    fun update(modification: Modification, vararg values: Any) {
        if (values.size > 1)
            throw UnsupportedOperationException("There are no more then one values allowed! Current size: ${values.size}")

        val newValue = values[0]
        when (modification) {
            DISPLAY_NAME -> {
                if (newValue !is String)
                    throw UnsupportedOperationException("'newValue' is not an String!")

                this.item.itemMeta.displayName(Component.text(newValue))
                update(ITEM_META, this.item.itemMeta)
            }

            LORE -> {
                if (!(newValue is Array<*> && newValue.isArrayOf<String>()))
                    throw UnsupportedOperationException("'newValue' is not an String Array!")

                @Suppress("UNCHECKED_CAST") val raw: Array<String> = newValue as Array<String>
                val lore = raw.map { line -> Component.text(line) }.toList()

                this.item.itemMeta.lore(lore)
                update(ITEM_META, this.item.itemMeta)
            }

            AMOUNT -> {
                if (newValue !is Int)
                    throw UnsupportedOperationException("'newValue' is not an Int!")

                this.item.amount = newValue
            }

            GLOWING -> {
                this.item.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
                this.item.addItemFlags(ItemFlag.HIDE_ENCHANTS)
                update(ITEM_META, this.item.itemMeta)
            }

            ENCHANTMENTS -> {
                if (newValue !is ItemEnchantment)
                    throw UnsupportedOperationException("'newValue' is not an ItemEnchantment!")

                newValue.performAction(this.item)
            }

            ITEM_META -> {
                if (newValue !is ItemMeta)
                    throw UnsupportedOperationException("'newValue' is not an ItemMeta!")

                this.item.itemMeta = newValue
            }
        }
    }

    enum class Modification {
        DISPLAY_NAME,
        LORE,
        AMOUNT,
        GLOWING,
        ENCHANTMENTS,
        ITEM_META
    }

    companion object {

        fun placeholder(material: Material): InteractiveItem {
            return of(ItemStack(material))
        }

        fun of(item: ItemStack): InteractiveItem {
            return InteractiveItem(item) { _, _ -> }
        }

        fun of(item: ItemStack, action: (InventoryPosition, InventoryClickEvent) -> Unit): InteractiveItem {
            return InteractiveItem(item, action)
        }

    }

}