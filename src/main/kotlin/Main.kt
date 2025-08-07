package io.github.adamsondavid

import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        logger.info("mcfs loaded")
        server.getWorld("world")!!.getChunkAt(1, 2).getBlock(0,-60, 0).type = Material.STONE // -60 is the first layer of air (on the floor)
        server.getWorld("world")!!.getChunkAt(1, 2).getBlock(0,319, 0).type = Material.STONE // 319 is the max for Y
    }
}
