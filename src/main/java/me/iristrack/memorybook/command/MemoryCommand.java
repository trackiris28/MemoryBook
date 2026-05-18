package me.iristrack.memorybook.command;

import me.iristrack.memorybook.Main;
import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.model.MemoryType;
import me.iristrack.memorybook.service.MemoryService;
import me.iristrack.memorybook.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MemoryCommand implements TabExecutor {
    private final Main plugin;
    private final MemoryService memoryService;

    public MemoryCommand(Main plugin, MemoryService memoryService) {
        this.plugin = plugin;
        this.memoryService = memoryService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageUtil.sendList(sender, "help");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add" -> add(sender, args);
            case "remove", "delete" -> remove(sender, args);
            case "edit" -> edit(sender, args);
            case "list" -> list(sender);
            case "profile" -> profile(sender, args);
            case "top" -> top(sender);
            case "reload" -> reload(sender);
            case "season" -> season(sender, args);
            case "points" -> points(sender, args);
            default -> MessageUtil.send(sender, "commands.unknown");
        }
        return true;
    }

    private boolean admin(CommandSender sender) {
        if (sender.hasPermission("memorybook.admin")) return true;
        MessageUtil.send(sender, "commands.no-permission");
        return false;
    }

    private void add(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 3) {
            MessageUtil.send(sender, "memory.usage-add");
            return;
        }
        MemoryType type = MemoryType.fromString(args[1]);
        TextParts parts = parseText(Arrays.copyOfRange(args, 2, args.length));
        Player actor = sender instanceof Player p ? p : null;
        MemoryEvent event = memoryService.createCustom(type, parts.title(), parts.description(), actor);
        MessageUtil.send(sender, "memory.added", Map.of("id", String.valueOf(event.id()), "title", event.title()));
    }

    private void edit(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 3) {
            MessageUtil.send(sender, "memory.usage-edit");
            return;
        }
        try {
            int id = Integer.parseInt(args[1]);
            TextParts parts = parseText(Arrays.copyOfRange(args, 2, args.length));
            MemoryEvent updated = memoryService.storage().replaceText(id, parts.title(), parts.description());
            if (updated == null) MessageUtil.send(sender, "memory.not-found", Map.of("id", String.valueOf(id)));
            else MessageUtil.send(sender, "memory.edited", Map.of("id", String.valueOf(id), "title", updated.title()));
        } catch (NumberFormatException ex) {
            MessageUtil.send(sender, "memory.invalid-id");
        }
    }

    private void remove(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 2) {
            MessageUtil.send(sender, "memory.usage-remove");
            return;
        }
        try {
            int id = Integer.parseInt(args[1]);
            if (memoryService.storage().remove(id)) MessageUtil.send(sender, "memory.removed", Map.of("id", String.valueOf(id)));
            else MessageUtil.send(sender, "memory.not-found", Map.of("id", String.valueOf(id)));
        } catch (NumberFormatException ex) {
            MessageUtil.send(sender, "memory.invalid-id");
        }
    }

    private void list(CommandSender sender) {
        MessageUtil.send(sender, "memory.list-header");
        List<MemoryEvent> events = memoryService.latest(10);
        if (events.isEmpty()) {
            MessageUtil.send(sender, "memory.empty");
            return;
        }
        for (MemoryEvent e : events) {
            MessageUtil.send(sender, "memory.list-entry", Map.of(
                    "id", String.valueOf(e.id()),
                    "icon", e.type().icon(),
                    "title", e.title(),
                    "players", e.playersText()
            ));
        }
    }

    private void profile(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtil.send(sender, "memory.usage-profile");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        List<MemoryEvent> events = memoryService.storage().byPlayer(target.getUniqueId(), args[1]);
        MessageUtil.send(sender, "profile.header", Map.of("player", args[1]));
        MessageUtil.send(sender, "profile.points", Map.of("points", String.valueOf(memoryService.storage().getMemoryPoints(target.getUniqueId()))));
        if (events.isEmpty()) {
            MessageUtil.send(sender, "profile.empty");
            return;
        }
        for (MemoryEvent e : events) {
            MessageUtil.send(sender, "profile.entry", Map.of(
                    "id", String.valueOf(e.id()),
                    "day", String.valueOf(e.serverDay()),
                    "icon", e.type().icon(),
                    "title", e.title()
            ));
        }
    }

    private void top(CommandSender sender) {
        Map<UUID, Integer> points = memoryService.storage().all().stream()
                .flatMap(e -> e.playerUuids().stream())
                .distinct()
                .collect(Collectors.toMap(uuid -> uuid, memoryService.storage()::getMemoryPoints));
        MessageUtil.send(sender, "top.header");
        if (points.isEmpty()) {
            MessageUtil.send(sender, "top.empty");
            return;
        }
        points.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .forEach(entry -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
                    String name = player.getName() == null ? entry.getKey().toString().substring(0, 8) : player.getName();
                    MessageUtil.send(sender, "top.entry", Map.of("player", name, "points", String.valueOf(entry.getValue())));
                });
    }

    private void reload(CommandSender sender) {
        if (!admin(sender)) return;
        plugin.reloadConfig();
        memoryService.storage().reload();
        MessageUtil.reload();
        if (plugin.discordSrvHook() != null) plugin.discordSrvHook().reload();
        MessageUtil.send(sender, "memory.reloaded");
    }

    private void season(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length >= 3 && args[1].equalsIgnoreCase("start")) {
            String season = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            memoryService.storage().setSeason(season);
            if (plugin.discordSrvHook() != null) plugin.discordSrvHook().sendSeasonStarted(season);
            MessageUtil.send(sender, "season.started", Map.of("season", season));
            return;
        }
        if (args.length >= 2 && args[1].equalsIgnoreCase("end")) {
            String previousSeason = memoryService.storage().currentSeason();
            memoryService.storage().setSeason(plugin.getConfig().getString("settings.inactive-season-name", "No Active Season"));
            if (plugin.discordSrvHook() != null) plugin.discordSrvHook().sendSeasonEnded(previousSeason);
            MessageUtil.send(sender, "season.ended");
            return;
        }
        MessageUtil.send(sender, "season.usage");
    }

    private void points(CommandSender sender, String[] args) {
        OfflinePlayer target;
        if (args.length >= 2) target = Bukkit.getOfflinePlayer(args[1]);
        else if (sender instanceof Player p) target = p;
        else {
            MessageUtil.send(sender, "memory.usage-points-console");
            return;
        }
        MessageUtil.send(sender, "memory.points", Map.of(
                "player", target.getName() == null ? "Unknown" : target.getName(),
                "points", String.valueOf(memoryService.storage().getMemoryPoints(target.getUniqueId()))
        ));
    }

    private TextParts parseText(String[] args) {
        String raw = String.join(" ", args);
        String[] parts = raw.split("\\|", 2);
        String title = parts[0].trim();
        String description = parts.length > 1 ? parts[1].trim() : title;
        return new TextParts(title, description);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return filter(List.of("add", "remove", "edit", "list", "profile", "top", "reload", "season", "points"), args[0]);
        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            List<String> types = new ArrayList<>();
            for (MemoryType type : MemoryType.values()) types.add(type.name().toLowerCase());
            return filter(types, args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("season")) return filter(List.of("start", "end"), args[1]);
        return List.of();
    }

    private List<String> filter(List<String> values, String prefix) {
        return values.stream().filter(v -> v.toLowerCase().startsWith(prefix.toLowerCase())).toList();
    }

    private record TextParts(String title, String description) {}
}
