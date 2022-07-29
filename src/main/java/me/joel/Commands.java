package me.joel;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.LocalDateTime;
import java.util.Objects;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

public class Commands extends ListenerAdapter
{

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        System.out.println("Joined server: \"" + event.getGuild().getName() + "\"");
        EmbedBuilder onJoin = new EmbedBuilder()
                .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                .setTitle("Thank you for inviting PawBot to " + event.getGuild().getName() + "!")
                .setColor(Util.randColor())
                .setDescription("Make sure to use /help to get the full commands list!")
                .addBlankField(false)
                .addField("Want to learn more?", "Click the Github button below to see the source code and complete documentation!", false)
                .addField("Want to invite PawBot to another server?", "Click the Invite button to invite PawBot!", false);

        Objects.requireNonNull(event.getGuild().getSystemChannel()).sendMessageEmbeds(onJoin.build()).setActionRow(
                Button.link("https://github.com/joelrico/PawBot", "Github"),
                Button.link("https://discord.com/api/oauth2/authorize?client_id=971239438892019743&permissions=1644971949559&scope=applications.commands%20bot", "Invite"))
                .queue();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event)
    {
        // ready message
        System.out.println("Server: \"" + event.getGuild().getName() + "\" is ready!");

        // commands register
        event.getGuild().upsertCommand("whois", "Provides user information")
                .addOption(OptionType.MENTIONABLE, "user", "Sends user info", true)
                .queue();

        event.getGuild().upsertCommand("confess", "Posts an anonymous confession")
                .addOption(OptionType.STRING, "message", "Confession message", true)
                .queue();

        event.getGuild().upsertCommand("afk", "Enables/disables AFK status")
                .queue();

    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        try {
            // Help
            if (event.getName().equals("help")) {
                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Util.randColor())
                        .setTitle("PawBot Commands", "https://github.com/joelrico/PawBot")
                        .setThumbnail("https://cdn.discordapp.com/avatars/971239438892019743/6931bbb87c32bf98a10d7ab9ff5f1b91.png?size=4096")
                        .addField("General Commands", """
                                `/help` : Lists commands
                                `/invite` : Returns bot invite link
                                `/ping` : Pings bot
                                `/coinflip` Flips a coin
                                `/truth` : Requests truth
                                `/dare` : Requests dare
                                `/afk` : Sets AFK status
                                `/avatar (user)` : Retrieves user (or target) profile picture
                                `/8ball (message)` : Asks the magic 8ball a question
                                `/confess` : Sends anonymous confession""", false);

                event.replyEmbeds(builder.build()).setEphemeral(true)
                        .addActionRow(
                                Button.success("helpGeneral", "General").asDisabled(),
                                Button.success("helpMod", "Moderation"),
                                Button.success("helpMusic", "Music"),
                                Button.link("https://github.com/joelrico/PawBot", "Github"),
                                Button.link("https://discord.com/api/oauth2/authorize?client_id=971239438892019743&permissions=1644971949559&scope=applications.commands%20bot", "Invite"))
                        .queue();
            }
            // Invite
            if (event.getName().equals("invite")) {
                EmbedBuilder invite = new EmbedBuilder()
                        .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                        .setTitle("Here's an invite link!")
                        .setDescription("[Click on this invite link to invite me to your server](https://discord.com/api/oauth2/authorize?client_id=971239438892019743&permissions=1644971949559&scope=applications.commands%20bot)")
                        .setFooter("Upon joining a server, a short message will be sent explaining usage")
                        .setColor(Util.randColor());
                event.replyEmbeds(invite.build()).setEphemeral(true).queue();
            }
            // Coin Flip
            if (event.getName().equals("coinflip")) {
                String flip;
                if (Util.randomWithRange(0, 100) > 50) flip = "Heads!";
                else flip = "Tails!";
                EmbedBuilder coin = new EmbedBuilder()
                        .setDescription(flip)
                        .setColor(Util.randColor());
                event.replyEmbeds(coin.build()).queue();
            }
            // Ping
            if (event.getName().equals("ping")) {
                EmbedBuilder ping = new EmbedBuilder()
                        .setTitle("Pong!")
                        .setColor(Util.randColor());
                event.replyEmbeds(ping.build()).setEphemeral(true).queue();
            }
            // 8Ball
            if (event.getName().equals("8ball")) {
                int randomResult = Util.randomWithRange(1, 19);
                String output = "null";
                switch (randomResult) {
                    case 1 -> output = "It is certain.";
                    case 2 -> output = ("It is decidedly so.");
                    case 3 -> output = ("Without a doubt.");
                    case 4 -> output = ("Yes definitely.");
                    case 5 -> output = ("You may rely on it.");
                    case 6 -> output = ("As I see it, yes.");
                    case 7 -> output = ("Outlook good.");
                    case 8 -> output = ("Yes.");
                    case 9 -> output = ("Signs point to yes.");
                    case 10 -> output = ("Reply hazy, try again.");
                    case 11 -> output = ("Ask again later.");
                    case 12 -> output = ("Better not tell you now.");
                    case 13 -> output = ("Cannot predict now.");
                    case 14 -> output = ("Concentrate and ask again.");
                    case 15 -> output = ("Don't count on it.");
                    case 16 -> output = ("My reply is no.");
                    case 17 -> output = ("My sources say no.");
                    case 18 -> output = ("Outlook not so good.");
                    case 19 -> output = ("Very doubtful.");
                }
                String question = Objects.requireNonNull(event.getOption("question")).getAsString();

                EmbedBuilder ball = new EmbedBuilder()
                        .setTitle("8Ball")
                        .setColor(Util.randColor())
                        .setThumbnail("https://cdn.discordapp.com/attachments/810456406620241931/981063293428957244/unknown.png?size=4096")
                        .addField("Your question:", question, false)
                        .addField(output, "", false);

                event.replyEmbeds(ball.build()).queue();
            }
            // Truth or Dare
            if (event.getName().equals("truthordare")) {

                if (Objects.equals(event.getSubcommandName(), "truth")) {
                    String truth = truthordare.truth();

                    EmbedBuilder truthNotDare = new EmbedBuilder()
                            .setTitle("Truth or Dare")
                            .addField("Truth: ", truth, false)
                            .setColor(Util.randColor());

                    event.replyEmbeds(truthNotDare.build())
                            .addActionRow(
                                    Button.success("truth", "Truth"),
                                    Button.success("dare", "Dare"),
                                    Button.danger("randomTruthOrDare", "Random")
                            )
                            .queue();
                } else if (Objects.equals(event.getSubcommandName(), "dare")) {
                    String dare = truthordare.dare();

                    EmbedBuilder dareNotTruth = new EmbedBuilder()
                            .setTitle("Truth or Dare")
                            .addField("Dare: ", dare, false)
                            .setColor(Util.randColor());

                    event.replyEmbeds(dareNotTruth.build())
                            .addActionRow(
                                    Button.success("truth", "Truth"),
                                    Button.success("dare", "Dare"),
                                    Button.danger("randomTruthOrDare", "Random")
                            )
                            .queue();
                } else if (Objects.equals(event.getSubcommandName(), "random")) {
                    if (Util.randomWithRange(0, 100) > 50) {
                        String dare = truthordare.dare();
                        EmbedBuilder dareNotTruth = new EmbedBuilder()
                                .setTitle("Truth or Dare")
                                .addField("Dare: ", dare, false)
                                .setColor(Util.randColor());

                        event.replyEmbeds(dareNotTruth.build())
                                .addActionRow(
                                        Button.success("truth", "Truth"),
                                        Button.success("dare", "Dare"),
                                        Button.danger("randomTruthOrDare", "Random")
                                )
                                .queue();
                    } else {
                        String truth = truthordare.truth();
                        EmbedBuilder truthNotDare = new EmbedBuilder()
                                .setTitle("Truth or Dare")
                                .addField("Truth: ", truth, false)
                                .setColor(Util.randColor());

                        event.replyEmbeds(truthNotDare.build())
                                .addActionRow(
                                        Button.success("truth", "Truth"),
                                        Button.success("dare", "Dare"),
                                        Button.danger("randomTruthOrDare", "Random")
                                )
                                .queue();
                    }
                }
            }
            // Avatar
            if (event.getName().equals("avatar")) {
                String targetName;
                String targetPFP;

                // DMs
                if (!event.isFromGuild()) {
                    User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
                    targetName = user.getName() + "#" + user.getDiscriminator();
                    targetPFP = user.getEffectiveAvatarUrl();
                }
                // Server
                else {
                    Member member = Objects.requireNonNull(event.getOption("user")).getAsMember();
                    assert member != null;
                    targetName = member.getEffectiveName() + "#" + member.getUser().getDiscriminator();
                    targetPFP = member.getEffectiveAvatarUrl();
                }
                // Embed
                EmbedBuilder avatar = new EmbedBuilder()
                        .setTitle(targetName)
                        .setImage(targetPFP)
                        .setColor(Util.randColor());
                event.replyEmbeds(avatar.build()).queue();
            }
            // Confess
            if (event.getName().equals("confess")) {
                String message = Objects.requireNonNull(event.getOption("message")).getAsString();
                String channelID = "";

                // If message contains @role or @everyone
                if (Objects.requireNonNull(event.getOption("message")).getMentions().getRoles().size() > 0 || message.contains("@everyone")) {
                    EmbedBuilder noMentions = new EmbedBuilder()
                            .setColor(Util.randColor())
                            .setDescription("You can't @ roles in a confession!");

                    event.replyEmbeds(noMentions.build()).setEphemeral(true).queue();
                    return;
                }
                // If message contains @member
                if (Objects.requireNonNull(event.getOption("message")).getMentions().getUsers().size() > 0) {
                    EmbedBuilder noMentions = new EmbedBuilder()
                            .setColor(Util.randColor())
                            .setDescription("You can't @ someone in a confession!");

                    event.replyEmbeds(noMentions.build()).setEphemeral(true).queue();
                    return;
                }

                EmbedBuilder confessionPost = new EmbedBuilder()
                        .setTitle("Anonymous Confession")
                        .setDescription("\"" + message + "\"")
                        .setColor(Util.randColor());

                // Find confessions channel
                try {
                    int channelNum = Objects.requireNonNull(event.getGuild()).getTextChannels().size();
                    for (int i = 0; i < channelNum; ++i) {
                        if (event.getGuild().getTextChannels().get(i).getName().contains("confessions")) {
                            channelID = event.getGuild().getTextChannels().get(i).getId();
                        }
                    }

                    Objects.requireNonNull(event.getGuild().getTextChannelById(channelID)).sendMessageEmbeds(confessionPost.build()).queue();

                    EmbedBuilder confessionSubmit = new EmbedBuilder()
                            .setTitle("Confession Submitted")
                            .setDescription("\"" + message + "\"")
                            .setColor(Util.randColor());

                    event.replyEmbeds(confessionSubmit.build()).setEphemeral(true).queue();
                } catch (Exception channelNotFound) {
                    EmbedBuilder confessionError = new EmbedBuilder()
                            .setTitle("Error!")
                            .setDescription("No confession channel found!")
                            .setColor(Util.randColor());

                    event.replyEmbeds(confessionError.build()).setEphemeral(true).queue();
                }
            }
            // Whois Command
            if (event.getName().equals("whois")) {
                Member member = Objects.requireNonNull(event.getOption("user")).getAsMember();
                assert member != null;
                User user = member.getUser();
                LocalDateTime joinTime = member.getTimeJoined().toLocalDateTime();
                LocalDateTime creationDate = user.getTimeCreated().toLocalDateTime();
                int numRoles = member.getRoles().size();
                StringBuilder roles = new StringBuilder();
                for (int i = 0; i < numRoles; ++i) {
                    roles.append("<@&").append(member.getRoles().get(i).getId()).append("> ");
                }
                if (!event.isFromGuild()) {
                    EmbedBuilder whois = new EmbedBuilder()
                            .setDescription(user.getAsMention())
                            .setAuthor(user.getName() + "#" + user.getDiscriminator(), user.getAvatarUrl(), user.getAvatarUrl())
                            .setThumbnail(user.getEffectiveAvatarUrl())
                            .addField("Joined", joinTime.getMonthValue() + "/" + joinTime.getDayOfMonth() + "/" + joinTime.getYear(), true)
                            .addField("Created", creationDate.getMonthValue() + "/" + creationDate.getDayOfMonth() + "/" + creationDate.getYear(), true)
                            .addField("Roles [" + numRoles + "]", roles.toString(), false)
                            .setFooter("ID: " + user.getId())
                            .setColor(Util.randColor());

                    event.replyEmbeds(whois.build()).queue();
                    return;
                }
                if (event.isFromGuild()) {
                    EmbedBuilder whois = new EmbedBuilder()
                            .setDescription(member.getAsMention())
                            .setAuthor(user.getName() + "#" + user.getDiscriminator(), user.getAvatarUrl(), user.getAvatarUrl())
                            .setThumbnail(member.getEffectiveAvatarUrl())
                            .addField("Joined", joinTime.getMonthValue() + "/" + joinTime.getDayOfMonth() + "/" + joinTime.getYear(), true)
                            .addField("Created", creationDate.getMonthValue() + "/" + creationDate.getDayOfMonth() + "/" + creationDate.getYear(), true)
                            .addField("Roles [" + numRoles + "]", roles.toString(), false)
                            .setFooter("ID: " + user.getId())
                            .setColor(Util.randColor());

                    event.replyEmbeds(whois.build()).queue();
                }

            }
            // AFK
            if (event.getName().equals("afk")) {

                try {

                    Member member = event.getMember();
                    assert member != null;

                    // Check for admin/owner
                    if (member.getPermissions().contains(Permission.ADMINISTRATOR) || member.isOwner()) {

                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("Owner/Admins cannot use /afk!")
                                .setColor(Util.randColor());

                        event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                        return;
                    }

                    // Return from AFK
                    if (member.getEffectiveName().startsWith("(AFK)")) {
                        String user = Objects.requireNonNull(event.getMember()).getEffectiveName();
                        StringBuilder username = new StringBuilder()
                                .append(user)
                                .delete(0, 5);
                        member.modifyNickname(username.toString()).queue();

                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("Welcome back, " + event.getMember().getAsMention() + "!")
                                .setColor(Util.randColor());

                        event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                        return;
                    }
                    if (!member.getEffectiveName().startsWith("(AFK)")) {
                        String newName = "(AFK) " + member.getEffectiveName();

                        EmbedBuilder builder = new EmbedBuilder()
                                .setDescription("Your AFK status has been set, " + event.getMember().getAsMention() + "!")
                                .setColor(Util.randColor());

                        member.modifyNickname(newName).queue();
                        event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                    }

                }
                // Exception Catch
                catch (Exception exception) {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setDescription("Unknown error occurred, try again later!")
                            .setFooter("Make sure PawBots role is set as high as possible in the role hierarchy if this error continues to occur!")
                            .setColor(Util.randColor());

                    event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                }

            }
        }
        catch (Exception e)
        {
            event.replyEmbeds(Util.genericError().build()).setEphemeral(true).queue();
        }

    }
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {

        if (event.getComponentId().startsWith("help"))
        {
            if (event.getComponentId().equals("helpGeneral"))
            {
                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Util.randColor())
                        .setTitle("PawBot Commands", "https://github.com/joelrico/PawBot")
                        .setThumbnail("https://cdn.discordapp.com/avatars/971239438892019743/6931bbb87c32bf98a10d7ab9ff5f1b91.png?size=4096")
                        .addField("General Commands", """
                                `/help` : Lists commands
                                `/invite` : Returns bot invite link
                                `/ping` : Pings bot
                                `/coinflip` Flips a coin
                                `/truth` : Requests truth
                                `/dare` : Requests dare
                                `/afk` : Sets AFK status
                                `/avatar (user)` : Retrieves user (or target) profile picture
                                `/8ball (message)` : Asks the magic 8ball a question
                                `/bark` : Self explanatory
                                `/meow` : ^^^
                                `/confess` : Sends anonymous confession""", false);

                event.editMessageEmbeds(builder.build())
                        .setActionRow(
                                Button.success("helpGeneral", "General").asDisabled(),
                                Button.success("helpMod", "Moderation"),
                                Button.success("helpMusic", "Music"),
                                Button.link("https://github.com/joelrico/PawBot", "Github"),
                                Button.link("https://discord.com/api/oauth2/authorize?client_id=971239438892019743&permissions=1644971949559&scope=applications.commands%20bot", "Invite"))
                        .queue();
            }
            else if (event.getComponentId().equals("helpMod"))
            {
                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Util.randColor())
                        .setTitle("PawBot Commands", "https://github.com/joelrico/PawBot")
                        .setThumbnail("https://cdn.discordapp.com/avatars/971239438892019743/6931bbb87c32bf98a10d7ab9ff5f1b91.png?size=4096")
                        .addField("Moderation Commands", """
                                `/kick (user) (reason)` : Kicks user with optional reason
                                `/ban (user) (reason)` : Bans user with optional reason
                                `/timeout (user) (length)` : Times out user (Default: 1hr)
                                `/purge (num)` : Purges messages (up to 100)
                                `/reload_commands` : Reloads bot commands (in case of commands not appearing)
                                `/broadcast (channel) (message)` : Sends message as PawBot""", false);

                event.editMessageEmbeds(builder.build())
                        .setActionRow(
                                Button.success("helpGeneral", "General"),
                                Button.success("helpMod", "Moderation").asDisabled(),
                                Button.success("helpMusic", "Music"),
                                Button.link("https://github.com/joelrico/PawBot", "Github"),
                                Button.link("https://discord.com/api/oauth2/authorize?client_id=971239438892019743&permissions=1644971949559&scope=applications.commands%20bot", "Invite"))
                        .queue();
            }
            else if (event.getComponentId().equals("helpMusic"))
            {
                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Util.randColor())
                        .setTitle("PawBot Commands", "https://github.com/joelrico/PawBot")
                        .setThumbnail("https://cdn.discordapp.com/avatars/971239438892019743/6931bbb87c32bf98a10d7ab9ff5f1b91.png?size=4096")
                        .addField("Music Commands", """
                                `/play (song)` : Accepts names and YT links
                                `/pause` : Pauses playback
                                `/resume` : Resumes playback
                                `/clear` : Clears queue
                                `/queue` : Displays song queue
                                `/playing` : Displays currently playing song
                                `/loop` : Loops the currently playing song until disabled/cleared/skipped
                                `/skip` : Skips song""", false);

                event.editMessageEmbeds(builder.build())
                        .setActionRow(
                                Button.success("helpGeneral", "General"),
                                Button.success("helpMod", "Moderation"),
                                Button.success("helpMusic", "Music").asDisabled(),
                                Button.link("https://github.com/joelrico/PawBot", "Github"),
                                Button.link("https://discord.com/api/oauth2/authorize?client_id=971239438892019743&permissions=1644971949559&scope=applications.commands%20bot", "Invite"))
                        .queue();
            }
        }

        if (event.getComponentId().equals("truth") || event.getComponentId().equals("dare") || event.getComponentId().equals("randomTruthOrDare"))
        {
            if (event.getComponentId().equals("truth"))
            {
                String truth = truthordare.truth();
                EmbedBuilder truthNotDare = new EmbedBuilder()
                        .setTitle("Truth or Dare")
                        .addField("Truth: ", truth, false)
                        .setColor(Util.randColor());

                event.replyEmbeds(truthNotDare.build())
                        .addActionRow(
                                Button.success("truth", "Truth"),
                                Button.success("dare", "Dare"),
                                Button.danger("randomTruthOrDare", "Random")
                        )
                        .queue();
            }
            else if (event.getComponentId().equals("dare"))
            {
                String dare = truthordare.dare();
                EmbedBuilder dareNotTruth = new EmbedBuilder()
                        .setTitle("Truth or Dare")
                        .addField("Dare: ", dare, false)
                        .setColor(Util.randColor());

                event.replyEmbeds(dareNotTruth.build())
                        .addActionRow(
                                Button.success("truth", "Truth"),
                                Button.success("dare", "Dare"),
                                Button.danger("randomTruthOrDare", "Random")
                        )
                        .queue();
            }
            else if (event.getComponentId().equals("randomTruthOrDare"))
            {

                if (Util.randomWithRange(0, 100) > 50)
                {
                    String dare = truthordare.dare();
                    EmbedBuilder dareNotTruth = new EmbedBuilder()
                            .setTitle("Truth or Dare")
                            .addField("Dare: ", dare, false)
                            .setColor(Util.randColor());

                    event.replyEmbeds(dareNotTruth.build())
                            .addActionRow(
                                    Button.success("truth", "Truth"),
                                    Button.success("dare", "Dare"),
                                    Button.danger("randomTruthOrDare", "Random")
                            )
                            .queue();
                }
                else
                {
                    String truth = truthordare.truth();
                    EmbedBuilder truthNotDare = new EmbedBuilder()
                            .setTitle("Truth or Dare")
                            .addField("Truth: ", truth, false)
                            .setColor(Util.randColor());

                    event.replyEmbeds(truthNotDare.build())
                            .addActionRow(
                                    Button.success("truth", "Truth"),
                                    Button.success("dare", "Dare"),
                                    Button.danger("randomTruthOrDare", "Random")
                            )
                            .queue();
                }
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        if (!event.getAuthor().isBot() && event.isFromGuild())
        {
            try
            {
                Member member = event.getMessage().getMentions().getMembers().get(0);

                // AFK Return
                if (member.getEffectiveName().contains("(AFK)"))
                {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setDescription("Mentioned member is AFK, " + Objects.requireNonNull(event.getMember()).getAsMention() + "!")
                            .setColor(Util.randColor());

                    event.getChannel().sendMessageEmbeds(builder.build()).queue();
                }
            }
            catch (Exception ignore) {}
        }
    }
}
