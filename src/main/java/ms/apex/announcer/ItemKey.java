package ms.apex.announcer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public enum ItemKey {
    DRAGON_EGG(Rarity.VERY_RARE),
    HEAVY_CORE(Rarity.VERY_RARE),
    WIND_BURST_III(Rarity.VERY_RARE),
    TRIDENT(Rarity.RARE),
    HEART_OF_THE_SEA(Rarity.RARE);

    private final Rarity rarity;

    ItemKey(Rarity rarity) {
        this.rarity = rarity;
    }

    public Rarity rarity() {
        return rarity;
    }

    public static Optional<ItemKey> detect(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR)
            return Optional.empty();
        Material type = stack.getType();

        if (type == Material.DRAGON_EGG)
            return Optional.of(DRAGON_EGG);
        if (type.name().equals("HEAVY_CORE"))
            return Optional.of(HEAVY_CORE);
        if (type == Material.TRIDENT)
            return Optional.of(TRIDENT);
        if (type == Material.HEART_OF_THE_SEA)
            return Optional.of(HEART_OF_THE_SEA);

        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (meta instanceof EnchantmentStorageMeta esm) {
                Integer lvl = esm.getStoredEnchants().get(Enchantment.WIND_BURST);
                if (lvl != null && lvl >= 3)
                    return Optional.of(WIND_BURST_III);
            }

            Integer lvl = meta.getEnchants().get(Enchantment.WIND_BURST);
            if (lvl != null && lvl >= 3)
                return Optional.of(WIND_BURST_III);
        }
        return Optional.empty();
    }
}
