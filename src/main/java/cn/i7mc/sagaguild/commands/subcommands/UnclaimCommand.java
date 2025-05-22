package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 取消领地声明命令
 * 允许公会取消当前区块的领地声明
 */
public class UnclaimCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public UnclaimCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "unclaim";
    }
    
    @Override
    public String getDescription() {
        return "取消当前区块的公会领地声明";
    }
    
    @Override
    public String getSyntax() {
        return "/guild unclaim";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"uc", "uncl"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // 检查权限
        if (!player.hasPermission("guild.claim")) {
            player.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }
        
        // 取消领地声明
        return plugin.getLandManager().unclaimLand(player, player.getLocation().getChunk());
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        // 没有特定的补全
        return new ArrayList<>();
    }
}
