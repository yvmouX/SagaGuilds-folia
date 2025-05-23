package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 离开公会命令
 * 允许玩家离开当前所在的公会
 */
public class LeaveCommand implements SubCommand {
    private final SagaGuild plugin;

    public LeaveCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "离开当前所在的公会";
    }

    @Override
    public String getSyntax() {
        return "/guild leave";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"quit", "exit"};
    }

    @Override
    public boolean execute(Player player, String[] args) {
        // 获取玩家所在公会
        Guild guild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-in-guild"));
            return true;
        }

        // 检查是否是会长
        if (guild.getOwnerUuid().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("members.leader-cannot-leave"));
            return true;
        }

        // 离开公会
        boolean success = plugin.getGuildManager().leaveGuild(player);
        if (!success) {
            player.sendMessage(plugin.getConfigManager().getMessage("members.leave-failed"));
            return true;
        }

        // 发送离开消息
        player.sendMessage(plugin.getConfigManager().getMessage("members.left",
                "guild", guild.getName()));

        // 通知公会成员
        plugin.getGuildManager().broadcastToGuild(guild.getId(),
                plugin.getConfigManager().getMessage("members.player-left",
                        "player", player.getName()),
                null);

        return true;
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        // 没有特定的补全
        return new ArrayList<>();
    }
}
