package cn.i7mc.sagaguild.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品构建器
 * 用于简化物品的创建和修改
 */
public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;
    
    /**
     * 创建物品构建器
     * @param material 物品材质
     */
    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }
    
    /**
     * 创建物品构建器
     * @param material 物品材质
     * @param amount 数量
     */
    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }
    
    /**
     * 创建物品构建器
     * @param item 物品堆
     */
    public ItemBuilder(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }
    
    /**
     * 设置显示名称
     * @param name 显示名称
     * @return 物品构建器
     */
    public ItemBuilder name(String name) {
        meta.displayName(Component.text(name));
        return this;
    }
    
    /**
     * 设置描述
     * @param lore 描述
     * @return 物品构建器
     */
    public ItemBuilder lore(String... lore) {
        List<Component> loreList = new ArrayList<>();
        for (String line : lore) {
            loreList.add(Component.text(line));
        }
        meta.lore(loreList);
        return this;
    }
    
    /**
     * 设置描述
     * @param lore 描述
     * @return 物品构建器
     */
    public ItemBuilder lore(List<String> lore) {
        List<Component> loreList = new ArrayList<>();
        for (String line : lore) {
            loreList.add(Component.text(line));
        }
        meta.lore(loreList);
        return this;
    }
    
    /**
     * 添加描述行
     * @param line 描述行
     * @return 物品构建器
     */
    public ItemBuilder addLoreLine(String line) {
        List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(Component.text(line));
        meta.lore(lore);
        return this;
    }
    
    /**
     * 添加附魔
     * @param enchantment 附魔
     * @param level 等级
     * @return 物品构建器
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }
    
    /**
     * 添加物品标志
     * @param flag 物品标志
     * @return 物品构建器
     */
    public ItemBuilder flag(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }
    
    /**
     * 设置是否发光
     * @param glow 是否发光
     * @return 物品构建器
     */
    public ItemBuilder glow(boolean glow) {
        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            meta.removeEnchant(Enchantment.DURABILITY);
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }
    
    /**
     * 设置是否不可破坏
     * @param unbreakable 是否不可破坏
     * @return 物品构建器
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }
    
    /**
     * 构建物品
     * @return 物品堆
     */
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
