package ru.prisonlife.pltrades.commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.prisonlife.pltrades.Request;
import ru.prisonlife.pltrades.Trade;
import ru.prisonlife.pltrades.Trader;
import ru.prisonlife.plugin.PLPlugin;

import java.util.ArrayList;
import java.util.List;

import static ru.prisonlife.pltrades.Main.colorize;
import static ru.prisonlife.pltrades.Main.trades;

/**
 * @author rntsdkv
 * @project PLTrades
 */

public class CommandTrade implements CommandExecutor {

    public static List<Request> requests = new ArrayList<>();

    private final PLPlugin plugin;
    public CommandTrade(PLPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        FileConfiguration configuration = plugin.getConfig();
        Player player = (Player) sender;

        String name = args[0];
        Player recipient = Bukkit.getPlayer(name);

        for (Request request : requests) {
            if (request.sender.equals(player)) {
                player.sendMessage(colorize(configuration.getString("messages.youAlreadyTrading")));
                return true;
            }
            else if (request.recipient.equals(recipient)) {
                player.sendMessage(colorize(configuration.getString("messages.playerAlreadyRequested")));
                return true;
            }
        }

        if (recipient != null && !recipient.isOnline()) {
            recipient.sendMessage(colorize(configuration.getString("messages.playerIsOffline")));
            return true;
        }

        for (Trade trade : trades) {
            if (trade.trader.equals(recipient) || trade.recipient.equals(recipient)) {
                player.sendMessage(colorize("messages.playerIsAlreadyTrading"));
                return true;
            }
        }

        requests.add(new Request(player, recipient));

        recipient.sendMessage(colorize("messages.tradeRequest".replace("%player%", player.getName())));
        TextComponent accept = new TextComponent(colorize("&l&2Принять"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(colorize("&l&2Принять трейд"))));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tradeaccept"));
        recipient.spigot().sendMessage(accept);

        TextComponent decline = new TextComponent(colorize("&l&cОтменить"));
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(colorize("&l&2Отменить трейд"))));
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tradedecline"));
        recipient.spigot().sendMessage(decline);
        return true;
    }
}
