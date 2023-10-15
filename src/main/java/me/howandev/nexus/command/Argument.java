package me.howandev.nexus.command;

import lombok.Getter;
import net.kyori.adventure.text.Component;

import static me.howandev.nexus.locale.Message.*;

@Getter
public class Argument {
    private final Component name;
    private final Component description;
    private final boolean required;
    public Argument(String nameKey, String descriptionKey, boolean required) {
        this.name = ARGUMENT.build(nameKey);
        this.description = ARGUMENT_DESCRIPTION.build(descriptionKey);
        this.required = required;
    }

    public Argument(String nameKey, String descriptionKey) {
        this.name = ARGUMENT.build(nameKey);
        this.description = ARGUMENT_DESCRIPTION.build(descriptionKey);
        this.required = true;
    }

    public Component asPrettyString() {
        if (required) {
            return Component.text()
                    .append(Component.text("<"))
                    .append(name)
                    .append(Component.text(">"))
                    .build();
        }

        return Component.text()
                .append(Component.text("[<"))
                .append(name)
                .append(Component.text(">]"))
                .build();
    }
}
