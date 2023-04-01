package net.spacetivity.inventory

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.entity.Player

class TestCommand(plugin: SpaceInventoryBootstrap) : CommandExecutor {

    init {
        val command: PluginCommand = plugin.getCommand("test")!!
        command.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false

        val customInventory = TestInventory(sender)
        customInventory.open()

        return true
    }

}