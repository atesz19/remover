package com.teszvesz.remover;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveChunk implements CommandExecutor {

    boolean wait = true;
    Plugin plugin;

    public RemoveChunk(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            World world = Bukkit.getWorld("world");
            loadAllChunks(world, commandSender);
            p.sendMessage("DONE!");
        }

        return true;
    }

    private void loadAllChunks(World world, CommandSender sender) {

        final Pattern regionPattern = Pattern.compile("r\\.([0-9-]+)\\.([0-9-]+)\\.mca");

        File worldDir = new File(Bukkit.getWorldContainer(), world.getName());
        File regionDir = new File(worldDir, "region");

        File[] regionFiles = regionDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return regionPattern.matcher(name).matches();
            }
        });

        Bukkit.getLogger().info("Found " + (regionFiles.length * 1024) + " chunk candidates in " + regionFiles.length + " files to check for loading ...");


        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {

                Runnable r;

                for (File f : regionFiles) {
                    // extract coordinates from filename
                    Matcher matcher = regionPattern.matcher(f.getName());
                    if (!matcher.matches()) {
                        Bukkit.getLogger().warning("FilenameFilter accepted unmatched filename: " + f.getName());
                        continue;
                    }

                    int mcaX = Integer.parseInt(matcher.group(1));
                    int mcaZ = Integer.parseInt(matcher.group(2));


                    for (int cx = 0; cx < 32; cx++) {
                        for (int cz = 0; cz < 32; cz++) {

                            final int cx2 = cx;
                            final int cz2 = cz;

                            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                                @Override
                                public void run() {

                                    boolean b = world.loadChunk((mcaX << 5) + cx2, (mcaZ << 5) + cz2, false);
                                    if(b){
                                        Chunk c = world.getChunkAt((mcaX << 5) + cx2, (mcaZ << 5) + cz2);
                                        sender.sendMessage("Loaded Chunk: " + c.getX() + ":" + c.getZ());
                                        Chunks.processChunk(c);
                                        c.unload();
                                    }


                                }
                            });

                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                }


            }

        });

    }


}
