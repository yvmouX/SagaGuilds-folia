package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 声明领地命令
 * 允许公会声明当前区块为领地
 */
public class ClaimCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public ClaimCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "claim";
    }
    
    @Override
    public String getDescription() {
        return "声明当前区块为公会领地";
    }
    
    @Override
    public String getSyntax() {
        return "/guild claim";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"c", "cl"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // 检查权限
        if (!player.hasPermission("guild.claim")) {
            player.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }
        
        // 声明领地
        return plugin.getLandManager().claimLand(player, player.getLocation().getChunk());
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        // 没有特定的补全
        return new ArrayList<>();
    }
}
