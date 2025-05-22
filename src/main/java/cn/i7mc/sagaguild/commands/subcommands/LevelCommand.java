package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 公会等级命令
 * 显示公会等级和经验信息
 */
public class LevelCommand implements SubCommand {
    private final SagaGuild plugin;
    
    public LevelCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "level";
    }
    
    @Override
    public String getDescription() {
        return "显示公会等级和经验信息";
    }
    
    @Override
    public String getSyntax() {
        return "/guild level [公会名]";
    }
    
    @Override
    public String[] getAliases() {
        return new String[]{"lv", "exp"};
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        Guild guild;
        
        // 根据参数获取公会
        if (args.length == 0) {
            // 显示玩家所在公会
            guild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
            if (guild == null) {
                player.sendMessage(plugin.getConfigManager().getMessage("guild.not-in-guild"));
                return true;
            }
        } else {
            // 显示指定公会
            String guildName = args[0];
            guild = plugin.getGuildManager().getGuildByName(guildName);
            if (guild == null) {
                player.sendMessage("§c找不到名为 §7" + guildName + " §c的公会！");
                return true;
            }
        }
        
        // 获取公会等级信息
        int level = guild.getLevel();
        int experience = guild.getExperience();
        int nextLevelExp = guild.getNextLevelExperience();
        
        // 显示等级信息
        player.sendMessage("§8§m-----§r §b公会等级信息 §8§m-----");
        player.sendMessage("§7公会: §f" + guild.getName());
        player.sendMessage("§7当前等级: §f" + level);
        player.sendMessage("§7当前经验: §f" + experience + "§7/§f" + nextLevelExp);
        
        // 计算进度条
        int progressBarLength = 20;
        int progress = (int) ((double) experience / nextLevelExp * progressBarLength);
        StringBuilder progressBar = new StringBuilder("§7[");
        
        for (int i = 0; i < progressBarLength; i++) {
            if (i < progress) {
                progressBar.append("§a|");
            } else {
                progressBar.append("§8|");
            }
        }
        
        progressBar.append("§7]");
        player.sendMessage("§7进度: " + progressBar.toString());
        
        // 显示等级福利
        player.sendMessage("§7等级福利:");
        player.sendMessage("§7- 领地上限: §f" + (plugin.getConfig().getInt("land.max-claims", 10) + level));
        player.sendMessage("§7- 银行容量: §f" + (plugin.getConfig().getInt("bank.initial-capacity", 10000) + level * plugin.getConfig().getInt("bank.capacity-increase", 5000)));
        
        player.sendMessage("§8§m-----------------------");
        
        return true;
    }
    
    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String arg = args[0].toLowerCase();
            for (Guild guild : plugin.getGuildManager().getAllGuilds()) {
                if (guild.getName().toLowerCase().startsWith(arg)) {
                    completions.add(guild.getName());
                }
            }
        }
        
        return completions;
    }
}
