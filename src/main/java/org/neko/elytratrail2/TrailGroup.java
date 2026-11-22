package org.neko.elytratrail2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A configured orbit group (icon, name color and its effects), parsed once per load/reload.
 */
public record TrailGroup(String name, Material icon, ChatColor color, List<ParticleConfig> effects) {

    private static final Material DEFAULT_ICON = Material.GLOWSTONE_DUST;

    public static Map<String, TrailGroup> loadAll(FileConfiguration config, Logger logger) {
        Map<String, TrailGroup> result = new LinkedHashMap<>();
        ConfigurationSection groups = config.getConfigurationSection("groups");
        if (groups == null) return Collections.emptyMap();

        for (String name : groups.getKeys(false)) {
            ConfigurationSection sec = groups.getConfigurationSection(name);
            if (sec == null) continue;
            result.put(name, load(name, sec, logger));
        }
        return Collections.unmodifiableMap(result);
    }

    private static TrailGroup load(String name, ConfigurationSection sec, Logger logger) {
        List<ParticleConfig> effects = new ArrayList<>();
        List<Map<?, ?>> effectList = sec.getMapList("effects");
        if (!effectList.isEmpty()) {
            for (int i = 0; i < effectList.size(); i++) {
                ParticleConfig pc = ParticleConfig.from(effectList.get(i), name + ".effects[" + i + "]", logger);
                if (pc != null) effects.add(pc);
            }
        } else if (sec.contains("particle")) {
            // Legacy single-effect format: particle keys directly under the group
            ParticleConfig pc = ParticleConfig.from(sec.getValues(false), name, logger);
            if (pc != null) effects.add(pc);
        }
        if (effects.isEmpty()) {
            logger.warning("Group '" + name + "' has no usable effects.");
        }

        return new TrailGroup(name, resolveIcon(name, sec, logger), resolveColor(name, sec, logger),
                List.copyOf(effects));
    }

    private static Material resolveIcon(String name, ConfigurationSection sec, Logger logger) {
        // Preferred: MATERIAL: DIAMOND. Legacy: icon: MATERIAL:DIAMOND
        for (String key : new String[]{"MATERIAL", "icon"}) {
            String value = sec.getString(key);
            if (value == null || value.isBlank()) continue;
            Material parsed = parseMaterial(value);
            if (parsed != null) return parsed;
            logger.warning("Invalid " + key + " '" + value + "' for group '" + name + "'. Falling back to " + DEFAULT_ICON + ".");
        }
        return DEFAULT_ICON;
    }

    private static Material parseMaterial(String value) {
        String trimmed = value.trim();
        if (trimmed.toUpperCase(Locale.ROOT).startsWith("MATERIAL:")) {
            trimmed = trimmed.substring("MATERIAL:".length()).trim();
        }
        Material m = Material.matchMaterial(trimmed);
        return (m != null && m.isItem() && !m.isAir()) ? m : null;
    }

    /**
     * COLOR accepts a color code ("&c") or a ChatColor name ("RED").
     * Without it, the original built-in colors for PREDATOR/MASTER/DIAMOND are used.
     */
    private static ChatColor resolveColor(String name, ConfigurationSection sec, Logger logger) {
        String value = sec.getString("COLOR");
        if (value != null && !value.isBlank()) {
            String v = value.trim();
            ChatColor c = null;
            if (v.length() == 2 && (v.charAt(0) == '&' || v.charAt(0) == ChatColor.COLOR_CHAR)) {
                c = ChatColor.getByChar(v.charAt(1));
            } else {
                try {
                    c = ChatColor.valueOf(v.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ignored) {
                    // handled below
                }
            }
            if (c != null && c.isColor()) return c;
            logger.warning("Invalid COLOR '" + value + "' for group '" + name + "'. Using default.");
        }
        return switch (name.toUpperCase(Locale.ROOT)) {
            case "PREDATOR" -> ChatColor.RED;
            case "MASTER" -> ChatColor.LIGHT_PURPLE;
            case "DIAMOND" -> ChatColor.AQUA;
            default -> ChatColor.YELLOW;
        };
    }
}
