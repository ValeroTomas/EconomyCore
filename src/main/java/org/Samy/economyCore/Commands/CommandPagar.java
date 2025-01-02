package org.Samy.economyCore.Commands;

import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPagar implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player emisor = (Player) sender;
        if (args.length != 2) {
            emisor.sendMessage("Uso: /pagar <jugador> <cantidad>");
            return true;
        }

        Player receptor = Bukkit.getPlayerExact(args[0]);
        if (receptor == null) {
            emisor.sendMessage("El jugador especificado no está en línea.");
            return true;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            emisor.sendMessage("Por favor, ingresa un número válido para la cantidad.");
            return true;
        }

        if (cantidad <= 0) {
            emisor.sendMessage("La cantidad debe ser mayor a 0.");
            return true;
        }

        if (ConfigManager.getBalance(emisor.getUniqueId()) < cantidad) {
            emisor.sendMessage("No tienes suficientes " + ConfigManager.getCurrencyName() + ".");
            return true;
        }

        ConfigManager.withdraw(emisor.getUniqueId(), cantidad);
        ConfigManager.deposit(receptor.getUniqueId(), cantidad);

        emisor.sendMessage("Se han transferido " + cantidad + " " + ConfigManager.getCurrencyName() + " a " + receptor.getName() + ".");
        receptor.sendMessage("Has recibido " + cantidad + " " + ConfigManager.getCurrencyName() + " de " + emisor.getName() + ".");
        return true;
    }
}