package cn.drcomo.drcomoupgradeguimi.gui;

import io.github.projectunified.uniitem.mmoitems.MMOItemsProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Utility to upgrade MMOItems items using UniItem provider.
 */
public final class MMOItemUtil {

    private MMOItemUtil() {
    }

    /**
     * Upgrade given item to latest definition.
     *
     * @param item original item
     * @return upgraded item or null if failed
     */
    @Nullable
    public static ItemStack upgrade(ItemStack item) {
        if (item == null) return null;
        MMOItemsProvider provider = MMOItemsProvider.get();
        String id = provider.id(item);
        if (id == null) return null;
        return provider.item(id);
    }
}
