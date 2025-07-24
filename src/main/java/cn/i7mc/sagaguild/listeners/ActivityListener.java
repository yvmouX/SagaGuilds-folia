package cn.i7mc.sagaguild.listeners;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.models.Guild;
import cn.i7mc.sagaguild.data.models.GuildActivity;
import cn.i7mc.sagaguild.data.models.GuildMember;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

/**
 * 活动监听器
 * 处理活动相关的事件
 */
public class ActivityListener implements Listener {
    private final SagaGuild plugin;
    
    public ActivityListener(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 玩家加入服务器事件
     * 用于通知玩家即将开始的活动
     * @param event 事件对象
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 获取玩家所在公会
        Guild guild = plugin.getGuildManager().getPlayerGuild(player.getUniqueId());
        if (guild == null) {
            return;
        }
        
        // 获取公会即将开始的活动
        List<GuildActivity> upcomingActivities = plugin.getActivityManager().getUpcomingGuildActivities(guild.getId());
        if (upcomingActivities.isEmpty()) {
            return;
        }
        
        // 延迟3秒发送通知，避免与其他加入消息冲突
        SagaGuild.getYLib().getScheduler().runLaterAsync(() -> {
            // 通知玩家即将开始的活动
            player.sendMessage(Component.text("=== 公会即将开始的活动 ===", NamedTextColor.GOLD));
            
            for (GuildActivity activity : upcomingActivities) {
                // 计算距离开始的时间
                long minutesUntilStart = (activity.getStartTime().getTime() - System.currentTimeMillis()) / (1000 * 60);
                String timeUntilStart;
                
                if (minutesUntilStart < 60) {
                    timeUntilStart = minutesUntilStart + " 分钟";
                } else {
                    timeUntilStart = (minutesUntilStart / 60) + " 小时 " + (minutesUntilStart % 60) + " 分钟";
                }
                
                // 发送活动信息
                player.sendMessage(
                    Component.text("- ", NamedTextColor.GRAY)
                        .append(Component.text(activity.getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" (", NamedTextColor.GRAY))
                        .append(Component.text(activity.getType().getDisplayName(), NamedTextColor.AQUA))
                        .append(Component.text("): ", NamedTextColor.GRAY))
                        .append(Component.text(timeUntilStart + " 后开始", NamedTextColor.GREEN))
                );
            }
            
            player.sendMessage(Component.text("使用 /guild activity list 查看更多活动信息", NamedTextColor.GRAY));
        }, 60);
    }
    
    /**
     * 检查玩家是否有权限管理活动
     * @param player 玩家
     * @param guildId 公会ID
     * @return 是否有权限
     */
    public boolean canManageActivities(Player player, int guildId) {
        // 获取玩家所在公会
        Guild guild = plugin.getGuildManager().getGuildById(guildId);
        if (guild == null) {
            return false;
        }
        
        // 检查是否是公会会长
        if (guild.getOwnerUuid().equals(player.getUniqueId())) {
            return true;
        }
        
        // 检查是否是公会管理员
        GuildMember member = plugin.getGuildManager().getGuildMember(guildId, player.getUniqueId());
        if (member != null && member.getRole() == GuildMember.Role.ADMIN) {
            return true;
        }
        
        // 检查是否有管理员权限
        return player.hasPermission("guild.admin");
    }
}
