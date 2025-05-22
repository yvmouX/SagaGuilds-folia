package cn.i7mc.sagaguild.gui.holders;

import cn.i7mc.sagaguild.data.models.Guild;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * 公会加入请求GUI持有者
 */
public class JoinRequestHolder implements InventoryHolder {
    private final Guild guild;
    private final int page;

    /**
     * 创建公会加入请求GUI持有者
     * @param guild 公会对象
     * @param page 页码
     */
    public JoinRequestHolder(Guild guild, int page) {
        this.guild = guild;
        this.page = page;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    /**
     * 获取公会对象
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
}
