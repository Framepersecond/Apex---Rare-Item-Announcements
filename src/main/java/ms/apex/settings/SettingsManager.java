package ms.apex.settings;

import ms.apex.announcer.ItemKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingsManager {
    private final File file;
    private final Map<UUID, PlayerSettings> cache = new HashMap<>();

    public SettingsManager(File dataFolder) {
        this.file = new File(dataFolder, "settings.yml");
    }

    public PlayerSettings get(UUID uuid) {
        return cache.computeIfAbsent(uuid, id -> new PlayerSettings());
    }

    public void load() {
        if (!file.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                PlayerSettings ps = new PlayerSettings();
                ps.setMasterEnabled(cfg.getBoolean(key + ".master", true));

                Map<ItemKey, Boolean> itemEnabled = new EnumMap<>(ItemKey.class);
                Map<ItemKey, Boolean> soundEnabled = new EnumMap<>(ItemKey.class);
                for (ItemKey ik : ItemKey.values()) {
                    itemEnabled.put(ik, cfg.getBoolean(key + ".items." + ik.name(), true));
                    soundEnabled.put(ik, cfg.getBoolean(key + ".sounds." + ik.name(), true));
                }
                itemEnabled.forEach(ps::setItemEnabled);
                soundEnabled.forEach(ps::setSoundEnabled);
                cache.put(uuid, ps);
            } catch (IllegalArgumentException ignored) { }
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, PlayerSettings> e : cache.entrySet()) {
            String base = e.getKey().toString();
            PlayerSettings ps = e.getValue();
            cfg.set(base + ".master", ps.isMasterEnabled());
            for (ItemKey ik : ItemKey.values()) {
                cfg.set(base + ".items." + ik.name(), ps.isItemEnabled(ik));
                cfg.set(base + ".sounds." + ik.name(), ps.isSoundEnabled(ik));
            }
        }
        try {
            cfg.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
