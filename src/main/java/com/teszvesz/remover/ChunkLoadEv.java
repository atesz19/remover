package com.teszvesz.remover;

import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ChunkLoadEv implements Listener {

    private boolean enabled = false;

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e){
        if (enabled) {
            processChunk(e.getChunk());
        }
    }

    public boolean enable(CommandSender sender, Command cmd, String label, String[] args) {
        enabled = !enabled;
        sender.sendMessage("Chunk processing on ChunkLoadEvent is " + (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
        return false;
    }

    public static void processChunk(Chunk c) {
        val maxX = 15;
        val maxY = c.getWorld().getMaxHeight() - 1;
        val maxZ = 15;

        for (var x = 0; x <= maxX; ++x) {
            for (var y = 0; y <= maxY; ++y) {
                for (var z = 0; z <= maxZ; ++z) {

                    for (val m : MainPlugin.blocksList) {
                        if (c.getBlock(x, y, z).getBlockData().getMaterial().equals(m)) {
                            c.getBlock(x, y, z).setType(Material.AIR);
                        }
                    }

                    if(c.getBlock(x, y, z).getState() instanceof InventoryHolder){
                        val ih = (InventoryHolder) c.getBlock(x, y, z).getState();
                        val i = ih.getInventory();

                        for (val m : MainPlugin.itemsList) {
                            if (i.contains(m)) {
                                Bukkit.getLogger().info("Removed: " + m);
                                i.remove(m);
                            }
                        }
                        shulkerRemove(i);
                    }
                }
            }
        }

        for(val e : c.getEntities()) {
            if(e instanceof ItemFrame){
                val itemF = (ItemFrame) e;

                for(val m : MainPlugin.itemsList){
                    val ifm = itemF.getItem().getType();
                    if(ifm.equals(m)){
                        itemF.setItem(new ItemStack(Material.AIR));
                    }
                }

                if (itemF.getItem().getItemMeta() instanceof BlockStateMeta) {
                    val im = (BlockStateMeta) itemF.getItem().getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        itemF.setItem(new ItemStack(Material.SHULKER_BOX, 1));
                    }
                }
            }

            if (e instanceof InventoryHolder){
                val i = ((InventoryHolder) e).getInventory();
                for(val m : MainPlugin.itemsList){
                    i.remove(m);
                }
                shulkerRemove(i);
            }
        }
    }


    public static void shulkerRemove(Inventory i){
        for (var j = 0; j < i.getSize(); j++) {
            val item = i.getItem(j);
            if(item != null) {
                if (item.getItemMeta() instanceof BlockStateMeta) {
                    val im = (BlockStateMeta) item.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {

                        val shulker = (ShulkerBox) im.getBlockState();

                        for(val m : MainPlugin.itemsList){
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
