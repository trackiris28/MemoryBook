package me.iristrack.memorybook.model;

import org.bukkit.ChatColor;

public enum MemoryRarity {
    COMMON(ChatColor.WHITE),
    RARE(ChatColor.AQUA),
    EPIC(ChatColor.LIGHT_PURPLE),
    LEGENDARY(ChatColor.GOLD),
    MYTHIC(ChatColor.DARK_PURPLE);

    private final ChatColor color;

    MemoryRarity(ChatColor color) { this.color = color; }
    public ChatColor color() { return color; }
}
