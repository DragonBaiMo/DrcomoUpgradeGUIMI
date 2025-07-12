package cn.drcomo.drcomoupgradeguimi.config;

import cn.drcomo.corelib.config.YamlUtil;
import cn.drcomo.corelib.util.DebugUtil;
import cn.drcomo.corelib.util.DebugUtil.LogLevel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Manage plugin configuration and provide accessors.
 */
public class ConfigManager {
    private final YamlUtil yamlUtil;
    private final DebugUtil logger;
    private Set<String> whitelist = new HashSet<>();
    private GuiConfig guiConfig;
    private LogLevel logLevel = LogLevel.INFO;

    public ConfigManager(YamlUtil yamlUtil, DebugUtil logger) {
        this.yamlUtil = yamlUtil;
        this.logger = logger;
    }

    /** Reload config.yml and parse fields. */
    public void reload() {
        yamlUtil.reloadConfig("config");
        parse();
    }

    private void parse() {
        whitelist.clear();
        whitelist.addAll(yamlUtil.getStringList("config", "whitelist", Collections.emptyList()));
        logLevel = LogLevel.valueOf(yamlUtil.getString("config", "debug-level", "INFO").toUpperCase());

        String title = yamlUtil.getString("config", "gui.title", "&8物品升级界面");
        int size = yamlUtil.getInt("config", "gui.size", 27);
        Set<Integer> decorativeSlots = new HashSet<>();
        Map<Integer, ItemStack> decorativeItems = new HashMap<>();
        ConfigurationSection decoSec = yamlUtil.getSection("config", "gui.decorative-slots");
        if (decoSec != null) {
            for (String key : decoSec.getKeys(false)) {
                List<Integer> slots = parseSlots(key);
                ItemStack item = itemFromSection(decoSec.getConfigurationSection(key));
                for (int slot : slots) {
                    decorativeSlots.add(slot);
                    decorativeItems.put(slot, item);
                }
            }
        }
        int closeSlot = yamlUtil.getInt("config", "gui.close-button-slot", 22);
        ItemStack closeItem = itemFromSection(yamlUtil.getSection("config", "gui.close-button"));
        guiConfig = new GuiConfig(title, size, decorativeSlots, decorativeItems, closeSlot, closeItem);
    }

    private List<Integer> parseSlots(String key) {
        String trimmed = key.replace("[", "").replace("]", "");
        String[] parts = trimmed.split(",");
        List<Integer> result = new ArrayList<>();
        for (String part : parts) {
            try {
                result.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private ItemStack itemFromSection(ConfigurationSection section) {
        if (section == null) return new ItemStack(Material.BARRIER);
        Material mat = Material.matchMaterial(section.getString("material", "BARRIER"));
        ItemStack item = new ItemStack(mat == null ? Material.BARRIER : mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(section.getString("name", "")));
            List<String> lore = section.getStringList("lore");
            if (!lore.isEmpty()) {
                meta.setLore(colorList(lore));
            }
            if (section.contains("custom-model-data")) {
                meta.setCustomModelData(section.getInt("custom-model-data"));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private String color(String text) {
        return cn.drcomo.corelib.color.ColorUtil.translateColors(text);
    }

    private List<String> colorList(List<String> list) {
        List<String> res = new ArrayList<>();
        for (String s : list) res.add(color(s));
        return res;
    }

    public Set<String> getWhitelist() {
        return whitelist;
    }

    public GuiConfig getGuiConfig() {
        return guiConfig;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    /** Holds parsed GUI configuration. */
    public record GuiConfig(String title, int size, Set<Integer> decorativeSlots,
                            Map<Integer, ItemStack> decorativeItems,
                            int closeButtonSlot, ItemStack closeButtonItem) {
    }
}
