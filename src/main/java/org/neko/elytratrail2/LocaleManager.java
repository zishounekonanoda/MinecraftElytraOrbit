package org.neko.elytratrail2;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class LocaleManager {

    private final ElytraTrail2 plugin;
    private final Map<String, FileConfiguration> loadedLocales = new HashMap<>();
    private final Map<UUID, String> playerLocales = new HashMap<>();
    private File playerLocalesFile;
    private FileConfiguration playerLocalesConfig;

    public LocaleManager(ElytraTrail2 plugin) {
        this.plugin = plugin;
        // Load default locales that are packaged with the JAR
        saveDefaultLocale("en");
        saveDefaultLocale("ja");
        // Load player preferences
        loadPlayerLocales();
    }

    private void saveDefaultLocale(String lang) {
        File langFile = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("messages_" + lang + ".yml", false);
        }
        loadedLocales.put(lang, YamlConfiguration.loadConfiguration(langFile));
    }

    public void reloadLocales() {
        loadedLocales.clear();
        saveDefaultLocale("en");
        saveDefaultLocale("ja");
        loadPlayerLocales();
    }

    private void loadPlayerLocales() {
        playerLocalesFile = new File(plugin.getDataFolder(), "player_locales.yml");
        playerLocalesConfig = YamlConfiguration.loadConfiguration(playerLocalesFile);
        if (playerLocalesFile.exists()) {
            for (String uuidString : playerLocalesConfig.getKeys(false)) {
                playerLocales.put(UUID.fromString(uuidString), playerLocalesConfig.getString(uuidString));
            }
        }
    }

    public void savePlayerLocales() {
        try {
            for (Map.Entry<UUID, String> entry : playerLocales.entrySet()) {
                playerLocalesConfig.set(entry.getKey().toString(), entry.getValue());
            }
            playerLocalesConfig.save(playerLocalesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player locales!", e);
        }
    }

    public void setPlayerLocale(Player player, String lang) {
        playerLocales.put(player.getUniqueId(), lang);
    }

    public String getPlayerLocale(Player player) {
        return playerLocales.getOrDefault(player.getUniqueId(), "en"); // Default to English
    }

    public String getString(String key, Player player) {
        String lang = getPlayerLocale(player);
        FileConfiguration langConfig = loadedLocales.getOrDefault(lang, loadedLocales.get("en"));
        String message = langConfig.getString(key, loadedLocales.get("en".toString()).getString(key, "&cMissing translation key: " + key));
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getString(String key, Player player, Map<String, String> placeholders) {
        String message = getString(key, player);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
}
