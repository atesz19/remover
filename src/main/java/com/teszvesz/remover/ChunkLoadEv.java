package com.teszvesz.remover;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ChunkLoadEv implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e){
        processChunk(e.getChunk());
    }

    public static void processChunk(Chunk c) {
        final int maxX = 15;
        final int maxY = c.getWorld().getMaxHeight() - 1;
        final int maxZ = 15;

        for (int x = 0; x <= maxX; ++x) {
            for (int y = 0; y <= maxY; ++y) {
                for (int z = 0; z <= maxZ; ++z) {

                    for (Material m : MainPlugin.blocksList) {
                        if (c.getBlock(x, y, z).getBlockData().getMaterial().equals(m)) {
                            c.getBlock(x, y, z).setType(Material.AIR);
                        }
                    }

                    if(c.getBlock(x, y, z).getState() instanceof InventoryHolder){
                        InventoryHolder ih = (InventoryHolder) c.getBlock(x, y, z).getState();
                        Inventory i = ih.getInventory();

                        for (Material m : MainPlugin.itemsList) {
                            if (i.contains(m)) {
                                Bukkit.getLogger().info("Removed: " + m.toString());
                                i.remove(m);
                            }
                        }
                        shulkerRemove(i);
                    }
                }
            }
        }

        for(Entity e : c.getEntities()) {
            if(e instanceof ItemFrame){
                ItemFrame itemF = (ItemFrame) e;

                for(Material m : MainPlugin.itemsList){
                    Material ifm = itemF.getItem().getType();
                    if(ifm.equals(m)){
                        itemF.setItem(new ItemStack(Material.AIR));
                    }
                }

                if (itemF.getItem().getItemMeta() instanceof BlockStateMeta) {
                    BlockStateMeta im = (BlockStateMeta) itemF.getItem().getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        itemF.setItem(new ItemStack(Material.SHULKER_BOX, 1));
                    }
                }
            }

            if (e instanceof InventoryHolder){
                Inventory i = ((InventoryHolder) e).getInventory();
                for(Material m : MainPlugin.itemsList){
                    i.remove(m);
                }
                shulkerRemove(i);
            }
        }
    }


    public static void shulkerRemove(Inventory i){
        for (int j = 0; j < i.getSize(); j++) {
            ItemStack item = i.getItem(j);
            if(item != null) {
                if (item.getItemMeta() instanceof BlockStateMeta) {
                    BlockStateMeta im = (BlockStateMeta) item.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {

                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();

                        for(Material m : MainPlugin.itemsList){
                            shulker.getInventory().remove(m);
                        }

                        im.setBlockState(shulker);
                        shulker.update();
                        item.setItemMeta(im);
                        i.setItem(j, item);

                    }
                }
            }
        }
    }

}
