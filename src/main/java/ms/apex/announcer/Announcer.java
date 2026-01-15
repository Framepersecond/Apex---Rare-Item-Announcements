package ms.apex.announcer;

import ms.apex.settings.PlayerSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;

public class Announcer {

    public static void announce(Player player, ItemKey key, PlayerSettings settings, String acquirerName, long serial) {
        if (player == null || key == null || settings == null)
            return;
        if (!settings.isMasterEnabled())
            return;
        if (!settings.isItemEnabled(key))
            return;

        String itemName = toNiceName(key);
        Component suffix = Component.text(" (#" + serial + ", " + key.rarity().getDisplayName() + ")",
                NamedTextColor.DARK_GRAY);

        if (key.rarity() == Rarity.VERY_RARE) {
            Component title = Component.text(itemName, NamedTextColor.GOLD).append(suffix);
            Component subtitle = Component.text("Collected by " + acquirerName + "!", NamedTextColor.YELLOW);
            Title.Times times = Title.Times.times(Duration.ofMillis(250), Duration.ofSeconds(2),
                    Duration.ofMillis(400));
            player.showTitle(Title.title(title, subtitle, times));
        } else {
            Component bar = Component.text(itemName, NamedTextColor.AQUA).append(suffix)
                    .append(Component.text(" by ", NamedTextColor.GRAY))
                    .append(Component.text(acquirerName, NamedTextColor.YELLOW));
            player.sendActionBar(bar);
        }

        if (settings.isSoundEnabled(key)) {
            Sound sound = soundFor(key);
            float volume = 1.0f;
            float pitch = key.rarity() == Rarity.VERY_RARE ? 1.0f : 1.2f;
            player.playSound(player.getLocation(), sound, volume, pitch);
        }

        // Private confirmation message to the acquirer
        player.sendMessage(Component.text("You acquired ", NamedTextColor.GRAY)
                .append(Component.text(itemName, NamedTextColor.AQUA))
                .append(Component.text(" (#" + serial + ") [" + key.rarity().getDisplayName() + "]",
                        NamedTextColor.DARK_GRAY)));
    }

    private static String toNiceName(ItemKey key) {
        return switch (key) {
            case DRAGON_EGG -> "Dragon Egg";
            case HEAVY_CORE -> "Heavy Core";
            case WIND_BURST_III -> "Wind Burst III";
            case TRIDENT -> "Trident";
            case HEART_OF_THE_SEA -> "Heart of the Sea";
        };
    }

    private static Sound soundFor(ItemKey key) {
        return switch (key.rarity()) {
            case VERY_RARE -> Sound.UI_TOAST_CHALLENGE_COMPLETE;
            case RARE -> Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        };
    }
}
