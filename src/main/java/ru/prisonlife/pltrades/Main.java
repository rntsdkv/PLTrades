package ru.prisonlife.pltrades;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import ru.prisonlife.pltrades.commands.CommandTrade;
import ru.prisonlife.pltrades.commands.CommandTradeAccept;
import ru.prisonlife.pltrades.commands.CommandTradeDecline;
import ru.prisonlife.pltrades.events.GUIListener;
import ru.prisonlife.plugin.PLPlugin;
import ru.prisonlife.plugin.PromisedPluginFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Main extends PLPlugin {

    public static List<Trade> trades = new ArrayList<>();

    @Override
    public String getPluginName() {
        return "PLTrades";
    }

    @Override
    public List<PromisedPluginFile> initPluginFiles() {
        return null;
    }

    @Override
    public void onEnable() {
        copyConfigFile();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        getCommand("trade").setExecutor(new CommandTrade(this));
        getCommand("tradeaccept").setExecutor(new CommandTradeAccept(this));
        getCommand("tradedecline").setExecutor(new CommandTradeDecline(this));
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new GUIListener(this), this);
    }

    private void copyConfigFile() {
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            getLogger().info("PLTrades | Default Config copying...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
    }

    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
