package com.kurtaymc.skybuddy;

import com.kurtaymc.skybuddy.commands.SkyBuddyCommand;
import com.kurtaymc.skybuddy.listeners.NpcInteractListener;
import com.kurtaymc.skybuddy.managers.BentoBoxManager;
import com.kurtaymc.skybuddy.managers.SS2Manager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Player;
import org.bukkit.entity.TraderLlama;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SkyBuddy extends JavaPlugin {

    public NamespacedKey npcKey;
    public NamespacedKey holoLineKey;
    public NamespacedKey islandKey;
    public final HashMap<UUID, UUID> editingPlayers = new HashMap<>();
    public final HashMap<UUID, List<UUID>> hologramMap = new HashMap<>();

    public enum SkyblockType { BENTOBOX, SUPERIOR, NONE }
    public SkyblockType activeSkyblock = SkyblockType.NONE;

    public double offsetX, offsetY, offsetZ;
    public float yaw;
    public EntityType entityType;
    public String defaultSize;
    public List<String> holoLines;
    public String clickCommand;

    public String llamaMenuTitle;
    public String beeMenuTitle;

    private FileConfiguration langConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.npcKey = new NamespacedKey(this, "buddy_npc");
        this.holoLineKey = new NamespacedKey(this, "holo_line");
        this.islandKey = new NamespacedKey(this, "island_id");

        loadSettings();

        if (getServer().getPluginManager().getPlugin("BentoBox") != null) {
            activeSkyblock = SkyblockType.BENTOBOX;
            getServer().getPluginManager().registerEvents(new BentoBoxManager(this), this);
            getLogger().info("Hooked into BentoBox!");
        } else if (getServer().getPluginManager().getPlugin("SuperiorSkyblock2") != null) {
            activeSkyblock = SkyblockType.SUPERIOR;
            getServer().getPluginManager().registerEvents(new SS2Manager(this), this);
            getLogger().info("Hooked into SuperiorSkyblock2!");
        } else {
            getLogger().warning("No supported Skyblock plugin found!");
        }

        getServer().getPluginManager().registerEvents(new NpcInteractListener(this), this);

        PluginCommand cmd = getCommand("skybuddy");
        if (cmd != null) {
            cmd.setExecutor(new SkyBuddyCommand(this));
        } else {
            getLogger().severe("Command 'skybuddy' not found in plugin.yml!");
        }

        getLogger().info("SkyBuddy has been enabled successfully.");
    }

    @Override
    public void onDisable() {
        editingPlayers.clear();
        hologramMap.clear();
        getLogger().info("SkyBuddy has been disabled.");
    }

    public void loadSettings() {
        reloadConfig();

        this.offsetX = getConfig().getDouble("spawn-offset.x");
        this.offsetY = getConfig().getDouble("spawn-offset.y");
        this.offsetZ = getConfig().getDouble("spawn-offset.z");
        this.yaw = (float) getConfig().getDouble("spawn-offset.yaw", 0.0);
        this.defaultSize = getConfig().getString("default-size", "ADULT");
        this.clickCommand = getConfig().getString("click-command", "is");

        String typeStr = getConfig().getString("entity-type", "TRADER_LLAMA").toUpperCase();
        if (typeStr.equals("BEE")) {
            this.entityType = EntityType.BEE;
        } else {
            this.entityType = EntityType.TRADER_LLAMA;
        }

        String lang = getConfig().getString("language", "en");
        File langDir = new File(getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        File enFile = new File(langDir, "lang-en.yml");
        if (!enFile.exists()) saveResource("lang/lang-en.yml", false);

        File trFile = new File(langDir, "lang-tr.yml");
        if (!trFile.exists()) saveResource("lang/lang-tr.yml", false);

        File targetLangFile = new File(langDir, "lang-" + lang + ".yml");
        if (!targetLangFile.exists()) {
            getLogger().warning("Language file not found: " + targetLangFile.getName() + " -> defaulting to lang-en.yml");
            targetLangFile = enFile;
        }

        langConfig = YamlConfiguration.loadConfiguration(targetLangFile);

        this.holoLines = langConfig.getStringList("holograms");
        if (this.holoLines == null || this.holoLines.isEmpty()) {
            this.holoLines = getConfig().getStringList("hologram-lines");
        }

        this.llamaMenuTitle = getRawMessage("menus.llama-title");
        this.beeMenuTitle = getRawMessage("menus.bee-title");
    }

    public String getMessage(String path) {
        String prefix = langConfig.getString("messages.prefix", "&8[&bSkyBuddy&8] &7");
        String msg = langConfig.getString("messages." + path, "&cMessage not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    public String getRawMessage(String path) {
        String msg = langConfig.getString(path, "&cString not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public List<String> getMessageList(String path) {
        List<String> list = langConfig.getStringList("messages." + path);
        List<String> colored = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            colored.add(ChatColor.RED + "Message list not found: " + path);
            return colored;
        }
        for (String s : list) {
            colored.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return colored;
    }

    public boolean isIslandMember(Player p, Location loc) {
        if (p.hasPermission("skybuddy.admin")) return true;
        if (activeSkyblock == SkyblockType.BENTOBOX) return BentoBoxManager.isMember(p, loc);
        if (activeSkyblock == SkyblockType.SUPERIOR) return SS2Manager.isMember(p, loc);
        return false;
    }

    public Location getIslandCenter(Player p) {
        if (activeSkyblock == SkyblockType.BENTOBOX) return BentoBoxManager.getIslandCenter(p);
        if (activeSkyblock == SkyblockType.SUPERIOR) return SS2Manager.getIslandCenter(p);
        return null;
    }

    public boolean isIslandOwner(Player p) {
        if (activeSkyblock == SkyblockType.BENTOBOX) return BentoBoxManager.isOwner(p);
        if (activeSkyblock == SkyblockType.SUPERIOR) return SS2Manager.isOwner(p);
        return false;
    }

    public String getIslandId(Player p) {
        if (activeSkyblock == SkyblockType.BENTOBOX) return BentoBoxManager.getIslandId(p);
        if (activeSkyblock == SkyblockType.SUPERIOR) return SS2Manager.getIslandId(p);
        return null;
    }

    public void clearOldBuddy(Location center, String islandId) {
        if (center == null || center.getWorld() == null) return;

        Location targetLoc = center.clone().add(this.offsetX, this.offsetY, this.offsetZ);
        for (Entity e : targetLoc.getWorld().getNearbyEntities(targetLoc, 20, 20, 20)) {
            if (e.getPersistentDataContainer().has(this.npcKey, PersistentDataType.BYTE)) {

                if (islandId != null && e.getPersistentDataContainer().has(this.islandKey, PersistentDataType.STRING)) {
                    String savedId = e.getPersistentDataContainer().get(this.islandKey, PersistentDataType.STRING);
                    if (!islandId.equals(savedId)) continue;
                }

                clearHolograms(e);
                e.remove();
            }
        }
    }

    public void clearHolograms(Entity buddy) {
        List<UUID> holos = this.hologramMap.remove(buddy.getUniqueId());
        if (holos != null) {
            for (UUID holoId : holos) {
                Entity e = getServer().getEntity(holoId);
                if (e != null) e.remove();
            }
        }

        // HATA ÇÖZÜMÜ: RAM silinmelerine karşı fiziksel alan taraması eklendi.
        // Asistan'ın etrafındaki "hayalet" kalmış eski hologramları bulup tamamen siler.
        Location loc = buddy.getLocation();
        if (loc != null && loc.getWorld() != null) {
            for (Entity e : loc.getWorld().getNearbyEntities(loc, 3.0, 5.0, 3.0)) {
                if (e instanceof ArmorStand && e.getPersistentDataContainer().has(this.npcKey, PersistentDataType.BYTE)) {
                    Byte type = e.getPersistentDataContainer().get(this.npcKey, PersistentDataType.BYTE);
                    if (type != null && type == (byte) 2) { // 2 = Sadece Hologramları hedef alır
                        e.remove();
                    }
                }
            }
        }
    }

    public void createHolograms(Entity buddy, Location spawnLoc, boolean isAdult) {
        if (this.holoLines == null || this.holoLines.isEmpty()) return;

        // Hologramlara da ada ID'si tanımlanıyor.
        String islandId = null;
        if (buddy.getPersistentDataContainer().has(this.islandKey, PersistentDataType.STRING)) {
            islandId = buddy.getPersistentDataContainer().get(this.islandKey, PersistentDataType.STRING);
        }

        double yOffset = 1.0;
        if (buddy instanceof TraderLlama) {
            yOffset = isAdult ? 1.8 : 1.0;
        } else if (this.entityType == EntityType.BEE) {
            yOffset = isAdult ? 0.8 : 0.4;
        }

        double y = spawnLoc.getY() + yOffset;
        List<UUID> attachedHolograms = new ArrayList<>();

        for (int i = this.holoLines.size() - 1; i >= 0; i--) {
            String line = this.holoLines.get(i);
            if (line == null || line.trim().isEmpty()) {
                y += 0.3; continue;
            }

            String stripped = ChatColor.stripColor(line.replace("&", "§"));
            if (stripped == null || stripped.trim().isEmpty()) {
                y += 0.3; continue;
            }

            Location lineLoc = spawnLoc.clone();
            lineLoc.setY(y);

            ArmorStand stand = (ArmorStand) spawnLoc.getWorld().spawnEntity(lineLoc, EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setMarker(true);
            stand.setGravity(false);
            stand.setBasePlate(false);
            stand.setCustomName(line.replace("&", "§"));
            stand.setCustomNameVisible(true);

            stand.getPersistentDataContainer().set(this.npcKey, PersistentDataType.BYTE, (byte) 2);
            stand.getPersistentDataContainer().set(this.holoLineKey, PersistentDataType.INTEGER, i);

            if (islandId != null) {
                stand.getPersistentDataContainer().set(this.islandKey, PersistentDataType.STRING, islandId);
            }

            attachedHolograms.add(stand.getUniqueId());
            y += 0.3;
        }

        this.hologramMap.put(buddy.getUniqueId(), attachedHolograms);
    }

    public void spawnBuddy(Location center, String islandId) {
        if (center == null || center.getWorld() == null) return;

        Location spawnLoc = center.clone().add(this.offsetX, this.offsetY, this.offsetZ);
        spawnLoc.setYaw(this.yaw);

        Entity buddy = spawnLoc.getWorld().spawnEntity(spawnLoc, this.entityType);
        buddy.teleport(spawnLoc);

        if (buddy instanceof LivingEntity) {
            LivingEntity livingBuddy = (LivingEntity) buddy;
            livingBuddy.setAI(false);
            livingBuddy.setSilent(true);
            livingBuddy.setRemoveWhenFarAway(false);
        }

        buddy.setGravity(false);
        buddy.setCustomNameVisible(false);
        buddy.setInvulnerable(true);

        buddy.getPersistentDataContainer().set(this.npcKey, PersistentDataType.BYTE, (byte) 1);

        if (islandId != null) {
            buddy.getPersistentDataContainer().set(this.islandKey, PersistentDataType.STRING, islandId);
        }

        boolean isAdult = !this.defaultSize.equalsIgnoreCase("BABY");

        if (buddy instanceof TraderLlama) {
            TraderLlama llama = (TraderLlama) buddy;
            llama.getInventory().setDecor(new ItemStack(Material.PURPLE_CARPET));
            llama.setColor(Llama.Color.WHITE);
            if (!isAdult) llama.setBaby();
            else llama.setAdult();
        }
        else if (buddy instanceof Ageable) {
            Ageable ageable = (Ageable) buddy;
            if (!isAdult) ageable.setBaby();
            else ageable.setAdult();
        }

        createHolograms(buddy, spawnLoc, isAdult);
    }
}