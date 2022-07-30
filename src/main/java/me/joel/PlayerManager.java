package me.joel;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private static boolean sendAdded = true;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildID) ->
        {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void loadAndPlay(MessageChannelUnion channel, String trackURL) {

        VoiceChannel voiceChannel;
        TextChannel textChannel;

        // define type
        if (channel.getType() == ChannelType.TEXT) {
            textChannel = channel.asTextChannel();
            final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());

            this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {

                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    musicManager.scheduler.queue(audioTrack);

                    if (sendAdded) {
                        // Time from ms to m:s
                        long trackLength = audioTrack.getInfo().length;
                        long minutes = (trackLength / 1000) / 60;
                        long seconds = ((trackLength / 1000) % 60);
                        String songSeconds = String.valueOf(seconds);
                        if (seconds < 10) songSeconds = "0" + seconds;

                        // Thumbnail
                        String trackThumbnail = getThumbnail(audioTrack.getInfo().uri);

                        EmbedBuilder builder = new EmbedBuilder()
                                .setColor(Util.randColor())
                                .setAuthor("Added to queue (#" + musicManager.scheduler.queue.size() + ")")
                                .setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
                                .setDescription("`[0:00 / [" + minutes + ":" + songSeconds + "]`")
                                .setThumbnail(trackThumbnail)
                                .addField("Requested by:", MusicCommands.member.getAsMention(), false)
                                .setFooter("Use /help for a list of music commands!");

                        if (musicManager.scheduler.queue.size() <= 0) {
                            builder.setAuthor(("Added to queue"));
                        }
                        textChannel.sendMessageEmbeds(builder.build()).queue();
                    }
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {

                    for (AudioTrack track : audioPlaylist.getTracks()) {
                        musicManager.scheduler.queue(track);
                    }

                    final List<AudioTrack> tracks = audioPlaylist.getTracks();
                    if (!tracks.isEmpty()) {
                        musicManager.scheduler.queue(tracks.get(0));

                        if (sendAdded) {
                            // Thumbnail
                            String trackThumbnail = getThumbnail(tracks.get(0).getInfo().uri);

                            // Playlist size
                            int playlistSize = audioPlaylist.getTracks().size();

                            EmbedBuilder builder = new EmbedBuilder()
                                    .setColor(Util.randColor())
                                    .setAuthor("Playlist queued")
                                    .setTitle(audioPlaylist.getName())
                                    .setDescription("`[" + playlistSize + "] songs`")
                                    .setThumbnail(trackThumbnail)
                                    .addField("Requested by:", MusicCommands.member.getAsMention(), false)
                                    .setFooter("Use /help for a list of music commands!");

                            textChannel.sendMessageEmbeds(builder.build()).queue();
                        }
                    }
                }

                @Override
                public void noMatches() {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setColor(Util.randColor())
                            .setDescription("No song found!")
                            .setFooter("Use /help for a list of music commands!");

                    textChannel.sendMessageEmbeds(builder.build()).queue();
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setColor(Util.randColor())
                            .setDescription("An error has occurred!")
                            .setFooter("Use /help for a list of music commands!");

                    textChannel.sendMessageEmbeds(builder.build()).queue();
                }
            });
        } else if (channel.getType() == ChannelType.VOICE) {
            voiceChannel = channel.asVoiceChannel();
            final GuildMusicManager musicManager = this.getMusicManager(voiceChannel.getGuild());

            this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {

                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    musicManager.scheduler.queue(audioTrack);

                    if (sendAdded) {
                        // Time from ms to m:s
                        long trackLength = audioTrack.getInfo().length;
                        long minutes = (trackLength / 1000) / 60;
                        long seconds = ((trackLength / 1000) % 60);
                        String songSeconds = String.valueOf(seconds);
                        if (seconds < 10) songSeconds = "0" + seconds;

                        // Thumbnail
                        String trackThumbnail = getThumbnail(audioTrack.getInfo().uri);

                        EmbedBuilder builder = new EmbedBuilder()
                                .setColor(Util.randColor())
                                .setAuthor("Added to queue (#" + musicManager.scheduler.queue.size() + ")")
                                .setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
                                .setDescription("`[0:00 / [" + minutes + ":" + songSeconds + "]`")
                                .setThumbnail(trackThumbnail)
                                .addField("Requested by:", MusicCommands.member.getAsMention(), false)
                                .setFooter("Use /help for a list of music commands!");

                        if (musicManager.scheduler.queue.size() <= 0) {
                            builder.setAuthor(("Added to queue"));
                        }
                        voiceChannel.sendMessageEmbeds(builder.build()).queue();
                    }
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {

                    for (AudioTrack track : audioPlaylist.getTracks()) {
                        musicManager.scheduler.queue(track);
                    }

                    final List<AudioTrack> tracks = audioPlaylist.getTracks();
                    if (!tracks.isEmpty()) {
                        musicManager.scheduler.queue(tracks.get(0));

                        if (sendAdded) {
                            // Thumbnail
                            String trackThumbnail = getThumbnail(tracks.get(0).getInfo().uri);

                            // Playlist size
                            int playlistSize = audioPlaylist.getTracks().size();

                            EmbedBuilder builder = new EmbedBuilder()
                                    .setColor(Util.randColor())
                                    .setAuthor("Playlist queued")
                                    .setTitle(audioPlaylist.getName())
                                    .setDescription("`[" + playlistSize + "] songs`")
                                    .setThumbnail(trackThumbnail)
                                    .addField("Requested by:", MusicCommands.member.getAsMention(), false)
                                    .setFooter("Use /help for a list of music commands!");

                            voiceChannel.sendMessageEmbeds(builder.build()).queue();
                        }
                    }
                }

                @Override
                public void noMatches() {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setColor(Util.randColor())
                            .setDescription("No song found!")
                            .setFooter("Use /help for a list of music commands!");

                    voiceChannel.sendMessageEmbeds(builder.build()).queue();
                }

                @Override
                public void loadFailed(FriendlyException e) {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setColor(Util.randColor())
                            .setDescription("An error has occurred!")
                            .setFooter("Use /help for a list of music commands!");

                    voiceChannel.sendMessageEmbeds(builder.build()).queue();
                }
            });
        }


    }

    public void loadAndPlayNoURI(MessageChannelUnion channel, String trackURL) {

        VoiceChannel voiceChannel;
        TextChannel textChannel;

        // define type
        if (channel.getType() == ChannelType.TEXT) {
            textChannel = channel.asTextChannel();
            final GuildMusicManager musicManager = this.getMusicManager(textChannel.getGuild());

            this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {

                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    musicManager.scheduler.queue(audioTrack);

                    if (sendAdded) {
                        EmbedBuilder builder = addedTrackToQueue(audioTrack, musicManager.scheduler.queue.size());
                        if (musicManager.scheduler.queue.size() <= 0) {
                            builder.setAuthor(("Added to queue"));
                        }
                        textChannel.sendMessageEmbeds(builder.build()).queue();
                    }
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {

                    final List<AudioTrack> tracks = audioPlaylist.getTracks();
                    if (!tracks.isEmpty()) {
                        musicManager.scheduler.queue(tracks.get(0));
                    }

                    if (sendAdded) {
                        AudioTrack audioTrack = tracks.get(0);
                        EmbedBuilder builder = addedTrackToQueue(audioTrack, musicManager.scheduler.queue.size());
                        if (musicManager.scheduler.queue.size() <= 0) {
                            builder.setAuthor(("Added to queue"));
                        }
                        textChannel.sendMessageEmbeds(builder.build()).queue();
                    }
                }

                @Override
                public void noMatches() {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setColor(Util.randColor())
                            .setDescription("No song found!")
                            .setFooter("Use /help for a list of music commands!");

                    textChannel.sendMessageEmbeds(builder.build()).queue();
                }

                @Override
                public void loadFailed(FriendlyException e) {

                }
            });
        } else if (channel.getType() == ChannelType.VOICE) {
            voiceChannel = channel.asVoiceChannel();
            final GuildMusicManager musicManager = this.getMusicManager(voiceChannel.getGuild());

            this.audioPlayerManager.loadItemOrdered(musicManager, trackURL, new AudioLoadResultHandler() {

                @Override
                public void trackLoaded(AudioTrack audioTrack) {
                    musicManager.scheduler.queue(audioTrack);

                    if (sendAdded) {
                        EmbedBuilder builder = addedTrackToQueue(audioTrack, musicManager.scheduler.queue.size());
                        if (musicManager.scheduler.queue.size() <= 0) {
                            builder.setAuthor(("Added to queue"));
                        }
                        voiceChannel.sendMessageEmbeds(builder.build()).queue();
                    }
                }

                @Override
                public void playlistLoaded(AudioPlaylist audioPlaylist) {

                    final List<AudioTrack> tracks = audioPlaylist.getTracks();
                    if (!tracks.isEmpty()) {
                        musicManager.scheduler.queue(tracks.get(0));
                    }

                    if (sendAdded) {
                        AudioTrack audioTrack = tracks.get(0);
                        EmbedBuilder builder = addedTrackToQueue(audioTrack, musicManager.scheduler.queue.size());
                        if (musicManager.scheduler.queue.size() <= 0) {
                            builder.setAuthor(("Added to queue"));
                        }
                        voiceChannel.sendMessageEmbeds(builder.build()).queue();
                    }
                }

                @Override
                public void noMatches() {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setColor(Util.randColor())
                            .setDescription("No song found!");

                    voiceChannel.sendMessageEmbeds(builder.build()).queue();
                }

                @Override
                public void loadFailed(FriendlyException e) {

                }
            });
        }

    }

    public static void setSendAdded(boolean set) {
        sendAdded = set;
    }

    public static PlayerManager getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new PlayerManager();
        return INSTANCE;
    }

    public static EmbedBuilder addedTrackToQueue(AudioTrack audioTrack, int queueSize) {

        // Time from ms to m:s
        long trackLength = audioTrack.getInfo().length;
        long minutes = (trackLength / 1000) / 60;
        long seconds = ((trackLength / 1000) % 60);
        String songSeconds = String.valueOf(seconds);
        if (seconds < 10) songSeconds = "0" + seconds;

        // Thumbnail
        String trackThumbnail = getThumbnail(audioTrack.getInfo().uri);

        return new EmbedBuilder()
                .setColor(Util.randColor())
                .setAuthor("Added to queue (#" + queueSize + ")")
                .setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
                .setDescription("`[0:00 / [" + minutes + ":" + songSeconds + "]`")
                .setThumbnail(trackThumbnail)
                .addField("Requested by:", MusicCommands.member.getAsMention(), false)
                .setFooter("Use /help for a list of music commands!");
    }

    // Gets YouTube thumbnail
    public static String getThumbnail(String link) {

        int linkLength = link.length() + 1;
        String linkPrefix = "https://img.youtube.com/vi/";
        String linkSuffix = "/0.jpg";
        StringBuilder stringBuilder = new StringBuilder()
                .append(link)
                .delete(0, linkLength - 12);
        String videoID = stringBuilder.toString();

        return linkPrefix + videoID + linkSuffix;
    }
}
