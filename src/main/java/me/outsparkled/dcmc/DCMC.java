package me.outsparkled.dcmc;

import org.bukkit.plugin.java.JavaPlugin;

public final class DCMC extends JavaPlugin {

    ChatConnecter chatConnecter;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        try {
            chatConnecter = new ChatConnecter(this);
        } catch (InvalidTokenException e) {
            getLogger().severe("Invalid token specified in config.yml, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(chatConnecter, this);
        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        if (chatConnecter != null) {
            chatConnecter.jda.shutdownNow();
        }
    }
}
