package org.neko.elytratrail2;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OrbitCommand implements CommandExecutor, TabCompleter {
    private final ElytraTrail2 plugin;
    private final LocaleManager lm;

    public OrbitCommand(ElytraTrail2 plugin) {
        this.plugin = plugin;
        this.lm = plugin.getLocaleManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (sender instanceof Player) ? (Player) sender : null;

        if (args.length == 0) {
            if (player != null && player.hasPermission("apexorbit.menu")) {
                GuiManager.openTrailGui(player, plugin, 0);
                return true;
            }
            sender.sendMessage(lm.getString("command.usage", player));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "menu":
            case "gui":
                if (player == null) {
                    sender.sendMessage(lm.getString("command.player_only", null)); // Console has no locale
                    return true;
                }
                if (!player.hasPermission("apexorbit.menu")) {
                    player.sendMessage(lm.getString("command.no_permission", player));
                    return true;
                }
                GuiManager.openTrailGui(player, plugin, 0);
                break;

            case "reload":
                if (!sender.hasPermission("apexorbit.admin")) {
                    sender.sendMessage(lm.getString("command.no_permission", player));
                    return true;
                }
                plugin.reloadConfig();
                lm.reloadLocales();
                sender.sendMessage(lm.getString("command.reloaded", player));
                break;

            case "tag":
                if (!sender.hasPermission("apexorbit.admin")) {
                    sender.sendMessage(lm.getString("command.no_permission", player));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(lm.getString("command.usage", player));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(lm.getString("command.player_not_found", player, Map.of("player", args[1])));
                    return true;
                }
                String group = args[2];
                if (!plugin.getConfiguredGroups().contains(group)) {
                    sender.sendMessage(lm.getString("command.group_not_found", player, Map.of("group", group)));
                    return true;
                }

                Set<String> configuredGroups = plugin.getConfiguredGroups();
                for (String existingTag : new ArrayList<>(target.getScoreboardTags())) {
                    if (configuredGroups.contains(existingTag)) {
                        target.removeScoreboardTag(existingTag);
                    }
                }
                target.addScoreboardTag(group);

                if (target.getGameMode() != org.bukkit.GameMode.CREATIVE && target.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                    target.setAllowFlight(true);
                }
                sender.sendMessage(lm.getString("command.tagged", player, Map.of("player", target.getName(), "group", group)));
                break;

            case "untag":
                if (!sender.hasPermission("apexorbit.admin")) {
                    sender.sendMessage(lm.getString("command.no_permission", player));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(lm.getString("command.usage", player));
                    return true;
                }
                Player targetToRemove = Bukkit.getPlayer(args[1]);
                if (targetToRemove == null) {
                    sender.sendMessage(lm.getString("command.player_not_found", player, Map.of("player", args[1])));
                    return true;
                }
                String groupToRemove = args[2];
                targetToRemove.removeScoreboardTag(groupToRemove);
                if (plugin.getMatchingGroupTag(targetToRemove).isEmpty()) {
                    if (targetToRemove.getGameMode() != org.bukkit.GameMode.CREATIVE && targetToRemove.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                        targetToRemove.setAllowFlight(false);
                    }
                }
                sender.sendMessage(lm.getString("command.untagged", player, Map.of("player", targetToRemove.getName(), "group", groupToRemove)));
                break;

            default:
                sender.sendMessage(lm.getString("command.usage", player));
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("apexorbit.admin")) {
                completions.add("tag");
                completions.add("untag");
                completions.add("reload");
            }
            if (sender.hasPermission("apexorbit.menu")) {
                completions.add("menu");
            }
            return completions.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("tag") || args[0].equalsIgnoreCase("untag"))) {
            if (sender.hasPermission("apexorbit.admin")) {
                return null; // Let server complete player names
            }
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("tag") || args[0].equalsIgnoreCase("untag"))) {
            if (!sender.hasPermission("apexorbit.admin")) return completions;

            if (args[0].equalsIgnoreCase("untag")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    Set<String> configuredGroups = plugin.getConfiguredGroups();
                    return target.getScoreboardTags().stream()
                            .filter(configuredGroups::contains)
                            .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }

            Set<String> groups = plugin.getConfiguredGroups();
            return groups.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return completions;
    }
}
