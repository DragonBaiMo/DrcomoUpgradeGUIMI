package cn.drcomo.drcomoupgradeguimi.command;

import cn.drcomo.corelib.message.MessageService;
import cn.drcomo.corelib.util.DebugUtil;
import cn.drcomo.drcomoupgradeguimi.config.ConfigManager;
import cn.drcomo.drcomoupgradeguimi.gui.UpgradeGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handle plugin commands.
 */
public class MainCommand implements CommandExecutor {
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("open")) {
            if (sender instanceof Player player) {
                upgradeGui.open(player);
            } else {
                sender.sendMessage("Only players can use this command.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            config.reload();
            messages.reloadLanguages();
            upgradeGui.rebuild();
            logger.setLevel(config.getLogLevel());
            messages.send(sender, "reload-complete");
            return true;
        }
        return false;
    }
}
