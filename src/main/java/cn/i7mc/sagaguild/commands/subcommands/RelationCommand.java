package cn.i7mc.sagaguild.commands.subcommands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.SubCommand;
import cn.i7mc.sagaguild.data.models.Guild;
import cn.i7mc.sagaguild.data.models.GuildMember;
import cn.i7mc.sagaguild.data.models.GuildWar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 公会关系命令
 * 用于管理公会之间的关系
 */
public class RelationCommand implements SubCommand {
    private final SagaGuild plugin;

    public RelationCommand(SagaGuild plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "relation";
    }

    @Override
    public String getDescription() {
        return "管理公会关系";
    }

    @Override
    public String getSyntax() {
        return "/guild relation [公会名]";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"rel", "relations"};
    }

    @Override
    public boolean execute(Player player, String[] args) {
        // 检查玩家是否在公会中
        Guild playerGuild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
        if (playerGuild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-in-guild"));
            return true;
        }

        // 检查玩家是否有权限管理公会关系
        GuildMember member = plugin.getGuildManager().getMemberByUuid(player.getUniqueId());
        if (member == null || (!member.isOwner() && !member.isAdmin())) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.no-permission"));
            return true;
        }

        // 如果没有指定子命令，打开公会关系管理GUI
        if (args.length < 1) {
            plugin.getGuiManager().openGuildRelationManageGUI(player, playerGuild, 1);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "list":
                // 显示关系列表
                showRelationList(player, playerGuild);
                break;

            case "ally":
                // 申请结盟
                if (args.length < 2) {
                    player.sendMessage("§c用法: /guild relation ally <公会名>");
                    return true;
                }
                handleAllyRequest(player, playerGuild, args[1]);
                break;

            case "war":
                // 宣战
                if (args.length < 2) {
                    player.sendMessage("§c用法: /guild relation war <公会名>");
                    return true;
                }
                handleWarDeclaration(player, playerGuild, args[1]);
                break;

            case "break":
                // 解除联盟
                if (args.length < 2) {
                    player.sendMessage("§c用法: /guild relation break <公会名>");
                    return true;
                }
                handleBreakAlliance(player, playerGuild, args[1]);
                break;

            case "ceasefire":
                // 请求停战
                if (args.length < 2) {
                    player.sendMessage("§c用法: /guild relation ceasefire <公会名>");
                    return true;
                }
                handleCeasefireRequest(player, playerGuild, args[1]);
                break;

            case "gui":
                // 打开关系GUI
                if (args.length < 2) {
                    plugin.getGuiManager().openGuildRelationManageGUI(player, playerGuild, 1);
                } else {
                    // 获取目标公会
                    String targetGuildName = args[1];
                    Guild targetGuild = plugin.getGuildManager().getGuildByName(targetGuildName);
                    if (targetGuild == null) {
                        player.sendMessage(plugin.getConfigManager().getMessage("guild.not-found", "guild", targetGuildName));
                        return true;
                    }

                    // 检查是否是自己的公会
                    if (playerGuild.getId() == targetGuild.getId()) {
                        player.sendMessage(plugin.getConfigManager().getMessage("guild.cannot-set-relation-self"));
                        return true;
                    }

                    // 打开公会关系设置GUI
                    plugin.getGuiManager().openGuildRelationGUI(player, targetGuild);
                }
                break;

            default:
                // 如果第一个参数不是子命令，而是公会名称
                Guild targetGuild = plugin.getGuildManager().getGuildByName(args[0]);
                if (targetGuild == null) {
                    player.sendMessage("§c未知的子命令或公会名称！用法: " + getSyntax());
                    return true;
                }

                // 检查是否是自己的公会
                if (playerGuild.getId() == targetGuild.getId()) {
                    player.sendMessage(plugin.getConfigManager().getMessage("guild.cannot-set-relation-self"));
                    return true;
                }

                // 打开公会关系设置GUI
                plugin.getGuiManager().openGuildRelationGUI(player, targetGuild);
                break;
        }

        return true;
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        List<String> completions = new ArrayList<>();

        // 第一个参数，提供子命令补全
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> subCommands = Arrays.asList("list", "ally", "war", "break", "ceasefire", "gui");

            for (String subCommand : subCommands) {
                if (subCommand.startsWith(input)) {
                    completions.add(subCommand);
                }
            }

            // 也可以直接输入公会名称
            for (Guild guild : plugin.getGuildManager().getAllGuilds()) {
                if (guild.getName().toLowerCase().startsWith(input)) {
                    completions.add(guild.getName());
                }
            }
        }
        // 第二个参数，根据子命令提供不同的补全
        else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String input = args[1].toLowerCase();

            // 对于需要公会名称的子命令
            if (subCommand.equals("ally") || subCommand.equals("war") ||
                subCommand.equals("break") || subCommand.equals("ceasefire") ||
                subCommand.equals("gui")) {

                Guild playerGuild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());

                for (Guild guild : plugin.getGuildManager().getAllGuilds()) {
                    // 排除玩家自己的公会
                    if (playerGuild != null && guild.getId() == playerGuild.getId()) {
                        continue;
                    }

                    if (guild.getName().toLowerCase().startsWith(input)) {
                        completions.add(guild.getName());
                    }
                }
            }
        }

        return completions;
    }

    /**
     * 显示公会关系列表
     * @param player 玩家
     * @param guild 公会
     */
    private void showRelationList(Player player, Guild guild) {
        player.sendMessage("§8§m-----§r §b公会关系列表 §8§m-----");

        // 显示联盟
        List<Integer> allies = plugin.getAllianceManager().getGuildAlliances(guild.getId());
        if (allies.isEmpty()) {
            player.sendMessage("§7联盟: §f无");
        } else {
            player.sendMessage("§7联盟:");
            for (int allyId : allies) {
                Guild allyGuild = plugin.getGuildManager().getGuildById(allyId);
                if (allyGuild != null) {
                    player.sendMessage("  §b" + allyGuild.getName() + " §8[§7" + allyGuild.getTag() + "§8]");
                }
            }
        }

        // 显示战争
        GuildWar war = plugin.getWarManager().getActiveWar(guild.getId());
        if (war == null) {
            player.sendMessage("§7战争: §f无");
        } else {
            int opponentId = war.getOpponentId(guild.getId());
            Guild opponent = plugin.getGuildManager().getGuildById(opponentId);
            if (opponent != null) {
                player.sendMessage("§7战争: §c" + opponent.getName() + " §8[§7" + opponent.getTag() + "§8]");
            }
        }

        player.sendMessage("§8§m-----------------------");
    }

    /**
     * 处理结盟请求
     * @param player 玩家
     * @param guild 玩家公会
     * @param targetGuildName 目标公会名称
     */
    private void handleAllyRequest(Player player, Guild guild, String targetGuildName) {
        // 获取目标公会
        Guild targetGuild = plugin.getGuildManager().getGuildByName(targetGuildName);
        if (targetGuild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-found", "guild", targetGuildName));
            return;
        }

        // 检查是否是自己的公会
        if (guild.getId() == targetGuild.getId()) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.cannot-set-relation-self"));
            return;
        }

        // 检查是否已经是联盟
        if (plugin.getAllianceManager().areGuildsAllied(guild.getId(), targetGuild.getId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("alliance.already-allied"));
            return;
        }

        // 检查是否处于战争状态
        GuildWar war = plugin.getWarManager().getActiveWarBetweenGuilds(guild.getId(), targetGuild.getId());
        if (war != null) {
            player.sendMessage(plugin.getConfigManager().getMessage("war.cannot-ally-during-war"));
            return;
        }

        // 发送结盟请求
        boolean success = plugin.getAllianceManager().sendAllianceRequest(guild.getId(), targetGuild.getId());
        if (success) {
            player.sendMessage(plugin.getConfigManager().getMessage("alliance.request-sent", "guild", targetGuild.getName()));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("alliance.alliance-request-failed"));
        }
    }

    /**
     * 处理宣战
     * @param player 玩家
     * @param guild 玩家公会
     * @param targetGuildName 目标公会名称
     */
    private void handleWarDeclaration(Player player, Guild guild, String targetGuildName) {
        // 获取目标公会
        Guild targetGuild = plugin.getGuildManager().getGuildByName(targetGuildName);
        if (targetGuild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-found", "guild", targetGuildName));
            return;
        }

        // 检查是否是自己的公会
        if (guild.getId() == targetGuild.getId()) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.cannot-set-relation-self"));
            return;
        }

        // 检查是否已经处于战争状态
        if (plugin.getWarManager().areGuildsAtWar(guild.getId(), targetGuild.getId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.already-at-war"));
            return;
        }

        // 检查是否是联盟
        if (plugin.getAllianceManager().areGuildsAllied(guild.getId(), targetGuild.getId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.cannot-war-ally"));
            return;
        }

        // 发起战争
        boolean success = plugin.getWarManager().inviteToWar(player, targetGuild.getName());
        if (!success) {
            player.sendMessage(plugin.getConfigManager().getMessage("war.war-declaration-failed"));
        }
    }

    /**
     * 处理解除联盟
     * @param player 玩家
     * @param guild 玩家公会
     * @param targetGuildName 目标公会名称
     */
    private void handleBreakAlliance(Player player, Guild guild, String targetGuildName) {
        // 获取目标公会
        Guild targetGuild = plugin.getGuildManager().getGuildByName(targetGuildName);
        if (targetGuild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-found", "guild", targetGuildName));
            return;
        }

        // 检查是否是自己的公会
        if (guild.getId() == targetGuild.getId()) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.cannot-set-relation-self"));
            return;
        }

        // 检查是否是联盟
        if (!plugin.getAllianceManager().areGuildsAllied(guild.getId(), targetGuild.getId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("alliance.not-allied"));
            return;
        }

        // 解除联盟
        boolean success = plugin.getAllianceManager().breakAlliance(player, targetGuild.getId());
        if (success) {
            player.sendMessage(plugin.getConfigManager().getMessage("alliance.broken", "guild", targetGuild.getName()));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("alliance.break-failed"));
        }
    }

    /**
     * 处理停战请求
     * @param player 玩家
     * @param guild 玩家公会
     * @param targetGuildName 目标公会名称
     */
    private void handleCeasefireRequest(Player player, Guild guild, String targetGuildName) {
        // 获取目标公会
        Guild targetGuild = plugin.getGuildManager().getGuildByName(targetGuildName);
        if (targetGuild == null) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-found", "guild", targetGuildName));
            return;
        }

        // 检查是否是自己的公会
        if (guild.getId() == targetGuild.getId()) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.cannot-set-relation-self"));
            return;
        }

        // 检查是否处于战争状态
        if (!plugin.getWarManager().areGuildsAtWar(guild.getId(), targetGuild.getId())) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.not-at-war"));
            return;
        }

        // 请求停战
        boolean success = plugin.getWarManager().requestCeasefire(player, targetGuild.getId());
        if (success) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.ceasefire-requested", "guild", targetGuild.getName()));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.ceasefire-request-failed"));
        }
    }
}
