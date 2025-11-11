package org.neko.elytratrail2;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public final class ElytraTrail2 extends JavaPlugin {
    private TrailTask trailTask;
    private LocaleManager localeManager;

    @Override
    public void onEnable() {
        // Initialize LocaleManager first
        localeManager = new LocaleManager(this);

        saveDefaultConfig();

        // Register events
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
