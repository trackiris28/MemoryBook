package me.iristrack.memorybook.gui;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.model.MemoryType;
import me.iristrack.memorybook.service.MemoryService;
import me.iristrack.memorybook.util.MessageUtil;
import me.iristrack.memorybook.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HistoryGui implements Listener {
    private final MemoryService memoryService;

    public HistoryGui(MemoryService memoryService) { this.memoryService = memoryService; }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, MessageUtil.get("gui.title"));
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 31};
        MemoryType[] types = MemoryType.values();
        for (int i = 0; i < types.length && i < slots.length; i++) {
            MemoryType type = types[i];
            inv.setItem(slots[i], item(type));
        }
        inv.setItem(49, simple(Material.BOOK, MessageUtil.get("gui.all-timeline-name"), MessageUtil.list("gui.all-timeline-lore")));
        player.openInventory(inv);
    }

    private ItemStack item(MemoryType type) {
        List<MemoryEvent> events = memoryService.storage().byType(type);
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtil.get("gui.type-count", Map.of("count", String.valueOf(events.size()))));
        events.stream().sorted(Comparator.comparingInt(MemoryEvent::id).reversed()).limit(4)
                .forEach(e -> lore.add(MessageUtil.get("gui.type-preview", Map.of("id", String.valueOf(e.id()), "title", e.title()))));
        lore.add(" ");
        lore.add(MessageUtil.get("gui.click-view"));
        return simple(type.material(), MessageUtil.get("gui.type-name", Map.of("icon", type.icon(), "type", type.displayName())), lore);
    }

    private ItemStack simple(Material material, String name, List<String> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(TextUtil.color(name));
        meta.setLore(lore.stream().map(TextUtil::color).toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(MessageUtil.get("gui.title"))) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        for (MemoryType type : MemoryType.values()) {
            if (item.getType() == type.material()) {
                player.closeInventory();
                List<MemoryEvent> list = memoryService.storage().byType(type);
                MessageUtil.send(player, "gui.chat-header", Map.of("icon", type.icon(), "type", type.displayName()));
                if (list.isEmpty()) {
                    MessageUtil.send(player, "memory.empty");
                    return;
                }
                list.stream().sorted(Comparator.comparingInt(MemoryEvent::id).reversed()).limit(8).forEach(e ->
                        MessageUtil.send(player, "gui.chat-entry", Map.of(
                                "id", String.valueOf(e.id()),
                                "day", String.valueOf(e.serverDay()),
                                "title", e.title(),
                                "description", e.description()
                        )));
                return;
            }
        }
    }
}
