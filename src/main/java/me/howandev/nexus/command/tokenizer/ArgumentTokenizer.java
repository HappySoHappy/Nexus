package me.howandev.nexus.command.tokenizer;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Tokenizes input into distinct "argument" tokens.
 *
 * <p>Splits on whitespace, except when surrounded by quotes.</p>
 */
public enum ArgumentTokenizer {
    EXECUTE {
        @Override
        public @NotNull List<String> tokenizeInput(final String args) {
            return new QuotedStringTokenizer(args).tokenize(true);
        }
    },
    TAB_COMPLETE {
        @Override
        public @NotNull List<String> tokenizeInput(final String args) {
            return new QuotedStringTokenizer(args).tokenize(false);
        }
    };

    public @NotNull List<String> tokenizeInput(final String @NotNull [] args) {
        return tokenizeInput(String.join(" ", args));
    }

    public abstract @NotNull List<String> tokenizeInput(final String args);
}
