package cn.i7mc.sagaguild.commands;

import cn.i7mc.sagaguild.SagaGuild;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 公会主命令
 * 处理公会命令的执行和补全
 */
public class GuildCommand implements CommandExecutor, TabCompleter {
    private final SagaGuild plugin;
    private final CommandManager commandManager;
    
    public GuildCommand(SagaGuild plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 检查是否是玩家
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        // 无参数时显示帮助
        if (args.length == 0) {
            SubCommand helpCommand = commandManager.getSubCommand("help");
            if (helpCommand != null) {
                return helpCommand.execute(player, args);
            }
            return false;
        }
        
        // 获取子命令
        SubCommand subCommand = commandManager.getSubCommand(args[0]);
        if (subCommand == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("general.invalid-command"));
            return true;
        }
        
        // 执行子命令
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        
        return subCommand.execute(player, subArgs);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        // 检查是否是玩家
        if (!(sender instanceof Player)) {
            return completions;
        }
        
        Player player = (Player) sender;
        
        // 第一个参数补全子命令
        if (args.length == 1) {
            String arg = args[0].toLowerCase();
            for (SubCommand subCommand : commandManager.getSubCommands()) {
                if (subCommand.getName().toLowerCase().startsWith(arg)) {
                    completions.add(subCommand.getName());
                }
            }
            return completions;
        }
        
        // 获取子命令
        SubCommand subCommand = commandManager.getSubCommand(args[0]);
        if (subCommand == null) {
            return completions;
        }
        
        // 子命令参数补全
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        
        return subCommand.tabComplete(player, subArgs);
    }
}
