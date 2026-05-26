package net.tutla.pvpPlus.config;

public enum ConfigSetting {
    HUB_LOCATION("hub_location", SettingType.LOCATION, "Teleport destination after fights");

    public final String key;
    public final SettingType type;
    public final String description;

    ConfigSetting(String key, SettingType type, String description) {
        this.key = key;
        this.type = type;
        this.description = description;
    }

    public static ConfigSetting fromKey(String key) {
        for (ConfigSetting s : values()) {
            if (s.key.equalsIgnoreCase(key)) return s;
        }
        return null;
    }
}
