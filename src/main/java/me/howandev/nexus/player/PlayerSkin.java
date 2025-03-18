package me.howandev.nexus.player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerSkin {
    private SkinType type;
    private String value;
    public PlayerSkin() {

    }

    public enum SkinType {
        /**
         * Represents a Skin that is an image stored on disk.
         */
        FILE,
        /**
         * Represents a Skin that is a URL pointing to an image.
         */
        URL,
        /**
         * Represents a Skin that is provided by Mojang API.
         */
        NAME;
    }
}
