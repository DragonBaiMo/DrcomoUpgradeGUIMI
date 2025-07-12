package cn.drcomo.drcomoupgradeguimi.listener;

import cn.drcomo.corelib.gui.GUISessionManager;
import cn.drcomo.corelib.gui.GuiManager;
import cn.drcomo.corelib.message.MessageService;
import cn.drcomo.drcomoupgradeguimi.config.ConfigManager;
import io.github.projectunified.uniitem.mmoitems.MMOItemsProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Handle drag events for upgrade GUI.
 */
public class GuiDragListener implements Listener {
    private final GUISessionManager sessionManager;
    private final GuiManager guiManager;
    private final ConfigManager config;
    private final MessageService messages;

    public GuiDragListener(GUISessionManager sessionManager, GuiManager guiManager,
                           ConfigManager config, MessageService messages) {
        this.sessionManager = sessionManager;
        this.guiManager = guiManager;
        this.config = config;
        this.messages = messages;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (!sessionManager.validateSessionInventory(player, inv)) {
            return;
        }
        ConfigManager.GuiConfig gui = config.getGuiConfig();
        for (int slot : e.getRawSlots()) {
            if (gui.decorativeSlots().contains(slot) || slot == gui.closeButtonSlot()) {
                e.setCancelled(true);
                return;
            }
        }
        ItemStack item = e.getOldCursor();
        if (item != null && !item.getType().isAir() && !isWhitelisted(item)) {
            e.setCancelled(true);
            messages.send(player, "invalid-item");
            guiManager.clearCursor(player, null);
        }
    }

    private boolean isWhitelisted(ItemStack item) {
        String id = MMOItemsProvider.get().id(item);
        if (id == null) return false;
        Set<String> w = config.getWhitelist();
        for (String s : w) {
            if (s.endsWith(";;" + id)) return true;
        }
        return false;
    }
}
