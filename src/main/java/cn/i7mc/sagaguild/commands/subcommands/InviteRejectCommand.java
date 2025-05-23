package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 拒绝公会邀请命令
 * 用于玩家拒绝公会邀请
 */
public class InviteRejectCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public InviteRejectCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "invitereject";
    }
    
    @Override
    public String getDescription() {
        return "拒绝公会邀请";
    }
    
    @Override
    public String getSyntax() {
        return "/guild invitereject";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"reject", "rejectinvite"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // 调用InviteCommand中的静态方法处理邀请拒绝
        InviteCommand.rejectInvitation(player);
        return true;
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        // 没有特定的补全
        return new ArrayList<>();
    }
}
