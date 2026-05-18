package me.iristrack.memorybook.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MemoryEvent {
    private final int id;
    private final MemoryType type;
    private final MemoryRarity rarity;
    private final String title;
    private final String description;
    private final List<UUID> playerUuids;
    private final List<String> playerNames;
    private final String world;
    private final Double x;
    private final Double y;
    private final Double z;
    private final long serverDay;
    private final long createdAt;
    private final String season;

    public MemoryEvent(int id, MemoryType type, MemoryRarity rarity, String title, String description,
                       List<UUID> playerUuids, List<String> playerNames, Location location,
                       long serverDay, long createdAt, String season) {
        this.id = id;
        this.type = Objects.requireNonNull(type);
        this.rarity = Objects.requireNonNull(rarity);
        this.title = Objects.requireNonNullElse(title, type.displayName());
        this.description = Objects.requireNonNullElse(description, "");
        this.playerUuids = new ArrayList<>(playerUuids == null ? List.of() : playerUuids);
        this.playerNames = new ArrayList<>(playerNames == null ? List.of() : playerNames);
        this.world = location == null ? null : location.getWorld().getName();
        this.x = location == null ? null : location.getX();
        this.y = location == null ? null : location.getY();
        this.z = location == null ? null : location.getZ();
        this.serverDay = serverDay;
        this.createdAt = createdAt;
        this.season = Objects.requireNonNullElse(season, "Default Season");
    }

    public int id() { return id; }
    public MemoryType type() { return type; }
    public MemoryRarity rarity() { return rarity; }
    public String title() { return title; }
    public String description() { return description; }
    public List<UUID> playerUuids() { return List.copyOf(playerUuids); }
    public List<String> playerNames() { return List.copyOf(playerNames); }
    public String world() { return world; }
    public Double x() { return x; }
    public Double y() { return y; }
    public Double z() { return z; }
    public long serverDay() { return serverDay; }
    public long createdAt() { return createdAt; }
    public String season() { return season; }

    public String playersText() {
        return playerNames.isEmpty() ? "Server" : String.join(", ", playerNames);
    }

    public String locationText() {
        if (world == null || x == null || y == null || z == null) return "Unknown";
        return world + " x:" + x.intValue() + " y:" + y.intValue() + " z:" + z.intValue();
    }

    public String dateText() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                .withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(createdAt));
    }

    public ConfigurationSection toSection(YamlConfiguration yaml) {
        ConfigurationSection section = yaml.createSection("events." + id);
        section.set("type", type.name());
        section.set("rarity", rarity.name());
        section.set("title", title);
        section.set("description", description);
        section.set("player-uuids", playerUuids.stream().map(UUID::toString).toList());
        section.set("player-names", playerNames);
        section.set("world", world);
        section.set("x", x);
        section.set("y", y);
        section.set("z", z);
        section.set("server-day", serverDay);
        section.set("created-at", createdAt);
        section.set("season", season);
        return section;
    }

    public static MemoryEvent fromSection(String idKey, ConfigurationSection section) {
        int id = Integer.parseInt(idKey);
        MemoryType type = MemoryType.fromString(section.getString("type", "CUSTOM"));
        MemoryRarity rarity;
        try { rarity = MemoryRarity.valueOf(section.getString("rarity", type.defaultRarity().name())); }
        catch (IllegalArgumentException ex) { rarity = type.defaultRarity(); }

        List<UUID> uuids = new ArrayList<>();
        for (String uuidText : section.getStringList("player-uuids")) {
            try { uuids.add(UUID.fromString(uuidText)); } catch (IllegalArgumentException ignored) {}
        }
        List<String> names = section.getStringList("player-names");
        String worldName = section.getString("world");
        Location location = null;
        if (worldName != null && Bukkit.getWorld(worldName) != null && section.contains("x")) {
            location = new Location(Bukkit.getWorld(worldName), section.getDouble("x"), section.getDouble("y"), section.getDouble("z"));
        }
        return new MemoryEvent(id, type, rarity,
                section.getString("title", type.displayName()),
                section.getString("description", ""),
                uuids, names, location,
                section.getLong("server-day", 0L),
                section.getLong("created-at", System.currentTimeMillis()),
                section.getString("season", "Default Season"));
    }
}
