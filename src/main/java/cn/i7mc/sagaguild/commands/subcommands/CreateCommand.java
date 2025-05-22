package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建公会命令
 * 允许玩家创建新公会
 */
public class CreateCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public CreateCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "create";
    }
    
    @Override
    public String getDescription() {
        return "创建一个新公会";
    }
    
    @Override
    public String getSyntax() {
        return "/guild create <名称> <标签> [描述]";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"c", "new"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // 检查权限
        if (!player.hasPermission("guild.create")) {
            player.sendMessage(plugin.getConfigManager().getMessage("general.no-permission"));
            return true;
        }
        
        // 检查参数
        if (args.length < 2) {
            player.sendMessage("§c用法: " + getSyntax());
            return true;
        }
        
        String name = args[0];
        String tag = args[1];
        
        // 构建描述
        StringBuilder description = new StringBuilder();
        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                description.append(args[i]).append(" ");
            }
        }
        
        // 检查玩家是否已经在公会中
        if (plugin.getGuildManager().getPlayerGuild(player.getUniqueId()) != null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.already-in-guild"));
            return true;
        }
        
        // 检查公会名称和标签是否已存在
        if (plugin.getGuildManager().getGuildByName(name) != null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.name-taken", "name", name));
            return true;
        }
        
        if (plugin.getGuildManager().getGuildByTag(tag) != null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.tag-taken", "tag", tag));
            return true;
        }
        
        // 检查名称和标签长度
        FileConfiguration config = plugin.getConfig();
        int minNameLength = config.getInt("guild.min-name-length", 3);
        int maxNameLength = config.getInt("guild.max-name-length", 16);
        int minTagLength = config.getInt("guild.min-tag-length", 2);
        int maxTagLength = config.getInt("guild.max-tag-length", 5);
        
        if (name.length() < minNameLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.name-too-short", "min", String.valueOf(minNameLength)));
            return true;
        }
        
        if (name.length() > maxNameLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.name-too-long", "max", String.valueOf(maxNameLength)));
            return true;
        }
        
        if (tag.length() < minTagLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.tag-too-short", "min", String.valueOf(minTagLength)));
            return true;
        }
        
        if (tag.length() > maxTagLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.tag-too-long", "max", String.valueOf(maxTagLength)));
            return true;
        }
        
        // 检查描述长度
        int maxDescriptionLength = config.getInt("guild.max-description-length", 100);
        if (description.length() > maxDescriptionLength) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.description-too-long", "max", String.valueOf(maxDescriptionLength)));
            return true;
        }
        
        // TODO: 检查创建费用
        
        // 创建公会
        Guild guild = plugin.getGuildManager().createGuild(player, name, tag, description.toString().trim());
        
        if (guild == null) {
            player.sendMessage("§c创建公会失败，请稍后再试！");
            return true;
        }
        
        // 发送成功消息
        player.sendMessage(plugin.getConfigManager().getMessage("guild.created", "guild", guild.getName()));
        
        return true;
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        // 没有特定的补全
        return new ArrayList<>();
    }
}
