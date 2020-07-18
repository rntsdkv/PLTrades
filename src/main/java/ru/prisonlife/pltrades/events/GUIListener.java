package ru.prisonlife.pltrades.events;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.prisonlife.pltrades.Main;
import ru.prisonlife.pltrades.Status;
import ru.prisonlife.pltrades.Trade;
import ru.prisonlife.pltrades.Trader;
import ru.prisonlife.plugin.PLPlugin;
import ru.prisonlife.util.InventoryUtil;

import static ru.prisonlife.pltrades.Main.colorize;
import static ru.prisonlife.pltrades.Main.trades;

/**
 * @author rntsdkv
 * @project PLTrades
 */

public class GUIListener implements Listener {

    private PLPlugin plugin;
    public GUIListener(PLPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory.getType() == InventoryType.PLAYER) return;

        for (Trade trade : trades) {
            boolean isTrader;
            if (trade.trader.equals(player)) isTrader = true;
            else if (trade.recipient.equals(player)) isTrader = false;
            else continue;

            if (trade.level == 1) {
                int slot = event.getSlot();
                ItemStack item = event.getCurrentItem();
                Material itemType = item.getType();

                if (itemType == Material.GREEN_DYE) {
                    event.setCancelled(true);
                    if (isTrader) {
                        Status status = trade.trader.status;
                        if (status == Status.NOT_READY) trade.trader.status = Status.READY;
                    }
                    else {
                        Status status = trade.recipient.status;
                        if (status == Status.NOT_READY) trade.recipient.status = Status.READY;
                    }
                    trade.update();
                }
                else if (itemType == Material.RED_DYE) {
                    event.setCancelled(true);
                    if (isTrader) {
                        Status status = trade.trader.status;
                        if (status == Status.READY) trade.trader.status = Status.NOT_READY;
                    }
                    else {
                        Status status = trade.recipient.status;
                        if (status == Status.READY) trade.recipient.status = Status.NOT_READY;
                    }
                    trade.update();
                } else if (slot != 10) {
                    event.setCancelled(true);
                } else {
                    if (isTrader) {
                        Trader trader = trade.trader;
                        trader.clearItems();
                        trader.putItem(event.getClickedInventory().getItem(slot));
                    }
                    else {
                        Trader recipient = trade.recipient;
                        recipient.clearItems();
                        recipient.putItem(event.getClickedInventory().getItem(slot));
                    }
                    trade.update();
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        FileConfiguration configuration = plugin.getConfig();
        Player player = (Player) event.getPlayer();

        for (Trade trade : trades) {
            if (trade.trader.equals(player)) {
                Trader recipient = trade.recipient;
                trades.remove(trade);
                recipient.closeInventory();

                trade.close();

                InventoryUtil.putItemStacks(player.getInventory(), trade.trader.items);
                InventoryUtil.putItemStacks(recipient.getInventory(), recipient.items);

                player.sendMessage(colorize(configuration.getString("messages.tradeClosed")));
                recipient.sendMessage(colorize(configuration.getString("messages.tradeClosed")));
            }
        }
    }
}
