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

    public NexusConfiguration merge(NexusConfiguration other) {
        NexusConfiguration merged = new NexusConfiguration(this.configuration);

        // Booleans and strings
        merged.chatUseFormat = other.chatUseFormat || this.chatUseFormat;
        merged.chatFormat = !other.chatFormat.equals("<%player%> %message%") ? other.chatFormat : this.chatFormat;

        // Color replacements
        merged.colorReplacement.colorReplacements.addAll(this.colorReplacement.colorReplacements);
        for (ColorReplacement cr : other.colorReplacement.colorReplacements) {
            ColorReplacement copy = new ColorReplacement()
                    .patterns(new ArrayList<>(cr.patterns()))
                    .replacement(cr.replacement());
            merged.colorReplacement.colorReplacements.add(copy);
        }

        // Chat replacements
        merged.chatReplacement.chatReplacements.addAll(this.chatReplacement.chatReplacements);
        for (ChatReplacement cr : other.chatReplacement.chatReplacements) {
            ChatReplacement copy = new ChatReplacement()
                    .permission(cr.permission())
                    .patterns(new ArrayList<>(cr.patterns()))
                    .replacement(cr.replacement());
            merged.chatReplacement.chatReplacements.add(copy);
        }

        // Chat filters
        merged.chatFilter.filters.addAll(this.chatFilter.filters);
        for (FilterConfiguration.Filter f : other.chatFilter.filters) {
            FilterConfiguration.Filter copy = merged.chatFilter.new Filter()
                    .action(f.action())
                    .replacement(f.replacement())
                    .strictCasing(f.strictCasing())
                    .commands(new ArrayList<>(f.commands()))
                    .patterns(new ArrayList<>(f.patterns()));
            merged.chatFilter.filters.add(copy);
        }

        // Rich format — prefer 'other' fields if set, fallback to base
        merged.chatRichFormat
                .prefix(!other.chatRichFormat.prefix().isEmpty() ? other.chatRichFormat.prefix() : this.chatRichFormat.prefix())
                .sender(!other.chatRichFormat.sender().isEmpty() ? other.chatRichFormat.sender() : this.chatRichFormat.sender())
                .suffix(!other.chatRichFormat.suffix().isEmpty() ? other.chatRichFormat.suffix() : this.chatRichFormat.suffix())
                .separator(!other.chatRichFormat.separator().isEmpty() ? other.chatRichFormat.separator() : this.chatRichFormat.separator())
                .message(!other.chatRichFormat.message().equals("%message%") ? other.chatRichFormat.message() : this.chatRichFormat.message());

        return merged;
    }


    public class ColorReplacementConfiguration {
        @Getter
        private List<ColorReplacement> colorReplacements = new ArrayList<>();

        public void load() {
            Configuration replacementsSection = configuration.getSection("chat.color-replacements");
            if (replacementsSection != null) {
                colorReplacements.clear();

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
            chatReplacements.clear();

            Configuration replacementsSection = configuration.getSection("chat.chat-replacements");
            if (replacementsSection != null) {

                for (String replacementId : replacementsSection.getKeys(false)) {
                    Configuration replacementConfiguration = replacementsSection.getSection(replacementId);

                    ChatReplacement chatReplacement = new ChatReplacement();

                    chatReplacement.permission(replacementConfiguration.getString("permission", ""));
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
        private String permission = "";
        private List<String> patterns = new ArrayList<>();
        private String replacement;
    }

    public class FilterConfiguration {
        public static final String CENSOR_CHAR = "*";

        @Getter
        private List<Filter> filters = new ArrayList<>();
        public void load() {
            filters.clear();

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
            GHOST;
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
