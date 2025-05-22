package cn.i7mc.sagaguild.gui.holders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * 公会列表持有者
 * 用于标识公会列表GUI
 */
public class GuildListHolder implements InventoryHolder {
    private final int page;
    
    public GuildListHolder(int page) {
        this.page = page;
    }
    
    /**
     * 获取页码
     * @return 页码
     */
    public int getPage() {
        return page;
    }
    
    @Override
    public Inventory getInventory() {
        return null; // 由Bukkit管理
    }
}
