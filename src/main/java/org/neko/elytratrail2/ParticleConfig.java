package org.neko.elytratrail2;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * One parsed particle effect. Built once when the config is loaded, not every tick.
 */
public class ParticleConfig {
    private static final Particle FALLBACK = Particle.FIREWORK;

    /** Pre-1.20.5 Bukkit names mapped to their current names, so older configs keep working. */
    private static final Map<String, String> LEGACY_NAMES = new HashMap<>();
    static {
        LEGACY_NAMES.put("REDSTONE", "DUST");
        LEGACY_NAMES.put("FIREWORKS_SPARK", "FIREWORK");
        LEGACY_NAMES.put("CRIT_MAGIC", "ENCHANTED_HIT");
        LEGACY_NAMES.put("SPELL_WITCH", "WITCH");
        LEGACY_NAMES.put("SPELL", "EFFECT");
        LEGACY_NAMES.put("SPELL_INSTANT", "INSTANT_EFFECT");
        LEGACY_NAMES.put("VILLAGER_HAPPY", "HAPPY_VILLAGER");
        LEGACY_NAMES.put("VILLAGER_ANGRY", "ANGRY_VILLAGER");
        LEGACY_NAMES.put("SMOKE_NORMAL", "SMOKE");
        LEGACY_NAMES.put("SMOKE_LARGE", "LARGE_SMOKE");
        LEGACY_NAMES.put("EXPLOSION_NORMAL", "POOF");
        LEGACY_NAMES.put("EXPLOSION_LARGE", "EXPLOSION");
        LEGACY_NAMES.put("EXPLOSION_HUGE", "EXPLOSION_EMITTER");
        LEGACY_NAMES.put("ENCHANTMENT_TABLE", "ENCHANT");
        LEGACY_NAMES.put("TOWN_AURA", "MYCELIUM");
        LEGACY_NAMES.put("DRIP_WATER", "DRIPPING_WATER");
        LEGACY_NAMES.put("DRIP_LAVA", "DRIPPING_LAVA");
        LEGACY_NAMES.put("WATER_SPLASH", "SPLASH");
        LEGACY_NAMES.put("WATER_BUBBLE", "BUBBLE");
        LEGACY_NAMES.put("WATER_WAKE", "FISHING");
        LEGACY_NAMES.put("WATER_DROP", "RAIN");
        LEGACY_NAMES.put("SUSPENDED", "UNDERWATER");
        LEGACY_NAMES.put("SUSPENDED_DEPTH", "UNDERWATER");
        LEGACY_NAMES.put("SNOW_SHOVEL", "ITEM_SNOWBALL");
        LEGACY_NAMES.put("SNOWBALL", "ITEM_SNOWBALL");
        LEGACY_NAMES.put("SLIME", "ITEM_SLIME");
        LEGACY_NAMES.put("TOTEM", "TOTEM_OF_UNDYING");
    }

    private final Particle particle;
    private final int count;
    private final double offset;
    private final double speed;
    private final Object data;
    private final EmitWhen when;

    private ParticleConfig(Particle particle, int count, double offset, double speed, Object data, EmitWhen when) {
        this.particle = particle;
        this.count = count;
        this.offset = offset;
        this.speed = speed;
        this.data = data;
        this.when = when;
    }

    public EmitWhen getWhen() {
        return when;
    }

    /**
     * Parses one effect entry. Returns null (and logs why) if the entry cannot be used.
     */
    public static ParticleConfig from(Map<?, ?> map, String context, Logger logger) {
        Object nameObj = map.get("particle");
        if (nameObj == null) return null;
        String name = nameObj.toString().trim().toUpperCase(Locale.ROOT);

        Particle particle = resolveParticle(name);
        if (particle == null) {
            logger.warning("[" + context + "] Unknown particle '" + name + "', falling back to " + FALLBACK.name());
            particle = FALLBACK;
        }

        Object data = null;
        if (particle == Particle.DUST) {
            data = parseDust(map.get("redstone"), context, logger);
        } else if (particle.getDataType() != Void.class) {
            logger.warning("[" + context + "] Particle '" + particle.name() + "' needs extra data ("
                    + particle.getDataType().getSimpleName() + ") which this plugin cannot configure; effect skipped.");
            return null;
        }

        int count = (int) number(map.get("count"), 8, "count", context, logger);
        double offset = number(map.get("offset"), 0.2, "offset", context, logger);
        double speed = number(map.get("speed"), 0.01, "speed", context, logger);
        Object whenObj = map.get("when");
        EmitWhen when = EmitWhen.fromString(whenObj == null ? "ALL" : whenObj.toString());

        return new ParticleConfig(particle, count, offset, speed, data, when);
    }

    public void spawn(Location loc) {
        World w = loc.getWorld();
        if (w == null) return;
        w.spawnParticle(particle, loc, count, offset, offset, offset, speed, data, false);
    }

    private static Particle resolveParticle(String name) {
        Particle p = tryValueOf(name);
        if (p != null) return p;
        String modern = LEGACY_NAMES.get(name);
        return modern == null ? null : tryValueOf(modern);
    }

    private static Particle tryValueOf(String name) {
        try {
            return Particle.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Particle.DustOptions parseDust(Object redstoneObj, String context, Logger logger) {
        Map<?, ?> dustMap = null;
        if (redstoneObj instanceof Map<?, ?> m) {
            dustMap = m;
        } else if (redstoneObj instanceof ConfigurationSection sec) {
            dustMap = sec.getValues(false);
        }
        if (dustMap == null) {
            return new Particle.DustOptions(Color.RED, 1.0f);
        }

        Color color = Color.RED;
        Object hexObj = dustMap.get("color");
        if (hexObj != null) {
            try {
                color = Color.fromRGB(parseHex(hexObj.toString()));
            } catch (IllegalArgumentException e) {
                logger.warning("[" + context + "] Invalid color '" + hexObj + "', using #FF0000");
            }
        }
        float size = (float) number(dustMap.get("size"), 1.0, "redstone.size", context, logger);
        return new Particle.DustOptions(color, size);
    }

    private static double number(Object value, double def, String key, String context, Logger logger) {
        if (value == null) return def;
        if (value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(value.toString().trim());
        } catch (NumberFormatException e) {
            logger.warning("[" + context + "] '" + key + "' is not a number ('" + value + "'), using " + def);
            return def;
        }
    }

    private static int parseHex(String s) {
        String t = s.trim();
        if (t.startsWith("#")) t = t.substring(1);
        if (t.length() == 3) {
            char r = t.charAt(0), g = t.charAt(1), b = t.charAt(2);
            t = "" + r + r + g + g + b + b;
        }
        if (t.length() != 6) throw new IllegalArgumentException("bad hex length");
        return Integer.parseInt(t, 16); // NumberFormatException is an IllegalArgumentException
    }
}
