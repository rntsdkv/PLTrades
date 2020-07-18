package ru.prisonlife.pltrades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import ru.prisonlife.plugin.PLPlugin;
import ru.prisonlife.util.InventoryUtil;

import java.util.Iterator;
import java.util.List;

import static ru.prisonlife.pltrades.Main.colorize;
import static ru.prisonlife.pltrades.Main.trades;

/**
 * @author rntsdkv
 * @project PLTrades
 */

public class Trade {

    private PLPlugin plugin;
    public Trade(PLPlugin plugin) {
        this.plugin = plugin;
    }

    public Trader trader;
    public Trader recipient;
    public int level;
    public BukkitTask particles;
    public BukkitTask task;

    public Trade(Player trader, Player recipient, int level) {
        this.trader = (Trader) trader;
        this.recipient = (Trader) recipient;
        this.level = level;

        openGUI();
        start();
    }

    private void openGUI() {
        if (level == 1) {
            ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
            ItemStack yellowGlass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
            ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemStack accept = new ItemStack(Material.GREEN_DYE);
            ItemStack decline = new ItemStack(Material.RED_DYE);
            ItemStack compass = new ItemStack(Material.COMPASS);

            Inventory gui1 = Bukkit.createInventory(null, 27, "Сделка с " + recipient.getName());
            gui1.setItem(0, whiteGlass); gui1.setItem(9, whiteGlass); gui1.setItem(18, yellowGlass);
            gui1.setItem(1, yellowGlass); gui1.setItem(10, null); gui1.setItem(19, yellowGlass);
            gui1.setItem(2, yellowGlass); gui1.setItem(11, whiteGlass); gui1.setItem(20, whiteGlass);
            gui1.setItem(3, compass); gui1.setItem(12, blackGlass); gui1.setItem(21, accept);
            gui1.setItem(4, blackGlass); gui1.setItem(13, blackGlass); gui1.setItem(22, blackGlass);
            gui1.setItem(5, compass); gui1.setItem(14, blackGlass); gui1.setItem(23, decline);
            gui1.setItem(6, yellowGlass); gui1.setItem(15, yellowGlass); gui1.setItem(24, whiteGlass);
            gui1.setItem(7, whiteGlass); gui1.setItem(16, null); gui1.setItem(25, whiteGlass);
            gui1.setItem(8, whiteGlass); gui1.setItem(17, yellowGlass); gui1.setItem(26, yellowGlass);

            Inventory gui2 = Bukkit.createInventory(null, 27, "Сделка с " + trader.getName());
            gui2.setItem(0, whiteGlass); gui2.setItem(9, whiteGlass); gui2.setItem(18, yellowGlass);
            gui2.setItem(1, yellowGlass); gui2.setItem(10, null); gui2.setItem(19, yellowGlass);
            gui2.setItem(2, yellowGlass); gui2.setItem(11, whiteGlass); gui2.setItem(20, whiteGlass);
            gui2.setItem(3, compass); gui2.setItem(12, blackGlass); gui2.setItem(21, accept);
            gui2.setItem(4, blackGlass); gui2.setItem(13, blackGlass); gui2.setItem(22, blackGlass);
            gui2.setItem(5, compass); gui2.setItem(14, blackGlass); gui2.setItem(23, decline);
            gui2.setItem(6, yellowGlass); gui2.setItem(15, yellowGlass); gui2.setItem(24, whiteGlass);
            gui2.setItem(7, whiteGlass); gui2.setItem(16, null); gui2.setItem(25, whiteGlass);
            gui2.setItem(8, whiteGlass); gui2.setItem(17, yellowGlass); gui2.setItem(26, yellowGlass);

            trader.openInventory(gui1);
            recipient.openInventory(gui2);
        }
    }

    private void start() {
        particles = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            trader.spawnParticle(Particle.LAVA, trader.getLocation(), 10);
            recipient.spawnParticle(Particle.LAVA, recipient.getLocation(), 10);
        }, 0, 20);
    }

    public void update() {
        if (trader.status == Status.READY) {
            trader.getOpenInventory().setItem(3, new ItemStack(Material.BELL));
            recipient.getOpenInventory().setItem(5, new ItemStack(Material.BELL));
        } else {
            trader.getOpenInventory().setItem(3, new ItemStack(Material.COMPASS));
            recipient.getOpenInventory().setItem(5, new ItemStack(Material.COMPASS));
        }

        if (recipient.status == Status.READY) {
            recipient.getOpenInventory().setItem(3, new ItemStack(Material.BELL));
            trader.getOpenInventory().setItem(5, new ItemStack(Material.BELL));
        } else {
            recipient.getOpenInventory().setItem(3, new ItemStack(Material.COMPASS));
            trader.getOpenInventory().setItem(5, new ItemStack(Material.COMPASS));
        }

        if (level == 1) {
            ItemStack item = null;
            if (trader.items.size() == 1) item = trader.items.get(0);
            recipient.getOpenInventory().setItem(16, item);

            item = null;
            if (recipient.items.size() == 1) item = recipient.items.get(0);
            trader.getOpenInventory().setItem(16, item);
        }

        if (trader.status == Status.READY && recipient.status == Status.READY && !task.isSync()) {
            task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                close();
                trades.remove(this);

                trader.closeInventory();
                recipient.closeInventory();
                FileConfiguration configuration = plugin.getConfig();

                if (!canPuttedItems(recipient.getInventory(), trader.items)) {
                    recipient.sendMessage(colorize(configuration.getString("messages.notEnoughSlots")));
                    trader.sendMessage(colorize("&l&cУ " + recipient.getName() + " недостаточно места в карманах! Сделка разорвана."));
                    return;
                }

                if (!canPuttedItems(trader.getInventory(), recipient.items)) {
                    trader.sendMessage(colorize(configuration.getString("messages.notEnoughSlots")));
                    recipient.sendMessage(colorize("&l&cУ " + trader.getName() + " недостаточно места в карманах! Сделка разорвана."));
                    return;
                }

                InventoryUtil.putItemStacks(trader.getInventory(), recipient.items);
                InventoryUtil.putItemStacks(recipient.getInventory(), trader.items);

                String message = configuration.getString("messages.tradeFinished");
                trader.sendMessage(colorize(message));
                recipient.sendMessage(colorize(message));
            }, 100);
        }
    }

    public void close() {
        particles.cancel();
    }

    private boolean canPuttedItems(Inventory inventory, List<ItemStack> items) {
        int inventorySze = inventory instanceof PlayerInventory ? 36 : inventory.getSize();
        int counter = 0;
        Iterator var = items.iterator();

        while (true) {
            while (var.hasNext()) {
                ItemStack item = (ItemStack) var.next();
                String localizedName = item.getItemMeta().getLocalizedName();
                int amount1 = item.getAmount();

                for (int j = 0; j < inventorySze; j++) {
                    ItemStack itemStack = inventory.getItem(j);
                    if (itemStack == null) {
                        counter++;
                        break;
                    }

                    if (itemStack.getItemMeta().getLocalizedName().equals(localizedName)) {
                        int amount2 = itemStack.getAmount();
                        if (amount1 + amount2 <= 64) {
                            counter++;
                            break;
                        }

                        amount1 -= 64 - amount2;
                    }
                }
            }

            return counter == items.size();
        }
    }
}
