package cn.i7mc.sagaguild.gui.listeners;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.models.Guild;
import cn.i7mc.sagaguild.data.models.GuildMember;
import cn.i7mc.sagaguild.gui.holders.GuildSettingsHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * 公会设置监听器
 * 处理公会设置GUI的点击事件
 */
public class GuildSettingsListener implements Listener {
    private final SagaGuild plugin;
    
    public GuildSettingsListener(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 物品栏点击事件
     * @param event 事件对象
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 检查是否是公会设置GUI
        if (!(event.getInventory().getHolder() instanceof GuildSettingsHolder)) {
            return;
        }
        
        // 取消事件
        event.setCancelled(true);
        
        // 检查是否是玩家
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        GuildSettingsHolder holder = (GuildSettingsHolder) event.getInventory().getHolder();
        Guild guild = holder.getGuild();
        
        // 检查玩家是否是公会会长
        GuildMember member = plugin.getGuildManager().getMemberByUuid(player.getUniqueId());
        if (member == null || !member.isOwner()) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.owner-only"));
            player.closeInventory();
            return;
        }
        
        // 获取点击的物品
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        // 处理点击事件
        switch (event.getSlot()) {
            case 10: // 公会公开性设置
                toggleGuildPublic(player, guild);
                // 刷新GUI
                plugin.getGuiManager().openGuildSettingsGUI(player, guild);
                break;
            case 12: // 公会描述设置
                // 关闭GUI，让玩家输入新的描述
                player.closeInventory();
                player.sendMessage(plugin.getConfigManager().getMessage("guild.enter-description"));
                // 这里需要一个聊天监听器来处理玩家的输入
                break;
            case 14: // 公会公告设置
                // 关闭GUI，让玩家输入新的公告
                player.closeInventory();
                player.sendMessage(plugin.getConfigManager().getMessage("guild.enter-announcement"));
                // 这里需要一个聊天监听器来处理玩家的输入
                break;
            case 16: // 公会标签颜色设置
                // 打开颜色选择GUI
                // 这里需要实现一个颜色选择GUI
                break;
            case 28: // 领地相关设置
                // 打开领地设置GUI
                // 这里需要实现一个领地设置GUI
                break;
            case 30: // 聊天设置
                // 打开聊天设置GUI
                // 这里需要实现一个聊天设置GUI
                break;
            case 49: // 返回
                player.closeInventory();
                plugin.getGuiManager().openGuildManageGUI(player, guild);
                break;
        }
    }
    
    /**
     * 切换公会公开性
     * @param player 玩家
     * @param guild 公会
     */
    private void toggleGuildPublic(Player player, Guild guild) {
        // 切换公会公开性
        guild.setPublic(!guild.isPublic());
        
        // 更新数据库
        boolean success = plugin.getGuildManager().updateGuild(guild);
        
        if (success) {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.public-toggled", "state", guild.isPublic() ? "公开" : "私有"));
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("guild.update-failed"));
        }
    }
}
