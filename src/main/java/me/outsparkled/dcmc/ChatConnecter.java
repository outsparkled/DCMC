package me.outsparkled.dcmc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.security.auth.login.LoginException;
import java.util.logging.Logger;

public class ChatConnecter extends ListenerAdapter implements Listener {
    public JDA jda;
    private final Logger logger;
    private final long chatChannelID;
    private final boolean disableMassPings;
    private final boolean disableRolePings;
    private final boolean disableMemberPings;
    private final String dcOutputFormat;
    private final String mcOutputFormat;

    public ChatConnecter(String token, Logger logger, long chatChannelID, boolean disableMassPings, boolean disableRolePings, boolean disableMemberPings, String dcOutputFormat, String mcOutputFormat) throws LoginException {
        this.logger = logger;
        this.disableMassPings = disableMassPings;
        this.disableRolePings = disableRolePings;
        this.disableMemberPings = disableMemberPings;
        this.chatChannelID = chatChannelID;
        this.dcOutputFormat = dcOutputFormat;
        this.mcOutputFormat = mcOutputFormat;
        try {
            jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES).disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE).addEventListeners(this).setActivity(Activity.playing("Minecraft")).build();
            jda.awaitReady();

            if (jda.getTextChannelById(chatChannelID) == null) {
                logger.warning(chatChannelID + " is not a valid channel id! Please set a channel the bot can access in config.yml!");
            }
        } catch (LoginException e) {
            throw new LoginException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e) {
        TextChannel textChannel = jda.getTextChannelById(chatChannelID);

        if (textChannel != null) {
            textChannel.sendMessage(getFinalToDCOutput(e)).queue();
        } else {
            logger.warning(chatChannelID + " is not a valid channel id! Please set a channel the bot can access in config.yml!");
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage() || event.getAuthor().isSystem()) return;
        System.out.println(event.getMessage().getContentRaw());
        if (event.getChannel().getIdLong() == chatChannelID && !event.getMessage().getContentRaw().equals("")) {
            Bukkit.broadcastMessage(getFinalToMCOutput(event));
        }
    }

    private String getFinalToDCOutput(AsyncPlayerChatEvent event) {
        String finalDCOutput = dcOutputFormat;
        finalDCOutput = finalDCOutput
                .replaceAll("\\{sender_mc_username}", event.getPlayer().getName())
                .replaceAll("\\{mc_message}", event.getMessage());

        if (disableMassPings) {
            finalDCOutput = finalDCOutput
                    .replaceAll("@everyone", "@Everyone")
                    .replaceAll("@here", "@Here");
        }
        if (disableRolePings) {
            finalDCOutput = finalDCOutput
                    .replaceAll("<@&", "< @&");
        }
        if (disableMemberPings) {
            finalDCOutput = finalDCOutput
                    .replaceAll("<@", "< @");
        }

        return finalDCOutput;
    }

    private String getFinalToMCOutput(GuildMessageReceivedEvent event) {
        String finalMCOutput = mcOutputFormat;
        finalMCOutput = finalMCOutput
                .replaceAll("\\{sender_dc_tag}", event.getAuthor().getAsTag())
                .replaceAll("\\{sender_dc_name}", event.getAuthor().getName())
                .replaceAll("\\{sender_dc_discriminator}", event.getAuthor().getDiscriminator())
                .replaceAll("\\{dc_message}", event.getMessage().getContentRaw());

        return finalMCOutput;
    }


}

