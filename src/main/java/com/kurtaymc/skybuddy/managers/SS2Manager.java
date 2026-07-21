package com.kurtaymc.skybuddy.managers;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.IslandCreateEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.kurtaymc.skybuddy.SkyBuddy;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class SS2Manager implements Listener {
    private final SkyBuddy plugin;

    public SS2Manager(SkyBuddy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIslandCreate(IslandCreateEvent event) {
        Island island = event.getIsland();

        new BukkitRunnable() {
            @Override
            public void run() {
                Location center = getSafeCenter(island);
                if (center != null) {
                    String islandId = island.getUniqueId().toString();
                    plugin.clearOldBuddy(center, islandId);
                    plugin.spawnBuddy(center, islandId);
                }
            }
        }.runTaskLater(plugin, 40L);
    }

    public static boolean isMember(Player player, Location loc) {
        Island island = SuperiorSkyblockAPI.getIslandAt(loc);
        SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
        if (island != null) {
            if (island.getOwner().equals(sPlayer)) return true;
            if (island.isMember(sPlayer)) return true;
        }
        return false;
    }

    public static Location getIslandCenter(Player player) {
        SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
        Island island = sPlayer.getIsland();
        return island != null ? getSafeCenter(island) : null;
    }

    public static boolean isOwner(Player player) {
        SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
        Island island = sPlayer.getIsland();
        return island != null && island.getOwner().equals(sPlayer);
    }

    public static String getIslandId(Player player) {
        SuperiorPlayer sPlayer = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
        Island island = sPlayer.getIsland();
        return island != null ? island.getUniqueId().toString() : null;
    }

    private static Location getSafeCenter(Island island) {
        Location safeLoc = null;

        try {
            java.util.Map<?, Location> homes = island.getIslandHomesAsDimensions();
            if (homes != null && !homes.isEmpty()) {
                safeLoc = homes.values().iterator().next().clone();
            }
        } catch (Throwable ignored) {}

        if (safeLoc == null) {
            try {
                safeLoc = ((Location) island.getClass().getMethod("getCenter").invoke(island)).clone();
                if (safeLoc != null) {
                    int highestY = safeLoc.getWorld().getHighestBlockYAt(safeLoc.getBlockX(), safeLoc.getBlockZ());
                    safeLoc.setY(highestY + 1.0);
                }
            } catch (Throwable ignored) {}
        }

        return safeLoc;
    }
}