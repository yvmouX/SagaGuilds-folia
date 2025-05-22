package cn.i7mc.sagaguild.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 团队工具类
 * 用于处理不同服务端API的兼容性问题
 */
public class TeamUtil {
    private static final Logger LOGGER = Bukkit.getLogger();
    private static boolean useAdventure = true;
    private static boolean initialized = false;
    private static Method legacyPrefixMethod = null;

    /**
     * 初始化工具类
     */
    private static void initialize() {
        if (initialized) {
            return;
        }

        try {
            // 尝试获取Team.prefix(Component)方法
            Team.class.getMethod("prefix", Component.class);
            useAdventure = true;
        } catch (NoSuchMethodException e) {
            useAdventure = false;
            
            // 尝试获取传统的Team.setPrefix(String)方法
            try {
                legacyPrefixMethod = Team.class.getMethod("setPrefix", String.class);
            } catch (NoSuchMethodException ex) {
            }
        }
        
        initialized = true;
    }

    /**
     * 设置团队前缀
     * @param team 团队对象
     * @param prefix 前缀组件
     */
    public static void setPrefix(Team team, Component prefix) {
        if (!initialized) {
            initialize();
        }
        
        if (team == null) {
            return;
        }
        
        try {
            if (useAdventure) {
                // 使用Adventure API
                team.prefix(prefix);
            } else if (legacyPrefixMethod != null) {
                // 使用传统方法
                String legacyPrefix = LegacyComponentSerializer.legacySection().serialize(prefix);
                legacyPrefixMethod.invoke(team, legacyPrefix);
            }
        } catch (Exception e) {
        }
    }
}
