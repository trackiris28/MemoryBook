package me.iristrack.memorybook.listener;

import me.iristrack.memorybook.model.MemoryType;
import me.iristrack.memorybook.service.MemoryService;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class BossKillListener implements Listener {
    private final JavaPlugin plugin;
    private final MemoryService memoryService;

    public BossKillListener(JavaPlugin plugin, MemoryService memoryService) {
        this.plugin = plugin;
        this.memoryService = memoryService;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (entity instanceof EnderDragon) {
            List<Player> players = nearbyPlayers(entity.getLocation(), plugin.getConfig().getDouble("settings.dragon-assist-radius", 80.0));
            if (players.isEmpty() && killer != null) players = List.of(killer);
            String names = players.isEmpty() ? "cộng đồng server" : String.join(", ", players.stream().map(Player::getName).toList());
            memoryService.createMemory(MemoryType.FIRST_DRAGON_KILL, null,
                    "Kỷ nguyên End bắt đầu",
                    "Ender Dragon đã bị đánh bại bởi " + names + ".",
                    players, entity.getLocation(), true);
        } else if (entity instanceof Wither) {
            List<Player> players = killer == null ? nearbyPlayers(entity.getLocation(), 40.0) : List.of(killer);
            String names = players.isEmpty() ? "cộng đồng server" : String.join(", ", players.stream().map(Player::getName).toList());
            memoryService.createMemory(MemoryType.FIRST_WITHER_KILL, null,
                    "Wither đầu tiên sụp đổ",
                    "Wither đầu tiên của mùa SMP đã bị đánh bại bởi " + names + ".",
                    players, entity.getLocation(), true);
        }
    }

    private List<Player> nearbyPlayers(Location location, double radius) {
        List<Player> result = new ArrayList<>();
        World world = location.getWorld();
        if (world == null) return result;
        double radiusSquared = radius * radius;
        for (Player player : world.getPlayers()) {
            if (player.getLocation().distanceSquared(location) <= radiusSquared) result.add(player);
        }
        return result;
    }
}
