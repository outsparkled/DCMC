package me.outsparkled.dcmc;

import org.bukkit.plugin.java.JavaPlugin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public final class DCMC extends JavaPlugin {

    JDA jda;
    Logger logger;
    ChatConnecter chatConnecter;
    FileConfiguration config;
    
    public DCMC() {
        this.logger = getLogger();
        this.config = getConfig();
    }    
    
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        try {
            startBot();
        } catch (InvalidTokenException e) {
            logger.severe("Invalid token specified in config.yml, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        chatConnecter = new ChatConnecter(jda, logger, config.getLong("chat-channel-id"), config.getBoolean("disable-mass-pings"), config.getBoolean("disable-role-pings"), config.getBoolean("disable-member-pings"));

        getServer().getPluginManager().registerEvents(chatConnecter, this);
        logger.info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        jda.shutdownNow();
    }
    
    public void startBot() throws InvalidTokenException {
        try {
            jda = JDABuilder.createDefault(plugin.getConfig().getString("bot-token"), GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES).disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE).addEventListeners(this).setActivity(Activity.playing("Minecraft")).build();
            jda.awaitReady();
        } catch (LoginException e) {
            throw new InvalidTokenException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (jda.getTextChannelById(getConfig().getLong("chat-channel-id")) == null) {
            logger.warning(getConfig().getLong("chat-channel-id") + " is not a valid channel id! Please set a channel the bot can access in config.yml!");
        }
    }

}
