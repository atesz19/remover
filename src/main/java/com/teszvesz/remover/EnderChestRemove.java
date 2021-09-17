package com.teszvesz.remover;

import com.lishid.openinv.IOpenInv;
import com.lishid.openinv.internal.ISpecialEnderChest;
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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class EnderChestRemove implements CommandExecutor {

    Plugin plugin;
    public EnderChestRemove(Plugin plugin) {
        this.plugin = plugin;
    }

    void removeAll(CommandSender sender) throws InstantiationException {

        Plugin openinv = plugin.getServer().getPluginManager().getPlugin("OpenInv");

        for(OfflinePlayer p : Bukkit.getOfflinePlayers()){
            Player player = ((IOpenInv) openinv).loadPlayer(p);
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
                ((IOpenInv) openinv).unload(p);
            }
        }

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        try {
            removeAll(commandSender);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return true;

    }
}
