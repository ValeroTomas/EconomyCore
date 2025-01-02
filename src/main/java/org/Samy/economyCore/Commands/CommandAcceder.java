package org.Samy.economyCore.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandAcceder implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player receptor = (Player) sender;

        if (args.length != 2) {
            receptor.sendMessage("Uso: /acceder <jugador> <cantidad>");
            return true;
        }

        Player solicitante = Bukkit.getPlayerExact(args[0]);
        if (solicitante == null) {
            receptor.sendMessage("El jugador especificado no está en línea.");
            return true;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            receptor.sendMessage("Por favor, ingresa un número válido para la cantidad.");
            return true;
        }

        // Verificar si existe una solicitud activa para este receptor
        UUID solicitanteId = ConfigManager.getSolicitud(receptor.getUniqueId());
        if (solicitanteId == null) {
            receptor.sendMessage("No tienes una solicitud activa para aceptar.");
            return true;
        }

        if (!solicitanteId.equals(solicitante.getUniqueId())) {
            receptor.sendMessage("La solicitud activa no corresponde al jugador especificado.");
            return true;
        }

        // Verificar que el receptor tiene suficiente saldo
        if (ConfigManager.getBalance(receptor.getUniqueId()) >= cantidad) {
            ConfigManager.withdraw(receptor.getUniqueId(), cantidad);
            ConfigManager.deposit(solicitante.getUniqueId(), cantidad);
            receptor.sendMessage("Has aceptado la solicitud y transferido " + cantidad + " " + ConfigManager.getCurrencyName() + " a " + solicitante.getName() + ".");
            solicitante.sendMessage("El jugador " + receptor.getName() + " ha aceptado tu solicitud de " + cantidad + " " + ConfigManager.getCurrencyName() + ".");

            // Eliminar la solicitud después de completar la transacción
            ConfigManager.eliminarSolicitud(receptor.getUniqueId());
        } else {
            receptor.sendMessage("No tienes suficiente saldo para completar esta transacción.");
        }

        return true;
    }
}
