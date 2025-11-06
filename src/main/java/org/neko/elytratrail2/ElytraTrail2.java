package org.neko.elytratrail2;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public final class ElytraTrail2 extends JavaPlugin implements Listener {
    private TrailTask trailTask;
    private LocaleManager localeManager;

    @Override
    public void onEnable() {
        // Initialize LocaleManager first
        localeManager = new LocaleManager(this);

        saveDefaultConfig();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        // Register command
        if (getCommand("orbit") != null) {
            OrbitCommand cmd = new OrbitCommand(this);
            getCommand("orbit").setExecutor(cmd);
            getCommand("orbit").setTabCompleter(cmd);
        }

        // Start task
        int period = Math.max(1, getConfig().getInt("tick_period", 2));
        trailTask = new TrailTask(this);
        trailTask.runTaskTimer(this, period, period);

        getLogger().info("ApexOrbitTrails enabled.");
    }

    @Override
    public void onDisable() {
        if (trailTask != null) trailTask.cancel();
        // Save player language preferences
        if (localeManager != null) {
            localeManager.savePlayerLocales();
        }
        getLogger().info("ApexOrbitTrails disabled.");
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        getMatchingGroupTag(p).ifPresent(group -> {
            if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                p.setAllowFlight(true);
            }
        });
    }

    public Set<String> getConfiguredGroups() {
        ConfigurationSection sec = getConfig().getConfigurationSection("groups");
        if (sec == null) return Collections.emptySet();
        return sec.getKeys(false);
    }

    public Optional<String> getMatchingGroupTag(Player p) {
        Set<String> groups = getConfiguredGroups();
        for (String tag : p.getScoreboardTags()) {
            if (groups.contains(tag)) {
                return Optional.of(tag);
            }
        }
        return Optional.empty();
    }
}
