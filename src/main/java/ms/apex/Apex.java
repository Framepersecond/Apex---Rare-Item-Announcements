package ms.apex;

import ms.apex.data.CounterManager;
import ms.apex.gui.SettingsMenu;
import ms.apex.listeners.CollectListener;
import ms.apex.listeners.SettingsMenuListener;
import ms.apex.settings.SettingsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Apex extends JavaPlugin {

    private SettingsManager settingsManager;
    private SettingsMenu settingsMenu;
    private CounterManager counterManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        settingsManager = new SettingsManager(getDataFolder());
        settingsManager.load();
        counterManager = new CounterManager(getDataFolder());
        counterManager.load();
        settingsMenu = new SettingsMenu(this, settingsManager);

        getServer().getPluginManager().registerEvents(new CollectListener(this, settingsManager, counterManager), this);
        getServer().getPluginManager().registerEvents(new SettingsMenuListener(settingsMenu), this);
    }

    @Override
    public void onDisable() {
        if (settingsManager != null)
            settingsManager.save();
        if (counterManager != null)
            counterManager.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("apex")) {
            if (args.length >= 1 && args[0].equalsIgnoreCase("settings")) {
                if (sender instanceof Player player) {
                    settingsMenu.open(player);
                } else {
                    sender.sendMessage("This command can only be used by players.");
                }
            } else {
                sender.sendMessage("Usage: /apex settings");
            }
            return true;
        }
        return false;
    }
}
