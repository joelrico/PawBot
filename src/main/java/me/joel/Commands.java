package me.joel;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Random;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.User;

public class Commands extends ListenerAdapter {

    String prefix = "paw";

    static String printCommands() {
        return "General Commands\n\n`paw ping` : Pings bot\n`paw truth` : Requests truth\n`paw dare` : Requests dare\n`paw afk` : Sets AFK status\n`paw av (input)` : Retrieves author (or target) profile picture\n`paw 8ball (input)` : Asks the magic 8ball a question\n\nModeration Commands\n\n`paw kick (user) (reason)` : Kicks user with required reason \n`paw ban (user) (reason)` : Bans user with required reason\n`paw timeout (user)` : Times out user (Default: 1hr)";
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Checks if user
        if (!event.getAuthor().isBot() && event.isFromGuild()) {

            // Grabs user input
            String messageSent = event.getMessage().getContentRaw();
            String[]botInput = messageSent.split(" ", 3);

            // Checks for valid command
            if (botInput[0].equalsIgnoreCase(prefix)) {

                try {

                    // Commands list
                    if (botInput[1].equalsIgnoreCase("help")) {
                        User user = event.getAuthor();
                        // DM's user
                        user.openPrivateChannel().flatMap(channel -> channel.sendMessage(printCommands())).queue();
                    }

                    // Ping
                    if (botInput[1].equalsIgnoreCase("ping")) {
                        String user = event.getAuthor().getName();
                        String server = event.getGuild().getName();
                        event.getTextChannel().sendMessage("Pong!").queue();
                    }

                    // Gets user profile picture

                    if (botInput[1].equalsIgnoreCase("av")) {
                        try {
                            if (!botInput[2].isEmpty()) {
                                User target = event.getMessage().getMentionedUsers().get(0);
                                String targetPFP = target.getEffectiveAvatarUrl();
                                event.getTextChannel().sendMessage(targetPFP).queue();
                                return;
                            }
                        }
                        catch (Exception ignored) {}
                    } // Target
                    if (botInput[1].equalsIgnoreCase("av")) {
                        try {
                            String userPFP = event.getAuthor().getEffectiveAvatarUrl();
                            event.getTextChannel().sendMessage(userPFP).queue();
                        }
                        catch (Exception ignored) {}
                    } // Author

                }

                catch (Exception ignore) {}
            }

        }
    }

}
