package cn.i7mc.sagaguild.gui.holders;

import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * 公会成员管理持有者
 * 用于标识公会成员管理GUI
 */
public class GuildMemberHolder implements InventoryHolder {
    private final Guild guild;
    private final int page;
    
    /**
     * 构造函数
     * @param guild 公会对象
     * @param page 页码
     */
    public GuildMemberHolder(Guild guild, int page) {
        this.guild = guild;
        this.page = page;
    }
    
    /**
     * 获取公会
     * @return 公会对象
     */
    public Guild getGuild() {
        return guild;
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
