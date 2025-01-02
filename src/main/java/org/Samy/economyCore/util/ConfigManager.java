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

    private static Map<UUID, UUID> solicitudes = new HashMap<>();
    private static Map<UUID, Long> tiemposExpiracion = new HashMap<>();
    private static final long TIEMPO_EXPIRACION = 30000L;  // 30 segundos en milisegundos

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

    // Almacenar la solicitud
    public static void agregarSolicitud(UUID jugadorSolicitante, UUID jugadorSolicitado, double cantidad) {
        solicitudes.put(jugadorSolicitado, jugadorSolicitante);
        tiemposExpiracion.put(jugadorSolicitado, System.currentTimeMillis() + TIEMPO_EXPIRACION);
    }

    // Verificar si hay una solicitud pendiente
    public static boolean tieneSolicitud(UUID jugador) {
        return solicitudes.containsKey(jugador) && System.currentTimeMillis() < tiemposExpiracion.get(jugador);
    }

    // Obtener el jugador que hizo la solicitud
    public static void eliminarSolicitud(UUID receptorId) {
        // Verificar si el receptor tiene una solicitud activa
        if (solicitudes.containsKey(receptorId)) {
            UUID solicitanteId = solicitudes.get(receptorId);

            // Eliminamos la solicitud desde el lado del receptor
            solicitudes.remove(receptorId);

            // También eliminamos cualquier solicitud inversa del solicitante hacia el receptor
            solicitudes.entrySet().removeIf(entry ->
                    entry.getKey().equals(solicitanteId) && entry.getValue().equals(receptorId)
            );

            // Notificar a los jugadores implicados
            Player receptor = Bukkit.getPlayer(receptorId);
            Player solicitante = Bukkit.getPlayer(solicitanteId);

            if (receptor != null) {
                receptor.sendMessage("La solicitud ha sido eliminada correctamente.");
            }
            if (solicitante != null) {
                solicitante.sendMessage("Tu solicitud ha sido procesada y eliminada correctamente.");
            }
        } else {
            // Notificar al receptor que no tiene una solicitud activa
            Player receptor = Bukkit.getPlayer(receptorId);
            if (receptor != null) {
                receptor.sendMessage("No se encontró una solicitud activa para eliminar.");
            }
        }

        // Verificar después de la operación si realmente la solicitud fue eliminada
        if (solicitudes.containsKey(receptorId)) {
            Player receptor = Bukkit.getPlayer(receptorId);
            if (receptor != null) {
                receptor.sendMessage("Error: la solicitud no fue eliminada correctamente.");
            }
        }
    }

    public static void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener una solicitud
    public static UUID getSolicitud(UUID receptor) {
        return solicitudes.get(receptor);
    }

    // Método para agregar una solicitud
    public static void agregarSolicitud(UUID receptor, UUID solicitante) {
        solicitudes.put(receptor, solicitante);
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

    public static double getBalance(UUID playerUUID) {
        return config.getDouble("players." + playerUUID + ".balance", 0.0);
    }

    public static void deposit(UUID playerUUID, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("La cantidad a depositar debe ser mayor que cero.");
        }
        double currentBalance = getBalance(playerUUID);
        config.set("players." + playerUUID + ".balance", currentBalance + amount);
        save();
    }

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
