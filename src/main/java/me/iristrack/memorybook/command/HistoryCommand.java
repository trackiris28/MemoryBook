package me.iristrack.memorybook.command;

import me.iristrack.memorybook.gui.HistoryGui;
import me.iristrack.memorybook.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistoryCommand implements CommandExecutor {
    private final HistoryGui historyGui;
    public HistoryCommand(HistoryGui historyGui) { this.historyGui = historyGui; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            TextUtil.send(sender, "&cLệnh này chỉ dùng được trong game.");
            return true;
        }
        historyGui.open(player);
        return true;
    }
}
