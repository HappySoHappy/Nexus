package me.howandev.nexus.player;

import lombok.Getter;
import lombok.Setter;
import me.howandev.nexus.configuration.impl.MemoryConfiguration;

/**
 * Represents a player that is online, not to be confused with "Online Mode" player.
 */
@Getter
@Setter
public class OnlinePlayer extends OfflinePlayer {
    private PlayerData playerData;
    private MemoryConfiguration attachedData; // prevents saving on purpose, don't store important data here!
    private boolean authenticated;
    public OnlinePlayer() {

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        OnlinePlayer player = (OnlinePlayer) obj;
        return playerData.equals(player.playerData);
    }

    @Override
    public int hashCode() {
        return playerData.hashCode();
    }
}
