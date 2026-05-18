package me.iristrack.memorybook.service;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.model.MemoryRarity;
import me.iristrack.memorybook.model.MemoryType;
import me.iristrack.memorybook.storage.MemoryStorage;
import me.iristrack.memorybook.util.MessageUtil;
import me.iristrack.memorybook.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MemoryService {
    private final Main plugin;
    private final MemoryStorage storage;

    public MemoryService(Main plugin, MemoryStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public MemoryStorage storage() { return storage; }

    public MemoryEvent createMemory(MemoryType type, MemoryRarity rarity, String title, String description,
                                    List<Player> players, Location location, boolean firstOnly) {
        if (firstOnly && storage.hasTypeInSeason(type, storage.currentSeason())) return null;

        List<UUID> uuids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        if (players != null) {
            for (Player player : players) {
                if (player == null) continue;
                uuids.add(player.getUniqueId());
                names.add(player.getName());
            }
        }

        long serverDay = 0L;
        if (location != null && location.getWorld() != null) serverDay = location.getWorld().getFullTime() / 24000L;

        MemoryEvent event = new MemoryEvent(
                storage.nextId(), type, rarity == null ? type.defaultRarity() : rarity,
                title, description, uuids, names, location, serverDay,
                System.currentTimeMillis(), storage.currentSeason()
        );
        storage.add(event);
        rewardPlayers(event);
        announce(event);
        if (plugin.discordSrvHook() != null) plugin.discordSrvHook().sendMemoryCreated(event);
        return event;
    }

    public MemoryEvent createCustom(MemoryType type, String title, String description, Player actor) {
        return createMemory(type, type.defaultRarity(), title, description,
                actor == null ? List.of() : List.of(actor), actor == null ? null : actor.getLocation(), false);
    }

    public List<MemoryEvent> latest(int limit) {
        return storage.all().stream()
                .sorted(Comparator.comparingInt(MemoryEvent::id).reversed())
                .limit(limit)
                .toList();
    }

    private void rewardPlayers(MemoryEvent event) {
        if (!plugin.getConfig().getBoolean("rewards.enabled", true)) return;
        int points = plugin.getConfig().getInt("rewards.memory-points." + event.type().name(), 0);
        String title = plugin.getConfig().getString("titles." + event.type().name(), "");
        for (UUID uuid : event.playerUuids()) {
            storage.addMemoryPoints(uuid, points);
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            if (points > 0) MessageUtil.send(player, "rewards.points", Map.of("points", String.valueOf(points)));
            if (!title.isBlank()) MessageUtil.send(player, "rewards.title-unlocked", Map.of("title", title));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f);
        }
    }

    private void announce(MemoryEvent event) {
        if (!plugin.getConfig().getBoolean("settings.broadcast-new-memory", true)) return;
        Bukkit.broadcastMessage(MessageUtil.get("broadcast.line"));
        Bukkit.broadcastMessage(MessageUtil.get("prefix") + MessageUtil.get("broadcast.created"));
        Bukkit.broadcastMessage(MessageUtil.get("broadcast.title", Map.of(
                "icon", event.type().icon(),
                "rarity_color", event.rarity().color().toString(),
                "title", event.title()
        )));
        Bukkit.broadcastMessage(MessageUtil.get("broadcast.description", Map.of("description", event.description())));
        Bukkit.broadcastMessage(MessageUtil.get("broadcast.line"));
    }
}
