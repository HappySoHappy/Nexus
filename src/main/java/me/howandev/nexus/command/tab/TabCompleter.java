package me.howandev.nexus.command.tab;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabCompleter {
    public static TabCompleter create() {
        return new TabCompleter();
    }
    private final Map<Integer, CompletionSupplier> suppliers = new HashMap<>();
    private int from = Integer.MAX_VALUE;
    private TabCompleter() {

    }

    /**
     * Marks that the given completion supplier should be used to compute tab
     * completions at the given index.
     *
     * @param position the position
     * @param supplier the supplier
     * @return this
     */
    @Contract("_, _ -> this")
    public @NotNull TabCompleter at(final int position, CompletionSupplier supplier) {
        if (position >= from)
            throw new IllegalArgumentException();

        suppliers.put(position, supplier);
        return this;
    }

    /**
     * Marks that the given completion supplier should be used to compute tab
     * completions at the given index and at all subsequent indexes infinitely.
     *
     * @param position the position
     * @param supplier the supplier
     * @return this
     */
    @Contract("_, _ -> this")
    public @NotNull TabCompleter from(final int position, CompletionSupplier supplier) {
        if (from != Integer.MAX_VALUE)
            throw new IllegalArgumentException();

        this.suppliers.put(position, supplier);
        this.from = position;
        return this;
    }

    public @NotNull List<String> complete(List<String> args) {
        int lastIndex = 0;
        String partial;

        // nothing entered yet
        if (args.isEmpty() || (partial = args.get(lastIndex = args.size() - 1)).trim().isEmpty())
            return getCompletions(lastIndex, "");

        // started typing something
        return getCompletions(lastIndex, partial);
    }

    private @NotNull List<String> getCompletions(final int position, String partial) {
        if (position >= this.from) {
            return this.suppliers.get(this.from).supplyCompletions(partial);
        }

        return this.suppliers.getOrDefault(position, CompletionSupplier.EMPTY).supplyCompletions(partial);
    }
}
