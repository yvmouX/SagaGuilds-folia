package cn.i7mc.sagaguild.utils;

import cn.i7mc.sagaguild.SagaGuild;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 玩家工具类
 * 用于处理玩家相关的兼容性问题
 */
public class PlayerUtil {
    private static final Logger LOGGER = Bukkit.getLogger();
    private static boolean useAdventure = true;
    private static boolean initialized = false;
    private static boolean debug = false;
    private static SagaGuild plugin = null;
    private static Method legacySendMessageMethod = null;
    private static Method adventureSendMessageMethod = null;

    /**
     * 设置插件实例
     * @param plugin 插件实例
     */
    public static void setPlugin(SagaGuild plugin) {
        PlayerUtil.plugin = plugin;
        if (plugin != null) {
            debug = plugin.getConfig().getBoolean("debug", false);
        }
    }

    /**
     * 调试日志
     * @param message 消息
     */
    private static void debug(String message) {
        if (debug) {
            LOGGER.info("[PlayerUtil] " + message);
        }
    }

    /**
     * 初始化工具类
     */
    private static void initialize() {
        if (initialized) {
            return;
        }

        try {
            // 检测服务器类型
            String serverType = Bukkit.getServer().getClass().getPackage().getName();
            debug("检测到服务器类型: " + serverType);

            // 尝试获取使用Component的sendMessage方法
            try {
                adventureSendMessageMethod = Player.class.getMethod("sendMessage", Component.class);
                useAdventure = true;
                debug("使用Adventure API发送消息");
            } catch (NoSuchMethodException e) {
                debug("Adventure API方法不可用: " + e.getMessage());
                useAdventure = false;
            }

            if (!useAdventure) {
                debug("尝试使用传统方法发送消息");
                // 尝试获取传统的sendMessage方法
                legacySendMessageMethod = Player.class.getMethod("sendMessage", String.class);
                debug("成功获取传统API方法");
            }
        } catch (NoSuchMethodException ex) {
            LOGGER.log(Level.SEVERE, "无法找到发送消息的方法，消息功能将不可用", ex);
            ex.printStackTrace();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "初始化玩家工具类时发生错误", ex);
            ex.printStackTrace();
        }

        initialized = true;
    }

    /**
     * 发送消息给玩家
     * @param player 玩家
     * @param component 消息组件
     */
    public static void sendMessage(Player player, Component component) {
        if (!initialized) {
            initialize();
        }

        if (player == null) {
            return;
        }

        try {
            // 转换为传统格式
            String legacyMessage = LegacyComponentSerializer.legacySection().serialize(component);

            // 尝试直接发送
            try {
                player.sendMessage(legacyMessage);
                debug("使用直接方法发送消息成功: " + legacyMessage);
            } catch (Exception directEx) {
                debug("直接方法发送消息失败，尝试反射方法");

                // 如果直接方法失败，尝试使用反射
                if (useAdventure && adventureSendMessageMethod != null) {
                    // 使用Adventure API
                    adventureSendMessageMethod.invoke(player, component);
                    debug("使用Adventure API反射方法发送消息成功");
                } else if (legacySendMessageMethod != null) {
                    // 使用传统反射方法
                    legacySendMessageMethod.invoke(player, legacyMessage);
                    debug("使用传统反射方法发送消息成功");
                } else {
                    throw new RuntimeException("没有可用的方法发送消息");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "发送消息失败", e);
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给玩家
     * @param player 玩家
     * @param message 消息字符串
     */
    public static void sendMessage(Player player, String message) {
        sendMessage(player, LegacyComponentSerializer.legacySection().deserialize(message));
    }
}
