package cn.i7mc.sagaguild.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 消息工具类
 * 用于处理消息的发送和格式化
 */
public class MessageUtil {
    /**
     * 将字符串转换为组件
     * @param message 消息字符串
     * @return 组件
     */
    public static Component toComponent(String message) {
        return LegacyComponentSerializer.legacySection().deserialize(message);
    }

    /**
     * 发送消息给玩家
     * @param player 玩家
     * @param message 消息
     */
    public static void sendMessage(Player player, String message) {
        PlayerUtil.sendMessage(player, toComponent(message));
    }

    /**
     * 发送标题给玩家
     * @param player 玩家
     * @param title 标题
     * @param subtitle 副标题
     * @param fadeIn 淡入时间
     * @param stay 停留时间
     * @param fadeOut 淡出时间
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * 广播消息
     * @param message 消息
     */
    public static void broadcast(String message) {
        Bukkit.broadcast(toComponent(message));
    }

    /**
     * 广播消息给有权限的玩家
     * @param message 消息
     * @param permission 权限
     */
    public static void broadcast(String message, String permission) {
        Bukkit.broadcast(toComponent(message), permission);
    }

    /**
     * 替换颜色代码
     * @param message 消息
     * @return 替换后的消息
     */
    public static String colorize(String message) {
        return message.replace("&", "§");
    }

    /**
     * 替换占位符
     * @param message 消息
     * @param placeholders 占位符数组，格式为 {占位符, 值, 占位符, 值, ...}
     * @return 替换后的消息
     */
    public static String replacePlaceholders(String message, String... placeholders) {
        String result = message;
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                result = result.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return result;
    }
}
