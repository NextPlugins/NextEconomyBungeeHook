package com.nextplugins.economy.bungeehook.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.api.event.operations.MoneyChangeEvent;
import com.nextplugins.economy.api.event.operations.MoneyWithdrawEvent;
import com.nextplugins.economy.api.event.transaction.TransactionCompletedEvent;
import com.nextplugins.economy.model.account.Account;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

@Data
public final class MoneyListener implements Listener {

    private static final NextEconomyAPI economy = NextEconomyAPI.getInstance();

    private final String targetServer;
    private final Plugin plugin;

    @EventHandler
    private void change(MoneyChangeEvent event) {
        final Player player = event.getPlayer();

        call(player, event.getCurrentAmount());
    }

    @EventHandler
    private void transaction(TransactionCompletedEvent event) {
        final Account account = economy.findAccountByPlayer(event.getPlayer());
        final Account targetAccount = economy.findAccountByPlayer(event.getTarget());

        if (targetAccount == null) return;

        call(event.getPlayer(), account.getBalance());
        call(event.getPlayer(), event.getTarget().getUniqueId(), targetAccount.getBalance());
    }

    private void call(Player player, double amount) {
        call(player, player.getUniqueId(), amount);
    }

    private void call(Player sender, UUID id, double amount) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeUTF("Forward");
        output.writeUTF("ALL");
        output.writeUTF("NextEconomy_" + targetServer.toUpperCase());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bytes);

        try {
            outputStream.writeUTF(id.toString());
            outputStream.writeDouble(amount);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        output.writeShort(bytes.toByteArray().length);
        output.write(bytes.toByteArray());

        sender.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
    }

}
