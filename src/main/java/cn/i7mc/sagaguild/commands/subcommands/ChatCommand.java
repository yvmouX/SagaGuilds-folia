package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import cn.i7mc.sagaguild.managers.ChatManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 公会聊天命令
 * 切换公会聊天模式
 */
public class ChatCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public ChatCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "chat";
    }
    
    @Override
    public String getDescription() {
        return "切换公会聊天模式";
    }
    
    @Override
    public String getSyntax() {
        return "/guild chat";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"c", "ch"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // 检查玩家是否在公会中
        Guild guild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-in-guild"));
            return true;
        }
        
        // 切换聊天模式
        ChatManager.ChatMode newMode = plugin.getChatManager().togglePlayerChatMode(player);
        
        // 显示当前模式
        String modeText;
        switch (newMode) {
            case GUILD:
                modeText = "§b公会聊天";
                break;
            case ALLIANCE:
                modeText = "§d联盟聊天";
                break;
            default:
                modeText = "§7普通聊天";
                break;
        }
        
        player.sendMessage("§7当前聊天模式: " + modeText);
        
        return true;
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        // 没有特定的补全
        return new ArrayList<>();
    }
}
