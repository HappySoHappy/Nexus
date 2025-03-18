package me.howandev.nexus.kit;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Kit { // shoudl be record but whatevs
    private String name;
    private Set<ItemStack> items;

    public Kit(String name) {
        this.name = name;
        this.items = new HashSet<>();
    }

    public Kit(String name, Set<ItemStack> items) {
        this.name = name;
        this.items = items;
    }

    public void addItem(ItemStack item) {
        items.add(item);
    }
}
