package org.neko.elytratrail2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TrailTask extends BukkitRunnable {
    private final ElytraTrail2 plugin;

    public TrailTask(ElytraTrail2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            TrailGroup group = plugin.getMatchingGroup(p).orElse(null);
            if (group == null || group.effects().isEmpty()) continue;

            boolean flying = p.isFlying();
            boolean gliding = p.isGliding();
            boolean airborne = !p.isOnGround();
            boolean moving = p.getVelocity().lengthSquared() > 0.01;
            Location loc = null;

            for (ParticleConfig effect : group.effects()) {
                boolean shouldEmit = switch (effect.getWhen()) {
                    case FLYING -> flying;
                    case GLIDING -> gliding;
                    case FALLING -> airborne && !flying && !gliding && moving;
                    case ALL -> flying || gliding || (airborne && moving);
                };
                if (shouldEmit) {
                    if (loc == null) loc = p.getLocation();
                    effect.spawn(loc);
                }
            }
        }
    }
}
