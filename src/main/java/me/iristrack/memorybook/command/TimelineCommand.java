package me.iristrack.memorybook.command;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.service.MemoryService;
import me.iristrack.memorybook.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

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

        MessageUtil.send(sender, "timeline.header", Map.of(
                "season", memoryService.storage().currentSeason(),
                "page", String.valueOf(page),
                "max_page", String.valueOf(maxPage)
        ));
        if (events.isEmpty()) {
            MessageUtil.send(sender, "timeline.empty");
            return true;
        }
        for (int i = from; i < to; i++) {
            MemoryEvent e = events.get(i);
            MessageUtil.send(sender, "timeline.entry", Map.of(
                    "id", String.valueOf(e.id()),
                    "day", String.valueOf(e.serverDay()),
                    "icon", e.type().icon(),
                    "rarity_color", e.rarity().color().toString(),
                    "title", e.title()
            ));
            MessageUtil.send(sender, "timeline.description", Map.of("description", e.description()));
        }
        if (page < maxPage) {
            MessageUtil.send(sender, "timeline.next-page", Map.of("next_page", String.valueOf(page + 1)));
        }
        return true;
    }
}
