package me.iristrack.memorybook.storage;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.model.MemoryType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemoryStorage {
    void reload();
    void save();
    MemoryEvent add(MemoryEvent event);
    boolean remove(int id);
    List<MemoryEvent> all();
    List<MemoryEvent> byType(MemoryType type);
    List<MemoryEvent> byPlayer(UUID playerUuid, String playerName);
    Optional<MemoryEvent> byId(int id);
    boolean hasType(MemoryType type);
    int nextId();
    void setSeason(String season);
    String currentSeason();
    int getMemoryPoints(UUID uuid);
    void addMemoryPoints(UUID uuid, int amount);
}
