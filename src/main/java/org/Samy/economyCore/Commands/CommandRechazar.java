package org.Samy.economyCore.Commands;

import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandRechazar implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player receptor = (Player) sender;
        UUID receptorUUID = receptor.getUniqueId();

        // Obtiene el UUID del emisor de la solicitud
        UUID emisorUUID = ConfigManager.getTransactionReceiver(receptorUUID);

        // Verifica si el receptor tiene una solicitud pendiente
        if (emisorUUID == null) {
            receptor.sendMessage("No tienes ninguna solicitud pendiente.");
            return false;
        }

        if (args.length != 1) {
            receptor.sendMessage("Uso correcto: /rechazar <jugador>");
            return true;
        }

        Player emisor = Bukkit.getPlayerExact(args[0]);

        if (emisor == null || !emisor.isOnline()) {
            receptor.sendMessage("El jugador especificado no está en línea.");
            return true;
        }

        UUID receptorId = receptor.getUniqueId();
        UUID emisorId = emisor.getUniqueId();

        if (!ConfigManager.hasActiveTransaction(receptorId) || !ConfigManager.hasActiveTransaction(emisorId)) {
            receptor.sendMessage("No hay ninguna transacción activa con este jugador.");
            return true;
        }

        if (!ConfigManager.getTransactionReceiver(emisorId).equals(receptorId)) {
            receptor.sendMessage("Solo el receptor de la solicitud puede rechazarla.");
            return true;
        }

        // Terminar la transacción
        ConfigManager.setActiveTransaction(receptorId, false);
        ConfigManager.setActiveTransaction(emisorId, false);

        receptor.sendMessage("Has rechazado la solicitud de " + emisor.getName() + ".");
        emisor.sendMessage(receptor.getName() + " ha rechazado tu solicitud.");

        return true;
    }
}