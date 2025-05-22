package cn.i7mc.sagaguild.commands;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.commands.subcommands.*;
import cn.i7mc.sagaguild.commands.subcommands.ListCommand;
import cn.i7mc.sagaguild.commands.subcommands.RelationCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令管理器
 * 负责注册和管理所有命令
 */
public class CommandManager {
    private final SagaGuild plugin;
    private final Map<String, SubCommand> subCommands;

    public CommandManager(SagaGuild plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();

        // 注册子命令
        registerSubCommands();
    }

    /**
     * 注册所有子命令
     */
    private void registerSubCommands() {
        // 注册帮助命令
        registerSubCommand(new HelpCommand(plugin));

        // 注册创建公会命令
        registerSubCommand(new CreateCommand(plugin));

        // 注册公会信息命令
        registerSubCommand(new InfoCommand(plugin));

        // 注册公会列表命令
        registerSubCommand(new ListCommand(plugin));

        // 注册加入公会命令
        registerSubCommand(new JoinCommand(plugin));

        // 注册领地命令
        registerSubCommand(new ClaimCommand(plugin));
        registerSubCommand(new UnclaimCommand(plugin));

        // 注册等级命令
        registerSubCommand(new LevelCommand(plugin));

        // 注册银行命令
        registerSubCommand(new BankCommand(plugin));

        // 注册聊天命令
        registerSubCommand(new ChatCommand(plugin));

        // 注册公会战命令
        registerSubCommand(new WarCommand(plugin));

        // 注册任务命令
        registerSubCommand(new TaskCommand(plugin));

        // 注册活动命令
        registerSubCommand(new ActivityCommand(plugin));

        // 注册联盟命令
        registerSubCommand(new AllyCommand(plugin));

        // 注册公会管理命令
        registerSubCommand(new ManagerCommand(plugin));

        // 注册公会关系命令
        registerSubCommand(new RelationCommand(plugin));

        // 其他子命令将在后续实现
    }

    /**
     * 注册子命令
     * @param subCommand 子命令对象
     */
    private void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
        for (String alias : subCommand.getAliases()) {
            subCommands.put(alias.toLowerCase(), subCommand);
        }
    }

    /**
     * 注册主命令
     */
    public void registerCommands() {
        plugin.getCommand("guild").setExecutor(new GuildCommand(plugin, this));
        plugin.getCommand("guild").setTabCompleter(new GuildCommand(plugin, this));
    }

    /**
     * 获取子命令
     * @param name 子命令名称
     * @return 子命令对象，不存在返回null
     */
    public SubCommand getSubCommand(String name) {
        return subCommands.get(name.toLowerCase());
    }

    /**
     * 获取所有子命令
     * @return 子命令列表
     */
    public List<SubCommand> getSubCommands() {
        return new ArrayList<>(subCommands.values());
    }
}
