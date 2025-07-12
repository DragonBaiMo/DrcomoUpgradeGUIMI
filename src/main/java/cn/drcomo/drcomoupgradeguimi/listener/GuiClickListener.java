package cn.drcomo.drcomoupgradeguimi.listener;

import cn.drcomo.corelib.gui.GUISessionManager;
import cn.drcomo.corelib.gui.GuiManager;
import cn.drcomo.corelib.util.DebugUtil;
import cn.drcomo.drcomoupgradeguimi.config.ConfigManager;
import cn.drcomo.corelib.message.MessageService;
import io.github.projectunified.uniitem.mmoitems.MMOItemsProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Handle click events inside upgrade GUI.
 */
public class GuiClickListener implements Listener {
    private final GUISessionManager sessionManager;
    private final GuiManager guiManager;
    private final ConfigManager config;
    private final DebugUtil logger;
    private final MessageService messages;

    public GuiClickListener(GUISessionManager sessionManager, GuiManager guiManager,
                            ConfigManager config, DebugUtil logger, MessageService messages) {
        this.sessionManager = sessionManager;
        this.guiManager = guiManager;
        this.config = config;
        this.logger = logger;
        this.messages = messages;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (!sessionManager.validateSessionInventory(player, inv)) {
            return;
        }
        e.setCancelled(true);
        ConfigManager.GuiConfig gui = config.getGuiConfig();
        int slot = e.getRawSlot();
        if (slot == gui.closeButtonSlot()) {
            player.closeInventory();
            return;
        }
        if (gui.decorativeSlots().contains(slot)) {
            return;
        }
        ItemStack current = e.getCursor();
        if (current == null || current.getType().isAir()) {
            e.setCancelled(false);
            return;
        }
        if (isWhitelisted(current)) {
            e.setCancelled(false);
        } else {
            messages.send(player, "invalid-item");
            guiManager.clearCursor(player, e);
        }
    }

    private boolean isWhitelisted(ItemStack item) {
        String id = MMOItemsProvider.get().id(item);
        if (id == null) return false;
        Set<String> w = config.getWhitelist();
        for (String s : w) {
            if (s.endsWith(";;" + id)) {
                return true;
            }
        }
        return false;
    }

}
