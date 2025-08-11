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
        val fileSize = readInt(inode, 0)
        return sequence {
            for (address in 0..<fileSize) yield(readByte(inode, address + 4))
        }
    }

    fun writeFile(inode: Int, input: Sequence<Byte>) {
        var fileSize = 0
        for ((i, byte) in input.withIndex()) {
            writeByte(inode, i + 4, byte)
            fileSize++
        }
        writeInt(inode, 0, fileSize)
    }

    fun writeInt(inode: Int, address: Int, value: Int) {
        writeByte(inode, address + 0, (value shr 0).toByte())
        writeByte(inode, address + 1, (value shr 8).toByte())
        writeByte(inode, address + 2, (value shr 16).toByte())
        writeByte(inode, address + 3, (value shr 24).toByte())
    }

    fun readInt(inode: Int, address: Int): Int {
        return ((readByte(inode, address + 3).toInt() and 0xFF) shl 24) or
                ((readByte(inode, address + 2).toInt() and 0xFF) shl 16) or
                ((readByte(inode, address + 1).toInt() and 0xFF) shl 8) or
                (readByte(inode, address + 0).toInt() and 0xFF)
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

    fun writeByte(inode: Int, address: Int, value: Byte) {
        getBlock(inode, address).type = materialArray[value.toInt() and 0xff]
    }

    fun readByte(inode: Int, address: Int): Byte {
        return materialMap.getValue(getBlock(inode, address).type).toByte()
    }

    val materialArray = arrayOf(
        Material.STONE,
        Material.GRANITE,
        Material.POLISHED_GRANITE,
        Material.DIORITE,
        Material.POLISHED_DIORITE,
        Material.ANDESITE,
        Material.POLISHED_ANDESITE,
        Material.DEEPSLATE,
        Material.COBBLED_DEEPSLATE,
        Material.POLISHED_DEEPSLATE,
        Material.CALCITE,
        Material.TUFF,
        Material.CHISELED_TUFF,
        Material.POLISHED_TUFF,
        Material.TUFF_BRICKS,
        Material.CHISELED_TUFF_BRICKS,
        Material.DRIPSTONE_BLOCK,
        Material.MUD,
        Material.COBBLESTONE,
        Material.OAK_PLANKS,
        Material.SPRUCE_PLANKS,
        Material.BIRCH_PLANKS,
        Material.JUNGLE_PLANKS,
        Material.ACACIA_PLANKS,
        Material.CHERRY_PLANKS,
        Material.DARK_OAK_PLANKS,
        Material.PALE_OAK_PLANKS,
        Material.MANGROVE_PLANKS,
        Material.BAMBOO_PLANKS,
        Material.CRIMSON_PLANKS,
        Material.WARPED_PLANKS,
        Material.BAMBOO_MOSAIC,
        Material.BEDROCK,
        Material.COAL_ORE,
        Material.DEEPSLATE_COAL_ORE,
        Material.IRON_ORE,
        Material.DEEPSLATE_IRON_ORE,
        Material.COPPER_ORE,
        Material.DEEPSLATE_COPPER_ORE,
        Material.GOLD_ORE,
        Material.DEEPSLATE_GOLD_ORE,
        Material.EMERALD_ORE,
        Material.DEEPSLATE_EMERALD_ORE,
        Material.LAPIS_ORE,
        Material.DEEPSLATE_LAPIS_ORE,
        Material.DIAMOND_ORE,
        Material.DEEPSLATE_DIAMOND_ORE,
        Material.NETHER_GOLD_ORE,
        Material.NETHER_QUARTZ_ORE,
        Material.ANCIENT_DEBRIS,
        Material.COAL_BLOCK,
        Material.RAW_IRON_BLOCK,
        Material.RAW_COPPER_BLOCK,
        Material.RAW_GOLD_BLOCK,
        Material.AMETHYST_BLOCK,
        Material.IRON_BLOCK,
        Material.DIAMOND_BLOCK,
        Material.NETHERITE_BLOCK,
        Material.OXIDIZED_COPPER,
        Material.CHISELED_COPPER,
        Material.EXPOSED_CHISELED_COPPER,
        Material.WEATHERED_CHISELED_COPPER,
        Material.OXIDIZED_CHISELED_COPPER,
        Material.WEATHERED_CUT_COPPER,
        Material.OXIDIZED_CUT_COPPER,
        Material.WAXED_COPPER_BLOCK,
        Material.WAXED_EXPOSED_COPPER,
        Material.WAXED_WEATHERED_COPPER,
        Material.WAXED_OXIDIZED_COPPER,
        Material.WAXED_CHISELED_COPPER,
        Material.WAXED_EXPOSED_CHISELED_COPPER,
        Material.WAXED_WEATHERED_CHISELED_COPPER,
        Material.WAXED_OXIDIZED_CHISELED_COPPER,
        Material.WAXED_CUT_COPPER,
        Material.WAXED_EXPOSED_CUT_COPPER,
        Material.WAXED_WEATHERED_CUT_COPPER,
        Material.WAXED_OXIDIZED_CUT_COPPER,
        Material.OAK_LOG,
        Material.SPRUCE_LOG,
        Material.BIRCH_LOG,
        Material.JUNGLE_LOG,
        Material.ACACIA_LOG,
        Material.CHERRY_LOG,
        Material.PALE_OAK_LOG,
        Material.DARK_OAK_LOG,
        Material.MANGROVE_LOG,
        Material.CRIMSON_STEM,
        Material.WARPED_STEM,
        Material.BAMBOO_BLOCK,
        Material.STRIPPED_OAK_LOG,
        Material.STRIPPED_SPRUCE_LOG,
        Material.STRIPPED_BIRCH_LOG,
        Material.STRIPPED_JUNGLE_LOG,
        Material.STRIPPED_ACACIA_LOG,
        Material.STRIPPED_CHERRY_LOG,
        Material.STRIPPED_DARK_OAK_LOG,
        Material.STRIPPED_PALE_OAK_LOG,
        Material.STRIPPED_MANGROVE_LOG,
        Material.STRIPPED_CRIMSON_STEM,
        Material.STRIPPED_WARPED_STEM,
        Material.STRIPPED_OAK_WOOD,
        Material.STRIPPED_SPRUCE_WOOD,
        Material.STRIPPED_BIRCH_WOOD,
        Material.STRIPPED_JUNGLE_WOOD,
        Material.STRIPPED_ACACIA_WOOD,
        Material.STRIPPED_CHERRY_WOOD,
        Material.STRIPPED_DARK_OAK_WOOD,
        Material.STRIPPED_PALE_OAK_WOOD,
        Material.STRIPPED_MANGROVE_WOOD,
        Material.STRIPPED_CRIMSON_HYPHAE,
        Material.STRIPPED_WARPED_HYPHAE,
        Material.OAK_WOOD,
        Material.SPRUCE_WOOD,
        Material.BIRCH_WOOD,
        Material.JUNGLE_WOOD,
        Material.ACACIA_WOOD,
        Material.CHERRY_WOOD,
        Material.PALE_OAK_WOOD,
        Material.DARK_OAK_WOOD,
        Material.MANGROVE_WOOD,
        Material.LAPIS_BLOCK,
        Material.SANDSTONE,
        Material.CHISELED_SANDSTONE,
        Material.CUT_SANDSTONE,
        Material.WHITE_WOOL,
        Material.ORANGE_WOOL,
        Material.MAGENTA_WOOL,
        Material.LIGHT_BLUE_WOOL,
        Material.YELLOW_WOOL,
        Material.LIME_WOOL,
        Material.PINK_WOOL,
        Material.GRAY_WOOL,
        Material.LIGHT_GRAY_WOOL,
        Material.CYAN_WOOL,
        Material.PURPLE_WOOL,
        Material.BLUE_WOOL,
        Material.BROWN_WOOL,
        Material.GREEN_WOOL,
        Material.RED_WOOL,
        Material.BLACK_WOOL,
        Material.PALE_MOSS_BLOCK,
        Material.SMOOTH_QUARTZ,
        Material.SMOOTH_RED_SANDSTONE,
        Material.SMOOTH_SANDSTONE,
        Material.SMOOTH_STONE,
        Material.BRICKS,
        Material.BOOKSHELF,
        Material.CHISELED_BOOKSHELF,
        Material.MOSSY_COBBLESTONE,
        Material.OBSIDIAN,
        Material.PURPUR_BLOCK,
        Material.PURPUR_PILLAR,
        Material.CRAFTING_TABLE,
        Material.CLAY,
        Material.PUMPKIN,
        Material.NETHERRACK,
        Material.BASALT,
        Material.POLISHED_BASALT,
        Material.SMOOTH_BASALT,
        Material.INFESTED_STONE,
        Material.INFESTED_COBBLESTONE,
        Material.INFESTED_MOSSY_STONE_BRICKS,
        Material.INFESTED_CRACKED_STONE_BRICKS,
        Material.INFESTED_CHISELED_STONE_BRICKS,
        Material.INFESTED_DEEPSLATE,
        Material.STONE_BRICKS,
        Material.MOSSY_STONE_BRICKS,
        Material.CRACKED_STONE_BRICKS,
        Material.CHISELED_STONE_BRICKS,
        Material.DEEPSLATE_BRICKS,
        Material.CRACKED_DEEPSLATE_BRICKS,
        Material.DEEPSLATE_TILES,
        Material.CRACKED_DEEPSLATE_TILES,
        Material.CHISELED_DEEPSLATE,
        Material.BROWN_MUSHROOM_BLOCK,
        Material.RED_MUSHROOM_BLOCK,
        Material.MUSHROOM_STEM,
        Material.MELON,
        Material.RESIN_BLOCK,
        Material.RESIN_BRICKS,
        Material.CHISELED_RESIN_BRICKS,
        Material.NETHER_BRICKS,
        Material.CRACKED_NETHER_BRICKS,
        Material.CHISELED_NETHER_BRICKS,
        Material.END_STONE,
        Material.END_STONE_BRICKS,
        Material.EMERALD_BLOCK,
        Material.CHISELED_QUARTZ_BLOCK,
        Material.QUARTZ_BLOCK,
        Material.QUARTZ_BRICKS,
        Material.QUARTZ_PILLAR,
        Material.WHITE_TERRACOTTA,
        Material.ORANGE_TERRACOTTA,
        Material.MAGENTA_TERRACOTTA,
        Material.LIGHT_BLUE_TERRACOTTA,
        Material.YELLOW_TERRACOTTA,
        Material.LIME_TERRACOTTA,
        Material.PINK_TERRACOTTA,
        Material.GRAY_TERRACOTTA,
        Material.LIGHT_GRAY_TERRACOTTA,
        Material.CYAN_TERRACOTTA,
        Material.PURPLE_TERRACOTTA,
        Material.BLUE_TERRACOTTA,
        Material.BROWN_TERRACOTTA,
        Material.GREEN_TERRACOTTA,
        Material.RED_TERRACOTTA,
        Material.BLACK_TERRACOTTA,
        Material.HAY_BLOCK,
        Material.TERRACOTTA,
        Material.PRISMARINE,
        Material.PRISMARINE_BRICKS,
        Material.DARK_PRISMARINE,
        Material.RED_SANDSTONE,
        Material.CHISELED_RED_SANDSTONE,
        Material.CUT_RED_SANDSTONE,
        Material.NETHER_WART_BLOCK,
        Material.WARPED_WART_BLOCK,
        Material.RED_NETHER_BRICKS,
        Material.BONE_BLOCK,
        Material.WHITE_GLAZED_TERRACOTTA,
        Material.ORANGE_GLAZED_TERRACOTTA,
        Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
        Material.YELLOW_GLAZED_TERRACOTTA,
        Material.LIME_GLAZED_TERRACOTTA,
        Material.PINK_GLAZED_TERRACOTTA,
        Material.GRAY_GLAZED_TERRACOTTA,
        Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
        Material.CYAN_GLAZED_TERRACOTTA,
        Material.PURPLE_GLAZED_TERRACOTTA,
        Material.BLUE_GLAZED_TERRACOTTA,
        Material.BROWN_GLAZED_TERRACOTTA,
        Material.GREEN_GLAZED_TERRACOTTA,
        Material.RED_GLAZED_TERRACOTTA,
        Material.BLACK_GLAZED_TERRACOTTA,
        Material.WHITE_CONCRETE,
        Material.ORANGE_CONCRETE,
        Material.MAGENTA_CONCRETE,
        Material.LIGHT_BLUE_CONCRETE,
        Material.YELLOW_CONCRETE,
        Material.LIME_CONCRETE,
        Material.PINK_CONCRETE,
        Material.GRAY_CONCRETE,
        Material.LIGHT_GRAY_CONCRETE,
        Material.CYAN_CONCRETE,
        Material.PURPLE_CONCRETE,
        Material.BLUE_CONCRETE,
        Material.BROWN_CONCRETE,
        Material.GREEN_CONCRETE,
        Material.RED_CONCRETE,
        Material.BLACK_CONCRETE,
        Material.BLACKSTONE,
        Material.GILDED_BLACKSTONE,
        Material.POLISHED_BLACKSTONE,
        Material.CHISELED_POLISHED_BLACKSTONE,
        Material.POLISHED_BLACKSTONE_BRICKS,
        Material.CRACKED_POLISHED_BLACKSTONE_BRICKS
    )
    val materialMap = materialArray.mapIndexed { index, value -> value to index }.toMap()
}
