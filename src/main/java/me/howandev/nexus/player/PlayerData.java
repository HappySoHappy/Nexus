package me.howandev.nexus.player;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Represents data that all network players have attached to them.
 */
@Getter
@Setter
public class PlayerData {
    private @NotNull UUID uuid;
    private @NotNull String username;

    private @Nullable String language;

    private Date firstLogin;
    private Date lastLogin;
    private Duration playTime;

    private @Nullable PlayerSkin skin;
    public PlayerData() {

    }

    public static PlayerData empty(UUID uniqueId, String username) {
        PlayerData data = new PlayerData();
        data.setUuid(uniqueId);
        data.setUsername(username);

        data.setLanguage(null);

        data.setFirstLogin(Date.from(Instant.now()));
        data.setLastLogin(Date.from(Instant.now()));
        data.setPlayTime(Duration.ZERO);

        data.setSkin(null);
        return data;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof PlayerData data))
            return false;

        return uuid.equals(data.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
