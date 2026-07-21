package com.kurtaymc.skybuddy.listeners;

import com.kurtaymc.skybuddy.SkyBuddy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TraderLlama;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class NpcInteractListener implements Listener {

    private final SkyBuddy plugin;

    public NpcInteractListener(SkyBuddy plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTarget(EntityTargetEvent event) {
        Entity e = event.getEntity();
        if (e.getPersistentDataContainer().has(plugin.npcKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity e = event.getEntity();
        if (!(e instanceof LivingEntity) && !(e instanceof ArmorStand)) return;
        if (!e.getPersistentDataContainer().has(plugin.npcKey, PersistentDataType.BYTE)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity e = event.getEntity();
        if (e.getPersistentDataContainer().has(plugin.npcKey, PersistentDataType.BYTE)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            plugin.clearHolograms(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEntityEvent event) {
        Entity e = event.getRightClicked();

        if (!(e instanceof LivingEntity) && !(e instanceof ArmorStand)) return;
        if (!e.getPersistentDataContainer().has(plugin.npcKey, PersistentDataType.BYTE)) return;

        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (e instanceof Bee) {
            Bee bee = (Bee) e;
            if (bee.getAnger() > 0 && bee.getAnger() < 999999) {
                bee.setAnger(0);
            }
        }

        Player player = event.getPlayer();
        if (!plugin.isIslandMember(player, e.getLocation())) {
            player.sendMessage(plugin.getMessage("interact-error"));
            return;
        }

        player.performCommand(plugin.clickCommand);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeftClick(EntityDamageByEntityEvent event) {
        Entity e = event.getEntity();

        if (!(e instanceof LivingEntity) && !(e instanceof ArmorStand)) return;
        if (!e.getPersistentDataContainer().has(plugin.npcKey, PersistentDataType.BYTE)) return;

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (!plugin.isIslandMember(player, e.getLocation())) {
                player.sendMessage(plugin.getMessage("interact-error"));
                return;
            }

            plugin.editingPlayers.put(player.getUniqueId(), e.getUniqueId());

            if (e instanceof TraderLlama) {
                openLlamaMenu(player);
            } else if (e.getType() == EntityType.BEE) {
                Bee bee = (Bee) e;
                if (bee.getAnger() > 0 && bee.getAnger() < 999999) {
                    bee.setAnger(0);
                }
                openBeeMenu(player);
            } else {
                player.sendMessage(plugin.getMessage("no-menu"));
            }
        }
    }

    private void openLlamaMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, plugin.llamaMenuTitle);
        ItemStack background = buildItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            menu.setItem(i, background);
        }

        menu.setItem(10, buildItem(Material.BLACK_CARPET, plugin.getRawMessage("menus.items.carpet-black")));
        menu.setItem(11, buildItem(Material.ORANGE_CARPET, plugin.getRawMessage("menus.items.carpet-orange")));
        menu.setItem(12, buildItem(Material.MAGENTA_CARPET, plugin.getRawMessage("menus.items.carpet-magenta")));
        menu.setItem(13, buildItem(Material.LIGHT_BLUE_CARPET, plugin.getRawMessage("menus.items.carpet-lightblue")));
        menu.setItem(14, buildItem(Material.YELLOW_CARPET, plugin.getRawMessage("menus.items.carpet-yellow")));
        menu.setItem(15, buildItem(Material.LIME_CARPET, plugin.getRawMessage("menus.items.carpet-lime")));
        menu.setItem(16, buildItem(Material.PINK_CARPET, plugin.getRawMessage("menus.items.carpet-pink")));

        menu.setItem(19, buildItem(Material.GRAY_CARPET, plugin.getRawMessage("menus.items.carpet-gray")));
        menu.setItem(20, buildItem(Material.CYAN_CARPET, plugin.getRawMessage("menus.items.carpet-cyan")));
        menu.setItem(21, buildItem(Material.PURPLE_CARPET, plugin.getRawMessage("menus.items.carpet-purple")));
        menu.setItem(22, buildItem(Material.BLUE_CARPET, plugin.getRawMessage("menus.items.carpet-blue")));
        menu.setItem(23, buildItem(Material.BROWN_CARPET, plugin.getRawMessage("menus.items.carpet-brown")));
        menu.setItem(24, buildItem(Material.GREEN_CARPET, plugin.getRawMessage("menus.items.carpet-green")));
        menu.setItem(25, buildItem(Material.RED_CARPET, plugin.getRawMessage("menus.items.carpet-red")));

        menu.setItem(39, buildItem(Material.BONE_MEAL, plugin.getRawMessage("menus.items.adult")));
        menu.setItem(41, buildItem(Material.GHAST_TEAR, plugin.getRawMessage("menus.items.baby")));

        menu.setItem(53, buildItem(Material.BARRIER, plugin.getRawMessage("menus.items.close")));

        player.openInventory(menu);
    }

    private void openBeeMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 27, plugin.beeMenuTitle);
        ItemStack background = buildItem(Material.YELLOW_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            menu.setItem(i, background);
        }

        menu.setItem(10, buildItem(Material.SUNFLOWER, plugin.getRawMessage("menus.items.bee-calm-none")));
        menu.setItem(11, buildItem(Material.HONEYCOMB, plugin.getRawMessage("menus.items.bee-calm-nectar")));
        menu.setItem(12, buildItem(Material.RED_DYE, plugin.getRawMessage("menus.items.bee-angry-none")));
        menu.setItem(13, buildItem(Material.HONEY_BOTTLE, plugin.getRawMessage("menus.items.bee-angry-nectar")));

        menu.setItem(15, buildItem(Material.BONE_MEAL, plugin.getRawMessage("menus.items.adult")));
        menu.setItem(16, buildItem(Material.GHAST_TEAR, plugin.getRawMessage("menus.items.baby")));

        menu.setItem(26, buildItem(Material.BARRIER, plugin.getRawMessage("menus.items.close")));

        player.openInventory(menu);
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(plugin.llamaMenuTitle) || title.equals(plugin.beeMenuTitle)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        if (title.equals(plugin.llamaMenuTitle) || title.equals(plugin.beeMenuTitle)) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) return;
            if (item.getType().name().contains("GLASS_PANE")) return;

            if (item.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }

            if (plugin.editingPlayers.containsKey(player.getUniqueId())) {
                UUID buddyId = plugin.editingPlayers.get(player.getUniqueId());
                Entity entity = Bukkit.getEntity(buddyId);

                if (entity != null) {
                    Location staticLoc = entity.getLocation();

                    if (title.equals(plugin.llamaMenuTitle) && entity instanceof TraderLlama) {
                        TraderLlama llama = (TraderLlama) entity;
                        if (item.getType().name().contains("CARPET")) {
                            llama.getInventory().setDecor(new ItemStack(item.getType()));
                        }
                        else if (item.getType() == Material.BONE_MEAL) {
                            llama.setAdult();
                            updateSize(llama, staticLoc, true);
                        }
                        else if (item.getType() == Material.GHAST_TEAR) {
                            llama.setBaby();
                            updateSize(llama, staticLoc, false);
                        }
                    }
                    else if (title.equals(plugin.beeMenuTitle) && entity instanceof Bee) {
                        Bee bee = (Bee) entity;

                        if (item.getType() == Material.SUNFLOWER) {
                            bee.setAnger(0);
                            bee.setHasNectar(false);
                        }
                        else if (item.getType() == Material.HONEYCOMB) {
                            bee.setAnger(0);
                            bee.setHasNectar(true);
                        }
                        else if (item.getType() == Material.RED_DYE) {
                            bee.setAnger(Integer.MAX_VALUE);
                            bee.setHasNectar(false);
                        }
                        else if (item.getType() == Material.HONEY_BOTTLE) {
                            bee.setAnger(Integer.MAX_VALUE);
                            bee.setHasNectar(true);
                        }
                        else if (item.getType() == Material.BONE_MEAL) {
                            bee.setAdult();
                            updateSize(bee, staticLoc, true);
                        }
                        else if (item.getType() == Material.GHAST_TEAR) {
                            bee.setBaby();
                            updateSize(bee, staticLoc, false);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(plugin.llamaMenuTitle) || title.equals(plugin.beeMenuTitle)) {
            plugin.editingPlayers.remove(event.getPlayer().getUniqueId());
        }
    }

    private void updateSize(Entity buddy, Location staticLoc, boolean isAdult) {
        new BukkitRunnable() {
            @Override
            public void run() {
                buddy.teleport(staticLoc);
            }
        }.runTaskLater(plugin, 1L);

        plugin.clearHolograms(buddy);
        plugin.createHolograms(buddy, staticLoc, isAdult);
    }

    private ItemStack buildItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}