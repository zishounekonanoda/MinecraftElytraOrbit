package org.neko.elytratrail2;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class GuiListener implements Listener {

    private final ElytraTrail2 plugin;
    private final LocaleManager lm;

    public GuiListener(ElytraTrail2 plugin) {
        this.plugin = plugin;
        this.lm = plugin.getLocaleManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() == null) return;

        Player player = (Player) event.getWhoClicked();

        if (inventory.getHolder() instanceof TrailGuiHolder) {
            handleMainGuiClick(event, player);
        } else if (inventory.getHolder() instanceof LangGuiHolder) {
            handleLangGuiClick(event, player);
        }
    }

    private void handleMainGuiClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        int currentPage = GuiManager.playerPages.getOrDefault(player.getUniqueId(), 0);

        // Navigation
        if (clickedItem.getType() == Material.ARROW) {
            if (clickedItem.getItemMeta().getDisplayName().contains(ChatColor.stripColor(lm.getString("gui.item.nav.next", player)))) {
                GuiManager.openTrailGui(player, plugin, currentPage + 1);
            } else if (clickedItem.getItemMeta().getDisplayName().contains(ChatColor.stripColor(lm.getString("gui.item.nav.prev", player)))) {
                GuiManager.openTrailGui(player, plugin, currentPage - 1);
            }
            return;
        }

        // Language Button
        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            GuiManager.openLanguageGui(player, plugin);
            return;
        }

        // Remove Button
        if (clickedItem.getType() == Material.BARRIER) {
            removeAllTrailTags(player);
            player.sendMessage(lm.getString("command.trail_removed", player));
            player.closeInventory();
            return;
        }

        // Trail Selection
        String displayName = clickedItem.getItemMeta().getDisplayName();
        String groupName = ChatColor.stripColor(displayName).replace(" Trail", "").replace("&l", "");

        if (plugin.getConfiguredGroups().contains(groupName)) {
            removeAllTrailTags(player);
            player.addScoreboardTag(groupName);
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                player.setAllowFlight(true);
            }
            player.sendMessage(lm.getString("command.trail_set", player, Map.of("trail", displayName)));
        } else {
            player.sendMessage(lm.getString("command.unknown_trail", player));
        }
        player.closeInventory();
    }

    private void handleLangGuiClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        if (clickedItem.getItemMeta().getDisplayName().equals(lm.getString("gui.item.lang.en", player))) {
            lm.setPlayerLocale(player, "en");
            player.sendMessage(lm.getString("command.lang_set", player));
        } else if (clickedItem.getItemMeta().getDisplayName().equals(lm.getString("gui.item.lang.ja", player))) {
            lm.setPlayerLocale(player, "ja");
            player.sendMessage(lm.getString("command.lang_set", player));
        }
        GuiManager.openTrailGui(player, plugin, 0); // Re-open main GUI
    }

    private void removeAllTrailTags(Player player) {
        Set<String> configuredGroups = plugin.getConfiguredGroups();
        for (String existingTag : new ArrayList<>(player.getScoreboardTags())) {
            if (configuredGroups.contains(existingTag)) {
                player.removeScoreboardTag(existingTag);
            }
        }
        if (plugin.getMatchingGroupTag(player).isEmpty()) {
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                player.setAllowFlight(false);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof TrailGuiHolder) {
            GuiManager.playerPages.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GuiManager.playerPages.remove(event.getPlayer().getUniqueId());
    }
}