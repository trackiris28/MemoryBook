package me.iristrack.memorybook.listener;

import me.iristrack.memorybook.model.MemoryType;
import me.iristrack.memorybook.service.MemoryService;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class ProgressListener implements Listener {
    private final MemoryService memoryService;

    public ProgressListener(MemoryService memoryService) { this.memoryService = memoryService; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        memoryService.createMemory(MemoryType.FIRST_JOIN, null,
                "Người đầu tiên đặt chân vào server",
                player.getName() + " là người đầu tiên được ghi vào lịch sử mùa SMP này.",
                List.of(player), player.getLocation(), true);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World.Environment env = player.getWorld().getEnvironment();
        if (env == World.Environment.NETHER) {
            memoryService.createMemory(MemoryType.FIRST_NETHER, null,
                    "Cánh cổng địa ngục mở ra",
                    player.getName() + " là người đầu tiên bước vào Nether.",
                    List.of(player), player.getLocation(), true);
        } else if (env == World.Environment.THE_END) {
            memoryService.createMemory(MemoryType.FIRST_END, null,
                    "Bầu trời End được khám phá",
                    player.getName() + " là người đầu tiên bước vào The End.",
                    List.of(player), player.getLocation(), true);
        }
    }
}
