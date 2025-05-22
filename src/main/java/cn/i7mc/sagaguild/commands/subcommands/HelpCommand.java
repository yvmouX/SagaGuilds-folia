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
                player.sendMessage("§7/guild invite <玩家> §f- 邀请玩家加入公会");
                player.sendMessage("§7/guild join <公会名> §f- 申请加入公会");
                player.sendMessage("§7/guild leave §f- 离开公会");
                break;
            case 2:
                player.sendMessage("§7/guild kick <玩家> §f- 踢出公会成员");
                player.sendMessage("§7/guild promote <玩家> §f- 提升成员职位");
                player.sendMessage("§7/guild demote <玩家> §f- 降低成员职位");
                player.sendMessage("§7/guild transfer <玩家> §f- 转让公会会长");
                player.sendMessage("§7/guild disband §f- 解散公会");
                player.sendMessage("§7/guild claim §f- 声明公会领地");
                break;
            case 3:
                player.sendMessage("§7/guild unclaim §f- 取消领地声明");
                player.sendMessage("§7/guild bank <存款/取款> <金额> §f- 公会银行操作");
                player.sendMessage("§7/guild war <公会名> §f- 发起公会战");
                player.sendMessage("§7/guild ally <公会名> §f- 结成联盟");
                player.sendMessage("§7/guild chat §f- 切换公会聊天");
                player.sendMessage("§7/guild settings §f- 打开公会设置");
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
