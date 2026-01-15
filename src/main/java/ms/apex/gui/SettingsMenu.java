package ms.apex.gui;

import ms.apex.Apex;
import ms.apex.announcer.ItemKey;
import ms.apex.settings.PlayerSettings;
import ms.apex.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu {
    public static final String TITLE = "Apex Settings";

    private final Apex plugin;
    private final SettingsManager settingsManager;
    private final NamespacedKey itemKey;

    public SettingsMenu(Apex plugin, SettingsManager settingsManager) {
        this.plugin = plugin;
        this.settingsManager = settingsManager;
        this.itemKey = new NamespacedKey(plugin, "apex_item");
    }

    public void open(Player player) {
        PlayerSettings ps = settingsManager.get(player.getUniqueId());
        Inventory inv = Bukkit.createInventory(player, 27, Component.text(TITLE));

        inv.setItem(10, masterItem(ps.isMasterEnabled()));

        inv.setItem(12, itemToggle(ItemKey.DRAGON_EGG, ps));
        inv.setItem(13, itemToggle(ItemKey.HEAVY_CORE, ps));
        inv.setItem(14, itemToggle(ItemKey.WIND_BURST_III, ps));

        inv.setItem(22, itemToggle(ItemKey.TRIDENT, ps));
        inv.setItem(23, itemToggle(ItemKey.HEART_OF_THE_SEA, ps));

        player.openInventory(inv);
    }

    private ItemStack masterItem(boolean enabled) {
        ItemStack is = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta meta = is.getItemMeta();
        meta.displayName(Component.text("Master Toggle", NamedTextColor.GOLD));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("All announcements: " + (enabled ? "ON" : "OFF"),
                enabled ? NamedTextColor.GREEN : NamedTextColor.RED));
        lore.add(Component.text("Click to toggle", NamedTextColor.GRAY));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, "MASTER");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        is.setItemMeta(meta);
        return is;
    }

    private ItemStack itemToggle(ItemKey key, PlayerSettings ps) {
        Material mat = switch (key) {
            case DRAGON_EGG -> Material.DRAGON_EGG;
            case HEAVY_CORE -> Material.valueOf("HEAVY_CORE");
            case WIND_BURST_III -> Material.ENCHANTED_BOOK;
            case TRIDENT -> Material.TRIDENT;
            case HEART_OF_THE_SEA -> Material.HEART_OF_THE_SEA;
        };
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        meta.displayName(Component.text(niceName(key), NamedTextColor.AQUA));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Show: " + (ps.isItemEnabled(key) ? "ON" : "OFF"),
                ps.isItemEnabled(key) ? NamedTextColor.GREEN : NamedTextColor.RED));
        lore.add(Component.text("Sound: " + (ps.isSoundEnabled(key) ? "ON" : "OFF"),
                ps.isSoundEnabled(key) ? NamedTextColor.GREEN : NamedTextColor.RED));
        lore.add(Component.text(" ", NamedTextColor.DARK_GRAY));
        lore.add(Component.text("Left-click: Toggle show", NamedTextColor.GRAY));
        lore.add(Component.text("Right-click: Toggle sound", NamedTextColor.GRAY));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, key.name());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        is.setItemMeta(meta);
        return is;
    }

    public void handleClick(Player player, ItemStack clicked, boolean rightClick) {
        if (clicked == null || clicked.getType() == Material.AIR)
            return;
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null)
            return;
        String tag = meta.getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
        if (tag == null)
            return;

        PlayerSettings ps = settingsManager.get(player.getUniqueId());
        if (tag.equals("MASTER")) {
            ps.setMasterEnabled(!ps.isMasterEnabled());
            settingsManager.save();
            open(player);
            return;
        }
        try {
            ItemKey key = ItemKey.valueOf(tag);
            if (rightClick) {
                ps.setSoundEnabled(key, !ps.isSoundEnabled(key));
            } else {
                ps.setItemEnabled(key, !ps.isItemEnabled(key));
            }
            settingsManager.save();
            open(player);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private String niceName(ItemKey key) {
        return switch (key) {
            case DRAGON_EGG -> "Dragon Egg";
            case HEAVY_CORE -> "Heavy Core";
            case WIND_BURST_III -> "Wind Burst III";
            case TRIDENT -> "Trident";
            case HEART_OF_THE_SEA -> "Heart of the Sea";
        };
    }
}
