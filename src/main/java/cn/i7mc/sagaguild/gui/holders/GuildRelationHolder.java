package cn.i7mc.sagaguild.gui.holders;

import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * 公会关系设置持有者
 * 用于标识公会关系设置GUI
 */
public class GuildRelationHolder implements InventoryHolder {
    private final Guild targetGuild;
    
    /**
     * 构造函数
     * @param targetGuild 目标公会
     */
    public GuildRelationHolder(Guild targetGuild) {
        this.targetGuild = targetGuild;
    }
    
    /**
     * 获取目标公会
     * @return 目标公会
     */
    public Guild getTargetGuild() {
        return targetGuild;
    }
    
    @Override
    public Inventory getInventory() {
        return null; // 由Bukkit管理
    }
}
