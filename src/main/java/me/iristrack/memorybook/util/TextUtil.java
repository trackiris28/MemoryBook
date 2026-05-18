package me.iristrack.memorybook.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class TextUtil {
    private TextUtil() {}

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    public static void sendList(CommandSender sender, List<String> messages) {
        for (String message : messages) send(sender, message);
    }
}
