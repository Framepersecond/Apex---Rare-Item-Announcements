package ms.apex.settings;

import ms.apex.announcer.ItemKey;

import java.util.EnumMap;
import java.util.Map;

public class PlayerSettings {
    private boolean masterEnabled = true;
    private final Map<ItemKey, Boolean> itemEnabled = new EnumMap<>(ItemKey.class);
    private final Map<ItemKey, Boolean> soundEnabled = new EnumMap<>(ItemKey.class);

    public PlayerSettings() {
        for (ItemKey key : ItemKey.values()) {
            itemEnabled.put(key, true);
            soundEnabled.put(key, true);
        }
    }

    public boolean isMasterEnabled() {
        return masterEnabled;
    }

    public void setMasterEnabled(boolean masterEnabled) {
        this.masterEnabled = masterEnabled;
    }

    public boolean isItemEnabled(ItemKey key) {
        return itemEnabled.getOrDefault(key, true);
    }

    public void setItemEnabled(ItemKey key, boolean enabled) {
        itemEnabled.put(key, enabled);
    }

    public boolean isSoundEnabled(ItemKey key) {
        return soundEnabled.getOrDefault(key, true);
    }

    public void setSoundEnabled(ItemKey key, boolean enabled) {
        soundEnabled.put(key, enabled);
    }

    public Map<ItemKey, Boolean> getItemEnabledMap() {
        return itemEnabled;
    }

    public Map<ItemKey, Boolean> getSoundEnabledMap() {
        return soundEnabled;
    }
}
