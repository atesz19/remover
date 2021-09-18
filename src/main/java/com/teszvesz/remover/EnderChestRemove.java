package com.teszvesz.remover;

import com.lishid.openinv.OpenInv;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class EnderChestRemove implements CommandExecutor {

    private final MainPlugin plugin;

    public EnderChestRemove(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        removeAll(sender);
        return true;
    }

    void removeAll(CommandSender sender) {
        OpenInv openinv = (OpenInv) plugin.getServer().getPluginManager().getPlugin("OpenInv");
        for(OfflinePlayer p : Bukkit.getOfflinePlayers()){
            Player player = openinv.loadPlayer(p);
            if(player != null) {
                sender.sendMessage(">" + p.getName() + " playerdata loaded...");
                Inventory inv = player.getEnderChest();

                for(Material m : MainPlugin.itemsList){
                    inv.remove(m);
                }

                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack item = inv.getItem(i);
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
                                inv.setItem(i, item);

                            }
                        }
                    }

                }

                player.saveData();
                openinv.unload(p);
            }
        }
    }

}
