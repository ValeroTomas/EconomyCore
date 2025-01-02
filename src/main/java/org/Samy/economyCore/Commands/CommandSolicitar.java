package org.Samy.economyCore.Commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.Samy.economyCore.util.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandSolicitar implements CommandExecutor {

    private final Plugin plugin; // Instancia del plugin
    private final Map<UUID, Map.Entry<UUID, Double>> solicitudes = new HashMap<>();
    private static final int TIEMPO_LIMITE = 30; // en segundos

    // Constructor que recibe el plugin
    public CommandSolicitar(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player emisor = (Player) sender;
        if (args.length != 2) {
            emisor.sendMessage("Uso: /solicitar <cantidad> <jugador>");
            return true;
        }

        // Obtener el jugador receptor
        Player receptor = Bukkit.getPlayerExact(args[1]);
        if (receptor == null) {
            emisor.sendMessage("El jugador especificado no está en línea.");
            return true;
        }

        if (emisor.getUniqueId().equals(receptor.getUniqueId())) {
            emisor.sendMessage("No podes enviarte una solicitud a vos mismo.");
            return true;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            emisor.sendMessage("Por favor, ingresa una cantidad válida.");
            return true;
        }

        if (cantidad <= 0) {
            emisor.sendMessage("La cantidad debe ser mayor que 0.");
            return true;
        }

        // Guardamos la solicitud
        solicitudes.put(receptor.getUniqueId(), new HashMap.SimpleEntry<>(emisor.getUniqueId(), cantidad));

        // Enviar el mensaje interactivo al receptor
        TextComponent mensaje = Component.text("Has recibido una solicitud de " + emisor.getName() + " por " + cantidad + " " + ConfigManager.getCurrencyName() + ". ")
                .append(createClickComponent("Aceptar", emisor.getName(), cantidad, true))
                .append(Component.text(" | "))
                .append(createClickComponent("Rechazar", emisor.getName(), cantidad, false));

        receptor.sendMessage(mensaje);

        // Programar la eliminación de la solicitud después del tiempo límite
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (solicitudes.containsKey(receptor.getUniqueId())) {
                solicitudes.remove(receptor.getUniqueId());
                receptor.sendMessage("La solicitud de " + emisor.getName() + " ha caducado.");
            }
        }, TIEMPO_LIMITE * 20L); // 20 ticks = 1 segundo

        emisor.sendMessage("Solicitud enviada a " + receptor.getName() + ".");
        return true;
    }

    private TextComponent createClickComponent(String texto, String emisorName, double cantidad, boolean aceptar) {
        ClickEvent eventoClick;
        if (aceptar) {
            eventoClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/acceder " + emisorName + " " + cantidad);
        } else {
            eventoClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/rechazar " + emisorName);
        }

        TextComponent component = Component.text(texto)
                .color(aceptar ? TextColor.color(0x00FF00) : TextColor.color(0xFF0000)) // Verde para aceptar, Rojo para rechazar
                .clickEvent(eventoClick);

        return component;
    }
}




