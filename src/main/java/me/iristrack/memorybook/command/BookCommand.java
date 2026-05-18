package me.iristrack.memorybook.command;

import me.iristrack.memorybook.service.BookService;
import me.iristrack.memorybook.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BookCommand implements CommandExecutor {
    private final BookService bookService;

    public BookCommand(BookService bookService) { this.bookService = bookService; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtil.send(sender, "commands.player-only");
            return true;
        }
        player.getInventory().addItem(bookService.createBook());
        MessageUtil.send(player, "book.received");
        return true;
    }
}
