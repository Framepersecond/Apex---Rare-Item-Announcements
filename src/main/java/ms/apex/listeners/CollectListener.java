package ms.apex.listeners;

import ms.apex.Apex;
import ms.apex.announcer.Announcer;
import ms.apex.announcer.ItemKey;
import ms.apex.data.CounterManager;
import ms.apex.settings.PlayerSettings;
import ms.apex.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class CollectListener implements Listener {

    private final SettingsManager settings;
    private final CounterManager counters;
    private final NamespacedKey announcedKey;
    private final NamespacedKey uidKey;
    private final NamespacedKey serialKey;
    private final NamespacedKey dragonOwnerKey;

    private static final Set<InventoryType> EXCLUDED_TOPS = EnumSet.of(
            InventoryType.CRAFTING,
            InventoryType.WORKBENCH,
            InventoryType.ANVIL,
            InventoryType.SMITHING,
            InventoryType.ENCHANTING,
            InventoryType.GRINDSTONE,
            InventoryType.STONECUTTER,
            InventoryType.CARTOGRAPHY,
            InventoryType.LOOM,
            InventoryType.FURNACE,
            InventoryType.BLAST_FURNACE,
            InventoryType.SMOKER,
            InventoryType.BREWING);

    public CollectListener(Apex plugin, SettingsManager settings, CounterManager counters) {
        this.settings = settings;
        this.counters = counters;
        this.announcedKey = new NamespacedKey(plugin, "apex_announced");
        this.uidKey = new NamespacedKey(plugin, "apex_uid");
        this.serialKey = new NamespacedKey(plugin, "apex_serial");
        this.dragonOwnerKey = new NamespacedKey(plugin, "apex_dragon_owner");
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        Entity e = event.getEntity();
        if (!(e instanceof Player player))
            return;
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();
        Optional<ItemKey> key = ItemKey.detect(stack);
        key.ifPresent(k -> handleAcquire(player, stack, k));
        item.setItemStack(stack);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))
            return;
        if (event.isCancelled())
            return;
        Inventory top = event.getView().getTopInventory();
        if (top == null)
            return;
        if (EXCLUDED_TOPS.contains(top.getType()))
            return;

        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory() != top)
            return;
        if (event.getSlotType() == InventoryType.SlotType.RESULT)
            return;

        InventoryAction action = event.getAction();
        if (!(action == InventoryAction.MOVE_TO_OTHER_INVENTORY || action == InventoryAction.PICKUP_ALL
                || action == InventoryAction.PICKUP_SOME || action == InventoryAction.PICKUP_HALF
                || action == InventoryAction.PICKUP_ONE || action == InventoryAction.COLLECT_TO_CURSOR)) {
            return;
        }

        ItemStack current = event.getCurrentItem();
        if (current == null || current.getType() == Material.AIR)
            return;
        Optional<ItemKey> key = ItemKey.detect(current);
        key.ifPresent(k -> handleAcquire(player, current, k));
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH
                && event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY)
            return;
        if (!(event.getPlayer() instanceof Player player))
            return;
        if (event.getCaught() instanceof Item item) {
            ItemStack stack = item.getItemStack();
            Optional<ItemKey> key = ItemKey.detect(stack);
            key.ifPresent(k -> handleAcquire(player, stack, k));
            item.setItemStack(stack);
        }
    }

    private void handleAcquire(Player player, ItemStack stack, ItemKey key) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (key == ItemKey.DRAGON_EGG) {
            Long serial = pdc.get(serialKey, PersistentDataType.LONG);
            String currentOwner = pdc.get(dragonOwnerKey, PersistentDataType.STRING);
            String newOwner = player.getUniqueId().toString();

            if (serial == null) {
                String uid = UUID.randomUUID().toString();
                long newSerial = counters.nextSerial(key);
                pdc.set(uidKey, PersistentDataType.STRING, uid);
                pdc.set(serialKey, PersistentDataType.LONG, newSerial);
                pdc.set(announcedKey, PersistentDataType.INTEGER, 1);
                pdc.set(dragonOwnerKey, PersistentDataType.STRING, newOwner);

                java.util.List<net.kyori.adventure.text.Component> lore = meta.lore();
                if (lore == null)
                    lore = new java.util.ArrayList<>();
                lore.add(Component.text("Apex Serial: #" + newSerial, NamedTextColor.DARK_GRAY));
                meta.lore(lore);
                stack.setItemMeta(meta);

                broadcastDragonAnnounce(player, key, newSerial);
                PlayerSettings ps = settings.get(player.getUniqueId());
                dragonEffects(player, ps);
            } else {
                if (currentOwner != null && currentOwner.equals(newOwner)) {
                    return;
                }
                pdc.set(dragonOwnerKey, PersistentDataType.STRING, newOwner);
                stack.setItemMeta(meta);

                broadcastDragonAnnounce(player, key, serial);
                PlayerSettings ps = settings.get(player.getUniqueId());
                dragonEffects(player, ps);
            }
            return;
        }

        if (pdc.has(serialKey, PersistentDataType.LONG)) {
            return;
        }

        String uid = UUID.randomUUID().toString();
        long serial = counters.nextSerial(key);
        pdc.set(uidKey, PersistentDataType.STRING, uid);
        pdc.set(serialKey, PersistentDataType.LONG, serial);
        pdc.set(announcedKey, PersistentDataType.INTEGER, 1);

        java.util.List<net.kyori.adventure.text.Component> lore = meta.lore();
        if (lore == null)
            lore = new java.util.ArrayList<>();
        lore.add(Component.text("Apex Serial: #" + serial, NamedTextColor.DARK_GRAY));
        meta.lore(lore);
        stack.setItemMeta(meta);

        PlayerSettings ps = settings.get(player.getUniqueId());
        Announcer.announce(player, key, ps, player.getName(), serial);
    }

    private void dragonEffects(Player player, PlayerSettings ps) {
        if (ps == null || !ps.isMasterEnabled() || !ps.isItemEnabled(ItemKey.DRAGON_EGG))
            return;

        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation().add(0, 1, 0), 120, 0.6, 1.0, 0.6,
                0.01);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 80, 0.5, 0.8, 0.5, 0.01);

        if (ps.isSoundEnabled(ItemKey.DRAGON_EGG)) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
        }
    }

    private void broadcastDragonAnnounce(Player acquirer, ItemKey key, long serial) {
        String acquirerName = acquirer.getName();
        for (Player target : Bukkit.getOnlinePlayers()) {
            PlayerSettings tps = settings.get(target.getUniqueId());
            Announcer.announce(target, key, tps, acquirerName, serial);
        }
    }
}
