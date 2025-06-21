package com.hmmbo.ultimate_Shop_Core.utils.timer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class Timer {

    private static boolean foliaPresent;
    private static Object regionSchedulerInstance;
    private static Method runAtTickMethod;
    private static Method runAtLocationMethod;

    private static Plugin pluginInstance;

    static {
        try {
            Class<?> regionSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            Class<?> serverClass = Class.forName("org.bukkit.Server");

            Method getServerMethod = Class.forName("org.bukkit.Bukkit").getMethod("getServer");
            Object server = getServerMethod.invoke(null);

            Method getRegionScheduler = serverClass.getMethod("getRegionScheduler");
            regionSchedulerInstance = getRegionScheduler.invoke(server);

            runAtTickMethod = regionSchedulerClass.getMethod("runAtTick", int.class, Runnable.class);

            try {
                runAtLocationMethod = regionSchedulerClass.getMethod("runAtLocation", Location.class, Runnable.class);
            } catch (NoSuchMethodException ignored) {
                runAtLocationMethod = null;
            }

            foliaPresent = true;
        } catch (Exception e) {
            foliaPresent = false;
        }
    }

    public static void setPluginInstance(Plugin plugin) {
        pluginInstance = plugin;
    }

    // Async immediate
    public static void runAsync(Runnable task) {
        if (foliaPresent) {
            try {
                runAtTickMethod.invoke(regionSchedulerInstance, 0, task);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        runAsyncFallback(task);
    }

    // Async delayed (ticks)
    public static void runAsyncDelayed(int ticks, Runnable task) {
        if (foliaPresent) {
            try {
                runAtTickMethod.invoke(regionSchedulerInstance, ticks, task);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        runAsyncDelayedFallback(ticks, task);
    }

    // Sync immediate
    public static void runSync(Runnable task) {
        if (foliaPresent) {
            try {
                runAtTickMethod.invoke(regionSchedulerInstance, 0, task);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        runSyncFallback(task);
    }

    // Sync delayed (ticks)
    public static void runSyncDelayed(int ticks, Runnable task) {
        if (foliaPresent) {
            try {
                runAtTickMethod.invoke(regionSchedulerInstance, ticks, task);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        runSyncDelayedFallback(ticks, task);
    }

    // Run task at location (folia region scheduler only)
    public static void runAtLocation(Location loc, Runnable task) {
        if (foliaPresent && runAtLocationMethod != null) {
            try {
                runAtLocationMethod.invoke(regionSchedulerInstance, loc, task);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        runSync(task);
    }

    // Run repeating task sync (spigot only fallback)
    public static BukkitTask runRepeatingSync(long delayTicks, long periodTicks, Runnable task) {
        checkPluginInstance();
        if (!foliaPresent) {
            return Bukkit.getScheduler().runTaskTimer(pluginInstance, task, delayTicks, periodTicks);
        } else {
            // Folia region scheduler has no repeating task method, fallback to Spigot sync repeating
            return Bukkit.getScheduler().runTaskTimer(pluginInstance, task, delayTicks, periodTicks);
        }
    }

    // Run repeating task async (spigot only fallback)
    public static BukkitTask runRepeatingAsync(long delayTicks, long periodTicks, Runnable task) {
        checkPluginInstance();
        if (!foliaPresent) {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(pluginInstance, task, delayTicks, periodTicks);
        } else {
            // Folia region scheduler has no repeating task method, fallback to Spigot async repeating
            return Bukkit.getScheduler().runTaskTimerAsynchronously(pluginInstance, task, delayTicks, periodTicks);
        }
    }

    // CompletableFuture wrapper for async tasks
    public static CompletableFuture<Void> runAsyncFuture(Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        runAsync(() -> {
            try {
                task.run();
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    // CompletableFuture wrapper for sync tasks
    public static CompletableFuture<Void> runSyncFuture(Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        runSync(() -> {
            try {
                task.run();
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    // Fallback methods

    private static void runAsyncFallback(Runnable task) {
        checkPluginInstance();
        Bukkit.getScheduler().runTaskAsynchronously(pluginInstance, task);
    }

    private static void runAsyncDelayedFallback(int ticks, Runnable task) {
        checkPluginInstance();
        Bukkit.getScheduler().runTaskLaterAsynchronously(pluginInstance, task, ticks);
    }

    private static void runSyncFallback(Runnable task) {
        checkPluginInstance();
        Bukkit.getScheduler().runTask(pluginInstance, task);
    }

    private static void runSyncDelayedFallback(int ticks, Runnable task) {
        checkPluginInstance();
        Bukkit.getScheduler().runTaskLater(pluginInstance, task, ticks);
    }

    private static void checkPluginInstance() {
        if (pluginInstance == null) {
            throw new IllegalStateException("Plugin instance not set. Call Timer.setPluginInstance(plugin) in your plugin's onEnable.");
        }
    }
}
