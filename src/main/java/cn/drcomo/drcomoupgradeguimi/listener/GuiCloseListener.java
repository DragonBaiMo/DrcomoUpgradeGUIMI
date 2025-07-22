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
        
        // 检查玩家手上是否有物品，如果有则返回到背包中
        org.bukkit.inventory.ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem != null && !cursorItem.getType().isAir()) {
            // 先清空玩家手上的物品，再返回到背包中
            player.setItemOnCursor(null);
            
            if (player.getInventory().addItem(cursorItem).size() > 0) {
                // 如果背包已满，则掉落在地上
                player.getWorld().dropItem(player.getLocation(), cursorItem);
                messages.send(player, "item-returned-dropped");
            } else {
                messages.send(player, "item-returned");
            }
        }
        
        if (gui.isUpgrading(player)) {
            return; // 已有升级任务在运行
        }
        messages.send(player, "upgrade-success");
        gui.processClose(player, inv);
        sessionManager.closeSession(player);
    }
}
