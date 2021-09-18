package com.teszvesz.remover;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.logging.Logger;

public final class TaskUtility {

    private static final Logger LOGGER = Logger.getLogger(TaskUtility.class.getCanonicalName());

    private TaskUtility() { }

    public static void waitForBukkitTask(Plugin plugin, Runnable runnable, long timeout, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                runnable.run();
                completableFuture.complete(true);
            } catch (Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        });
        completableFuture.get(timeout, timeUnit);
    }

    public static <T> CompletableFuture<T> runWithTimingsAsync(final String name, final Supplier<T> task) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        Thread thread = new Thread(() -> withTimings(name, () -> {
            try {
                completableFuture.complete(task.get());
            } catch (Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        }), name);
        thread.setDaemon(true);
        thread.start();
        return completableFuture;
    }

    public static void withTimings(String taskName, Runnable runnable) {
        long start = System.currentTimeMillis();
        LOGGER.info("STARTED: " + taskName + "...");
        runnable.run();
        LOGGER.info("FINISHED: " + taskName + ", took " + (System.currentTimeMillis() - start) + "ms");
    }

}
