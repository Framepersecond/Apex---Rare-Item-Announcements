package ms.apex.listeners;

import ms.apex.gui.SettingsMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SettingsMenuListener implements Listener {

    private final SettingsMenu menu;

    public SettingsMenuListener(SettingsMenu menu) {
        this.menu = menu;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getView().title() == null) return;
        String title = event.getView().title().toString();
        if (!title.contains(SettingsMenu.TITLE)) return; // adventure Component toString contains text
        event.setCancelled(true);
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.PLAYER) return;
        ItemStack clicked = event.getCurrentItem();
        boolean rightClick = event.isRightClick();
        menu.handleClick((org.bukkit.entity.Player) event.getWhoClicked(), clicked, rightClick);
    }
}
