package com.teszvesz.remover;


import lombok.val;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainPlugin extends JavaPlugin {

    public static final List<Material> itemsList = Collections.unmodifiableList(Arrays.asList(
            Material.SPAWNER,
            Material.PAPER,
            Material.CARROT,
            Material.CHORUS_FRUIT,
            Material.CHORUS_FLOWER,
            Material.SWEET_BERRIES,
            Material.CACTUS,
            Material.IRON_BLOCK,
            Material.IRON_INGOT,
            Material.IRON_NUGGET,
            Material.POTATO,
            Material.BAKED_POTATO,
            Material.ARROW,
            Material.PRISMARINE_SHARD,
            Material.PRISMARINE_CRYSTALS,
            Material.KELP,
            Material.DRIED_KELP,
            Material.DRIED_KELP_BLOCK,
            Material.GHAST_TEAR,
            Material.SPONGE,
            Material.WET_SPONGE,
            Material.PUMPKIN,
            Material.PUMPKIN_SEEDS,
            Material.MELON,
            Material.MELON_SEEDS,
            Material.MELON_SLICE,
            Material.BLAZE_ROD,
            Material.MAGMA_CREAM,
            Material.ICE,
            Material.BLUE_ICE,
            Material.PACKED_ICE,
            Material.FROSTED_ICE,
            Material.BEETROOT,
            Material.BEETROOT_SEEDS,
            Material.SUGAR_CANE,
            Material.BAMBOO,
            Material.SLIME_BALL,
            Material.SLIME_BLOCK,
            Material.SPIDER_EYE,
            Material.HONEY_BLOCK,
            Material.HONEY_BOTTLE,
            Material.HONEYCOMB,
            Material.HONEYCOMB_BLOCK,
            Material.WHEAT,
            Material.WHEAT_SEEDS,
            Material.NETHER_WART,
            Material.NETHER_WART_BLOCK,
            Material.WHITE_WOOL,
            Material.CARROT,
            Material.HAY_BLOCK,
            Material.SPAWNER,
            Material.TRIPWIRE,
            Material.TRIPWIRE_HOOK,
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.JUNGLE_LOG,
            Material.OAK_LOG,
            Material.SPRUCE_LOG,
            Material.DARK_OAK_LOG,
            Material.ACACIA_WOOD,
            Material.BIRCH_WOOD,
            Material.JUNGLE_WOOD,
            Material.OAK_WOOD,
            Material.SPRUCE_WOOD,
            Material.DARK_OAK_WOOD,
            Material.SNOW,
            Material.SNOW_BLOCK,
            Material.SNOWBALL,
            Material.FERMENTED_SPIDER_EYE,
            Material.LEATHER,
            Material.STRING,
            Material.FEATHER,
            Material.ROTTEN_FLESH,
            Material.BONE,
            Material.BONE_BLOCK,
            Material.GUNPOWDER,
            Material.ENDER_PEARL,
            Material.COOKED_COD,
            Material.COOKED_SALMON,
            Material.COD,
            Material.SALMON,
            Material.TROPICAL_FISH,
            Material.PUFFERFISH,
            Material.MUTTON,
            Material.COOKED_MUTTON
    ));
    public static final List<Material> blocksList = Collections.unmodifiableList(Arrays.asList(
            Material.SPAWNER,
            Material.BONE_BLOCK,
            Material.PUMPKIN,
            Material.HAY_BLOCK,
            Material.HONEY_BLOCK,
            Material.DRIED_KELP_BLOCK,
            Material.MELON,
            Material.SLIME_BLOCK,
            Material.IRON_BLOCK,
            Material.SPONGE,
            Material.WET_SPONGE,
            Material.OBSERVER
    ));

    @Override
    public void onEnable() {
        val cl = new ChunkLoadEv();

        getServer().getPluginManager().registerEvents(cl, this);

        getCommand("enablechunkremove").setExecutor(cl::enable);
        getCommand("removefromchunk").setExecutor(new RemoveChunk(this));
        getCommand("removefrominv").setExecutor(new InvRemove(this));
    }

}
