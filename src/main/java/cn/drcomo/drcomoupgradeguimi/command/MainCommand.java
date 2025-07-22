package cn.drcomo.drcomoupgradeguimi.command;

import cn.drcomo.corelib.message.MessageService;
import cn.drcomo.corelib.util.DebugUtil;
import cn.drcomo.drcomoupgradeguimi.config.ConfigManager;
import cn.drcomo.drcomoupgradeguimi.gui.UpgradeGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handle plugin commands.
 */
public class MainCommand implements CommandExecutor, TabCompleter {
    private final UpgradeGui upgradeGui;
    private final ConfigManager config;
    private final MessageService messages;
    private final DebugUtil logger;

    public MainCommand(UpgradeGui upgradeGui, ConfigManager config,
                       MessageService messages, DebugUtil logger) {
        this.upgradeGui = upgradeGui;
        this.config = config;
        this.messages = messages;
        this.logger = logger;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("open".startsWith(args[0].toLowerCase())) completions.add("open");
            if ("reload".startsWith(args[0].toLowerCase())) completions.add("reload");
            return completions;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 若通过别名 upitemgui 调用，强制打开界面
        if (label.equalsIgnoreCase("upitemgui")) {
            args = new String[]{"open"};
        }
        if (args.length == 0 || args[0].equalsIgnoreCase("open")) {
            if (sender instanceof Player player) {
                upgradeGui.open(player);
                messages.send(player, "open-success");
            } else {
                sender.sendMessage("only-player");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            // 权限检查：仅允许持有 drcomoupgrade.reload 的发送者执行重载
            if (!sender.hasPermission("drcomoupgrade.reload")) {
                if (sender instanceof Player player) {
                    messages.send(player, "no-permission");
                } else {
                    sender.sendMessage("no-permission");
                }
                return true;
            }
            config.reload();
            messages.reloadLanguages();
            upgradeGui.rebuild();
            logger.setLevel(config.getLogLevel());
            if (sender instanceof Player player) {
                messages.send(player, "reload-complete");
            } else {
                sender.sendMessage("reload-complete");
            }
            return true;
        }
        if (sender instanceof Player player) {
            messages.sendRaw(player, "§c用法: /" + label + " [open|reload]");
        } else {
            sender.sendMessage("用法: /" + label + " [open|reload]");
        }
        return false;
    }
}
