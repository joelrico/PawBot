package me.joel.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.joel.lavaplayer.AudioEventAdapter;
import me.joel.lavaplayer.PlayerManager;
import me.joel.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Playing extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        var invoke = event.getName();

        if (event.getGuild() == null) return;

        if (invoke.equals("playing")) {

            // JDA AudioManager
            final AudioManager audioManager = event.getGuild().getAudioManager();

            AudioTrack track = PlayerManager.getINSTANCE().getMusicManager(audioManager.getGuild()).player.getPlayingTrack();

            if (track == null) {
                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Color.red)
                        .setDescription("No song is playing!")
                        .setFooter("Use /help for a list of music commands!");

                event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                return;
            }

            EmbedBuilder builder = nowPlaying(track);
            builder.setFooter("");

            // uses custom emojis from paw patrol
            if (event.getJDA().getGuildById("645471751316307998").getEmojiById("1008015504155361322") != null && event.getJDA().getGuildById("645471751316307998").getEmojiById("1008015531405746197") != null) {

                Emoji forward = event.getJDA().getGuildById("645471751316307998").getEmojiById("1008015504155361322");
                Emoji backward = event.getJDA().getGuildById("645471751316307998").getEmojiById("1008015531405746197");

                event.replyEmbeds(builder.build()).setEphemeral(true)
                        .addActionRow(
                                Button.primary("rewind", backward),
                                Button.primary("pause", Emoji.fromFormatted("U+23F8")),
                                Button.primary("resume", Emoji.fromFormatted("U+25B6")),
                                Button.primary("forward", forward)
                        )
                        .addActionRow(
                                Button.primary("shuffle", Emoji.fromFormatted("U+1F501")),
                                Button.primary("loop", Emoji.fromFormatted("U+1F500"))
                        )
                        .queue();
                return;
            }

            event.replyEmbeds(builder.build()).setEphemeral(true)
                    .addActionRow(
                            Button.primary("rewind", Emoji.fromFormatted("U+23EA")),
                            Button.primary("pause", Emoji.fromFormatted("U+23F8")),
                            Button.primary("resume", Emoji.fromFormatted("U+25B6")),
                            Button.primary("forward", Emoji.fromFormatted("U+23E9"))
                    )
                    .addActionRow(
                            Button.primary("shuffle", Emoji.fromFormatted("U+1F501")),
                            Button.primary("loop", Emoji.fromFormatted("U+1F500"))
                    )
                    .queue();

        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        var invoke = event.getComponentId();

        switch (invoke) {
            case "pause" -> {
                PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.setPaused(true);

                EmbedBuilder builder = nowPlaying(PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.getPlayingTrack());
                builder.setFooter("");

                event.editMessageEmbeds(builder.build()).queue();
            }
            case "resume" -> {
                PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.setPaused(false);

                EmbedBuilder builder = nowPlaying(PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.getPlayingTrack());
                builder.setFooter("");

                event.editMessageEmbeds(builder.build()).queue();
            }
            case "forward" -> {
                if (PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.queue.size() != 0) {
                    PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).scheduler.nextTrack();
                }

                EmbedBuilder builder = nowPlaying(PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.getPlayingTrack());
                builder.setFooter("");

                event.editMessageEmbeds(builder.build()).queue();
            }
            case "rewind" -> {
                PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.getPlayingTrack().setPosition(0);

                EmbedBuilder builder = nowPlaying(PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.getPlayingTrack());
                builder.setFooter("");

                event.editMessageEmbeds(builder.build()).queue();
            }
            case "shuffle" -> {
                if (AudioEventAdapter.isShuffling()) AudioEventAdapter.setShuffle(false);
                if (!AudioEventAdapter.isShuffling()) AudioEventAdapter.setShuffle(true);

                EmbedBuilder builder = nowPlaying(PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.getPlayingTrack());
                builder.setFooter("");

                event.editMessageEmbeds(builder.build()).queue();
            }
            case "loop" -> {
                if (AudioEventAdapter.isLooping()) AudioEventAdapter.setLoop(false);
                if (!AudioEventAdapter.isLooping()) AudioEventAdapter.setLoop(true);

                EmbedBuilder builder = nowPlaying(PlayerManager.getINSTANCE().getMusicManager(event.getGuild()).player.getPlayingTrack());
                builder.setFooter("");

                event.editMessageEmbeds(builder.build()).queue();
            }
        }
    }

    /**
     * Gets track current time
     * @param track Track
     * @return Time in [h:m:s] format
     */
    public static String getTrackCurrentTime(AudioTrack track) {

        // seconds are measured in thousands
        int totalSeconds = (int) (Math.ceil(track.getPosition()) / 1000);
        int totalMinutes = 0;
        int totalHours = 0;

        // seconds -> minutes, minutes -> hours
        while (totalSeconds >= 60) {
            totalSeconds = totalSeconds - 60;
            totalMinutes++;
        }
        while (totalMinutes >= 60) {
            totalMinutes = totalMinutes - 60;
            totalHours++;
        }

        String totalSecondsString = String.valueOf(totalSeconds);
        if (totalSeconds < 10) totalSecondsString = "0" + totalSecondsString;

        if (totalHours > 0) {
            String totalMinutesString = String.valueOf(totalMinutes);
            if (totalMinutes < 10) totalMinutesString = "0" + totalMinutesString;

            String totalHoursString = String.valueOf(totalHours);
            if (totalHours < 10) totalHoursString = "0" + totalHoursString;

            return "[" + totalHoursString + ":" + totalMinutesString + ":" + totalSecondsString + "]";
        }

        return "[" + totalMinutes + ":" + totalSecondsString + "]";
    }

    /**
     * Gets track total time
     * @param track Track
     * @return Time in {h:m:s] format
     */
    public static String getTrackTotalTime(AudioTrack track) {

        // Time from ms to m:s
        long trackLength = track.getInfo().length;
        long minutes = (trackLength / 1000) / 60;
        long seconds = ((trackLength / 1000) % 60);
        long hours = 0;

        while (minutes >= 60) {
            minutes -= 60;
            hours++;
        }

        String songHours = String.valueOf(hours);
        if (hours < 10) songHours = "0" + hours;
        String songMinutes = String.valueOf(minutes);
        if (minutes < 10) songMinutes = "0" + minutes;
        String songSeconds = String.valueOf(seconds);
        if (seconds < 10) songSeconds = "0" + seconds;

        if (hours <= 0) return "[" + songMinutes + ":" + songSeconds + "]";

        return "[" + songHours + ":" + songMinutes + ":" + songSeconds + "]";
    }

    public static EmbedBuilder nowPlaying(AudioTrack track) {

        // Thumbnail
        String trackThumbnail = PlayerManager.getThumbnail(track.getInfo().uri);

        return new EmbedBuilder()
                .setColor(Util.randColor())
                .setAuthor("Now Playing")
                .setTitle(track.getInfo().title, track.getInfo().uri)
                .setDescription("`" + getTrackCurrentTime(track) + " / " + getTrackTotalTime(track) + "`")
                .setThumbnail(trackThumbnail);
    }
}
