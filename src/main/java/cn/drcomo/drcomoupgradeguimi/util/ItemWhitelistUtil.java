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

        // 统一格式：去除前缀、替换分隔符，转小写
        String normFull = full.replace("mmoitems:", "").replace(";;", ":").toLowerCase();
        String normId = idOnly != null ? idOnly.toLowerCase() : "";

        // 若既无法识别 ItemKey 也无法识别 ID，则直接拒绝
        if (idOnly == null && key == null) {
            return false;
        }
        for (String w : config.getWhitelist()) {
            String normW = w.replace(";;", ":").toLowerCase();
            if (normW.equals(normFull) || (!normId.isEmpty() && normW.equals(normId))) {
                return true; // 完整匹配
            }
        }
        // DEBUG 日志输出
        if (logger != null && logger.getLevel() == DebugUtil.LogLevel.DEBUG) {
            logger.debug("Whitelist 无匹配，ItemKey=" + full + ", id=" + idOnly + ", config=" + config.getWhitelist());
        }
        return false;
    }
} 