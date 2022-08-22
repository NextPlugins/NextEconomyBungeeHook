package com.nextplugins.economy.bungeehook.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nextplugins.economy.api.NextEconomyAPI;
import com.nextplugins.economy.api.event.operations.MoneyChangeEvent;
import com.nextplugins.economy.api.event.transaction.TransactionRequestEvent;
import com.nextplugins.economy.model.account.Account;
import lombok.Data;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

@Data
public final class TransactionListener implements Listener {

    private static final NextEconomyAPI economy = NextEconomyAPI.getInstance();

    private final String targetServer;
    private final Plugin plugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    private void handle(MoneyChangeEvent event) {
        call(event.getPlayer(), event.getCurrentAmount());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void handle(TransactionRequestEvent event) {
        if (event.isCancelled()) return;

        final Player sender = event.getPlayer();
        final OfflinePlayer target = event.getTarget();

        final Account account = event.getAccount();

        call(sender, account.getBalance());
        call(sender, target.getUniqueId(), economy.findAccountByPlayer(target).getBalance());
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
