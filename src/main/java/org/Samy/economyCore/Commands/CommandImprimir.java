package org.Samy.economyCore.Commands;

import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class CommandImprimir implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack esmeraldas = player.getInventory().getItemInMainHand();

        if (esmeraldas.getType() != Material.EMERALD) {
            player.sendMessage("Debes tener esmeraldas en la mano para usar este comando.");
            return true;
        }

        int cantidadEsmeraldas = esmeraldas.getAmount();
        if (cantidadEsmeraldas <= 0) {
            player.sendMessage("No tienes esmeraldas en tu mano.");
            return true;
        }

        double tasaConversion = ConfigManager.getConversionRate();
        double monedasGeneradas = cantidadEsmeraldas * tasaConversion;

        ConfigManager.deposit(player.getUniqueId(), monedasGeneradas);
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        player.sendMessage("Has convertido " + cantidadEsmeraldas + " esmeraldas en " + monedasGeneradas + " " + ConfigManager.getCurrencyName() + ".");
        return true;
    }
}