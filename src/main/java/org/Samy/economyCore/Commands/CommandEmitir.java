package org.Samy.economyCore.Commands;

import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEmitir implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("economycore.admin")) {
            sender.sendMessage("No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Uso: /emitir <nuevoNombreMoneda>");
            return true;
        }

        String nuevoNombreMoneda = args[0];
        ConfigManager.setCurrency(nuevoNombreMoneda, ConfigManager.getConversionRate());
        sender.sendMessage("El nombre de la moneda se cambió a: " + nuevoNombreMoneda);
        return true;
    }
}