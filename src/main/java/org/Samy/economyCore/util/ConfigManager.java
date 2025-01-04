package org.Samy.economyCore.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class ConfigManager {

    private static File configFile;
    private static FileConfiguration config;

    public static void setup(File dataFolder) {
        configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        if (!config.contains("currency.name")) {
            config.set("currency.name", "Monedas");
        }
        if (!config.contains("currency.conversionRate")) {
            config.set("currency.conversionRate", 1.0);
        }
        save();
    }

    public static void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Map para almacenar el receptor de cada transacción
    private static final HashMap<UUID, UUID> transactionReceivers = new HashMap<>();
    private static final HashMap<UUID, Boolean> activeTransactions = new HashMap<>();

    private static final HashMap<UUID, Double> transactionAmounts = new HashMap<>();

    // Método para guardar el monto solicitado
    public static void setTransactionAmount(UUID playerId, double amount) {
        transactionAmounts.put(playerId, amount);
    }

    // Método para obtener el monto solicitado
    public static double getTransactionAmount(UUID playerId) {
        return transactionAmounts.getOrDefault(playerId, 0.0);
    }

    // Establece el receptor de una transacción
    public static void setTransactionReceiver(UUID emisorId, UUID receptorId) {
        transactionReceivers.put(emisorId, receptorId);
    }

    // Obtiene el receptor de una transacción
    public static UUID getTransactionReceiver(UUID emisorId) {
        return transactionReceivers.getOrDefault(emisorId, null);
    }

    public static boolean hasActiveTransaction(UUID playerId) {
        return activeTransactions.getOrDefault(playerId, false);
    }

    public static void setActiveTransaction(UUID playerId, boolean active) {
        activeTransactions.put(playerId, active);
    }



    public static String getCurrencyName() {
        return config.getString("currency.name", "Monedas");
    }

    public static double getConversionRate() {
        return config.getDouble("currency.conversionRate", 1.0);
    }

    public static void setCurrency(String name, double conversionRate) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la moneda no puede estar vacío.");
        }
        if (conversionRate <= 0) {
            throw new IllegalArgumentException("La tasa de conversión debe ser mayor que cero.");
        }
        config.set("currency.name", name);
        config.set("currency.conversionRate", conversionRate);
        save();
    }

    //Obtiene el dinero de la cuenta de un jugador
    public static double getBalance(UUID playerUUID) {
        return config.getDouble("players." + playerUUID + ".balance", 0.0);
    }

    //Deposita dinero de la cuenta de un jugador
    public static void deposit(UUID playerUUID, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("La cantidad a depositar debe ser mayor que cero.");
        }
        double currentBalance = getBalance(playerUUID);
        config.set("players." + playerUUID + ".balance", currentBalance + amount);
        save();
    }

    //Quita dinero de la cuenta de un jugador
    public static boolean withdraw(UUID playerUUID, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("La cantidad a retirar debe ser mayor que cero.");
        }
        double currentBalance = getBalance(playerUUID);
        if (currentBalance >= amount) {
            config.set("players." + playerUUID + ".balance", currentBalance - amount);
            save();
            return true;
        }
        return false;
    }
}
