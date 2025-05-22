package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 公会银行命令
 * 管理公会银行的存取款
 */
public class BankCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public BankCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "bank";
    }
    
    @Override
    public String getDescription() {
        return "管理公会银行";
    }
    
    @Override
    public String getSyntax() {
        return "/guild bank <info/deposit/withdraw> [金额]";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"b", "money"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // 检查玩家是否在公会中
        Guild guild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-in-guild"));
            return true;
        }
        
        // 检查参数
        if (args.length == 0) {
            // 显示银行信息
            showBankInfo(player, guild);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "info":
                // 显示银行信息
                showBankInfo(player, guild);
                break;
            case "deposit":
                // 存款
                if (args.length < 2) {
                    player.sendMessage("§c用法: /guild bank deposit <金额>");
                    return true;
                }
                
                try {
                    double amount = Double.parseDouble(args[1]);
                    plugin.getBankManager().deposit(player, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c金额必须是一个有效的数字！");
                }
                break;
            case "withdraw":
                // 取款
                if (args.length < 2) {
                    player.sendMessage("§c用法: /guild bank withdraw <金额>");
                    return true;
                }
                
                try {
                    double amount = Double.parseDouble(args[1]);
                    plugin.getBankManager().withdraw(player, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c金额必须是一个有效的数字！");
                }
                break;
            default:
                player.sendMessage("§c未知的子命令！用法: " + getSyntax());
                break;
        }
        
        return true;
    }
    
    /**
     * 显示银行信息
     * @param player 玩家
     * @param guild 公会
     */
    private void showBankInfo(Player player, Guild guild) {
        double balance = plugin.getBankManager().getBalance(guild.getId());
        double capacity = plugin.getBankManager().getCapacity(guild.getId());
        
        player.sendMessage("§8§m-----§r §b公会银行信息 §8§m-----");
        player.sendMessage("§7公会: §f" + guild.getName());
        player.sendMessage("§7余额: §f" + (int) balance);
        player.sendMessage("§7容量: §f" + (int) capacity);
        
        // 计算进度条
        int progressBarLength = 20;
        int progress = (int) ((double) balance / capacity * progressBarLength);
        StringBuilder progressBar = new StringBuilder("§7[");
        
        for (int i = 0; i < progressBarLength; i++) {
            if (i < progress) {
                progressBar.append("§a|");
            } else {
                progressBar.append("§8|");
            }
        }
        
        progressBar.append("§7]");
        player.sendMessage("§7使用情况: " + progressBar.toString());
        
        player.sendMessage("§7存款命令: §f/guild bank deposit <金额>");
        player.sendMessage("§7取款命令: §f/guild bank withdraw <金额>");
        player.sendMessage("§8§m-----------------------");
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String arg = args[0].toLowerCase();
            List<String> subCommands = Arrays.asList("info", "deposit", "withdraw");
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(arg)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("deposit") || subCommand.equals("withdraw")) {
                completions.add("100");
                completions.add("1000");
                completions.add("10000");
            }
        }
        
        return completions;
    }
}
