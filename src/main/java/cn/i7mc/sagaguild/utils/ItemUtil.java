package cn.i7mc.sagaguild.utils;

import cn.i7mc.sagaguild.SagaGuild;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 物品工具类
 * 用于处理不同服务端API的兼容性问题
 */
public class ItemUtil {
    private static final Logger LOGGER = Bukkit.getLogger();
    private static boolean useAdventure = true;
    private static boolean initialized = false;
    private static boolean debug = false;
    private static SagaGuild plugin = null;
    private static Method legacyDisplayNameMethod = null;
    private static Method legacyLoreMethod = null;
    private static Method legacyGetDisplayNameMethod = null;
    private static Method legacyGetLoreMethod = null;
    private static Method adventureDisplayNameMethod = null;
    private static Method adventureLoreMethod = null;
    private static Method adventureGetDisplayNameMethod = null;
    private static Method adventureGetLoreMethod = null;

    /**
     * 设置插件实例
     * @param plugin 插件实例
     */
    public static void setPlugin(SagaGuild plugin) {
        ItemUtil.plugin = plugin;
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
            LOGGER.info("[ItemUtil] " + message);
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

            // 尝试获取使用Component的displayName方法
            try {
                adventureDisplayNameMethod = ItemMeta.class.getMethod("displayName", Component.class);
                // 尝试获取使用Component的lore方法
                adventureLoreMethod = ItemMeta.class.getMethod("lore", List.class);
                // 尝试获取使用Component的getDisplayName方法
                adventureGetDisplayNameMethod = ItemMeta.class.getMethod("displayName");
                // 尝试获取使用Component的getLore方法
                adventureGetLoreMethod = ItemMeta.class.getMethod("lore");
                useAdventure = true;
                debug("使用Adventure API设置物品名称和描述");
            } catch (NoSuchMethodException e) {
                debug("Adventure API方法不可用: " + e.getMessage());
                useAdventure = false;
            }

            if (!useAdventure) {
                debug("尝试使用传统方法设置物品名称和描述");
                // 尝试获取传统的setDisplayName方法
                legacyDisplayNameMethod = ItemMeta.class.getMethod("setDisplayName", String.class);
                legacyLoreMethod = ItemMeta.class.getMethod("setLore", List.class);
                legacyGetDisplayNameMethod = ItemMeta.class.getMethod("getDisplayName");
                legacyGetLoreMethod = ItemMeta.class.getMethod("getLore");
                debug("成功获取传统API方法");
            }
        } catch (NoSuchMethodException ex) {
            LOGGER.log(Level.SEVERE, "无法找到设置物品名称和描述的方法，物品功能将不可用", ex);
            ex.printStackTrace();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "初始化物品工具类时发生错误", ex);
            ex.printStackTrace();
        }

        initialized = true;
    }

    /**
     * 设置物品显示名称
     * @param meta 物品元数据
     * @param displayName 显示名称
     */
    public static void setDisplayName(ItemMeta meta, Component displayName) {
        if (!initialized) {
            initialize();
        }

        if (meta == null) {
            return;
        }

        try {
            // 转换为传统格式
            String legacyName = LegacyComponentSerializer.legacySection().serialize(displayName);

            // 尝试直接设置
            try {
                meta.setDisplayName(legacyName);
                debug("使用直接方法设置物品名称成功: " + legacyName);
            } catch (Exception directEx) {
                debug("直接方法设置物品名称失败，尝试反射方法");

                // 如果直接方法失败，尝试使用反射
                if (useAdventure && adventureDisplayNameMethod != null) {
                    // 使用Adventure API
                    adventureDisplayNameMethod.invoke(meta, displayName);
                    debug("使用Adventure API反射方法设置物品名称成功");
                } else if (legacyDisplayNameMethod != null) {
                    // 使用传统反射方法
                    legacyDisplayNameMethod.invoke(meta, legacyName);
                    debug("使用传统反射方法设置物品名称成功");
                } else {
                    throw new RuntimeException("没有可用的方法设置物品名称");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "设置物品显示名称失败", e);
            e.printStackTrace();
        }
    }

    /**
     * 设置物品描述
     * @param meta 物品元数据
     * @param lore 描述
     */
    public static void setLore(ItemMeta meta, List<Component> lore) {
        if (!initialized) {
            initialize();
        }

        if (meta == null) {
            return;
        }

        try {
            // 直接使用传统方法，确保兼容性
            List<String> legacyLore = new ArrayList<>();
            for (Component line : lore) {
                legacyLore.add(LegacyComponentSerializer.legacySection().serialize(line));
            }

            // 尝试使用传统方法设置lore
            try {
                meta.setLore(legacyLore);
                debug("使用直接方法设置物品描述成功");
            } catch (Exception directEx) {
                debug("直接方法设置物品描述失败，尝试反射方法");

                // 如果直接方法失败，尝试使用反射
                if (useAdventure && adventureLoreMethod != null) {
                    // 使用Adventure API
                    adventureLoreMethod.invoke(meta, lore);
                    debug("使用Adventure API反射方法设置物品描述成功");
                } else if (legacyLoreMethod != null) {
                    // 使用传统反射方法
                    legacyLoreMethod.invoke(meta, legacyLore);
                    debug("使用传统反射方法设置物品描述成功");
                } else {
                    throw new RuntimeException("没有可用的方法设置物品描述");
                }
            }

            // 调试输出
            debug("设置物品描述: " + lore.size() + " 行");
            if (debug) {
                for (int i = 0; i < lore.size(); i++) {
                    Component line = lore.get(i);
                    debug("  " + i + ": " + LegacyComponentSerializer.legacySection().serialize(line));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "设置物品描述失败", e);
            e.printStackTrace(); // 打印详细堆栈信息
        }
    }

    /**
     * 获取物品显示名称
     * @param meta 物品元数据
     * @return 显示名称
     */
    public static String getDisplayName(ItemMeta meta) {
        if (!initialized) {
            initialize();
        }

        if (meta == null) {
            return "";
        }

        try {
            if (useAdventure) {
                // 使用Adventure API
                Object result = adventureGetDisplayNameMethod.invoke(meta);
                if (result != null) {
                    Component component = (Component) result;
                    return LegacyComponentSerializer.legacySection().serialize(component);
                }
            } else if (legacyGetDisplayNameMethod != null) {
                // 使用传统方法
                Object result = legacyGetDisplayNameMethod.invoke(meta);
                if (result != null) {
                    return (String) result;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "获取物品显示名称失败", e);
        }

        return "";
    }

    /**
     * 获取物品描述
     * @param meta 物品元数据
     * @return 描述
     */
    public static List<String> getLore(ItemMeta meta) {
        if (!initialized) {
            initialize();
        }

        if (meta == null) {
            return new ArrayList<>();
        }

        try {
            if (useAdventure) {
                // 使用Adventure API
                Object result = adventureGetLoreMethod.invoke(meta);
                if (result != null) {
                    @SuppressWarnings("unchecked")
                    List<Component> lore = (List<Component>) result;
                    List<String> legacyLore = new ArrayList<>();
                    for (Component line : lore) {
                        legacyLore.add(LegacyComponentSerializer.legacySection().serialize(line));
                    }
                    return legacyLore;
                }
            } else if (legacyGetLoreMethod != null) {
                // 使用传统方法
                Object result = legacyGetLoreMethod.invoke(meta);
                if (result != null) {
                    @SuppressWarnings("unchecked")
                    List<String> lore = (List<String>) result;
                    return lore;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "获取物品描述失败", e);
        }

        return new ArrayList<>();
    }
}
