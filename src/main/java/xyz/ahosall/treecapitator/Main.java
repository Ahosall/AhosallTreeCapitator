package xyz.ahosall.treecapitator;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.ahosall.treecapitator.listeners.TreeCapitatorListener;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Está pronto para ser utilizado!");

        // Register plugin events
        getServer()
                .getPluginManager()
                .registerEvents(new TreeCapitatorListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Desativado com sucesso...");
    }
}