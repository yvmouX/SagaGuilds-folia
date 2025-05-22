package cn.i7mc.sagaguild.commands;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * 子命令接口
 * 定义子命令的通用方法
 */
public interface SubCommand {
    /**
     * 获取命令名称
     * @return 命令名称
     */
    String getName();
    
    /**
     * 获取命令描述
     * @return 命令描述
     */
    String getDescription();
    
    /**
     * 获取命令语法
     * @return 命令语法
     */
    String getSyntax();
    
    /**
     * 获取命令别名
     * @return 命令别名数组
     */
    String[] getAliases();
    
    /**
     * 执行命令
     * @param player 执行命令的玩家
     * @param args 命令参数
     * @return 是否成功执行
     */
    boolean execute(Player player, String[] args);
    
    /**
     * 命令补全
     * @param player 执行命令的玩家
     * @param args 命令参数
     * @return 补全列表
     */
    List<String> tabComplete(Player player, String[] args);
}
