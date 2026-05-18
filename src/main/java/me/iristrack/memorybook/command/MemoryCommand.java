package me.iristrack.memorybook.command;

import me.iristrack.memorybook.model.MemoryEvent;
import me.iristrack.memorybook.model.MemoryType;
import me.iristrack.memorybook.service.MemoryService;
import me.iristrack.memorybook.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemoryCommand implements TabExecutor {
    private final JavaPlugin plugin;
    private final MemoryService memoryService;

    public MemoryCommand(JavaPlugin plugin, MemoryService memoryService) {
        this.plugin = plugin;
        this.memoryService = memoryService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            TextUtil.send(sender, "&dMemoryBook commands:");
            TextUtil.send(sender, "&f/memory list &7- Xem sự kiện gần đây");
            TextUtil.send(sender, "&f/memory profile <player> &7- Xem ký ức người chơi");
            TextUtil.send(sender, "&f/memory points [player] &7- Xem Memory Points");
            TextUtil.send(sender, "&f/memory add <type> <title> | <description> &7- Admin thêm sự kiện");
            TextUtil.send(sender, "&f/memory remove <id> &7- Admin xóa sự kiện");
            TextUtil.send(sender, "&f/memory season start <name> &7- Admin đổi season");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add" -> add(sender, args);
            case "remove", "delete" -> remove(sender, args);
            case "list" -> list(sender);
            case "profile" -> profile(sender, args);
            case "reload" -> reload(sender);
            case "season" -> season(sender, args);
            case "points" -> points(sender, args);
            default -> TextUtil.send(sender, "&cLệnh không hợp lệ. Dùng /memory để xem trợ giúp.");
        }
        return true;
    }

    private boolean admin(CommandSender sender) {
        if (sender.hasPermission("memorybook.admin")) return true;
        TextUtil.send(sender, plugin.getConfig().getString("messages.no-permission", "&cNo permission."));
        return false;
    }

    private void add(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 3) {
            TextUtil.send(sender, "&cDùng: /memory add <type> <title> | <description>");
            return;
        }
        MemoryType type = MemoryType.fromString(args[1]);
        String raw = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        String[] parts = raw.split("\\|", 2);
        String title = parts[0].trim();
        String description = parts.length > 1 ? parts[1].trim() : title;
        Player actor = sender instanceof Player p ? p : null;
        MemoryEvent event = memoryService.createCustom(type, title, description, actor);
        TextUtil.send(sender, "&aĐã thêm memory #" + event.id() + ": &f" + event.title());
    }

    private void remove(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 2) {
            TextUtil.send(sender, "&cDùng: /memory remove <id>");
            return;
        }
        try {
            int id = Integer.parseInt(args[1]);
            if (memoryService.storage().remove(id)) TextUtil.send(sender, "&aĐã xóa memory #" + id);
            else TextUtil.send(sender, "&cKhông tìm thấy memory #" + id);
        } catch (NumberFormatException ex) {
            TextUtil.send(sender, "&cID phải là số.");
        }
    }

    private void list(CommandSender sender) {
        List<MemoryEvent> events = memoryService.storage().all();
        TextUtil.send(sender, "&d===== Recent Memories =====");
        if (events.isEmpty()) {
            TextUtil.send(sender, "&7Chưa có sự kiện nào.");
            return;
        }
        int start = Math.max(0, events.size() - 10);
        for (int i = events.size() - 1; i >= start; i--) {
            MemoryEvent e = events.get(i);
            TextUtil.send(sender, "&8#" + e.id() + " &f" + e.type().icon() + " " + e.title() + " &8- &7" + e.playersText());
        }
    }

    private void profile(CommandSender sender, String[] args) {
        if (args.length < 2) {
            TextUtil.send(sender, "&cDùng: /memory profile <player>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        List<MemoryEvent> events = memoryService.storage().byPlayer(target.getUniqueId(), args[1]);
        TextUtil.send(sender, "&d===== Memories of " + args[1] + " =====");
        TextUtil.send(sender, "&7Memory Points: &f" + memoryService.storage().getMemoryPoints(target.getUniqueId()));
        if (events.isEmpty()) {
            TextUtil.send(sender, "&7Người chơi này chưa có ký ức nào.");
            return;
        }
        for (MemoryEvent e : events) {
            TextUtil.send(sender, "&8#" + e.id() + " &7Day " + e.serverDay() + " &f" + e.type().icon() + " " + e.title());
        }
    }

    private void reload(CommandSender sender) {
        if (!admin(sender)) return;
        plugin.reloadConfig();
        memoryService.storage().reload();
        TextUtil.send(sender, "&aMemoryBook đã reload.");
    }

    private void season(CommandSender sender, String[] args) {
        if (!admin(sender)) return;
        if (args.length < 3 || !args[1].equalsIgnoreCase("start")) {
            TextUtil.send(sender, "&cDùng: /memory season start <name>");
            return;
        }
        String season = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        memoryService.storage().setSeason(season);
        TextUtil.send(sender, "&aĐã đặt season hiện tại: &f" + season);
    }

    private void points(CommandSender sender, String[] args) {
        OfflinePlayer target;
        if (args.length >= 2) target = Bukkit.getOfflinePlayer(args[1]);
        else if (sender instanceof Player p) target = p;
        else {
            TextUtil.send(sender, "&cConsole dùng: /memory points <player>");
            return;
        }
        TextUtil.send(sender, "&d" + target.getName() + " &7có &f" + memoryService.storage().getMemoryPoints(target.getUniqueId()) + " &7Memory Points.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return filter(List.of("add", "remove", "list", "profile", "reload", "season", "points"), args[0]);
        if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            List<String> types = new ArrayList<>();
            for (MemoryType type : MemoryType.values()) types.add(type.name().toLowerCase());
            return filter(types, args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("season")) return filter(List.of("start"), args[1]);
        return List.of();
    }

    private List<String> filter(List<String> values, String prefix) {
        return values.stream().filter(v -> v.toLowerCase().startsWith(prefix.toLowerCase())).toList();
    }
}
