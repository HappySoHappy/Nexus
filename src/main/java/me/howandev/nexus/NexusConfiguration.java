package me.howandev.nexus;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.howandev.nexus.configuration.Configuration;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private ColorReplacementConfiguration colorReplacement = new ColorReplacementConfiguration();

    @Getter
    @Setter
    @Accessors(fluent = true)
    private ChatReplacementConfiguration chatReplacement = new ChatReplacementConfiguration();

    @Getter
    @Setter
    @Accessors(fluent = true)
    private FilterConfiguration chatFilter = new FilterConfiguration();

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
        colorReplacement.load();
        chatReplacement.load();
        chatFilter.load();
        chatRichFormat.load();
    }

    public class ColorReplacementConfiguration {
        @Getter
        private List<ColorReplacement> colorReplacements = new ArrayList<>();

        public void load() {
            Configuration replacementsSection = configuration.getSection("chat.color-replacements");
            if (replacementsSection != null) {
                for (String replacementId : replacementsSection.getKeys(false)) {
                    Configuration replacementConfiguration = replacementsSection.getSection(replacementId);

                    ColorReplacement colorReplacement = new ColorReplacement();

                    colorReplacement.patterns((List<String>) replacementConfiguration.getList("patterns"));
                    colorReplacement.replacement(replacementConfiguration.getString("replacement"));

                    colorReplacements.add(colorReplacement);
                }
            }
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public class ColorReplacement {
        private List<String> patterns = new ArrayList<>();
        private String replacement;
    }

    public class ChatReplacementConfiguration {
        @Getter
        private List<ChatReplacement> chatReplacements = new ArrayList<>();

        public void load() {
            Configuration replacementsSection = configuration.getSection("chat.chat-replacements");
            if (replacementsSection != null) {
                for (String replacementId : replacementsSection.getKeys(false)) {
                    Configuration replacementConfiguration = replacementsSection.getSection(replacementId);

                    ChatReplacement chatReplacement = new ChatReplacement();

                    chatReplacement.patterns((List<String>) replacementConfiguration.getList("patterns"));
                    chatReplacement.replacement(replacementConfiguration.getString("replacement"));

                    chatReplacements.add(chatReplacement);
                }
            }
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public class ChatReplacement {
        private List<String> patterns = new ArrayList<>();
        private String replacement;
    }

    public class FilterConfiguration {
        public static final String CENSOR_CHAR = "*";

        @Getter
        private List<Filter> filters = new ArrayList<>();
        public void load() {
            Configuration filtersSection = configuration.getSection("chat.filters");
            if (filtersSection != null) {
                for (String filterId : filtersSection.getKeys(false)) {
                    Configuration filterConfiguration = filtersSection.getSection(filterId);

                    Filter filter = new Filter();
                    filter.action(filterConfiguration.getEnumConstant(Action.class, "action", Action.CENSOR));
                    filter.replacement(filterConfiguration.getString("replacement"));
                    filter.strictCasing(filterConfiguration.getBoolean("strict-casing", false));
                    filter.commands((List<String>) filterConfiguration.getList("commands"));
                    filter.patterns((List<String>) filterConfiguration.getList("patterns"));

                    filters.add(filter);
                }
            }
        }

        @Getter
        @Setter
        @Accessors(fluent = true)
        public class Filter {
            private Action action = Action.CENSOR;
            private @Nullable String replacement = null;
            private boolean strictCasing = false;
            private List<String> commands = new ArrayList<>();
            private List<String> patterns = new ArrayList<>();
        }

        public enum Action {
            CENSOR,
            REPLACE,
            DENY,
            GHOST,
            NOTIFY;
        }
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
