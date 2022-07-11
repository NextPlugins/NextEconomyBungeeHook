package com.nextplugins.economy.bungeehook.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.model.account.Account;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Optional;
import java.util.UUID;

@Data
public final class ChannelListener implements PluginMessageListener {

    private static final NextEconomyAPI economy = NextEconomyAPI.getInstance();

    private final String currentServer;

    @Override
    @SneakyThrows
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equalsIgnoreCase("BungeeCord")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subChannel = in.readUTF();

        if (!subChannel.equalsIgnoreCase("NextEconomy_" + currentServer.toUpperCase())) return;

        final short len = in.readShort();

        byte[] data = new byte[len];

        in.readFully(data);

        final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));

        final String playerId = inputStream.readUTF();
        final OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(playerId));

        final Account account = economy.findAccountByPlayer(target);

        if (account == null) return;

        account.setBalance(inputStream.readDouble());
    }

}
