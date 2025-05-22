package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 公会列表命令
 * 打开公会列表GUI，允许玩家查看所有公会
 */
public class ListCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public ListCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "list";
    }
    
    @Override
    public String getDescription() {
        return "打开公会列表GUI";
    }
    
    @Override
    public String getSyntax() {
        return "/guild list [页码]";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"ls", "guilds"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // 解析页码
        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getConfigManager().getMessage("general.invalid-page"));
                return false;
            }
        }
        
        // 打开公会列表GUI
        plugin.getGuiManager().openGuildListGUI(player, page);
        return true;
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // 页码，提供1-5页
            completions.add("1");
            completions.add("2");
            completions.add("3");
            completions.add("4");
            completions.add("5");
        }
        
        return completions;
    }
}
