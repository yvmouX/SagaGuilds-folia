package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 接受公会邀请命令
 * 用于玩家接受公会邀请
 */
public class InviteAcceptCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public InviteAcceptCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "inviteaccept";
    }
    
    @Override
    public String getDescription() {
        return "接受公会邀请";
    }
    
    @Override
    public String getSyntax() {
        return "/guild inviteaccept";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"accept", "acceptinvite"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // 调用InviteCommand中的静态方法处理邀请接受
        InviteCommand.acceptInvitation(player);
        return true;
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        // 没有特定的补全
        return new ArrayList<>();
    }
}
