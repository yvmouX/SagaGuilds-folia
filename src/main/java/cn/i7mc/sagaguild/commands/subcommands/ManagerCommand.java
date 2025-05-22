package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import cn.i7mc.sagaguild.data.models.GuildMember;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 公会管理命令
 * 打开公会管理界面
 */
public class ManagerCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public ManagerCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "manager";
    }
    
    @Override
    public String getDescription() {
        return "打开公会管理界面";
    }
    
    @Override
    public String getSyntax() {
        return "/guild manager";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"manage", "admin", "management"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // 检查玩家是否在公会中
        Guild guild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-in-guild"));
            return true;
        }
        
        // 检查玩家是否有权限管理公会
        GuildMember member = plugin.getGuildManager().getMemberByUuid(player.getUniqueId());
        if (member == null || !member.isElder()) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.no-permission"));
            return true;
        }
        
        // 打开公会管理界面
        plugin.getGuiManager().openGuildManageGUI(player, guild);
        return true;
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        // 没有特定的补全
        return new ArrayList<>();
    }
}
