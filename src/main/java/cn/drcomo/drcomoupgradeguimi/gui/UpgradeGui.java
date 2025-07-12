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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.plugin.Plugin;
import io.github.projectunified.uniitem.mmoitems.MMOItemsProvider;

/**
 * Create and manage upgrade GUI using template inventory.
 */
public class UpgradeGui {
    public static final String SESSION_ID = "upgrade-gui";

    private final GuiManager guiManager;
    private final GUISessionManager sessionManager;
    private final ConfigManager config;
    private final DebugUtil logger;
    private final Plugin plugin;
    private Inventory template;

    /**
     * 单例化的 MMOItems 提供者，避免频繁创建实例。
     */
    public static final MMOItemsProvider PROVIDER = new MMOItemsProvider();

    /** 防重入：记录正在异步升级的玩家 */
    private final Set<UUID> upgrading = ConcurrentHashMap.newKeySet();

    public UpgradeGui(Plugin plugin, GuiManager guiManager, GUISessionManager sessionManager,
                       ConfigManager config, DebugUtil logger) {
        this.guiManager = guiManager;
        this.sessionManager = sessionManager;
        this.config = config;
        this.logger = logger;
        this.plugin = plugin;
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
        UUID uid = player.getUniqueId();
        if (!upgrading.add(uid)) {
            // 已在处理，直接忽略重复触发
            return;
        }

        GuiConfig cfg = config.getGuiConfig();
        // 在主线程下收集物品并清空槽位
        List<ItemStack> snapshot = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (cfg.decorativeSlots().contains(i) || i == cfg.closeButtonSlot()) {
                continue;
            }
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            snapshot.add(item.clone());
            inv.setItem(i, null);
        }
        player.updateInventory();

        // 异步处理升级
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // 复制快照，避免并发修改
            List<ItemStack> asyncSnapshot = new ArrayList<>(snapshot);

            // 回主线程批量升级并发放
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    List<ItemStack> results = new ArrayList<>();
                    for (ItemStack item : asyncSnapshot) {
                        results.addAll(upgradeItem(item));
                    }

                    for (ItemStack res : results) {
                        Map<Integer, ItemStack> leftover = player.getInventory().addItem(res);
                        if (!leftover.isEmpty()) {
                            leftover.values().forEach(it -> player.getWorld().dropItem(player.getLocation(), it));
                        }
                    }
                } finally {
                    // 无论成功或异常都清理状态，防止死锁
                    upgrading.remove(uid);
                }
            });
        });
    }

    /** 升级单个物品并保持数量，返回可能拆分后的多份物品 */
    private List<ItemStack> upgradeItem(ItemStack item) {
        List<ItemStack> list = new ArrayList<>();
        MMOItemsProvider provider = UpgradeGui.PROVIDER;
        String id = provider.id(item);
        ItemStack upgraded = id != null ? provider.item(id) : null;

        int amount = item.getAmount();
        if (upgraded == null) {
            ItemStack clone = item.clone();
            list.add(clone);
            return list;
        }

        int maxStack = upgraded.getMaxStackSize();
        if (maxStack >= amount) {
            ItemStack stack = upgraded.clone();
            stack.setAmount(amount);
            list.add(stack);
        } else {
            int remaining = amount;
            while (remaining > 0) {
                int give = Math.min(maxStack, remaining);
                ItemStack part = upgraded.clone();
                part.setAmount(give);
                list.add(part);
                remaining -= give;
            }
        }
        return list;
    }

    /** 判断玩家是否正在升级，供外部监听器快速退出 */
    public boolean isUpgrading(Player player) {
        return upgrading.contains(player.getUniqueId());
    }

    /**
     * 插件卸载阶段紧急清理：同步返还所有 GUI 内物品，不做升级。
     * 由于调度器已关闭，任何异步或同步任务都无法执行，只能直接操作。
     */
    public void flushOnDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // 只处理仍处于本 GUI 会话的玩家
            if (!sessionManager.hasSession(player)) continue;
            if (!SESSION_ID.equals(sessionManager.getCurrentSessionId(player))) continue;

            Inventory inv = player.getOpenInventory().getTopInventory();
            GuiConfig cfg = config.getGuiConfig();
            for (int i = 0; i < inv.getSize(); i++) {
                if (cfg.decorativeSlots().contains(i) || i == cfg.closeButtonSlot()) {
                    continue;
                }
                ItemStack item = inv.getItem(i);
                if (item == null || item.getType().isAir()) continue;
                Map<Integer, ItemStack> leftover = player.getInventory().addItem(item.clone());
                if (!leftover.isEmpty()) {
                    leftover.values().forEach(it -> player.getWorld().dropItem(player.getLocation(), it));
                }
            }
        }
    }
}
