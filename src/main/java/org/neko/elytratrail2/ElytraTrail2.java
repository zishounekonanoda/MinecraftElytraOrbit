package org.neko.elytratrail2;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ElytraTrail2 extends JavaPlugin {
    private TrailTask trailTask;
    private LocaleManager localeManager;
    private Map<String, TrailGroup> groups = Collections.emptyMap();

    @Override
    public void onEnable() {
        // Initialize LocaleManager first
        localeManager = new LocaleManager(this);

        saveDefaultConfig();
        loadGroups();

        // Register events
        getServer().getPluginManager().registerEvents(new GuiListener(this), this);

        // Register command
        if (getCommand("orbit") != null) {
            OrbitCommand cmd = new OrbitCommand(this);
            getCommand("orbit").setExecutor(cmd);
            getCommand("orbit").setTabCompleter(cmd);
        }

        startTrailTask();

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

    /** Re-reads config.yml and the message files, re-parses all groups and restarts the task (tick_period may have changed). */
    public void reloadAll() {
        reloadConfig();
        loadGroups();
        localeManager.reloadLocales();
        startTrailTask();
    }

    private void loadGroups() {
        groups = TrailGroup.loadAll(getConfig(), getLogger());
        getLogger().info("Loaded " + groups.size() + " orbit group(s).");
    }

    private void startTrailTask() {
        if (trailTask != null) trailTask.cancel();
        int period = Math.max(1, getConfig().getInt("tick_period", 1));
        trailTask = new TrailTask(this);
        trailTask.runTaskTimer(this, period, period);
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    /** Parsed groups in config order. */
    public Map<String, TrailGroup> getGroups() {
        return groups;
    }

    public Set<String> getConfiguredGroups() {
        return groups.keySet();
    }

    public Optional<TrailGroup> getMatchingGroup(Player p) {
        for (String tag : p.getScoreboardTags()) {
            TrailGroup group = groups.get(tag);
            if (group != null) return Optional.of(group);
        }
        return Optional.empty();
    }
}
