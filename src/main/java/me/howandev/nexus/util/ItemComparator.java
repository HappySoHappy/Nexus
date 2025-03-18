package me.howandev.nexus.util;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ItemComparator {
    private final Set<Predicate<ItemStack>> comparisonSet = new HashSet<>();

    /**
     * When comparing for example damage against an {@link ItemStack},
     * if this value is true the damage comparison will fail if that {@link ItemStack} is not an {@code instanceof} {@link org.bukkit.inventory.meta.Damageable}.
     * Defaults to false - if a damage comparison is done and given {@link ItemStack} is not an {@code instanceof} {@link org.bukkit.inventory.meta.Damageable} it will return true regardless if operation would be successful or not.
     */
    @Getter
    private final boolean isMetaStrict;
    public ItemComparator() {
        this.isMetaStrict = false;
    }

    public ItemComparator(boolean isMetaStrict) {
        this.isMetaStrict = isMetaStrict;
    }

    public void addComparison(Predicate<ItemStack> comparison) {
        comparisonSet.add(comparison);
    }

    public void addComparison(Predicate<ItemStack>... comparisons) {
        comparisonSet.addAll(Arrays.asList(comparisons));
    }

    public boolean compare(ItemStack itemStack) {
        for (Predicate<ItemStack> comparison : comparisonSet) {
            if (!comparison.test(itemStack)) return false;
        }

        return true;
    }

    public static class Builder {
        private final ItemComparator comparator = new ItemComparator();

        public Builder material(Material material) {
            comparator.addComparison((itemStack -> itemStack.getType() == material));
            return this;
        }

        public Builder amount(int amount) {
            comparator.addComparison((itemStack -> itemStack.getAmount() == amount));
            return this;
        }

        public Builder damage(int damage) {
            comparator.addComparison((itemStack) -> {
                ItemMeta meta = itemStack.getItemMeta();
                if (comparator.isMetaStrict()) {
                    if (meta instanceof Damageable damageable) {
                        return damageable.getDamage() == damage;
                    }

                    return false;
                }

                if (meta instanceof Damageable damageable) {
                    return damageable.getDamage() == damage;
                }

                // Comparison not strict and unable to compare, return true
                return true;
            });

            return this;
        }

        public ItemComparator build() {
            return comparator;
        }
    }
}
