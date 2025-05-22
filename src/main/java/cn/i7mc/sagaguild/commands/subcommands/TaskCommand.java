package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import cn.i7mc.sagaguild.data.models.GuildTask;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 公会任务命令
 * 查看和管理公会任务
 */
public class TaskCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public TaskCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "task";
    }
    
    @Override
    public String getDescription() {
        return "查看和管理公会任务";
    }
    
    @Override
    public String getSyntax() {
        return "/guild task <list/info> [任务ID]";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"t", "tasks", "quest"};
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
            // 显示任务列表
            showTaskList(player, guild);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list":
                // 显示任务列表
                showTaskList(player, guild);
                break;
                
            case "info":
                // 显示任务详情
                if (args.length < 2) {
                    player.sendMessage("§c用法: /guild task info <任务ID>");
                    return true;
                }
                
                try {
                    int taskId = Integer.parseInt(args[1]);
                    showTaskInfo(player, taskId);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c任务ID必须是一个有效的数字！");
                }
                break;
                
            default:
                player.sendMessage("§c未知的子命令！用法: " + getSyntax());
                break;
        }
        
        return true;
    }
    
    /**
     * 显示任务列表
     * @param player 玩家
     * @param guild 公会
     */
    private void showTaskList(Player player, Guild guild) {
        // 获取公会活跃任务
        List<GuildTask> activeTasks = plugin.getTaskManager().getActiveTasks(guild.getId());
        
        player.sendMessage("§8§m-----§r §a公会任务列表 §8§m-----");
        
        if (activeTasks.isEmpty()) {
            player.sendMessage("§7当前没有活跃的任务！");
        } else {
            for (GuildTask task : activeTasks) {
                // 显示任务基本信息
                player.sendMessage("§7ID: §f" + task.getId() + " §7- §f" + task.getDescription());
                
                // 显示任务进度
                int progress = task.getProgressPercentage();
                StringBuilder progressBar = new StringBuilder("§7[");
                
                for (int i = 0; i < 10; i++) {
                    if (i < progress / 10) {
                        progressBar.append("§a|");
                    } else {
                        progressBar.append("§8|");
                    }
                }
                
                progressBar.append("§7] §f").append(task.getProgress()).append("§7/§f").append(task.getTarget());
                player.sendMessage(progressBar.toString());
            }
        }
        
        player.sendMessage("§7使用 §f/guild task info <任务ID> §7查看详细信息");
        player.sendMessage("§8§m-----------------------");
    }
    
    /**
     * 显示任务详情
     * @param player 玩家
     * @param taskId 任务ID
     */
    private void showTaskInfo(Player player, int taskId) {
        // 获取任务
        GuildTask task = plugin.getTaskManager().getTask(taskId);
        if (task == null) {
            player.sendMessage("§c找不到ID为 §7" + taskId + " §c的任务！");
            return;
        }
        
        // 检查是否是玩家公会的任务
        Guild guild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
        if (guild == null || task.getGuildId() != guild.getId()) {
            player.sendMessage("§c这不是你公会的任务！");
            return;
        }
        
        // 显示任务详情
        player.sendMessage("§8§m-----§r §a任务详情 §8§m-----");
        player.sendMessage("§7ID: §f" + task.getId());
        player.sendMessage("§7描述: §f" + task.getDescription());
        player.sendMessage("§7类型: §f" + task.getType().getDisplayName());
        player.sendMessage("§7进度: §f" + task.getProgress() + "§7/§f" + task.getTarget() + " §7(§f" + task.getProgressPercentage() + "%§7)");
        player.sendMessage("§7奖励: §f" + task.getRewardExp() + " §7经验, §f" + task.getRewardMoney() + " §7金钱");
        
        // 显示任务状态
        String statusText;
        switch (task.getStatus()) {
            case ACTIVE:
                statusText = "§a进行中";
                break;
            case COMPLETED:
                statusText = "§6已完成";
                break;
            case EXPIRED:
                statusText = "§c已过期";
                break;
            default:
                statusText = "§7未知";
                break;
        }
        
        player.sendMessage("§7状态: " + statusText);
        
        // 显示时间信息
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        player.sendMessage("§7创建时间: §f" + sdf.format(task.getCreatedAt()));
        
        if (task.getExpiresAt() != null) {
            player.sendMessage("§7过期时间: §f" + sdf.format(task.getExpiresAt()));
        }
        
        if (task.getCompletedAt() != null) {
            player.sendMessage("§7完成时间: §f" + sdf.format(task.getCompletedAt()));
        }
        
        player.sendMessage("§8§m-----------------------");
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String arg = args[0].toLowerCase();
            List<String> subCommands = Arrays.asList("list", "info");
            
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(arg)) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            // 获取玩家公会
            Guild guild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
            if (guild != null) {
                // 获取公会活跃任务
                List<GuildTask> activeTasks = plugin.getTaskManager().getActiveTasks(guild.getId());
                
                String arg = args[1].toLowerCase();
                for (GuildTask task : activeTasks) {
                    String taskId = String.valueOf(task.getId());
                    if (taskId.startsWith(arg)) {
                        completions.add(taskId);
                    }
                }
            }
        }
        
        return completions;
    }
}
