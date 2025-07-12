package cn.drcomo.drcomoupgradeguimi.listener;

import cn.drcomo.corelib.gui.GUISessionManager;
import cn.drcomo.corelib.gui.GuiManager;
import cn.drcomo.corelib.message.MessageService;
import cn.drcomo.drcomoupgradeguimi.config.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cn.drcomo.drcomoupgradeguimi.util.ItemWhitelistUtil;
import cn.drcomo.corelib.util.DebugUtil;

/**
 * Handle drag events for upgrade GUI.
 */
public class GuiDragListener implements Listener {
    private final GUISessionManager sessionManager;
    private final GuiManager guiManager;
    private final ConfigManager config;
    private final DebugUtil logger;
    private final MessageService messages;

    public GuiDragListener(GUISessionManager sessionManager, GuiManager guiManager,
                           ConfigManager config, DebugUtil logger, MessageService messages) {
        this.sessionManager = sessionManager;
        this.guiManager = guiManager;
        this.config = config;
        this.logger = logger;
        this.messages = messages;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory guiInv = e.getView().getTopInventory();
        if (!sessionManager.validateSessionInventory(player, guiInv)) {
            return;
        }
        ConfigManager.GuiConfig gui = config.getGuiConfig();
        for (int slot : e.getRawSlots()) {
            if (slot >= guiInv.getSize()) continue;
            if (gui.decorativeSlots().contains(slot) || slot == gui.closeButtonSlot()) {
                e.setCancelled(true);
                return;
            }
        }
        ItemStack item = e.getOldCursor();
        if (item != null && !item.getType().isAir() && !ItemWhitelistUtil.isWhitelisted(item, config, logger)) {
            e.setCancelled(true);
            messages.send(player, "invalid-item");
        }
    }
}
