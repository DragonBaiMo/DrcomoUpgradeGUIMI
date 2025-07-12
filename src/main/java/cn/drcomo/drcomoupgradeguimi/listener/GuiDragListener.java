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
import io.github.projectunified.uniitem.api.ItemKey;
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
    private static final MMOItemsProvider PROVIDER = new MMOItemsProvider();

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
        if (item != null && !item.getType().isAir() && !isWhitelisted(item)) {
            e.setCancelled(true);
            messages.send(player, "invalid-item");
        }
    }

    /**
     * 与 GuiClickListener 相同的白名单判断逻辑。
     */
    private boolean isWhitelisted(ItemStack item) {
        io.github.projectunified.uniitem.api.ItemKey key = PROVIDER.key(item);
        String full = key != null ? key.toString() : "";
        String idOnly = PROVIDER.id(item);
        String normFull = full.replace("mmoitems:", "").replace(";;", ":").toLowerCase();
        String normId = idOnly != null ? idOnly.toLowerCase() : "";
        if (idOnly == null && key == null) {
            return false;
        }
        for (String w : config.getWhitelist()) {
            String normW = w.replace(";;", ":").toLowerCase();
            if (normW.equals(normFull) || (!normId.isEmpty() && normW.equals(normId))) {
                return true;
            }
        }
        if (logger.getLevel() == DebugUtil.LogLevel.DEBUG) {
            logger.debug("Whitelist 无匹配，ItemKey=" + full + ", id=" + idOnly + ", config=" + config.getWhitelist());
        }
        return false;
    }
}
