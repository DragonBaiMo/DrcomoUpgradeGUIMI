package cn.drcomo.drcomoupgradeguimi.gui;

import cn.drcomo.corelib.gui.GUISessionManager;
import cn.drcomo.corelib.gui.GuiManager;
import cn.drcomo.corelib.util.DebugUtil;
import cn.drcomo.drcomoupgradeguimi.config.ConfigManager;
import cn.drcomo.drcomoupgradeguimi.config.ConfigManager.GuiConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

/**
 * Create and manage upgrade GUI using template inventory.
 */
public class UpgradeGui {
    public static final String SESSION_ID = "upgrade-gui";

    private final GuiManager guiManager;
    private final GUISessionManager sessionManager;
    private final ConfigManager config;
    private final DebugUtil logger;
    private Inventory template;

    public UpgradeGui(GuiManager guiManager, GUISessionManager sessionManager,
                       ConfigManager config, DebugUtil logger) {
        this.guiManager = guiManager;
        this.sessionManager = sessionManager;
        this.config = config;
        this.logger = logger;
        buildTemplate();
    }

    /** Rebuild template when config changes. */
    public void rebuild() {
        buildTemplate();
    }

    private void buildTemplate() {
        GuiConfig cfg = config.getGuiConfig();
        template = Bukkit.createInventory(null, cfg.size(), cfg.title());
        for (Map.Entry<Integer, ItemStack> e : cfg.decorativeItems().entrySet()) {
            template.setItem(e.getKey(), e.getValue());
        }
        template.setItem(cfg.closeButtonSlot(), cfg.closeButtonItem());
    }

    /** Open GUI for player. */
    public void open(Player player) {
        sessionManager.openSession(player, SESSION_ID, p -> template);
        logger.debug("Open gui for " + player.getName());
    }

    /** Process all items inside inventory on close. */
    public void processClose(Player player, Inventory inv) {
        GuiConfig cfg = config.getGuiConfig();
        for (int i = 0; i < inv.getSize(); i++) {
            if (cfg.decorativeSlots().contains(i) || i == cfg.closeButtonSlot()) {
                continue;
            }
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            handleItem(player, item);
        }
        player.updateInventory();
    }

    private void handleItem(Player player, ItemStack item) {
        ItemStack upgraded = MMOItemUtil.upgrade(item);
        ItemStack result = upgraded != null ? upgraded : item;
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(result);
        if (!leftover.isEmpty()) {
            leftover.values().forEach(it -> player.getWorld().dropItem(player.getLocation(), it));
        }
    }
}
