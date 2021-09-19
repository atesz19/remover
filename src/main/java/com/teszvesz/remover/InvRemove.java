package com.teszvesz.remover;

import com.drtshock.playervaults.PlayerVaults;
import com.drtshock.playervaults.vaultmanagement.VaultManager;
import com.lishid.openinv.OpenInv;
import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

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
        val openinv = (OpenInv) plugin.getServer().getPluginManager().getPlugin("OpenInv");
        val TIMEOUT = 30; // 30 sec

        TaskUtility.runWithTimingsAsync(plugin, "inventory removal", () -> {
            var checkpoint = System.currentTimeMillis() + 15000;
            AtomicLong processedPlayerCount = new AtomicLong(0);
            val op = Bukkit.getOfflinePlayers();
            sender.sendMessage("processing of " + op.length + " players started...");
            for (val p : op) {
                try {
                    TaskUtility.waitForBukkitTask(plugin, () -> {
                        val player = openinv.loadPlayer(p);
                        if (player != null) {
                            plugin.getLogger().info("loaded data for " + p.getName());
                            plugin.getLogger().info("processing inventory...");
                            val inv = player.getInventory();
                            for(val m : MainPlugin.itemsList){
                                inv.remove(m);
                            }
                            //ShulkerDelete
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

                            plugin.getLogger().info("processing enderchest...");
                            val ender = player.getEnderChest();
                            for(val m : MainPlugin.itemsList){
                                ender.remove(m);
                            }
                            for (var i = 0; i < ender.getSize(); i++) {
                                val item = ender.getItem(i);
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
                                            ender.setItem(i, item);
                                        }
                                    }
                                }
                            }

                            plugin.getLogger().info("processing playervault...");
                            for(var i = 1; i <= 100; i++){
                                if(VaultManager.getInstance().vaultExists(player.getUniqueId().toString(),i)){
                                    val pv = VaultManager.getInstance().getVault(player.getUniqueId().toString(), i);
                                    if(pv != null){
                                        for(val m : MainPlugin.itemsList){
                                            pv.remove(m);
                                        }
                                        for (var j = 0; j < pv.getSize(); j++) {
                                            val item = pv.getItem(j);
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
                            processedPlayerCount.incrementAndGet();
                        }
                    }, TIMEOUT, TimeUnit.SECONDS);
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (TimeoutException e) {
                    plugin.getLogger().warning("failed to process inventory of " + p.getName() + " in " + TIMEOUT + "s");
                } catch (ExecutionException e) {
                    plugin.getLogger().warning("failed to process inventory of " + p.getName());
                    e.printStackTrace();
                }
                if (checkpoint <= System.currentTimeMillis()) {
                    checkpoint = System.currentTimeMillis() + 15_000;
                    sender.sendMessage("processed " + processedPlayerCount.get() + " players...");
                }
            }
            val msg = "Processed " + processedPlayerCount.get() + " players of " + op.length + " (" + (op.length-processedPlayerCount.get()) + " missed)";
            plugin.getLogger().info(msg);
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(msg);
            }
            return true;
        });
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
        for (var i = 6; i != 0; i--) {
            if (player.getPlayer().hasPermission("playervaults.size." + i)) {
                return i * 9;
            }
        }
        return PlayerVaults.getInstance().getDefaultVaultSize();
    }

}
