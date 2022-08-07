package me.joel.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers slash commands
 */
public class Register extends ListenerAdapter {

    /**
     * Registers guild commands
     * @param event GuildReadyEvent
     */
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {

        List<CommandData> guildCommandData = new ArrayList<>();

        // general
        guildCommandData.add(Commands.slash("whois", "Provides user information").addOption(OptionType.MENTIONABLE, "user", "Sends user info", true));
        guildCommandData.add(Commands.slash("afk", "Sets AFK status"));
        guildCommandData.add(Commands.slash("confess", "Sends anonymous confession").addOption(OptionType.STRING, "message", "Confession message", true));
        guildCommandData.add(Commands.context(Command.Type.USER, "Get member avatar"));
        guildCommandData.add(Commands.context(Command.Type.USER, "Get member info"));

        // mod
        guildCommandData.add(Commands.slash("broadcast", "Broadcasts message in selected channel"));
        guildCommandData.add(Commands.slash("purge", "Purges up to 100 messages").addOption(OptionType.INTEGER, "number", "Number of messages to purge", true).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)));

        // music
        guildCommandData.add(Commands.slash("play", "Requests a song").addOption(OptionType.STRING, "song", "Accepts Spotify, YouTube, or Apple Music", true));
        guildCommandData.add(Commands.slash("pause", "Pause playback"));
        guildCommandData.add(Commands.slash("resume", "Resume playback"));
        guildCommandData.add(Commands.slash("clear", "Clears queue"));
        guildCommandData.add(Commands.slash("queue", "Displays music queue"));
        guildCommandData.add(Commands.slash("playing", "Displays currently playing song"));
        guildCommandData.add(Commands.slash("loop", "Loops currently playing song"));
        guildCommandData.add(Commands.slash("shuffle", "Shuffles music queue"));
        guildCommandData.add(Commands.slash("volume", "Sets bot volume").addOption(OptionType.STRING, "num", "Sets volume (between 1 and 100)", true));
        guildCommandData.add(Commands.slash("skip", "Skips song").addOption(OptionType.INTEGER, "target", "Removes selected song from queue", false).addOption(OptionType.INTEGER, "num", "Removes \"x\" number of songs", false));

        event.getGuild().updateCommands().addCommands(guildCommandData).queue();
    }

    /**
     * Registers global commands
     * @param event ReadyEvent
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        //event.getJDA().updateCommands().queue();

        SubcommandData truth = new SubcommandData("truth", "Generates a random truth question");
        SubcommandData dare = new SubcommandData("dare", "Generates a random dare question");
        SubcommandData random = new SubcommandData("random", "Generates a random question");

        List<CommandData> globalCommandData = new ArrayList<>();
        globalCommandData.add(Commands.slash("help", "Lists commands"));
        globalCommandData.add(Commands.slash("8ball", "Asks the magic 8ball a question").addOption(OptionType.STRING, "question", "Your question to the 8ball", true));
        globalCommandData.add(Commands.slash("coinflip", "Flips a coin for heads or tails"));
        globalCommandData.add(Commands.slash("truthordare", "Generates a random truth/dare question").addSubcommands(truth, dare, random));
        globalCommandData.add(Commands.slash("ping", "Sends pong"));
        globalCommandData.add(Commands.slash("avatar", "Sends user avatar").addOption(OptionType.MENTIONABLE, "user", "Sends mentioned users avatar", true));

        event.getJDA().updateCommands().addCommands(globalCommandData).queue();
    }
}
