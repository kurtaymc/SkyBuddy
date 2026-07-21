package com.kurtaymc.skybuddy.managers;

import com.kurtaymc.skybuddy.SkyBuddy;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.island.IslandCreatedEvent;
import world.bentobox.bentobox.api.events.island.IslandResetEvent;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;

import java.util.Optional;

public class BentoBoxManager implements Listener {
    private final SkyBuddy plugin;
    public static IslandsManager islandsManager;

    public BentoBoxManager(SkyBuddy plugin) {
        this.plugin = plugin;
        islandsManager = BentoBox.getInstance().getIslands();
    }

    @EventHandler
    public void onIslandCreate(IslandCreatedEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location center = event.getIsland().getCenter();
                String islandId = String.valueOf(event.getIsland().getUniqueId());
                plugin.clearOldBuddy(center, islandId);
                plugin.spawnBuddy(center, islandId);
            }
        }.runTaskLater(plugin, 40L);
    }

    @EventHandler
    public void onIslandReset(IslandResetEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location center = event.getIsland().getCenter();
                String islandId = String.valueOf(event.getIsland().getUniqueId());
                plugin.clearOldBuddy(center, islandId);
                plugin.spawnBuddy(center, islandId);
            }
        }.runTaskLater(plugin, 40L);
    }

    public static boolean isMember(Player player, Location loc) {
        Optional<Island> islandOpt = islandsManager.getIslandAt(loc);
        if (islandOpt.isPresent()) {
            Island island = islandOpt.get();
            if (island.getOwner() != null && island.getOwner().equals(player.getUniqueId())) return true;
            if (island.getMembers().containsKey(player.getUniqueId())) return true;
        }
        return false;
    }

    public static Location getIslandCenter(Player player) {
        Island island = islandsManager.getIsland(player.getWorld(), player.getUniqueId());
        return island != null ? island.getCenter() : null;
    }

    public static boolean isOwner(Player player) {
        Island island = islandsManager.getIsland(player.getWorld(), player.getUniqueId());
        return island != null && island.getOwner() != null && island.getOwner().equals(player.getUniqueId());
    }

    public static String getIslandId(Player player) {
        Island island = islandsManager.getIsland(player.getWorld(), player.getUniqueId());
        return island != null ? String.valueOf(island.getUniqueId()) : null;
    }
}