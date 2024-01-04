package me.howandev.nexus.locale;

import me.howandev.nexus.command.sender.Sender;
import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

public interface Message {
    Args1<String> ARGUMENT = (argument) -> text()
            .append(translatable()
                    .key("argument." + argument)
            ).build();

    Args1<String> ARGUMENT_DESCRIPTION = (argument) -> text()
            .append(translatable()
                    .key("argument.description." + argument)
            ).build();

    Args1<String> COMMAND_DESCRIPTION = (command) -> text()
            .append(translatable()
                    .key("command." + command + ".description")
            ).build();

    Args1<String> COMMAND_CONSOLE_ONLY = (command) -> text()
            .append(translatable()
                    .key("command.console-only")
                    .args(
                            text(command)
                    )
            ).build();

    Args1<String> COMMAND_MISSING_PERMISSION = (command) -> text()
            .append(translatable()
                    .key("command.missing-permission")
                    .args(
                            text(command)
                    )
            ).build();

    Args0 COMMAND_UNEXPECTED_EXCEPTION = () -> text()
            .append(translatable()
                    .key("command.unexpected-exception")
            ).build();

    Args1<String> COMMAND_UNKNOWN = (command) -> text()
            .append(translatable()
                    .key("command.unknown")
                    .args(
                            text(command)
                    )
            ).build();

    Args1<Component> COMMAND_WRONG_USAGE = (properUsage) -> text()
            .append(translatable()
                    .key("command.wrong-usage")
                    .args(
                            properUsage
                    )
            ).build();

    Args1<String> COMMAND_FEED_OTHER = (target) -> text()
            .append(translatable()
                    .key("command.feed.feed-other")
                    .args(
                            text(target)
                    )
            ).build();

    Args0 COMMAND_FEED_SELF = () -> text()
            .append(translatable()
                    .key("command.feed.feed-self")
            ).build();


    //region Command Fly
    Args1<String> COMMAND_FLIGHT_ENABLED_OTHER = (target) -> text()
            .append(translatable()
                    .key("command.fly.set_enabled-other")
                    .args(
                            text(target)
                    )
            ).build();

    Args1<String> COMMAND_FLIGHT_DISABLED_OTHER = (target) -> text()
            .append(translatable()
                    .key("command.fly.set_disabled-other")
                    .args(
                            text(target)
                    )
            ).build();

    Args0 COMMAND_FLIGHT_ENABLED_SELF = () -> text()
            .append(translatable()
                    .key("command.fly.set_enabled-self")
            ).build();

    Args0 COMMAND_FLIGHT_DISABLED_SELF = () -> text()
            .append(translatable()
                    .key("command.fly.set_disabled-self")
            ).build();
    //endregion

    //region Command - Speed
    Args2<String, Float> COMMAND_SPEED_SET_FLY_OTHER = (target, speed) -> text()
            .append(translatable()
                    .key("command.speed.set_fly-other")
                    .args(
                            text(target),
                            text(speed)
                    )
            ).build();

    Args2<String, Float> COMMAND_SPEED_SET_WALK_OTHER = (target, speed) -> text()
            .append(translatable()
                    .key("command.speed.set_walk-other")
                    .args(
                            text(target),
                            text(speed)
                    )
            ).build();

    Args1<Float> COMMAND_SPEED_SET_FLY_SELF = (speed) -> text()
            .append(translatable()
                    .key("command.speed.set_fly-self")
                    .args(
                            text(speed)
                    )
            ).build();

    Args1<Float> COMMAND_SPEED_SET_WALK_SELF = (speed) -> text()
            .append(translatable()
                    .key("command.speed.set_walk-self")
                    .args(
                            text(speed)
                    )
            ).build();
    //endregion

    Args1<String> COMMAND_GAMEMODE_MISSING_MODE_PERMISSION = (gamemode) -> text()
            .append(translatable()
                    .key("command.gamemode.missing-mode-permission")
                    .args(
                            translatable()
                                    .key("gamemode." + gamemode)
                    )
            ).build();

    Args2<String, String> COMMAND_GAMEMODE_SET_OTHER = (target, gamemode) -> text()
            .append(translatable()
                    .key("command.gamemode.set-other")
                    .args(
                            text(target),
                            translatable()
                                    .key("gamemode." + gamemode)
                    )
            ).build();

    Args1<String> COMMAND_GAMEMODE_SET_SELF = (gamemode) -> text()
            .append(translatable()
                    .key("command.gamemode.set-self")
                    .args(
                            translatable()
                                    .key("gamemode." + gamemode)
                    )
            ).build();

    Args1<String> COMMAND_GAMEMODE_UNKNOWN_MODE = (gamemode) -> text()
            .append(translatable()
                    .key("command.gamemode.unknown-mode")
                    .args(
                            text(gamemode)
                    )
            ).build();

    Args1<String> COMMAND_HEAL_OTHER = (target) -> text()
            .append(translatable()
                    .key("command.heal.heal-other")
                    .args(
                            text(target)
                    )
            ).build();

    Args0 COMMAND_HEAL_SELF = () -> text()
            .append(translatable()
                    .key("command.heal.heal-self")
            ).build();

    Args0 SEARCH_PLAYER_NOT_FOUND = () -> text()
            .append(translatable()
                    .key("search.not-found.entity.player")
            ).build();

    interface Args0 {
        Component build();

        default void send(Sender sender) {
            sender.sendMessage(build());
        }
    }

    interface Args1<A0> {
        Component build(A0 arg0);

        default void send(Sender sender, A0 arg0) {
            sender.sendMessage(build(arg0));
        }
    }

    interface Args2<A0, A1> {
        Component build(A0 arg0, A1 arg1);

        default void send(Sender sender, A0 arg0, A1 arg1) {
            sender.sendMessage(build(arg0, arg1));
        }
    }

    interface Args3<A0, A1, A2> {
        Component build(A0 arg0, A1 arg1, A2 arg2);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2) {
            sender.sendMessage(build(arg0, arg1, arg2));
        }
    }

    interface Args4<A0, A1, A2, A3> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3));
        }
    }

    interface Args5<A0, A1, A2, A3, A4> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4));
        }
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {
        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4, arg5));
        }
    }
}
