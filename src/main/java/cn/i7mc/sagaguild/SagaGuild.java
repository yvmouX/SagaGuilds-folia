package cn.i7mc.sagaguild;

import cn.i7mc.sagaguild.commands.CommandManager;
import cn.i7mc.sagaguild.config.ConfigManager;
import cn.i7mc.sagaguild.data.DatabaseManager;
import cn.i7mc.sagaguild.data.dao.JoinRequestDAO;
import cn.i7mc.sagaguild.gui.GUIManager;
import cn.i7mc.sagaguild.listeners.ActivityListener;
import cn.i7mc.sagaguild.listeners.ChatListener;
import cn.i7mc.sagaguild.listeners.ExperienceListener;
import cn.i7mc.sagaguild.listeners.LandListener;
import cn.i7mc.sagaguild.listeners.PlayerListener;
import cn.i7mc.sagaguild.listeners.TaskListener;
import cn.i7mc.sagaguild.listeners.WarListener;
import cn.i7mc.sagaguild.managers.*;
import cn.i7mc.sagaguild.utils.InventoryUtil;
import cn.i7mc.sagaguild.utils.ItemUtil;
import cn.i7mc.sagaguild.utils.PlayerUtil;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SagaGuild 插件主类
 * 负责插件的初始化、加载和卸载，管理所有模块的生命周期
 */
public class SagaGuild extends JavaPlugin {
    // 单例模式
    private static SagaGuild instance;

    // 各管理器实例
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private GuildManager guildManager;
    private MemberManager memberManager;
    private LandManager landManager;
    private BankManager bankManager;
    private WarManager warManager;
    private TaskManager taskManager;
    private ChatManager chatManager;
    private ActivityManager activityManager;
    private AllianceManager allianceManager;
    private CommandManager commandManager;
    private GUIManager guiManager;

    // DAO实例
    private JoinRequestDAO joinRequestDAO;

    @Override
    public void onEnable() {
        // 初始化单例
        instance = this;

        // 初始化配置
        configManager = new ConfigManager(this);
        configManager.loadConfigs();

        // 初始化工具类
        boolean debug = getConfig().getBoolean("debug", false);
        ItemUtil.setPlugin(this);
        PlayerUtil.setPlugin(this);
        InventoryUtil.setDebug(debug);

        // 初始化数据库
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        // 初始化DAO
        joinRequestDAO = new JoinRequestDAO(this);

        // 初始化管理器
        initializeManagers();

        // 注册命令
        commandManager = new CommandManager(this);
        commandManager.registerCommands();

        // 注册监听器
        registerListeners();

        // 初始化GUI系统
        guiManager = new GUIManager(this);

        getLogger().info("SagaGuild 插件已成功加载！");
    }

    @Override
    public void onDisable() {
        // 保存数据
        if (databaseManager != null) {
            databaseManager.close();
        }

        // 卸载资源
        getLogger().info("SagaGuild 插件已成功卸载！");
    }

    /**
     * 初始化所有管理器
     */
    private void initializeManagers() {
        guildManager = new GuildManager(this);
        memberManager = new MemberManager(this);
        landManager = new LandManager(this);
        bankManager = new BankManager(this);
        warManager = new WarManager(this);
        taskManager = new TaskManager(this);
        chatManager = new ChatManager(this);
        activityManager = new ActivityManager(this);
        allianceManager = new AllianceManager(this);
    }

    /**
     * 注册所有事件监听器
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new LandListener(this), this);
        getServer().getPluginManager().registerEvents(new ExperienceListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new WarListener(this), this);
        getServer().getPluginManager().registerEvents(new TaskListener(this), this);
        getServer().getPluginManager().registerEvents(new ActivityListener(this), this);
    }

    /**
     * 获取单例实例
     * @return SagaGuild实例
     */
    public static SagaGuild getInstance() {
        return instance;
    }

    // 获取各管理器的方法
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public MemberManager getMemberManager() {
        return memberManager;
    }

    public LandManager getLandManager() {
        return landManager;
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public WarManager getWarManager() {
        return warManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public AllianceManager getAllianceManager() {
        return allianceManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public JoinRequestDAO getJoinRequestDAO() {
        return joinRequestDAO;
    }
}
