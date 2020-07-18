package ru.prisonlife.pltrades.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.prisonlife.pltrades.Request;
import ru.prisonlife.plugin.PLPlugin;

import static ru.prisonlife.pltrades.Main.colorize;
import static ru.prisonlife.pltrades.commands.CommandTrade.requests;

/**
 * @author rntsdkv
 * @project PLTrades
 */

public class CommandTradeAccept implements CommandExecutor {

    private PLPlugin plugin;
    public CommandTradeAccept(PLPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        for (Request request : requests) {
            if (!request.recipient.equals(player)) continue;

            request.start();
            return true;
        }

        player.sendMessage(colorize(plugin.getConfig().getString("messages.haveNoRequest")));
        return true;
    }
}
