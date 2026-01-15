package ms.apex.data;

import ms.apex.announcer.ItemKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class CounterManager {
    private final File file;
    private final Map<ItemKey, Long> counters = new EnumMap<>(ItemKey.class);

    public CounterManager(File dataFolder) {
        this.file = new File(dataFolder, "counters.yml");
        for (ItemKey k : ItemKey.values())
            counters.put(k, 0L);
    }

    public void load() {
        if (!file.exists())
            return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (ItemKey k : ItemKey.values()) {
            counters.put(k, cfg.getLong("serial." + k.name(), counters.getOrDefault(k, 0L)));
        }
    }

    public synchronized long nextSerial(ItemKey key) {
        long next = counters.getOrDefault(key, 0L) + 1L;
        counters.put(key, next);
        save();
        return next;
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<ItemKey, Long> e : counters.entrySet()) {
            cfg.set("serial." + e.getKey().name(), e.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
