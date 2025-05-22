package cn.i7mc.sagaguild.listeners;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * 领地监听器
 * 处理领地相关的事件
 */
public class LandListener implements Listener {
    private final SagaGuild plugin;
    
    public LandListener(SagaGuild plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 玩家破坏方块事件
     * @param event 事件对象
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        
        // 检查玩家是否有权限在该区块中破坏方块
        if (!plugin.getLandManager().hasPermission(player, chunk)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("land.no-permission"));
        }
    }
    
    /**
     * 玩家放置方块事件
     * @param event 事件对象
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        
        // 检查玩家是否有权限在该区块中放置方块
        if (!plugin.getLandManager().hasPermission(player, chunk)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("land.no-permission"));
        }
    }
    
    /**
     * 玩家交互事件
     * @param event 事件对象
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 检查是否与方块交互
        if (event.getClickedBlock() == null) {
            return;
        }
        
        Player player = event.getPlayer();
        Chunk chunk = event.getClickedBlock().getChunk();
        
        // 检查玩家是否有权限在该区块中交互
        if (!plugin.getLandManager().hasPermission(player, chunk)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getConfigManager().getMessage("land.no-permission"));
        }
    }
    
    /**
     * 玩家移动事件
     * @param event 事件对象
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // 检查是否跨区块移动
        if (event.getFrom().getChunk().equals(event.getTo().getChunk())) {
            return;
        }
        
        Player player = event.getPlayer();
        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();
        
        // 检查是否进入或离开领地
        int fromGuildId = plugin.getLandManager().getChunkGuildId(fromChunk);
        int toGuildId = plugin.getLandManager().getChunkGuildId(toChunk);
        
        if (fromGuildId != toGuildId) {
            if (toGuildId != -1) {
                // 进入领地
                Guild guild = plugin.getGuildManager().getGuildById(toGuildId);
                if (guild != null) {
                    player.sendMessage(plugin.getConfigManager().getMessage("land.entered", 
                            "guild", guild.getName()));
                }
            } else if (fromGuildId != -1) {
                // 离开领地
                Guild guild = plugin.getGuildManager().getGuildById(fromGuildId);
                if (guild != null) {
                    player.sendMessage(plugin.getConfigManager().getMessage("land.left-area", 
                            "guild", guild.getName()));
                }
            }
        }
    }
}
