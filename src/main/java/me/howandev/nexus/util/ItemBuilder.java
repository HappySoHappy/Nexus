package me.howandev.nexus.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder implements Cloneable {
    private ItemStack itemStack;
    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }



    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(itemStack);
    }

    public ItemStack toItemStack() {
        return itemStack;
    }

    public ItemStack build() {
        return itemStack;
    }
}
