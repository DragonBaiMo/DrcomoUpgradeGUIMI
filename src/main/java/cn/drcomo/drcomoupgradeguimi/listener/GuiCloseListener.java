package cn.drcomo.drcomoupgradeguimi.listener;

import cn.drcomo.corelib.gui.GUISessionManager;
import cn.drcomo.corelib.gui.GuiManager;
import cn.drcomo.corelib.message.MessageService;
import cn.drcomo.drcomoupgradeguimi.gui.UpgradeGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * Handle inventory close event to process upgrading.
 */
public class GuiCloseListener implements Listener {
    private final GUISessionManager sessionManager;
    private final GuiManager guiManager;
    private final MessageService messages;
    private final UpgradeGui gui;

    public GuiCloseListener(GUISessionManager sessionManager, GuiManager guiManager,
                            MessageService messages, UpgradeGui gui) {
        this.sessionManager = sessionManager;
        this.guiManager = guiManager;
        this.messages = messages;
        this.gui = gui;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        if (!sessionManager.validateSessionInventory(player, inv)) {
            return;
        }
        if (gui.isUpgrading(player)) {
            return; // 已有升级任务在运行
        }
        messages.send(player, "upgrade-success");
        gui.processClose(player, inv);
        sessionManager.closeSession(player);
    }
}
