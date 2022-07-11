package com.nextplugins.economy.bungeehook;

import com.nextplugins.economy.bungeehook.listener.ChannelListener;
import com.nextplugins.economy.bungeehook.listener.MoneyListener;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

@Getter
public final class NextEconomyBungeeHook extends JavaPlugin {

    private static final int PLUGIN_ID = 15729;

    private final Messenger messenger = Bukkit.getMessenger();

    private Metrics metrics;
    private String bungeeMode;
    private String currentServer;
    private String targetServer;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        metrics = new Metrics(this, PLUGIN_ID);

        setupTargets();
        enableChannel();

        getServer().getPluginManager().registerEvents(new MoneyListener(targetServer, this), this);
    }

    @Override
    public void onDisable() {
        disableChannel();
    }

    private void enableChannel() {
        messenger.registerOutgoingPluginChannel(this, "BungeeCord");
        messenger.registerIncomingPluginChannel(this, "BungeeCord", new ChannelListener(getCurrentServer()));
    }

    private void setupTargets() {
        final ConfigurationSection configuration = getConfig().getConfigurationSection("server");

        currentServer = configuration.getString("current");
        targetServer = configuration.getString("target");
    }

    private void disableChannel() {
        messenger.unregisterOutgoingPluginChannel(this);
        messenger.unregisterIncomingPluginChannel(this);
    }



}
