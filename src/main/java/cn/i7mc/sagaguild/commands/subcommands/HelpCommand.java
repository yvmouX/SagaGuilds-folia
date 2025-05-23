package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 帮助命令
 * 显示插件的帮助信息
 */
public class HelpCommand implements SubCommand {
    private final SagaGuild plugin;

    public HelpCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "显示公会插件帮助信息";
    }

    @Override
    public String getSyntax() {
        return "/guild help [页码]";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"h", "?"};
    }

    @Override
    public boolean execute(Player player, String[] args) {
        int page = 1;

        // 解析页码
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // 显示帮助信息
        player.sendMessage("§8§m-----§r §b公会插件帮助 §7(第 " + page + " 页)§8 §8§m-----");

        // 根据页码显示不同的命令
        switch (page) {
            case 1:
                player.sendMessage("§7/guild help [页码] §f- 显示帮助信息");
                player.sendMessage("§7/guild create <名称> <标签> [描述] §f- 创建公会");
                player.sendMessage("§7/guild info [公会名] §f- 查看公会信息");
                player.sendMessage("§7/guild list [页码] §f- 查看公会列表");
                player.sendMessage("§7/guild join <公会名> §f- 申请加入公会");
                player.sendMessage("§7/guild invite <玩家名> §f- 邀请玩家加入公会");
                player.sendMessage("§7/guild leave §f- 离开当前公会");
                player.sendMessage("§7/guild manager §f- 打开公会管理界面");
                player.sendMessage("§7/guild level [公会名] §f- 显示公会等级信息");
                break;
            case 2:
                player.sendMessage("§7/guild claim §f- 声明当前区块为公会领地");
                player.sendMessage("§7/guild unclaim §f- 取消当前区块的公会领地声明");
                player.sendMessage("§7/guild bank <info/deposit/withdraw> [金额] §f- 公会银行操作");
                player.sendMessage("§7/guild chat §f- 切换公会聊天模式");
                player.sendMessage("§7/guild war <declare/accept/status/history> [公会名] §f- 管理公会战");
                player.sendMessage("§7/guild task <list/info> [任务ID] §f- 查看和管理公会任务");
                player.sendMessage("§7/guild ally <add/remove/list> [公会名] §f- 管理公会联盟");
                break;
            case 3:
                player.sendMessage("§7/guild activity <list/info/create/join/leave/cancel> [参数...] §f- 管理公会活动");
                player.sendMessage("§7/guild relation <list/ally/war/break/ceasefire/gui> [公会名] §f- 管理公会关系");
                break;
            default:
                player.sendMessage("§c没有更多帮助信息！");
                break;
        }

        player.sendMessage("§8§m-----------------------");

        return true;
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("1");
            completions.add("2");
            completions.add("3");
        }

        return completions;
    }
}
