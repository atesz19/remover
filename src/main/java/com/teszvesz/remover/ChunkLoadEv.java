package com.teszvesz.remover;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadEv implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e){
        Chunks.processChunk(e.getChunk());
    }

}
