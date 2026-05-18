package me.iristrack.memorybook;

import me.iristrack.memorybook.command.BookCommand;
import me.iristrack.memorybook.command.HistoryCommand;
import me.iristrack.memorybook.command.MemoryCommand;
import me.iristrack.memorybook.command.TimelineCommand;
import me.iristrack.memorybook.discord.DiscordSrvHook;
import me.iristrack.memorybook.gui.HistoryGui;
import me.iristrack.memorybook.listener.BossKillListener;
import me.iristrack.memorybook.listener.ProgressListener;
import me.iristrack.memorybook.service.BookService;
import me.iristrack.memorybook.service.MemoryService;
import me.iristrack.memorybook.storage.YamlMemoryStorage;
import me.iristrack.memorybook.util.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {
    private MemoryService memoryService;
    private BookService bookService;
    private HistoryGui historyGui;
    private DiscordSrvHook discordSrvHook;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        MessageUtil.load(this);

        YamlMemoryStorage storage = new YamlMemoryStorage(this);
        this.discordSrvHook = new DiscordSrvHook(this);
        this.discordSrvHook.setup();
        this.memoryService = new MemoryService(this, storage);
        this.bookService = new BookService(this, memoryService);
        this.historyGui = new HistoryGui(memoryService);

        Objects.requireNonNull(getCommand("timeline")).setExecutor(new TimelineCommand(this, memoryService));
        Objects.requireNonNull(getCommand("history")).setExecutor(new HistoryCommand(historyGui));
        Objects.requireNonNull(getCommand("book")).setExecutor(new BookCommand(bookService));
        Objects.requireNonNull(getCommand("memory")).setExecutor(new MemoryCommand(this, memoryService));

        getServer().getPluginManager().registerEvents(new ProgressListener(memoryService), this);
        getServer().getPluginManager().registerEvents(new BossKillListener(this, memoryService), this);
        getServer().getPluginManager().registerEvents(historyGui, this);

        getLogger().info("MemoryBook v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (memoryService != null) memoryService.storage().save();
        getLogger().info("MemoryBook disabled.");
    }

    public MemoryService memoryService() { return memoryService; }
    public BookService bookService() { return bookService; }
    public DiscordSrvHook discordSrvHook() { return discordSrvHook; }
}
