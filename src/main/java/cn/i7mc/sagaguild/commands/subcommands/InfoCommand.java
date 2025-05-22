package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import cn.i7mc.sagaguild.data.models.GuildMember;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 公会信息命令
 * 显示公会的详细信息
 */
public class InfoCommand implements SubCommand {
    private final SagaGuild plugin;

    public InfoCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "显示公会信息";
    }

    @Override
    public String getSyntax() {
        return "/guild info [公会名]";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"i", "show"};
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

        // 获取公会信息
        String name = guild.getName();
        String tag = guild.getTag();
        String description = guild.getDescription();
        String announcement = guild.getAnnouncement();
        int level = guild.getLevel();
        int experience = guild.getExperience();
        int nextLevelExp = guild.getNextLevelExperience();
        boolean isPublic = guild.isPublic();

        // 获取会长信息
        OfflinePlayer owner = Bukkit.getOfflinePlayer(guild.getOwnerUuid());
        String ownerName = owner.getName() != null ? owner.getName() : "未知";

        // 获取成员信息
        List<GuildMember> members = plugin.getGuildManager().getGuildMembers(guild.getId());
        int totalMembers = members.size();
        int onlineMembers = 0;

        for (GuildMember member : members) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member.getPlayerUuid());
            if (offlinePlayer.isOnline()) {
                onlineMembers++;
            }
        }

        // 格式化创建时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createdAt = sdf.format(guild.getCreatedAt());

        // 显示公会信息
        player.sendMessage(plugin.getConfigManager().getMessage("guild.info-header",
                "guild", name, "tag", tag));

        if (!description.isEmpty()) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.info-description",
                    "description", description));
        }

        player.sendMessage("§7会长: §f" + ownerName);
        player.sendMessage("§7成员: §f" + onlineMembers + "§7/§f" + totalMembers);
        player.sendMessage("§7等级: §f" + level + " §7(§f" + experience + "§7/§f" + nextLevelExp + "§7)");
        player.sendMessage("§7公开: §f" + (isPublic ? "是" : "否"));
        player.sendMessage("§7创建时间: §f" + createdAt);

        if (!announcement.isEmpty()) {
            player.sendMessage("§7公告: §f" + announcement);
        }

        // 显示战争状态
        player.sendMessage("§c战争状态:");
        cn.i7mc.sagaguild.data.models.GuildWar war = plugin.getWarManager().getActiveWar(guild.getId());
        if (war != null) {
            int opponentId = war.getOpponentId(guild.getId());
            Guild opponent = plugin.getGuildManager().getGuildById(opponentId);
            if (opponent != null) {
                String statusText;
                switch (war.getStatus()) {
                    case PENDING:
                        statusText = "§e等待中";
                        break;
                    case PREPARING:
                        statusText = "§6准备中";
                        break;
                    case ONGOING:
                        statusText = "§c进行中";
                        break;
                    default:
                        statusText = "§7未知";
                        break;
                }
                player.sendMessage("  §7状态: " + statusText);
                player.sendMessage("  §7对手公会: §f" + opponent.getName());
            }
        } else {
            player.sendMessage("  §7无");
        }

        // 显示联盟信息
        player.sendMessage("§a联盟关系:");
        List<Integer> alliances = plugin.getAllianceManager().getGuildAlliances(guild.getId());
        if (!alliances.isEmpty()) {
            int count = 0;
            for (int allyId : alliances) {
                Guild ally = plugin.getGuildManager().getGuildById(allyId);
                if (ally != null) {
                    player.sendMessage("  §7- §f" + ally.getName());
                    count++;
                    if (count >= 5) {
                        if (alliances.size() > 5) {
                            player.sendMessage("  §7...以及其他 " + (alliances.size() - 5) + " 个公会");
                        }
                        break;
                    }
                }
            }
        } else {
            player.sendMessage("  §7无");
        }

        player.sendMessage(plugin.getConfigManager().getMessage("guild.info-footer"));

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
