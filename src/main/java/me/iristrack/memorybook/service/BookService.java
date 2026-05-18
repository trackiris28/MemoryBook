package me.iristrack.memorybook.service;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookService {
    private final JavaPlugin plugin;
    private final MemoryService memoryService;

    public BookService(JavaPlugin plugin, MemoryService memoryService) {
        this.plugin = plugin;
        this.memoryService = memoryService;
    }

    public ItemStack createBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(MessageUtil.get("book.title"));
        meta.setAuthor(MessageUtil.get("book.author"));

        List<String> pages = new ArrayList<>();
        pages.add(MessageUtil.get("book.intro-page", Map.of("season", memoryService.storage().currentSeason())));

        int max = plugin.getConfig().getInt("settings.max-book-events", 60);
        List<MemoryEvent> events = memoryService.storage().all();
        int start = Math.max(0, events.size() - max);
        for (int i = start; i < events.size(); i++) {
            MemoryEvent e = events.get(i);
            pages.add(MessageUtil.get("book.event-page", Map.of(
                    "icon", e.type().icon(),
                    "title", e.title(),
                    "day", String.valueOf(e.serverDay()),
                    "players", e.playersText(),
                    "time", e.dateText(),
                    "rarity", e.rarity().name(),
                    "description", e.description()
            )));
        }
        meta.setPages(pages);
        book.setItemMeta(meta);
        return book;
    }
}
