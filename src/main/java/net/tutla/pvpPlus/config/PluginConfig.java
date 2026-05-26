package net.tutla.pvpPlus.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class PluginConfig {
    private final JavaPlugin plugin;

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Optional<Location> getLocation(ConfigSetting setting) {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection(setting.key);
        if (sec == null || !sec.contains("world")) return Optional.empty();
        String worldName = sec.getString("world");
        if (worldName == null) return Optional.empty();
        World world = Bukkit.getWorld(worldName);
        if (world == null) return Optional.empty();
        return Optional.of(new Location(
                world,
                sec.getDouble("x"),
                sec.getDouble("y"),
                sec.getDouble("z"),
                (float) sec.getDouble("yaw"),
                (float) sec.getDouble("pitch")
        ));
    }

    public void setLocation(ConfigSetting setting, Location loc) {
        String base = setting.key;
        plugin.getConfig().set(base + ".world", loc.getWorld().getName());
        plugin.getConfig().set(base + ".x", loc.getX());
        plugin.getConfig().set(base + ".y", loc.getY());
        plugin.getConfig().set(base + ".z", loc.getZ());
        plugin.getConfig().set(base + ".yaw", (double) loc.getYaw());
        plugin.getConfig().set(base + ".pitch", (double) loc.getPitch());
        plugin.saveConfig();
    }

    public boolean getBoolean(ConfigSetting setting, boolean def) {
        return plugin.getConfig().getBoolean(setting.key, def);
    }

    public void setBoolean(ConfigSetting setting, boolean value) {
        plugin.getConfig().set(setting.key, value);
        plugin.saveConfig();
    }

    public void clearSetting(ConfigSetting setting) {
        plugin.getConfig().set(setting.key, null);
        plugin.saveConfig();
    }

    public String describe(ConfigSetting setting) {
        return switch (setting.type) {
            case LOCATION -> {
                Optional<Location> loc = getLocation(setting);
                yield loc.map(l -> String.format("%.1f, %.1f, %.1f in %s",
                        l.getX(), l.getY(), l.getZ(), l.getWorld().getName()))
                        .orElse("<not set>");
            }
            case BOOLEAN -> String.valueOf(getBoolean(setting, false));
        };
    }
}
