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
import io.github.projectunified.uniitem.api.ItemKey;

/**
 * Handle click events inside upgrade GUI.
 */
public class GuiClickListener implements Listener {
    private final GUISessionManager sessionManager;
    private final GuiManager guiManager;
    private final ConfigManager config;
    private final DebugUtil logger;
    private final MessageService messages;
    private static final MMOItemsProvider PROVIDER = new MMOItemsProvider();

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
        Inventory guiInv = e.getView().getTopInventory();
        if (!sessionManager.validateSessionInventory(player, guiInv)) {
            return;
        }
        Inventory clickedInv = e.getClickedInventory();
        if (clickedInv == null) {
            return;
        }

        // ===== 玩家背包中的 Shift 点击 / 数字键交换 等危险操作 =====
        if (clickedInv != guiInv) {
            // 仅当目标可能移动到 GUI 时需要校验
            if (e.isShiftClick() || guiManager.isDangerousClick(e.getClick())) {
                ItemStack moved = e.getCurrentItem();
                if (moved != null && !moved.getType().isAir() && !isWhitelisted(moved)) {
                    e.setCancelled(true);
                    messages.send(player, "invalid-item");
                }
            }
            return; // 不继续处理 GUI 内槽位逻辑
        }

        // ===== 点击 GUI 内部槽位 =====
        e.setCancelled(true); // 先拦截，后续条件放行
        ConfigManager.GuiConfig gui = config.getGuiConfig();
        int slot = e.getRawSlot();
        if (slot == gui.closeButtonSlot()) {
            player.closeInventory();
            return;
        }
        if (gui.decorativeSlots().contains(slot)) {
            return;
        }
        ItemStack current;
        switch (e.getClick()) {
            case NUMBER_KEY -> {
                current = player.getInventory().getItem(e.getHotbarButton());
            }
            case SWAP_OFFHAND -> {
                current = player.getInventory().getItemInOffHand();
            }
            default -> {
                current = e.getCursor();
            }
        }
        if (current == null || current.getType().isAir()) {
            e.setCancelled(false);
            return;
        }
        if (isWhitelisted(current)) {
            e.setCancelled(false);
        } else {
            messages.send(player, "invalid-item");
        }
    }

    /**
     * 判断物品是否在白名单：
     * 1. 先尝试完整字符串匹配（ItemKey#toString 与配置完全一致）；
     * 2. 若失败，则解析 MMOItems ID 并与配置项中的 "TYPE;;ID" 或单独 ID 部分进行比较。
     */
    private boolean isWhitelisted(ItemStack item) {
        io.github.projectunified.uniitem.api.ItemKey key = PROVIDER.key(item);
        String full = key != null ? key.toString() : "";
        String idOnly = PROVIDER.id(item);
        // 统一格式：去除前缀、替换分隔符，转小写
        String normFull = full.replace("mmoitems:", "").replace(";;", ":").toLowerCase();
        String normId = idOnly != null ? idOnly.toLowerCase() : "";
        if (idOnly == null && key == null) {
            return false; // 既无法识别 ItemKey，也无法识别 ID
        }
        for (String w : config.getWhitelist()) {
            String normW = w.replace(";;", ":").toLowerCase();
            if (normW.equals(normFull) || (!normId.isEmpty() && normW.equals(normId))) {
                return true; // 完整匹配
            }
        }
        if (logger.getLevel() == DebugUtil.LogLevel.DEBUG) {
            logger.debug("Whitelist 无匹配，ItemKey=" + full + ", id=" + idOnly + ", config=" + config.getWhitelist());
        }
        return false;
    }

}
