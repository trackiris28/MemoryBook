package me.iristrack.memorybook.service;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

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
        meta.setTitle(TextUtil.color("Memory Book"));
        meta.setAuthor("SMP Server");
        List<String> pages = new ArrayList<>();
        pages.add(TextUtil.color("&0&lMemory Book\n\n&0Season: " + memoryService.storage().currentSeason() + "\n\n&0Cuốn sách này ghi lại lịch sử đáng nhớ của server SMP."));

        int max = plugin.getConfig().getInt("settings.max-book-events", 60);
        List<MemoryEvent> events = memoryService.storage().all();
        int start = Math.max(0, events.size() - max);
        for (int i = start; i < events.size(); i++) {
            MemoryEvent e = events.get(i);
            pages.add(TextUtil.color("&0" + e.type().icon() + " &l" + e.title() + "\n\n" +
                    "&0Day: " + e.serverDay() + "\n" +
                    "Players: " + e.playersText() + "\n" +
                    "Time: " + e.dateText() + "\n\n" +
                    e.description()));
        }
        meta.setPages(pages);
        book.setItemMeta(meta);
        return book;
    }
}
