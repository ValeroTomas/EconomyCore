package org.Samy.economyCore.Commands;

import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CommandRechazar implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player receptor = (Player) sender;

        // Verificar si hay una solicitud activa
        UUID solicitanteId = ConfigManager.getSolicitud(receptor.getUniqueId());
        if (solicitanteId == null) {
            receptor.sendMessage("No tienes una solicitud activa para rechazar.");
            return true;
        }

        Player solicitante = Bukkit.getPlayer(solicitanteId);
        if (solicitante != null) {
            receptor.sendMessage("Has rechazado la solicitud de " + solicitante.getName() + ".");
            solicitante.sendMessage("Tu solicitud ha sido rechazada por " + receptor.getName() + ".");
        } else {
            receptor.sendMessage("El jugador solicitante ya no está en línea.");
        }

        // Eliminar la solicitud después de rechazar
        ConfigManager.eliminarSolicitud(receptor.getUniqueId());

        return true;
    }
}

