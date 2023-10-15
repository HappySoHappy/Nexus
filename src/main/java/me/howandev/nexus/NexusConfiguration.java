package me.howandev.nexus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.howandev.nexus.configuration.Configuration;

import java.io.IOException;

public class NexusConfiguration {
    @Getter
    @Setter
    @Accessors(fluent = true)
    private boolean chatUseFormat = false;

    @Getter
    @Setter
    @Accessors(fluent = true)
    private String chatFormat = "<%player%> %message%";

    @Getter
    @Setter
    @Accessors(fluent = true)
    private RichFormatConfiguration chatRichFormat = new RichFormatConfiguration();
    private final Configuration configuration;
    public NexusConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void load() throws IOException {
        chatUseFormat(configuration.getBoolean("chat.use-format", chatUseFormat));
        chatFormat(configuration.getString("chat.format", chatFormat));
        chatRichFormat.load();
    }

    public class RichFormatConfiguration {
        @Getter
        @Setter
        @Accessors(fluent = true)
        private String prefix = "";

        @Getter
        @Setter
        @Accessors(fluent = true)
        private String sender = "<%player%>";

        @Getter
        @Setter
        @Accessors(fluent = true)
        private String suffix = "";

        @Getter
        @Setter
        @Accessors(fluent = true)
        private String separator = "";


        @Getter
        @Setter
        @Accessors(fluent = true)
        private String message = "%message%";

        public void load() {
            prefix(configuration.getString("chat.rich-format.prefix", prefix));
            sender(configuration.getString("chat.rich-format.sender", sender));
            suffix(configuration.getString("chat.rich-format.suffix", suffix));
            separator(configuration.getString("chat.rich-format.separator", separator));
            message(configuration.getString("chat.rich-format.message", message));
        }
    }
}
