package me.joel.commands.guild_config;

import me.joel.Database;
import me.joel.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Starboard extends ListenerAdapter {

    // SL4FJ Logger
    final Logger log = LoggerFactory.getLogger( Starboard.class );

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("star")) {

            if (event.getSubcommandName().equals("limit")) {
                int num = event.getOption("num").getAsInt();
                if (num <= 0) num = 1;

                String sql = "UPDATE \"public\".\"starboard_settings\" SET star_limit= " + num + " WHERE guild_id=" + event.getGuild().getId();

                try {
                    Database.getConnect().createStatement().execute(sql);
                } catch (SQLException e) {
                    log.error("Failed to configure starboard settings", e);
                }

                GuildSettings.starboard_limit.put(event.getGuild(), num);
                event.reply("Star limit set to: `" + num + "`").queue();
            }

            else if (event.getSubcommandName().equals("self")) {
                boolean can_star = event.getOption("can_star").getAsBoolean();
                GuildSettings.starboard_self.put(event.getGuild(), can_star);

                if (can_star) {
                    String sql = "UPDATE \"public\".\"starboard_settings\" SET star_self=1 WHERE guild_id=" + event.getGuild().getId();

                    try {
                        Database.getConnect().createStatement().execute(sql);
                    } catch (SQLException e) {
                        log.error("Failed to configure starboard settings", e);
                    }

                    event.reply("Users can now star their own messages").queue();
                }
                else {
                    String sql = "UPDATE \"public\".\"starboard_settings\" SET star_self=0 WHERE guild_id=" + event.getGuild().getId();

                    try {
                        Database.getConnect().createStatement().execute(sql);
                    } catch (SQLException e) {
                        log.error("Failed to configure starboard settings", e);
                    }

                    event.reply("Users can no longer star their own messages").queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (!event.isFromGuild()) return;

        Emoji star = Emoji.fromUnicode("U+2B50");
        User reactor = event.getUser();

        event.retrieveMessage().map(message -> {

            User user = message.getAuthor();

            if (message.getReaction(star) == null) return null;

            int count = message.getReaction(star).getCount();

            //  Checks against self_star
            if (user == reactor) {
                String sql = "SELECT star_self FROM \"public\".\"starboard_settings\" WHERE guild_id=" + event.getGuild().getId();
                boolean self_star;

                try {
                    ResultSet set = Database.getConnect().createStatement().executeQuery(sql);
                    set.next();

                    self_star = set.getBoolean(1);

                    if (!self_star) {
                        count++;
                        return null;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            int limit = 3;
            String msg = message.getContentStripped();

            try {
                String sql1 = "SELECT star_limit FROM \"public\".\"starboard_settings\" WHERE guild_id=" + event.getGuild().getId();
                ResultSet set = Database.getConnect().createStatement().executeQuery(sql1);
                set.next();

                limit = set.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }


            if (count >= limit) {

                // Get ID
                String sql = "SELECT starboard_ch FROM \"public\".\"starboard_settings\" WHERE guild_id=" + event.getGuild().getId();
                TextChannel channel;
                try {
                    ResultSet set = Database.getConnect().createStatement().executeQuery(sql);
                    set.next();

                    String channelID = set.getString(1);

                    if (channelID == null) return null;

                    channel = event.getGuild().getTextChannelById(channelID);

                    EmbedBuilder builder = new EmbedBuilder()
                            .setAuthor(user.getName(), user.getEffectiveAvatarUrl(), user.getEffectiveAvatarUrl())
                            .setDescription(msg)
                            .addField("Source", "[Click here](" + event.getJumpUrl() + ")", false)
                            .setColor(Util.randColor());

                    channel.sendMessageEmbeds(builder.build()).queue();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }).queue();

    }
}
