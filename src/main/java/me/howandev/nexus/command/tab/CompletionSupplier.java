package me.howandev.nexus.command.tab;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CompletionSupplier {
    CompletionSupplier EMPTY = partial -> Collections.emptyList();

    static CompletionSupplier startsWith(String... strings) {
        return startsWith(() -> Arrays.stream(strings));
    }

    static CompletionSupplier startsWith(Collection<String> strings) {
        return startsWith(strings::stream);
    }

    static CompletionSupplier startsWith(Supplier<Stream<String>> stringsSupplier) {
        return partial -> stringsSupplier.get().filter(startsWithIgnoreCase(partial)).collect(Collectors.toList());
    }

    static CompletionSupplier contains(String... strings) {
        return contains(() -> Arrays.stream(strings));
    }

    static CompletionSupplier contains(Collection<String> strings) {
        return contains(strings::stream);
    }

    static CompletionSupplier contains(Supplier<Stream<String>> stringsSupplier) {
        return partial -> stringsSupplier.get().filter(containsIgnoreCase(partial)).collect(Collectors.toList());
    }

    List<String> supplyCompletions(String partial);

    private static Predicate<String> startsWithIgnoreCase(String prefix) {
        return string -> {
            if (string.length() < prefix.length()) {
                return false;
            }
            return string.regionMatches(true, 0, prefix, 0, prefix.length());
        };
    }

    private static Predicate<String> containsIgnoreCase(String substring) {
        return string -> {
            if (string.length() < substring.length()) {
                return false;
            }
            return string.toLowerCase(Locale.ROOT).contains(substring.toLowerCase(Locale.ROOT));
        };
    }
}
