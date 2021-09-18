package com.teszvesz.remover;

import com.drtshock.playervaults.PlayerVaults;
import com.drtshock.playervaults.vaultmanagement.VaultManager;
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

public class InvRemove implements CommandExecutor {

    private final MainPlugin plugin;

    public InvRemove(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        removeAll(sender);
        return true;
    }

    void removeAll(CommandSender sender) {
        OpenInv openinv = (OpenInv) plugin.getServer().getPluginManager().getPlugin("OpenInv");

        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            Player player = openinv.loadPlayer(p);
            if (player != null) {
                sender.sendMessage(">" + p.getName() + " playerdata loaded...");
                Inventory inv = player.getInventory();

                for(Material m : MainPlugin.itemsList){
                    inv.remove(m);
                }

                //ShulkerDelete

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
                //playervaults
                for(int i = 1; i <= 100; i++){
                    if(VaultManager.getInstance().vaultExists(player.getUniqueId().toString(),i)){
                        Inventory pv = VaultManager.getInstance().getVault(player.getUniqueId().toString(), i);
                        if(pv != null){

                            for(Material m : MainPlugin.itemsList){
                                pv.remove(m);
                            }


                            for (int j = 0; j < pv.getSize(); j++) {
                                ItemStack item = pv.getItem(j);
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
                                            pv.setItem(j, item);

                                        }
                                    }
                                }

                            }

                            VaultManager.getInstance().saveVault(pv, player.getUniqueId().toString(), i);
                        }
                    }
                }

                player.saveData();
                openinv.unload(p);
            }

        }

    }


    /**
     * Get the max size vault a player is allowed to have.
     *
     * @param player that is having his permissions checked.
     * @return max size as integer. If no max size is set then it will default to the configured default.
     */
    public static int getMaxVaultSize(OfflinePlayer player) {
        if (player == null || !player.isOnline()) {
            return 6 * 9;
        }
        for (int i = 6; i != 0; i--) {
            if (player.getPlayer().hasPermission("playervaults.size." + i)) {
                return i * 9;
            }
        }
        return PlayerVaults.getInstance().getDefaultVaultSize();
    }

}
