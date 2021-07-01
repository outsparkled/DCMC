package me.outsparkled.dcmc;

import org.bukkit.plugin.java.JavaPlugin;

public final class DCMC extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        try {
            getServer().getPluginManager().registerEvents(new ChatConnecter(this), this);
        } catch (InvalidTokenException e) {
            getLogger().severe("Invalid token specified in config.yml, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
