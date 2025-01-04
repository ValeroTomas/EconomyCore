package org.Samy.economyCore.Commands;

import org.Samy.economyCore.util.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandSolicitar implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player emisor = (Player) sender;

        if (args.length != 2) {
            emisor.sendMessage("Uso correcto: /solicitar <monto> <jugador>");
            return true;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            emisor.sendMessage("Por favor, ingresa un monto válido.");
            return true;
        }

        if (cantidad <= 0) {
            emisor.sendMessage("La cantidad debe ser mayor a 0.");
            return true;
        }

        Player receptor = Bukkit.getPlayerExact(args[1]);

        if (receptor == null || !receptor.isOnline()) {
            emisor.sendMessage("El jugador especificado no está en línea.");
            return true;
        }

        if (emisor.equals(receptor)) {
            emisor.sendMessage("No puedes enviarte una solicitud a ti mismo.");
            return true;
        }

        if (ConfigManager.hasActiveTransaction(emisor.getUniqueId()) || ConfigManager.hasActiveTransaction(receptor.getUniqueId())) {
            emisor.sendMessage("Tú o el jugador objetivo ya tienen una transacción activa.");
            return true;
        }

        // Establecer el receptor de la transacción
        ConfigManager.setTransactionReceiver(emisor.getUniqueId(), receptor.getUniqueId());

        // Antes de enviar la solicitud
        ConfigManager.setTransactionAmount(emisor.getUniqueId(), cantidad);
        ConfigManager.setTransactionAmount(receptor.getUniqueId(), cantidad);

        // Marcar transacciones activas
        ConfigManager.setActiveTransaction(emisor.getUniqueId(), true);
        ConfigManager.setActiveTransaction(receptor.getUniqueId(), true);

        // Enviar mensaje interactivo al receptor
        TextComponent mensaje = Component.text("Has recibido una solicitud de " + emisor.getName() + " por " + cantidad + " " + ConfigManager.getCurrencyName() + ". ")
                .append(createClickComponent("Aceptar", emisor.getName(), cantidad, true))
                .append(Component.text(" | "))
                .append(createClickComponent("Rechazar", emisor.getName(), cantidad, false));

        receptor.sendMessage(mensaje);

        // Programar la caducidad
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("EconomyCore"), () -> {
            if (ConfigManager.hasActiveTransaction(emisor.getUniqueId()) && ConfigManager.hasActiveTransaction(receptor.getUniqueId())) {
                ConfigManager.setActiveTransaction(emisor.getUniqueId(), false);
                ConfigManager.setActiveTransaction(receptor.getUniqueId(), false);
                emisor.sendMessage("La solicitud a " + receptor.getName() + " ha caducado.");
                receptor.sendMessage("La solicitud de " + emisor.getName() + " ha caducado.");
            }
        }, 600L); // 600 ticks = 30 segundos

        emisor.sendMessage("Le pediste " + cantidad + " " + ConfigManager.getCurrencyName() + " a " + receptor.getName() + ".");
        return true;
    }

    private TextComponent createClickComponent(String action, String emisorName, double amount, boolean accept) {
        String command = accept ? "/acceder " + emisorName : "/rechazar " + emisorName;
        return Component.text("[" + action + "]").clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(command))
                .hoverEvent(Component.text("Haz clic para " + (accept ? "aceptar" : "rechazar") + " la solicitud."));
    }
}






