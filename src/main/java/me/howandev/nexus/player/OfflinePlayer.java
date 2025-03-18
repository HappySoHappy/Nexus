package me.howandev.nexus.player;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a player that if offline, not to be confused with a "Cracked" player.
 */
@Getter
@Setter
public class OfflinePlayer {
    private PlayerData playerData;
    public OfflinePlayer() {

    }

    public String getUsername() {
        return playerData.getUsername();
    }

    public UUID getUuid() {
        return playerData.getUuid();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        OfflinePlayer player = (OfflinePlayer) obj;
        return playerData.equals(player.playerData);
    }

    @Override
    public int hashCode() {
        return playerData.hashCode();
    }
}
