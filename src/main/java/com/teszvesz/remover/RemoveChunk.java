package com.teszvesz.remover;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemoveChunk implements CommandExecutor {

    private final MainPlugin plugin;

    public RemoveChunk(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        World world = Bukkit.getWorld("world");
        if (world != null) {
            loadAllChunks(world, sender);
            sender.sendMessage("DONE!");
        } else {
            sender.sendMessage("World not found!");
        }
        return true;
    }

    private void loadAllChunks(World world, CommandSender sender) {

        final Pattern regionPattern = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");

        File worldDir = new File(Bukkit.getWorldContainer(), world.getName());
        File regionDir = new File(worldDir, "region");

        File[] regionFiles = regionDir.listFiles((dir, name) -> regionPattern.matcher(name).matches());

        Bukkit.getLogger().info("Found " + (regionFiles.length * 1024) + " chunk candidates in " + regionFiles.length + " files to check for loading ...");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
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
                        final int chunkX = (mcaX << 5) + cx;
                        final int chunkZ = (mcaZ << 5) + cz;

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            boolean b = world.loadChunk(chunkX, chunkZ, false);
                            if(b){
                                Chunk c = world.getChunkAt(chunkX, chunkZ);
                                sender.sendMessage("Loaded Chunk: " + c.getX() + ":" + c.getZ());
                                ChunkLoadEv.processChunk(c);
                                c.unload();
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
        });

    }

}
