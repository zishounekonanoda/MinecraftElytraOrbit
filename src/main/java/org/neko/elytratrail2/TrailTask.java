package org.neko.elytratrail2;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrailTask extends BukkitRunnable {
    private final ElytraTrail2 plugin;

    public TrailTask(ElytraTrail2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Optional<String> groupOpt = plugin.getMatchingGroupTag(p);
            if (groupOpt.isEmpty()) continue;

            String group = groupOpt.get();
            ConfigurationSection groupSec = plugin.getConfig().getConfigurationSection("groups." + group);
            if (groupSec == null) continue;

            List<Map<?, ?>> effectsList = groupSec.getMapList("effects");

            if (!effectsList.isEmpty()) {
                // New multi-effect format
                for (Map<?, ?> effectMap : effectsList) {
                    // Cast to a more specific map type for processing
                    @SuppressWarnings("unchecked")
                    Map<String, Object> typedMap = (Map<String, Object>) effectMap;
                    spawnEffectForMap(p, typedMap);
                }
            } else {
                // Legacy single-effect format
                spawnEffectForMap(p, groupSec.getValues(false));
            }
        }
    }

    private void spawnEffectForMap(Player p, Map<String, Object> map) {
        if (!map.containsKey("particle")) {
            return; // Not a valid effect section
        }

        EmitWhen when = EmitWhen.fromString((String) map.getOrDefault("when", "ALL"));
        boolean airborne = !p.isOnGround();
        boolean moving = p.getVelocity().lengthSquared() > 0.01;

        boolean shouldEmit;
        switch (when) {
            case FLYING -> shouldEmit = p.isFlying();
            case GLIDING -> shouldEmit = p.isGliding();
            case FALLING -> shouldEmit = airborne && !p.isFlying() && !p.isGliding() && moving;
            default -> shouldEmit = (p.isFlying() || p.isGliding() || (airborne && moving));
        }

        if (shouldEmit) {
            ParticleConfig pc = ParticleConfig.from(map, plugin.getLogger());
            pc.spawn(p.getLocation());
        }
    }
}
