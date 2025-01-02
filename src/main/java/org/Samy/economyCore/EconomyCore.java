package org.Samy.economyCore;

import org.Samy.economyCore.Commands.*;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.Samy.economyCore.util.ConfigManager;

public final class EconomyCore extends JavaPlugin {

    private Map<String, Double> balances = new HashMap<>();
    private String currencyName = "Moneda"; // Nombre de la moneda por defecto
    private double conversionRate = 1.0; // Tasa de conversión por defecto (1 esmeralda = 1 moneda)
    private FileConfiguration config;

    private Map<UUID, UUID> solicitudes; // Mapa de solicitudes
    @Override
    public void onEnable() {
        // Cargar configuración predeterminada si no existe
        saveDefaultConfig();

        // Inicializar ConfigManager
        ConfigManager.setup(getDataFolder());

        solicitudes = new HashMap<>();

        // Registrar comandos
        getCommand("emitir").setExecutor(new CommandEmitir());
        getCommand("billetera").setExecutor(new CommandBilletera());
        getCommand("pagar").setExecutor(new CommandPagar());
        getCommand("imprimir").setExecutor(new CommandImprimir());
        getCommand("conversion").setExecutor(new CommandConversion());
        getCommand("eHelp").setExecutor(new CommandEHelp());
        getCommand("solicitar").setExecutor(new CommandSolicitar(this));
        getCommand("acceder").setExecutor(new CommandAcceder());
        getCommand("rechazar").setExecutor(new CommandRechazar());
        getLogger().info("EconomyCore ha sido habilitado.");
    }

    @Override
    public void onDisable() {
        /// PLUGIN SHUTDOWN LOGIC
        getLogger().info("EconomyCore desactivado.");
    }

    // Función para obtener el balance de un jugador
    public double getBalance(Player player) {
        return balances.getOrDefault(player.getName(), 0.0);
    }

    // Función para agregar dinero a un jugador
    public void addMoney(Player player, double amount) {
        double currentBalance = getBalance(player);
        balances.put(player.getName(), currentBalance + amount);
    }

    // Función para restar dinero de un jugador
    public void subtractMoney(Player player, double amount) {
        double currentBalance = getBalance(player);
        if (currentBalance >= amount) {
            balances.put(player.getName(), currentBalance - amount);
        }
    }

    // Función para transferir dinero entre dos jugadores
    public void transferMoney(Player sender, Player receiver, double amount) {
        if (getBalance(sender) >= amount) {
            subtractMoney(sender, amount);
            addMoney(receiver, amount);
            sender.sendMessage("Se han transferido " + amount + " " + currencyName + " a " + receiver.getName());
            receiver.sendMessage("Has recibido " + amount + " " + currencyName + " de " + sender.getName());
        } else {
            sender.sendMessage("No tienes suficientes " + currencyName + ".");
        }
    }

    public void agregarSolicitud(UUID emisor, UUID receptor) {
        solicitudes.put(receptor, emisor);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ///CAMBIAR NOMBRE DE LA MONEDA
        if (command.getName().equalsIgnoreCase("emitir")) {
            if (sender.hasPermission("economycore.emitir") && args.length == 1) {
                currencyName = args[0];
                config.set("currency-name", currencyName); // Guardar el nuevo nombre de la moneda en la configuración
                saveConfig();
                sender.sendMessage("El nombre de la moneda ha sido cambiado a " + currencyName);
                return true;
            } else {
                sender.sendMessage("No tienes permisos para usar este comando o no has proporcionado un nombre.");
                return false;
            }

        }

        ///MOSTRAR SALDO DE JUGADOR
        if (command.getName().equalsIgnoreCase("billetera")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                double balance = getBalance(player);
                player.sendMessage("Tu saldo es: $" + balance + " (" + currencyName + ")");
            } else {
                sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            }
            return true;
        }

        ///PAGAR A UN JUGADOR
        if (command.getName().equalsIgnoreCase("pagar")) {
            if (sender instanceof Player) {
                if (args.length == 2) {
                    Player senderPlayer = (Player) sender;
                    Player receiverPlayer = getServer().getPlayer(args[0]);
                    double amount;
                    try {
                        amount = Double.parseDouble(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("Por favor ingresa una cantidad válida.");
                        return false;
                    }

                    if (receiverPlayer != null) {
                        transferMoney(senderPlayer, receiverPlayer, amount);
                    } else {
                        sender.sendMessage("El jugador especificado no está en línea.");
                    }
                } else {
                    sender.sendMessage("Uso correcto: /pagar <jugador> <cantidad>");
                }
            }
            return true;
        }

        ///CONVERTIR ESMERALDAS
        if (command.getName().equalsIgnoreCase("imprimir")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.getInventory().getItemInMainHand().getType() == Material.EMERALD) {
                    double amount = player.getInventory().getItemInMainHand().getAmount() * conversionRate;
                    addMoney(player, amount);
                    player.getInventory().getItemInMainHand().setAmount(0); // Eliminar esmeraldas
                    player.sendMessage("Has convertido " + amount + " " + currencyName + " de esmeraldas.");
                } else {
                    player.sendMessage("Debes tener esmeraldas en la mano.");
                }
            }
            return true;
        }
        return false;
    }
}
