package me.outsparkled.dcmc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
    public Logger logger;
    public long chatChannelID;
    public boolean disableMassPings;
    public boolean disableRolePings;
    public boolean disableMemberPings;

    public ChatConnecter(JDA jda, Logger logger, long chatChannelID, boolean disableMassPings, boolean disableRolePings, boolean disableMemberPings) throws InvalidTokenException {
        this.jda = jda;
        this.logger = logger;
        this.disableMassPings = disableMassPings;
        this.disableRolePings = disableRolePings;
        this.disableMemberPings = disableMemberPings;
        this.chatChannelID = chatChannelID;
        
        // startBot(); MOVE TO MAIN
    }
/**
MOVE TO MAIN
    public void startBot() throws InvalidTokenException {

        try {
            jda = JDABuilder.createDefault(plugin.getConfig().getString("bot-token"), GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES).disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE).addEventListeners(this).setActivity(Activity.playing("Minecraft")).build();
            jda.awaitReady();
        } catch (LoginException e) {
            throw new InvalidTokenException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (jda.getTextChannelById(plugin.getConfig().getLong("chat-channel-id")) == null) {
            logger.warning(plugin.getConfig().getLong("chat-channel-id") + " is not a valid channel id! Please set a channel the bot can access in config.yml!");
        }
    }
**/
    @EventHandler
    public void chatEvent(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        TextChannel textChannel = jda.getTextChannelById(chatChannelID);

        if (textChannel != null) {
            textChannel.sendMessage("**" + e.getPlayer().getName() + "**: " + message).queue();
        } else {
            logger.warning(chatChannelID + " is not a valid channel id! Please set a channel the bot can access in config.yml!");
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage() || event.getAuthor().isSystem()) return;
        if (event.getChannel().getIdLong() == chatChannelID) {

            String message = event.getMessage().getContentRaw();
            User author = event.getAuthor();
            
            Bukkit.broadcastMessage("<" + author.getAsTag() + "> " + message);
          
            

        }
    }
}
