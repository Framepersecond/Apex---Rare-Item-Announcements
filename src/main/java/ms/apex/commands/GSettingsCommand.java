package ms.apex.commands;

import ms.apex.gui.SettingsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GSettingsCommand extends Command {

    private final SettingsMenu settingsMenu;

    public GSettingsCommand(SettingsMenu settingsMenu) {
        super("gsettings");
        this.settingsMenu = settingsMenu;
        this.setDescription("Öffnet die Guardian Einstellungen.");
        this.setUsage("/gsettings");
        this.setPermission("guardian.settings");
        this.setAliases(Arrays.asList("guardiansettings", "gset"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern verwendet werden.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("guardian.settings")) {
            player.sendMessage("§cDu hast keine Berechtigung, diesen Befehl zu verwenden.");
            return true;
        }
        settingsMenu.open(player);
        return true;
    }
}
