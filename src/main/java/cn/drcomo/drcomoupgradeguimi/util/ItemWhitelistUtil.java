package cn.drcomo.drcomoupgradeguimi.util;

import cn.drcomo.drcomoupgradeguimi.config.ConfigManager;
import cn.drcomo.corelib.util.DebugUtil;
import io.github.projectunified.uniitem.mmoitems.MMOItemsProvider;
import org.bukkit.inventory.ItemStack;

/**
 * 物品白名单工具类。
 * <p>
 * 统一封装原先分散在 {@code GuiClickListener} 与 {@code GuiDragListener}
 * 中的白名单判断逻辑，避免重复代码。
 */
public final class ItemWhitelistUtil {

    private static final MMOItemsProvider PROVIDER = new MMOItemsProvider();

    private ItemWhitelistUtil() {
        // 禁止实例化
    }

    /**
     * 判断给定物品是否被允许放入升级 GUI。
     *
     * @param item    待检测物品
     * @param config  插件配置管理器，用于读取白名单配置
     * @param logger  调试日志工具
     * @return true 表示物品在白名单内
     */
    public static boolean isWhitelisted(ItemStack item, ConfigManager config, DebugUtil logger) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        io.github.projectunified.uniitem.api.ItemKey key = PROVIDER.key(item);
        String full = key != null ? key.toString() : "";
        String idOnly = PROVIDER.id(item);

        // ===== 解析 Type =====
        String type = null;
        if (idOnly != null) {
            int idxSemi = idOnly.indexOf(";;");
            int idxColon = idOnly.indexOf(":");
            int idx = idxSemi >= 0 ? idxSemi : idxColon;
            type = idx > -1 ? idOnly.substring(0, idx) : idOnly;
        } else if (!full.isEmpty()) {
            // full 形如 "mmoitems:TYPE:ID"
            String cleaned = full.replace("mmoitems:", "");
            int idx = cleaned.indexOf(":");
            type = idx > -1 ? cleaned.substring(0, idx) : cleaned;
        }

        String typeLower = type != null ? type.toLowerCase() : "";

        // ===== 1) 先按 Type 判断 =====
        if (!typeLower.isEmpty() && config.getWhitelistTypes().contains(typeLower)) {
            return true;
        }

        // ===== 2) 再按精确 ID 判断 =====
        // 统一格式：去除前缀、替换分隔符，转小写
        String normFull = full.replace("mmoitems:", "").replace(";;", ":").toLowerCase();
        String normId = idOnly != null ? idOnly.toLowerCase() : "";

        for (String w : config.getWhitelistIds()) {
            String normW = w.replace(";;", ":").toLowerCase();
            if (normW.equals(normFull) || (!normId.isEmpty() && normW.equals(normId))) {
                return true; // 完整匹配
            }
        }

        // DEBUG 日志输出
        if (logger != null && logger.getLevel() == DebugUtil.LogLevel.DEBUG) {
            logger.debug("Whitelist 无匹配，ItemKey=" + full + ", id=" + idOnly + ", type=" + type +
                    ", typeWhite=" + config.getWhitelistTypes() + ", idWhite=" + config.getWhitelistIds());
        }
        return false;
    }
} 