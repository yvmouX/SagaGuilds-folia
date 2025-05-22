package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 加入公会命令
 * 允许玩家申请加入公开的公会
 */
public class JoinCommand implements SubCommand {
    private final SagaGuild plugin;

    public JoinCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "申请加入一个公会";
    }

    @Override
    public String getSyntax() {
        return "/guild join <公会名>";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"j"};
    }

    @Override
    public boolean execute(Player player, String[] args) {
        // 检查参数
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().getMessage("general.invalid-args"));
            player.sendMessage("§c用法: " + getSyntax());
            return false;
        }

        // 获取公会名称
        String guildName = args[0];

        // 检查玩家是否已经在公会中
        if (plugin.getGuildManager().getPlayerGuild(player.getUniqueId()) != null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.already-in-guild"));
            return false;
        }

        // 获取公会
        Guild guild = plugin.getGuildManager().getGuildByName(guildName);
        if (guild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-found"));
            return false;
        }

        // 检查公会是否公开
        if (!guild.isPublic()) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.need-invitation"));
            return false;
        }

        // 发送加入申请
        boolean success = plugin.getGuildManager().requestJoinGuild(player, guild.getId());
        if (!success) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.join-request-failed"));
            return false;
        }

        // 注意：成功消息已经在GuildManager.requestJoinGuild方法中发送，这里不需要重复发送

        return true;
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 获取所有公开的公会
            List<Guild> guilds = plugin.getGuildManager().getAllGuilds();
            String arg = args[0].toLowerCase();

            for (Guild guild : guilds) {
                if (guild.isPublic() && guild.getName().toLowerCase().startsWith(arg)) {
                    completions.add(guild.getName());
                }
            }
        }

        return completions;
    }
}
