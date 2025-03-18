package me.howandev.nexus.command.tokenizer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizes strings on whitespace, but ignoring whitespace enclosed within quotes.
 */
public class QuotedStringTokenizer {
    private final String string;
    private int cursor;
    public QuotedStringTokenizer(String string) {
        this.string = string;
    }

    public List<String> tokenize(final boolean omitEmptyStringAtEnd) {
        List<String> output = new ArrayList<>();
        while (hasNext())
            output.add(readString());

        if (!omitEmptyStringAtEnd && this.cursor > 0 && isWhitespace(peek(-1)))
            output.add("");

        return output;
    }

    private static boolean isQuoteCharacter(final char c) {
        // return c == '"' || c == '“' || c == '”';
        return c == '\u0022' || c == '\u201C' || c == '\u201D';
    }

    private static boolean isWhitespace(final char c) {
        return c == ' ';
    }

    private @NotNull String readString() {
        if (isQuoteCharacter(peek()))
            return readQuotedString();

        return readUnquotedString();
    }

    private @NotNull String readUnquotedString() {
        final int start = this.cursor;
        while (hasNext() && !isWhitespace(peek()))
            skip();

        final int end = this.cursor;

        if (hasNext())
            skip(); // skip whitespace

        return this.string.substring(start, end);
    }

    private @NotNull String readQuotedString() {
        skip(); // skip start quote

        final int start = this.cursor;
        while (hasNext() && !isQuoteCharacter(peek()))
            skip();

        final int end = this.cursor;

        if (hasNext())
            skip(); // skip end quote

        if (hasNext() && isWhitespace(peek()))
            skip(); // skip whitespace

        return string.substring(start, end);
    }

    private boolean hasNext() {
        return cursor + 1 <= string.length();
    }

    private char peek() {
        return string.charAt(cursor);
    }

    private char peek(final int offset) {
        return string.charAt(cursor + offset);
    }

    private void skip() {
        cursor++;
    }
}
