package org.neko.elytratrail2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.Locale;

public class GuiManager {

    public static final Map<UUID, Integer> playerPages = new HashMap<>();
    private static final int ITEMS_PER_PAGE = 45;

    public static void openTrailGui(Player player, ElytraTrail2 plugin, int page) {
        LocaleManager lm = plugin.getLocaleManager();
        List<String> configuredGroups = new ArrayList<>(plugin.getConfiguredGroups());
        int totalPages = (int) Math.ceil((double) configuredGroups.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        page = Math.max(0, Math.min(page, totalPages - 1));
        playerPages.put(player.getUniqueId(), page);

        String guiTitle = lm.getString("gui.title.main", player, Map.of("page", String.valueOf(page + 1), "totalPages", String.valueOf(totalPages)));
        Inventory gui = Bukkit.createInventory(new TrailGuiHolder(), 54, guiTitle);

        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, configuredGroups.size());

        for (int i = startIndex; i < endIndex; i++) {
            String groupName = configuredGroups.get(i);
            gui.addItem(createDisplayItem(groupName, player, lm, plugin));
        }

        if (page > 0) {
            gui.setItem(45, createNavItem(Material.ARROW, lm.getString("gui.item.nav.prev", player)));
        }

        gui.setItem(48, createNavItem(Material.PLAYER_HEAD, lm.getString("gui.item.lang.name", player)));
        gui.setItem(49, createNavItem(Material.BARRIER, lm.getString("gui.item.remove_trail.name", player)));

        if (endIndex < configuredGroups.size()) {
            gui.setItem(53, createNavItem(Material.ARROW, lm.getString("gui.item.nav.next", player)));
        }

        player.openInventory(gui);
    }

    public static void openLanguageGui(Player player, ElytraTrail2 plugin) {
        LocaleManager lm = plugin.getLocaleManager();
        String guiTitle = lm.getString("gui.title.lang", player);
        Inventory gui = Bukkit.createInventory(new LangGuiHolder(), 9, guiTitle);

        ItemStack english = createNavItem(Material.RED_BANNER, lm.getString("gui.item.lang.en", player));
        ItemStack japanese = createNavItem(Material.WHITE_BANNER, lm.getString("gui.item.lang.ja", player));

        gui.setItem(3, english);
        gui.setItem(5, japanese);

        player.openInventory(gui);
    }

    private static ItemStack createNavItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createDisplayItem(String groupName, Player player, LocaleManager lm, ElytraTrail2 plugin) {
        Material material = resolveMaterialForGroup(groupName, plugin);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            ChatColor color = getChatColorForGroup(groupName);
            Map<String, String> placeholders = Map.of(
                    "color", color.toString() + ChatColor.BOLD,
                    "trail", groupName
            );
            meta.setDisplayName(lm.getString("gui.item.trail.name", player, placeholders));
            meta.setLore(Collections.singletonList(lm.getString("gui.item.trail.lore", player)));
            NamespacedKey key = new NamespacedKey(plugin, "trail_id");
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, groupName);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static Material resolveMaterialForGroup(String groupName, ElytraTrail2 plugin) {
        String basePath = "groups." + groupName + ".";
        // Preferred new syntax: MATERIAL: DIAMOND
        String iconValue = plugin.getConfig().getString(basePath + "MATERIAL");
        Material parsed = parseMaterial(iconValue);

        if (parsed == null) {
            // Backward compatibility with icon: MATERIAL:DIAMOND
            String legacyIconValue = plugin.getConfig().getString(basePath + "icon");
            parsed = parseMaterial(legacyIconValue);
            if (parsed == null && legacyIconValue != null && !legacyIconValue.isBlank()) {
                plugin.getLogger().warning("Invalid icon '" + legacyIconValue + "' for group '" + groupName + "'. Falling back to GLOWSTONE_DUST.");
            }
            if (parsed != null) {
                return parsed;
            }
        } else {
            return parsed;
        }

        if (iconValue != null && !iconValue.isBlank()) {
            plugin.getLogger().warning("Invalid MATERIAL entry '" + iconValue + "' for group '" + groupName + "'. Falling back to GLOWSTONE_DUST.");
        }
        return Material.GLOWSTONE_DUST;
    }

    private static Material parseMaterial(String iconValue) {
        if (iconValue == null || iconValue.isBlank()) {
            return null;
        }
        String trimmed = iconValue.trim();
        if (trimmed.toUpperCase(Locale.ROOT).startsWith("MATERIAL:")) {
            trimmed = trimmed.substring("MATERIAL:".length()).trim();
        }
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return Material.valueOf(trimmed.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static ChatColor getChatColorForGroup(String groupName) {
        return switch (groupName.toUpperCase()) {
            case "PREDATOR" -> ChatColor.RED;
            case "MASTER" -> ChatColor.LIGHT_PURPLE;
            case "DIAMOND" -> ChatColor.AQUA;
            default -> ChatColor.YELLOW;
        };
    }
}
