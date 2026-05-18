package me.iristrack.memorybook.service;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.model.MemoryRarity;
import me.iristrack.memorybook.model.MemoryType;
import me.iristrack.memorybook.storage.MemoryStorage;
import me.iristrack.memorybook.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemoryService {
    private final JavaPlugin plugin;
    private final MemoryStorage storage;

    public MemoryService(JavaPlugin plugin, MemoryStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public MemoryStorage storage() { return storage; }

    public MemoryEvent createMemory(MemoryType type, MemoryRarity rarity, String title, String description,
                                    List<Player> players, Location location, boolean firstOnly) {
        if (firstOnly && storage.hasType(type)) return null;

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
        if (location != null && location.getWorld() != null) {
            serverDay = location.getWorld().getFullTime() / 24000L;
        }

        MemoryEvent event = new MemoryEvent(
                storage.nextId(), type, rarity == null ? type.defaultRarity() : rarity,
                title, description, uuids, names, location, serverDay,
                System.currentTimeMillis(), storage.currentSeason()
        );
        storage.add(event);
        rewardPlayers(event);
        announce(event);
        return event;
    }

    public MemoryEvent createCustom(MemoryType type, String title, String description, Player actor) {
        return createMemory(type, type.defaultRarity(), title, description,
                actor == null ? List.of() : List.of(actor), actor == null ? null : actor.getLocation(), false);
    }

    private void rewardPlayers(MemoryEvent event) {
        if (!plugin.getConfig().getBoolean("rewards.enabled", true)) return;
        int points = plugin.getConfig().getInt("rewards.memory-points." + event.type().name(), 0);
        String title = plugin.getConfig().getString("titles." + event.type().name(), "");
        for (UUID uuid : event.playerUuids()) {
            storage.addMemoryPoints(uuid, points);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (points > 0) TextUtil.send(player, "&d+" + points + " Memory Points");
                if (!title.isBlank()) TextUtil.send(player, "&fBạn đã mở khóa danh hiệu: &e" + title);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f);
            }
        }
    }

    private void announce(MemoryEvent event) {
        if (!plugin.getConfig().getBoolean("settings.broadcast-new-memory", true)) return;
        String prefix = plugin.getConfig().getString("messages.prefix", "&dMemoryBook &8» &r");
        Bukkit.broadcastMessage(TextUtil.color("&8━━━━━━━━━━━━━━━━━━━━"));
        Bukkit.broadcastMessage(TextUtil.color(prefix + "&fMột trang lịch sử mới đã được viết!"));
        Bukkit.broadcastMessage(TextUtil.color(event.type().icon() + " " + event.rarity().color() + event.title()));
        Bukkit.broadcastMessage(TextUtil.color("&7" + event.description()));
        Bukkit.broadcastMessage(TextUtil.color("&8━━━━━━━━━━━━━━━━━━━━"));
    }
}
