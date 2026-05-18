package me.iristrack.memorybook.model;

import org.bukkit.Material;

public enum MemoryType {
    FIRST_JOIN("*", "First Join", Material.PLAYER_HEAD, MemoryRarity.COMMON),
    FIRST_NETHER("F", "First Nether", Material.NETHERRACK, MemoryRarity.RARE),
    FIRST_END("E", "First End", Material.END_STONE, MemoryRarity.EPIC),
    FIRST_DRAGON_KILL("D", "First Dragon Kill", Material.DRAGON_HEAD, MemoryRarity.LEGENDARY),
    FIRST_WITHER_KILL("W", "First Wither Kill", Material.WITHER_SKELETON_SKULL, MemoryRarity.EPIC),
    FIRST_ELYTRA("LY", "First Elytra", Material.ELYTRA, MemoryRarity.LEGENDARY),
    FIRST_WARDEN_DEATH("WD", "First Warden Death", Material.SCULK_SHRIEKER, MemoryRarity.RARE),
    FULL_NETHERITE("N", "Full Netherite", Material.NETHERITE_CHESTPLATE, MemoryRarity.EPIC),
    LEVEL_100("100", "Level 100", Material.EXPERIENCE_BOTTLE, MemoryRarity.EPIC),
    SURVIVED_100_DAYS("100D", "Survived 100 Days", Material.CLOCK, MemoryRarity.RARE),
    BUILD_AWARD("B", "Build Award", Material.GOLD_BLOCK, MemoryRarity.EPIC),
    TOWN("T", "Town", Material.BELL, MemoryRarity.RARE),
    FUNNY_DEATH("X", "Funny Death", Material.SKELETON_SKULL, MemoryRarity.COMMON),
    SERVER_EVENT("!", "Server Event", Material.FIREWORK_ROCKET, MemoryRarity.RARE),
    CUSTOM("M", "Custom", Material.WRITABLE_BOOK, MemoryRarity.COMMON);

    private final String icon;
    private final String displayName;
    private final Material material;
    private final MemoryRarity defaultRarity;

    MemoryType(String icon, String displayName, Material material, MemoryRarity defaultRarity) {
        this.icon = icon;
        this.displayName = displayName;
        this.material = material;
        this.defaultRarity = defaultRarity;
    }

    public String icon() { return icon; }
    public String displayName() { return displayName; }
    public Material material() { return material; }
    public MemoryRarity defaultRarity() { return defaultRarity; }

    public static MemoryType fromString(String input) {
        if (input == null) return CUSTOM;
        String normalized = input.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        for (MemoryType type : values()) {
            String display = type.displayName.toUpperCase().replace(' ', '_');
            if (type.name().equals(normalized) || display.equals(normalized)) return type;
        }
        return CUSTOM;
    }
}
