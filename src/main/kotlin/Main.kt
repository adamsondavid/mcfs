package io.github.adamsondavid

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import kotlin.math.abs

class Main : JavaPlugin() {
    private lateinit var world: World

    override fun onEnable() {
        this.world = super.server.getWorld("world")!!
        logger.info("plugin loaded")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        try {
            if (command.name == "read") {
                val inode =
                    args.getOrNull(0)?.toIntOrNull() ?: throw IllegalArgumentException("invalid argument: inode")
                sender.sendMessage(String(readFile(inode).toList().toByteArray()))
            }
            if (command.name == "write") {
                val inode =
                    args.getOrNull(0)?.toIntOrNull() ?: throw IllegalArgumentException("invalid argument: inode")
                writeFile(inode, args.sliceArray(1 until args.size).joinToString(" ").toByteArray().asSequence())
            }
            return true
        } catch (e: Exception) {
            sender.sendMessage(e.message)
            return false
        }
    }

    fun readFile(inode: Int): Sequence<Byte> {
        var fileSize = 0
        for (bitIndex in 0..31) {
            val bit = if (readBit(inode, bitIndex)) 1 else 0
            fileSize = (fileSize shl 1) or bit
        }
        return sequence {
            for (byteIndex in 0..<fileSize) {
                var byte = 0
                for (bitIndex in 7 downTo 0) {
                    val bit = if (readBit(inode, byteIndex * 8 + bitIndex + 32)) 1 else 0
                    byte = (byte shl 1) or bit
                }
                yield(byte.toByte())
            }
        }
    }

    fun writeFile(inode: Int, input: Sequence<Byte>) {
        var fileSize = 0
        for ((i, byte) in input.withIndex()) {
            for (bitIndex in 0..7) {
                val bit = (byte.toInt() shr bitIndex) and 1
                writeBit(inode, i * 8 + bitIndex + 32, bit == 1)
            }
            fileSize++
        }
        for (bitIndex in 0..31) {
            val bit = (fileSize shr bitIndex) and 1
            writeBit(inode, 31 - bitIndex, bit == 1)
        }
    }

    fun getBlock(inode: Int, address: Int): Block {
        val CHUNK_WIDTH = 16
        val CHUNK_HEIGHT = 16
        val MIN_ELEVATION = -60
        val MAX_ELEVATION = 319
        val CHUNK_ELEVATION = abs(MIN_ELEVATION) + MAX_ELEVATION + 1

        val x = address % CHUNK_WIDTH
        val z = (address / CHUNK_WIDTH) % CHUNK_HEIGHT
        val y = ((address / (CHUNK_WIDTH * CHUNK_HEIGHT)) % CHUNK_ELEVATION) + MIN_ELEVATION
        val chunk = address / (CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_ELEVATION)
        return world.getChunkAt(chunk, inode).getBlock(x, y, z)
    }

    fun writeBit(inode: Int, address: Int, value: Boolean) {
        getBlock(inode, address).type = if (value) Material.WHITE_WOOL else Material.BLACK_WOOL
    }

    fun readBit(inode: Int, address: Int): Boolean {
        return getBlock(inode, address).type == Material.WHITE_WOOL
    }
}
