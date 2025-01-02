package org.Samy.economyCore.Commands;

import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBilletera implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player player = (Player) sender;
        double saldo = ConfigManager.getBalance(player.getUniqueId());
        String nombreMoneda = ConfigManager.getCurrencyName();
        player.sendMessage("Tu saldo es: " + saldo + " " + nombreMoneda);
        return true;
    }
}
