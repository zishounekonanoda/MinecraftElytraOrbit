package org.neko.elytratrail2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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
            gui.addItem(createDisplayItem(groupName, player, lm));
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

    private static ItemStack createDisplayItem(String groupName, Player player, LocaleManager lm) {
        Material material = getMaterialForGroup(groupName);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(getChatColorForGroup(groupName) + "" + ChatColor.BOLD + groupName + " Trail");
            meta.setLore(Collections.singletonList(lm.getString("gui.item.trail.lore", player)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static Material getMaterialForGroup(String groupName) {
        return switch (groupName.toUpperCase()) {
            case "PREDATOR" -> Material.NETHERITE_INGOT;
            case "MASTER" -> Material.AMETHYST_SHARD;
            case "DIAMOND" -> Material.DIAMOND;
            default -> Material.GLOWSTONE_DUST;
        };
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
