package me.iristrack.memorybook.storage;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.model.MemoryType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlMemoryStorage implements MemoryStorage {
    private final JavaPlugin plugin;
    private final File file;
    private YamlConfiguration yaml;
    private final Map<Integer, MemoryEvent> events = new LinkedHashMap<>();

    public YamlMemoryStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "memories.yml");
        reload();
    }

    @Override
    public void reload() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException e) { throw new RuntimeException(e); }
        }
        yaml = YamlConfiguration.loadConfiguration(file);
        if (!yaml.contains("meta.next-id")) yaml.set("meta.next-id", 1);
        if (!yaml.contains("meta.current-season")) yaml.set("meta.current-season", plugin.getConfig().getString("settings.current-season", "SMP Season 1"));
        events.clear();
        ConfigurationSection section = yaml.getConfigurationSection("events");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection eventSection = section.getConfigurationSection(key);
                if (eventSection == null) continue;
                try {
                    MemoryEvent event = MemoryEvent.fromSection(key, eventSection);
                    events.put(event.id(), event);
                } catch (Exception ex) {
                    plugin.getLogger().warning("Cannot load memory event " + key + ": " + ex.getMessage());
                }
            }
        }
        save();
    }

    @Override
    public void save() {
        try { yaml.save(file); } catch (IOException e) { plugin.getLogger().severe("Cannot save memories.yml: " + e.getMessage()); }
    }

    @Override
    public MemoryEvent add(MemoryEvent event) {
        events.put(event.id(), event);
        yaml.set("events." + event.id(), null);
        event.toSection(yaml);
        yaml.set("meta.next-id", Math.max(yaml.getInt("meta.next-id", 1), event.id() + 1));
        save();
        return event;
    }

    @Override
    public MemoryEvent replaceText(int id, String title, String description) {
        MemoryEvent old = events.get(id);
        if (old == null) return null;
        MemoryEvent updated = new MemoryEvent(
                old.id(), old.type(), old.rarity(), title, description,
                old.playerUuids(), old.playerNames(), old.toLocation(),
                old.serverDay(), old.createdAt(), old.season()
        );
        add(updated);
        return updated;
    }

    @Override
    public boolean remove(int id) {
        MemoryEvent removed = events.remove(id);
        if (removed == null) return false;
        yaml.set("events." + id, null);
        save();
        return true;
    }

    @Override
    public List<MemoryEvent> all() {
        ArrayList<MemoryEvent> list = new ArrayList<>(events.values());
        list.sort(Comparator.comparingInt(MemoryEvent::id));
        return list;
    }

    @Override
    public List<MemoryEvent> byType(MemoryType type) {
        return all().stream().filter(e -> e.type() == type).toList();
    }

    @Override
    public List<MemoryEvent> byPlayer(UUID playerUuid, String playerName) {
        return all().stream().filter(e ->
                (playerUuid != null && e.playerUuids().contains(playerUuid)) ||
                        (playerName != null && e.playerNames().stream().anyMatch(n -> n.equalsIgnoreCase(playerName)))
        ).toList();
    }

    @Override
    public Optional<MemoryEvent> byId(int id) { return Optional.ofNullable(events.get(id)); }

    @Override
    public boolean hasType(MemoryType type) { return events.values().stream().anyMatch(e -> e.type() == type); }

    @Override
    public boolean hasTypeInSeason(MemoryType type, String season) {
        return events.values().stream().anyMatch(e -> e.type() == type && e.season().equalsIgnoreCase(season));
    }

    @Override
    public int nextId() { return yaml.getInt("meta.next-id", 1); }

    @Override
    public void setSeason(String season) {
        yaml.set("meta.current-season", season);
        save();
    }

    @Override
    public String currentSeason() { return yaml.getString("meta.current-season", "SMP Season 1"); }

    @Override
    public int getMemoryPoints(UUID uuid) { return yaml.getInt("players." + uuid + ".memory-points", 0); }

    @Override
    public void addMemoryPoints(UUID uuid, int amount) {
        if (uuid == null || amount == 0) return;
        String path = "players." + uuid + ".memory-points";
        yaml.set(path, getMemoryPoints(uuid) + amount);
        save();
    }
}
