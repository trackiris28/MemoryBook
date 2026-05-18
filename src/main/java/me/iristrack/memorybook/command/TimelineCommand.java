package me.iristrack.memorybook.command;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.service.MemoryService;
import me.iristrack.memorybook.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class TimelineCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final MemoryService memoryService;

    public TimelineCommand(JavaPlugin plugin, MemoryService memoryService) {
        this.plugin = plugin;
        this.memoryService = memoryService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page = 1;
        if (args.length > 0) {
            try { page = Math.max(1, Integer.parseInt(args[0])); } catch (NumberFormatException ignored) {}
        }
        int pageSize = plugin.getConfig().getInt("settings.timeline-page-size", 8);
        List<MemoryEvent> events = memoryService.storage().all();
        int maxPage = Math.max(1, (int) Math.ceil(events.size() / (double) pageSize));
        page = Math.min(page, maxPage);
        int from = (page - 1) * pageSize;
        int to = Math.min(events.size(), from + pageSize);

        TextUtil.send(sender, "&d===== Timeline - " + memoryService.storage().currentSeason() + " &7(" + page + "/" + maxPage + ") &d=====");
        if (events.isEmpty()) {
            TextUtil.send(sender, "&7Chưa có trang lịch sử nào.");
            return true;
        }
        for (int i = from; i < to; i++) {
            MemoryEvent e = events.get(i);
            TextUtil.send(sender, "&8#" + e.id() + " &7Day " + e.serverDay() + " &f" + e.type().icon() + " " + e.rarity().color() + e.title());
            TextUtil.send(sender, "&8  ↳ &7" + e.description());
        }
        TextUtil.send(sender, "&7Dùng &f/timeline " + (page + 1) + " &7để xem trang tiếp theo.");
        return true;
    }
}
