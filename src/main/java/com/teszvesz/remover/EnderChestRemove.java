package com.teszvesz.remover;

import com.lishid.openinv.OpenInv;
import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
        val openinv = (OpenInv) plugin.getServer().getPluginManager().getPlugin("OpenInv");
        for(val p : Bukkit.getOfflinePlayers()){
            val player = openinv.loadPlayer(p);
            if(player != null) {
                sender.sendMessage(">" + p.getName() + " playerdata loaded...");
                val inv = player.getEnderChest();

                for(val m : MainPlugin.itemsList){
                    inv.remove(m);
                }

                for (var i = 0; i < inv.getSize(); i++) {
                    val item = inv.getItem(i);
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
