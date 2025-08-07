package io.github.adamsondavid

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        logger.info("mcfs loaded")
    }
}
