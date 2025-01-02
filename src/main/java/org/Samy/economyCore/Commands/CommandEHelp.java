package org.Samy.economyCore.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEHelp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar si el emisor es un jugador
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player player = (Player) sender;
        // Mensaje de ayuda actualizado
        player.sendMessage("§6--- EconomyCore v1.0 por Samy---");
        player.sendMessage("");
        player.sendMessage("  §a/billetera §7- Muestra tu saldo actual.");
        player.sendMessage("  §a/pagar <jugador> <cantidad> §7- Transfiere monedas a otro jugador.");
        player.sendMessage("  §a/imprimir §7- Convierte esmeraldas en monedas.");
        player.sendMessage("  §a/solicitar <cantidad> <jugador> §7- Envía una solicitud de dinero a otro jugador.");
        player.sendMessage("  §a/acceder <jugador> §7- Acepta una solicitud de dinero recibida.");
        player.sendMessage("  §a/rechazar <jugador> §7- Rechaza una solicitud de dinero recibida.");
        player.sendMessage("  §a/eHelp §7- Muestra esta guía de comandos.");
        // Mostrar el comando /conversion solo si el jugador tiene permiso de operador
        if (player.hasPermission("economycore.admin")) {
            player.sendMessage("  §6/emitir <nuevoNombreMoneda> §7- Cambia el nombre de la moneda (Administrador).");
            player.sendMessage("  §6/conversion <tasaConversion> §7- Cambia la tasa de conversión de esmeraldas (Administrador).");
        }
        player.sendMessage("§6------------------------------------------");
        return true;
    }
}