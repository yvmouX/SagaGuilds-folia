package cn.i7mc.sagaguild.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 物品栏工具类
 * 用于处理不同服务端API的兼容性问题
 */
public class InventoryUtil {
    private static final Logger LOGGER = Bukkit.getLogger();
    private static boolean useAdventure = true;
    private static boolean initialized = false;
    private static boolean debug = false;
    private static Method legacyCreateInventoryMethod = null;
    private static Method adventureCreateInventoryMethod = null;

    /**
     * 设置调试模式
     * @param debug 是否启用调试
     */
    public static void setDebug(boolean debug) {
        InventoryUtil.debug = debug;
    }

    /**
     * 初始化工具类
     */
    private static void initialize() {
        if (initialized) {
            return;
        }

        try {
            // 尝试获取使用Component的createInventory方法
            adventureCreateInventoryMethod = Bukkit.class.getMethod("createInventory", InventoryHolder.class, int.class, Component.class);
            useAdventure = true;
        } catch (NoSuchMethodException e) {
            useAdventure = false;

            // 尝试获取传统的createInventory方法
            try {
                legacyCreateInventoryMethod = Bukkit.class.getMethod("createInventory", InventoryHolder.class, int.class, String.class);
            } catch (NoSuchMethodException ex) {
            }
        }

        initialized = true;
    }

    /**
     * 创建物品栏
     * @param holder 物品栏持有者
     * @param size 物品栏大小
     * @param title 物品栏标题
     * @return 创建的物品栏
     */
    public static Inventory createInventory(InventoryHolder holder, int size, Component title) {
        if (!initialized) {
            initialize();
        }

        try {
            if (useAdventure) {
                // 使用Adventure API
                return (Inventory) adventureCreateInventoryMethod.invoke(null, holder, size, title);
            } else if (legacyCreateInventoryMethod != null) {
                // 使用传统方法，将Component转换为String
                // 使用LegacyComponentSerializer将Component转换为传统格式字符串
                String legacyTitle = LegacyComponentSerializer.legacySection().serialize(title);
                if (debug) {
                }
                return (Inventory) legacyCreateInventoryMethod.invoke(null, holder, size, legacyTitle);
            }
        } catch (Exception e) {
        }

        // 如果上述方法都失败，尝试使用无标题的方法
        return Bukkit.createInventory(holder, size);
    }
}
