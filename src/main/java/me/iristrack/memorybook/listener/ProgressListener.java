package me.iristrack.memorybook.listener;

import me.iristrack.memorybook.model.MemoryType;
import me.iristrack.memorybook.service.MemoryService;
import me.iristrack.memorybook.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class ProgressListener implements Listener {
    private final MemoryService memoryService;

    public ProgressListener(MemoryService memoryService) { this.memoryService = memoryService; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        create(player, MemoryType.FIRST_JOIN);
        checkSurvivalDays(player);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World.Environment env = player.getWorld().getEnvironment();
        if (env == World.Environment.NETHER) create(player, MemoryType.FIRST_NETHER);
        else if (env == World.Environment.THE_END) create(player, MemoryType.FIRST_END);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        checkElytra(player);
        checkNetherite(player);
        checkSurvivalDays(player);
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {
        if (event.getNewLevel() >= 100) create(event.getPlayer(), MemoryType.LEVEL_100);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent
                && damageEvent.getDamager().getType() == EntityType.WARDEN) {
            create(player, MemoryType.FIRST_WARDEN_DEATH);
        }
    }

    private void checkElytra(Player player) {
        if (contains(player, Material.ELYTRA)) create(player, MemoryType.FIRST_ELYTRA);
    }

    private void checkNetherite(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) return;
        if (is(equipment.getHelmet(), Material.NETHERITE_HELMET)
                && is(equipment.getChestplate(), Material.NETHERITE_CHESTPLATE)
                && is(equipment.getLeggings(), Material.NETHERITE_LEGGINGS)
                && is(equipment.getBoots(), Material.NETHERITE_BOOTS)) {
            create(player, MemoryType.FULL_NETHERITE);
        }
    }

    private void checkSurvivalDays(Player player) {
        int ticks = player.getStatistic(Statistic.TIME_SINCE_DEATH);
        if (ticks >= 100 * 24000) create(player, MemoryType.SURVIVED_100_DAYS);
    }

    private void create(Player player, MemoryType type) {
        Map<String, String> placeholders = Map.of("player", player.getName());
        memoryService.createMemory(type, null,
                MessageUtil.get("events." + type.name() + ".title", placeholders),
                MessageUtil.get("events." + type.name() + ".description", placeholders),
                List.of(player), player.getLocation(), true);
    }

    private boolean contains(Player player, Material material) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (is(item, material)) return true;
        }
        return is(player.getInventory().getChestplate(), material);
    }

    private boolean is(ItemStack item, Material material) {
        return item != null && item.getType() == material;
    }
}
