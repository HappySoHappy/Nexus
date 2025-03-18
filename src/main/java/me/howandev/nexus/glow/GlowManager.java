package me.howandev.nexus.glow;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Deprecated // doesnt work on server running NEZNAMY TAB or smth
//that because tab really tries to override teams, set teams tag color to glow color for it to work
public class GlowManager implements Listener {

    private static final Map<NamedTextColor, Team> colorTeamMap = new HashMap<>();
    public GlowManager() {

    }

    public void setGlowing(@NotNull Player player, boolean glowing) {
        setGlowing(player, glowing, null);
    }

    public void setGlowing(@NotNull Player player, boolean glowing, @Nullable NamedTextColor color) {
        setGlowing(player, glowing, color, null);
    }

    /**
     * Applies colored outline to player's entity, if duration is set - target will be able to get rid of the effect by dying,
     * drinking milk or otherwise clearing their status effects, this applies to external sources as well.
     *
     * @param player target
     * @param glowing if false, removes glow and returns.
     * @param color glowing color, if null {@link NamedTextColor#WHITE} is going to be used instead.
     * @param duration duration of glowing, if null glowing effect never expires.
     */
    public void setGlowing(@NotNull Player player, boolean glowing, @Nullable NamedTextColor color, @Nullable Duration duration) {
        Team team = getOrCreateTeam(color != null ? color : NamedTextColor.WHITE);
        if (!glowing) {
            removeGlowing(player);
            return;
        }

        int glowingTicks = (duration != null)
                ? (int) Math.min(duration.toSeconds() * 20, Integer.MAX_VALUE)
                : PotionEffect.INFINITE_DURATION;

        team.addPlayer(player);

        if (glowingTicks == PotionEffect.INFINITE_DURATION || glowingTicks == Integer.MAX_VALUE) {
            player.setGlowing(true);
        } else {
            PotionEffect glowingEffect = new PotionEffect(PotionEffectType.GLOWING, glowingTicks, 1, true, false, false);
            player.addPotionEffect(glowingEffect);
        }
    }

    public boolean isGlowing(@NotNull Player player) {
        return isGlowing(player, null);
    }

    public boolean isGlowing(@NotNull Player player, @Nullable NamedTextColor color) {
        Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

        for (NamedTextColor teamColor : NamedTextColor.NAMES.values()) {
            Team team = scoreboard.getTeam(teamColor.toString());
            if (team == null) continue;

            if (team.hasPlayer(player)) {
                if (color != null) {
                    if (team.color() == color) return true;
                    continue;
                }

                return true;
            }
        }

        return false;
    }

    public void removeGlowing(@NotNull Player player) {
        Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

        for (NamedTextColor color : NamedTextColor.NAMES.values()) {
            Team team = scoreboard.getTeam(color.toString());
            if (team == null) continue;

            player.removePotionEffect(PotionEffectType.GLOWING);
            player.setGlowing(false);
            team.removePlayer(player);
        }
    }

    private @NotNull Team getOrCreateTeam(NamedTextColor color) {
        return colorTeamMap.computeIfAbsent(color, c -> {

            Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
            Team team = scoreboard.getTeam(color.toString());
            if (team == null) {
                team = scoreboard.registerNewTeam(color.toString());
                team.color(color);
            }

            return team;
        });
    }
}
