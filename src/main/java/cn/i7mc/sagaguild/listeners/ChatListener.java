package cn.i7mc.sagaguild.listeners;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 聊天监听器
 * 处理聊天相关的事件
 */
public class ChatListener implements Listener {
    private final SagaGuild plugin;

    public ChatListener(SagaGuild plugin) {
        this.plugin = plugin;
    }

    /**
     * 玩家聊天事件
     * 注意：使用AsyncPlayerChatEvent而非AsyncChatEvent以确保与各种服务端的兼容性
     * 虽然AsyncPlayerChatEvent已被弃用，但为了兼容Mohist等服务端，仍需使用此事件
     * @param event 事件对象
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    @SuppressWarnings("deprecation") // 使用已弃用的API以确保兼容性
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // 获取消息内容
        String message = event.getMessage();

        // 处理聊天消息
        boolean cancel = plugin.getChatManager().handleChat(player, message);

        // 如果是公会聊天，取消原始事件
        if (cancel) {
            event.setCancelled(true);
        }
    }

    /**
     * 玩家加入服务器事件
     * @param event 事件对象
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 检查玩家是否在公会中
        Guild guild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
        if (guild != null) {
            // 添加玩家到公会队伍
            plugin.getChatManager().addPlayerToTeam(player, guild.getId());
        }
    }

    /**
     * 玩家离开服务器事件
     * @param event 事件对象
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // 从公会队伍中移除玩家
        plugin.getChatManager().removePlayerFromTeam(player);
    }
}
