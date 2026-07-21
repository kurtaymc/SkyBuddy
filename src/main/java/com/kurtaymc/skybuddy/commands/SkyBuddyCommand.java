package com.kurtaymc.skybuddy.commands;

import com.kurtaymc.skybuddy.SkyBuddy;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyBuddyCommand implements CommandExecutor {
    private final SkyBuddy plugin;

    public SkyBuddyCommand(SkyBuddy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("skybuddy.admin")) {
                    plugin.loadSettings();
                    sender.sendMessage(plugin.getMessage("reload-success"));
                } else {
                    sender.sendMessage(plugin.getMessage("no-permission"));
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("respawn") || args[0].equalsIgnoreCase("spawn")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessage("only-players"));
                    return true;
                }

                Player player = (Player) sender;
                Location center = plugin.getIslandCenter(player);

                // BUG 1 FIX: Adanın merkezi null dönüyorsa oyuncu koordinatını kopyalama, işlemi iptal et.
                if (center == null) {
                    player.sendMessage(plugin.getMessage("need-island"));
                    return true;
                }

                if (!plugin.isIslandOwner(player) && !player.hasPermission("skybuddy.admin")) {
                    player.sendMessage(plugin.getMessage("not-owner"));
                    return true;
                }

                String islandId = plugin.getIslandId(player);
                plugin.clearOldBuddy(center, islandId);
                plugin.spawnBuddy(center, islandId);

                player.sendMessage(plugin.getMessage("spawn-success"));
                return true;
            }
        }

        for (String line : plugin.getMessageList("help-menu")) {
            sender.sendMessage(line);
        }
        return true;
    }
}