package me.outsparkled.dcmc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.logging.Logger;

public final class DCMC extends JavaPlugin {

    private ChatConnecter chatConnecter;
    
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Logger logger = getLogger();
        FileConfiguration config = getConfig();

        try {
            chatConnecter = new ChatConnecter(config.getString("bot-token"), logger, config.getLong("chat-channel-id"), config.getBoolean("disable-mass-pings"), config.getBoolean("disable-role-pings"), config.getBoolean("disable-member-pings"), config.getString("dc-output-format"), config.getString("mc-output-format"));
        } catch (LoginException e) {
            logger.severe("Invalid token specified in config.yml, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(chatConnecter, this);
        logger.info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        if (chatConnecter != null) {
            chatConnecter.jda.shutdownNow();
        }
    }
}
