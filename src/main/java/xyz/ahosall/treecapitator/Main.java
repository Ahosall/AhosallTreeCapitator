package xyz.ahosall.treecapitator;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Está pronto para ser utilizado!");

        // Register plugin events
        getServer().getPluginManager().registerEvents(new EventsListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Desativado com sucesso...");
    }
}