package com.nextplugins.economy.bungeehook;

import com.nextplugins.economy.NextEconomy;
import com.nextplugins.economy.bungeehook.listener.ChannelListener;
import com.nextplugins.economy.bungeehook.listener.TransactionListener;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

@Getter
public final class NextEconomyBungeeHook extends JavaPlugin {

    private static final int PLUGIN_ID = 15729;

    private final Messenger messenger = Bukkit.getMessenger();
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    private Metrics metrics;
    private String currentServer;
    private String targetServer;

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        if (!checkEconomyVersion()) return;

        metrics = new Metrics(this, PLUGIN_ID);

        setupTargets();
        enableChannel();

       pluginManager.registerEvents(new TransactionListener(targetServer, this), this);
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

    private boolean checkEconomyVersion() {
        final NextEconomy economy = NextEconomy.getInstance();

        final int version = Integer.parseInt(economy.getDescription().getVersion().replace(".", ""));

        if (version < 214) {
            getLogger().severe("É necessária a versão 2.1.4 ou superior do NextEconomy.");

            pluginManager.disablePlugin(this);
        }

        return version >= 214;
    }

}
