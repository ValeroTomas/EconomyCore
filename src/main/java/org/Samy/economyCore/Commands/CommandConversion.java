package org.Samy.economyCore.Commands;

import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandConversion implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("economycore.operator")) {
            sender.sendMessage("No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Uso: /conversion <tasaConversion>");
            return true;
        }

        double nuevaTasa;
        try {
            nuevaTasa = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Por favor, ingresa un número válido para la tasa de conversión.");
            return true;
        }

        if (nuevaTasa <= 0) {
            sender.sendMessage("La tasa de conversión debe ser mayor a 0.");
            return true;
        }

        ConfigManager.setCurrency(ConfigManager.getCurrencyName(), nuevaTasa);
        sender.sendMessage("La tasa de conversión ha sido actualizada a: " + nuevaTasa);
        return true;
    }
}