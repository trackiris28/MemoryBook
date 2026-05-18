package me.iristrack.memorybook.gui;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.model.MemoryType;
import me.iristrack.memorybook.service.MemoryService;
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

public class HistoryGui implements Listener {
    private final MemoryService memoryService;
    private static final String TITLE = TextUtil.color("&5📖 Memory Book History");

    public HistoryGui(MemoryService memoryService) { this.memoryService = memoryService; }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        int[] slots = {10, 11, 12, 13, 14, 15, 16, 28, 30, 32, 34};
        MemoryType[] types = MemoryType.values();
        for (int i = 0; i < types.length && i < slots.length; i++) {
            MemoryType type = types[i];
            inv.setItem(slots[i], item(type));
        }
        inv.setItem(49, simple(Material.BOOK, "&dTất cả timeline", List.of("&7Dùng &f/timeline &7để xem đầy đủ.")));
        player.openInventory(inv);
    }

    private ItemStack item(MemoryType type) {
        List<MemoryEvent> events = memoryService.storage().byType(type);
        List<String> lore = new ArrayList<>();
        lore.add("&7Số sự kiện: &f" + events.size());
        events.stream().sorted(Comparator.comparingInt(MemoryEvent::id).reversed()).limit(4)
                .forEach(e -> lore.add("&8#" + e.id() + " &f" + e.title()));
        lore.add(" ");
        lore.add("&eClick để xem trong chat");
        return simple(type.material(), "&f" + type.icon() + " " + type.displayName(), lore);
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
        if (!event.getView().getTitle().equals(TITLE)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        for (MemoryType type : MemoryType.values()) {
            if (item.getType() == type.material()) {
                player.closeInventory();
                List<MemoryEvent> list = memoryService.storage().byType(type);
                TextUtil.send(player, "&d===== " + type.icon() + " " + type.displayName() + " =====");
                if (list.isEmpty()) {
                    TextUtil.send(player, "&7Chưa có sự kiện nào.");
                    return;
                }
                list.stream().sorted(Comparator.comparingInt(MemoryEvent::id).reversed()).limit(8).forEach(e ->
                        TextUtil.send(player, "&8#" + e.id() + " &7Day " + e.serverDay() + " &f" + e.title() + " &8- &7" + e.description()));
                return;
            }
        }
    }
}
