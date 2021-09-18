package com.teszvesz.remover;

import lombok.val;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.regex.Pattern;

public class RemoveChunk implements CommandExecutor {

    private static final Pattern REGION_PATTERN = Pattern.compile("r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca");

    private final MainPlugin plugin;

    public RemoveChunk(MainPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        val world = Bukkit.getWorld("world");
        if (world != null) {
            loadAllChunks(world, sender);
            sender.sendMessage("DONE!");
        } else {
            sender.sendMessage("World not found!");
        }
        return true;
    }

    private void loadAllChunks(World world, CommandSender sender) {

        val worldDir = new File(Bukkit.getWorldContainer(), world.getName());
        val regionDir = new File(worldDir, "region");

        val regionFiles = regionDir.listFiles((dir, name) -> REGION_PATTERN.matcher(name).matches());

        Bukkit.getLogger().info("Found " + (regionFiles.length * 1024) + " chunk candidates in " + regionFiles.length + " files to check for loading ...");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (val f : regionFiles) {
                // extract coordinates from filename
                val matcher = REGION_PATTERN.matcher(f.getName());
                if (!matcher.matches()) {
                    Bukkit.getLogger().warning("FilenameFilter accepted unmatched filename: " + f.getName());
                    continue;
                }

                val mcaX = Integer.parseInt(matcher.group(1));
                val mcaZ = Integer.parseInt(matcher.group(2));

                for (var cx = 0; cx < 32; cx++) {
                    for (var cz = 0; cz < 32; cz++) {
                        val chunkX = (mcaX << 5) + cx;
                        val chunkZ = (mcaZ << 5) + cz;

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            val b = world.loadChunk(chunkX, chunkZ, false);
                            if(b){
                                val c = world.getChunkAt(chunkX, chunkZ);
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
