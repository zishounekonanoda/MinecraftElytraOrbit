package org.neko.elytratrail2;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.Map;
import java.util.logging.Logger;

public class ParticleConfig {
    private final Particle particle;
    private final int count;
    private final double offset;
    private final double speed;
    private final Object data; // For REDSTONE, etc.

    private ParticleConfig(Particle particle, int count, double offset, double speed, Object data) {
        this.particle = particle;
        this.count = count;
        this.offset = offset;
        this.speed = speed;
        this.data = data;
    }

    // Now accepts a Map directly, which is more robust than wrapping in MemorySection
    public static ParticleConfig from(Map<String, Object> map, Logger logger) {
        String name = (String) map.getOrDefault("particle", "FIREWORKS_SPARK");
        Particle particle;
        Object data = null;

        try {
            if (name.equalsIgnoreCase("REDSTONE")) {
                particle = Particle.DUST;
                Object redstoneObj = map.get("redstone");
                if (redstoneObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> redstoneMap = (Map<String, Object>) redstoneObj;
                    String hex = (String) redstoneMap.getOrDefault("color", "#FF0000");
                    double size = ((Number) redstoneMap.getOrDefault("size", 1.0)).doubleValue();
                    data = new Particle.DustOptions(Color.fromRGB(parseHex(hex)), (float) size);
                } else {
                    data = new Particle.DustOptions(Color.RED, 1.0f);
                }
            } else {
                particle = Particle.valueOf(name.toUpperCase());
            }
        } catch (IllegalArgumentException ex) {
            logger.warning("Unknown particle '" + name + "', fallback to FIREWORKS_SPARK");
            particle = Particle.valueOf("FIREWORKS_SPARK");
        }

        int count = ((Number) map.getOrDefault("count", 8)).intValue();
        double offset = ((Number) map.getOrDefault("offset", 0.2)).doubleValue();
        double speed = ((Number) map.getOrDefault("speed", 0.01)).doubleValue();

        return new ParticleConfig(particle, count, offset, speed, data);
    }

    public void spawn(Location loc) {
        World w = loc.getWorld();
        if (w == null) return;
        w.spawnParticle(particle, loc, count, offset, offset, offset, speed, data, false);
    }

    private static int parseHex(String s) {
        String t = s.trim();
        if (t.startsWith("#")) t = t.substring(1);
        if (t.length() == 3) {
            char r = t.charAt(0), g = t.charAt(1), b = t.charAt(2);
            t = "" + r + r + g + g + b + b;
        }
        return (int) Long.parseLong(t, 16);
    }
}