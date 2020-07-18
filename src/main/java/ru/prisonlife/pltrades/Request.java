package ru.prisonlife.pltrades;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.prisonlife.plugin.PLPlugin;

import static ru.prisonlife.pltrades.Main.colorize;
import static ru.prisonlife.pltrades.Main.trades;
import static ru.prisonlife.pltrades.commands.CommandTrade.requests;

/**
 * @author rntsdkv
 * @project PLTrades
 */

public class Request {

    private PLPlugin plugin;
    public Request(PLPlugin plugin) {
        this.plugin = plugin;
    }

    public Player sender;
    public Player recipient;
    public int time;
    public BukkitTask task;

    public Request(Player sender, Player recipient) {
        this.sender = sender;
        this.recipient = recipient;
        time = 30;
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            reduceTime();
            if (time == 0) {
                sender.sendMessage(colorize(plugin.getConfig().getString("messages.requestTimeOut")));
                requests.remove(this);
                task.cancel();
            }
        }, 20, 20);
    }

    public void reduceTime() { time--; }

    public void start() {
        // TODO заменить 1 на уровень сделок у sender'a
        trades.add(new Trade(sender, recipient, 1));
    }

    public void close() {
        sender.sendMessage(colorize(plugin.getConfig().getString("messages.requestClose")));
    }
}
