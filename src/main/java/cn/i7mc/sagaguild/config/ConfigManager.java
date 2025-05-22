package cn.i7mc.sagaguild.config;

import cn.i7mc.sagaguild.SagaGuild;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 配置管理器
 * 负责加载和管理插件的配置文件
 */
public class ConfigManager {
    private final SagaGuild plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    public ConfigManager(SagaGuild plugin) {
        this.plugin = plugin;
    }

    /**
     * 加载所有配置文件
     */
    public void loadConfigs() {
        // 加载主配置文件
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        // 加载消息配置文件
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        // 确保配置文件是最新的
        updateConfigs();
    }

    /**
     * 更新配置文件，添加缺失的配置项
     */
    private void updateConfigs() {
        updateConfig("config.yml", config);
        updateConfig("messages.yml", messages);
    }

    /**
     * 更新指定配置文件
     * @param fileName 配置文件名
     * @param config 配置对象
     */
    private void updateConfig(String fileName, FileConfiguration config) {
        try {
            // 加载默认配置
            InputStreamReader reader = new InputStreamReader(
                    plugin.getResource(fileName), StandardCharsets.UTF_8);
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);

            // 检查并添加缺失的配置项
            boolean updated = false;
            for (String key : defaultConfig.getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defaultConfig.get(key));
                    updated = true;
                }
            }

            // 如果有更新，保存配置
            if (updated) {
                config.save(new File(plugin.getDataFolder(), fileName));
            }
        } catch (IOException e) {
            plugin.getLogger().severe("无法更新配置文件 " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * 重新加载所有配置
     */
    public void reloadConfigs() {
        plugin.reloadConfig();
        config = plugin.getConfig();

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * 获取主配置
     * @return 主配置对象
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * 获取消息配置
     * @return 消息配置对象
     */
    public FileConfiguration getMessages() {
        return messages;
    }

    /**
     * 获取格式化的消息
     * @param path 消息路径
     * @return 格式化后的消息
     */
    public String getMessage(String path) {
        String message = messages.getString(path);
        if (message == null) {
            // 记录调试信息
            if (isDebugEnabled()) {
                plugin.getLogger().warning("消息未找到: " + path);
                plugin.getLogger().warning("当前加载的消息键: " + messages.getKeys(true).size() + " 个");
                plugin.getLogger().warning("检查 messages.yml 文件是否存在并且包含该消息键");
            }
            return "消息未找到: " + path;
        }

        // 替换前缀
        String prefix = messages.getString("prefix", "&8[&bSagaGuild&8] &f");
        message = message.replace("{prefix}", prefix);

        // 替换颜色代码
        return message.replace("&", "§");
    }

    /**
     * 获取格式化的消息，并替换占位符
     * @param path 消息路径
     * @param placeholders 占位符数组，格式为 {占位符, 值, 占位符, 值, ...}
     * @return 格式化后的消息
     */
    public String getMessage(String path, String... placeholders) {
        String message = getMessage(path);

        // 替换占位符
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }

        return message;
    }

    /**
     * 检查是否启用调试模式
     * @return 是否启用调试模式
     */
    public boolean isDebugEnabled() {
        return config.getBoolean("debug", false);
    }
}
