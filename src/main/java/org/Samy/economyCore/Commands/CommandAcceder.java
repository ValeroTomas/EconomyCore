package org.Samy.economyCore.Commands;

import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandAcceder implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Verificar que el jugador está ejecutando el comando
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player receptor = (Player) sender;

        // Verificar que el jugador ha especificado al emisor en el comando
        if (args.length != 1) {
            receptor.sendMessage("Uso correcto: /acceder <jugador>");
            return true;
        }

        Player emisor = Bukkit.getPlayerExact(args[0]);

        // Verificar si el emisor está en línea
        if (emisor == null || !emisor.isOnline()) {
            receptor.sendMessage("El jugador especificado no está en línea.");
            return true;
        }

        UUID receptorId = receptor.getUniqueId();
        UUID emisorId = emisor.getUniqueId();

        // Verificar si existe una transacción activa para ambos jugadores
        if (!ConfigManager.hasActiveTransaction(receptorId) || !ConfigManager.hasActiveTransaction(emisorId)) {
            receptor.sendMessage("No hay ninguna transacción activa con este jugador.");
            return true;
        }

        // Verificar si el receptor es el correcto para aceptar la solicitud
        if (!ConfigManager.getTransactionReceiver(emisorId).equals(receptorId)) {
            receptor.sendMessage("Solo el receptor de la solicitud puede aceptarla.");
            return true;
        }

        // Obtener la cantidad solicitada desde ConfigManager
        double cantidad = ConfigManager.getTransactionAmount(emisorId);

        // Verificar si el receptor tiene suficientes fondos
        double saldoReceptor = ConfigManager.getBalance(receptorId);

        if (saldoReceptor < cantidad) {
            receptor.sendMessage("No tienes suficientes fondos para completar la transacción.");
            return true;
        }

        // Realizar la transacción
        // Retirar fondos al receptor
        if (!ConfigManager.withdraw(receptorId, cantidad)) {
            receptor.sendMessage("Hubo un problema al retirar el dinero de tu cuenta.");
            return true;
        }

        // Depositar fondos al emisor
        ConfigManager.deposit(emisorId, cantidad);

        // Cambiar el estado de la transacción a falsa para ambos jugadores
        ConfigManager.setActiveTransaction(receptorId, false);
        ConfigManager.setActiveTransaction(emisorId, false);

        // Informar a ambos jugadores
        receptor.sendMessage("Has aceptado la solicitud de " + emisor.getName() + " por " + cantidad + " " + ConfigManager.getCurrencyName() + ".");
        emisor.sendMessage(receptor.getName() + " ha aceptado tu solicitud. Has recibido " + cantidad + " " + ConfigManager.getCurrencyName() + ".");

        return true;
    }
}

