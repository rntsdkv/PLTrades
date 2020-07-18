package ru.prisonlife.pltrades;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Trader implements Player {

    public List<ItemStack> items = new ArrayList<>();
    public Status status = Status.NOT_READY;

    public List<ItemStack> getItems() { return items; }

    public void clearItems() { items.clear(); }

    public void putItem(ItemStack item) { items.add(item); }
}
