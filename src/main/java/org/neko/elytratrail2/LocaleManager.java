package org.neko.elytratrail2;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class LocaleManager {

    private final ElytraTrail2 plugin;
    private final Map<String, FileConfiguration> loadedLocales = new HashMap<>();
    private final Map<UUID, String> playerLocales = new HashMap<>();
    private static final String DEFAULT_LANG = "en";
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
        playerLocales.clear();
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
            if (!playerLocalesFile.exists()) {
                playerLocalesFile.getParentFile().mkdirs();
            }
            playerLocalesConfig.save(playerLocalesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player locales!", e);
        }
    }

    public void setPlayerLocale(Player player, String lang) {
        playerLocales.put(player.getUniqueId(), normalizeLanguage(lang));
    }

    public String getPlayerLocale(Player player) {
        if (player == null) {
            return DEFAULT_LANG;
        }
        return playerLocales.computeIfAbsent(player.getUniqueId(), uuid -> detectPlayerLocale(player));
    }

    private String detectPlayerLocale(Player player) {
        String detected = invokeLocaleGetter(player, "locale");
        if (detected == null) {
            detected = invokeLocaleGetter(player, "getLocale");
        }
        return normalizeLanguage(detected);
    }

    private String invokeLocaleGetter(Player player, String methodName) {
        try {
            Object result = player.getClass().getMethod(methodName).invoke(player);
            if (result instanceof Locale locale) {
                return locale.getLanguage();
            }
            if (result != null) {
                return result.toString();
            }
        } catch (ReflectiveOperationException | SecurityException ignored) {
            // Method not available on this server implementation
        }
        return null;
    }

    private String normalizeLanguage(String lang) {
        if (lang == null || lang.isBlank()) {
            return DEFAULT_LANG;
        }
        String normalized = lang.trim().toLowerCase(Locale.ROOT);
        int separator = normalized.indexOf('_');
        if (separator > -1) {
            normalized = normalized.substring(0, separator);
        }
        if (!loadedLocales.containsKey(normalized)) {
            return DEFAULT_LANG;
        }
        return normalized;
    }

    public String getString(String key, Player player) {
        String lang = (player != null) ? getPlayerLocale(player) : DEFAULT_LANG;
        FileConfiguration langConfig = loadedLocales.getOrDefault(lang, loadedLocales.get(DEFAULT_LANG));
        String message = langConfig.getString(key);
        if (message == null) {
            message = loadedLocales.get(DEFAULT_LANG).getString(key, "&cMissing translation key: " + key);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getString(String key, Player player, Map<String, String> placeholders) {
        String message = getString(key, player);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return message;
    }
}
