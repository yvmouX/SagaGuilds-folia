package cn.i7mc.sagaguild.data.models;

import java.util.Date;
import java.util.UUID;

/**
 * 公会数据模型
 */
public class Guild {
    private int id;
    private String name;
    private String tag;
    private String description;
    private String announcement;
    private UUID ownerUuid;
    private int level;
    private int experience;
    private boolean isPublic;
    private Date createdAt;
    private String tagColor = "7"; // 默认标签颜色为灰色

    /**
     * 创建一个新的公会对象
     * @param id 公会ID
     * @param name 公会名称
     * @param tag 公会标签
     * @param description 公会描述
     * @param announcement 公会公告
     * @param ownerUuid 会长UUID
     * @param level 公会等级
     * @param experience 公会经验
     * @param isPublic 是否公开
     * @param createdAt 创建时间
     * @param tagColor 标签颜色
     */
    public Guild(int id, String name, String tag, String description, String announcement,
                UUID ownerUuid, int level, int experience, boolean isPublic, Date createdAt, String tagColor) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.announcement = announcement;
        this.ownerUuid = ownerUuid;
        this.level = level;
        this.experience = experience;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
        this.tagColor = tagColor;
    }

    /**
     * 创建一个新的公会对象（兼容旧版本）
     * @param id 公会ID
     * @param name 公会名称
     * @param tag 公会标签
     * @param description 公会描述
     * @param announcement 公会公告
     * @param ownerUuid 会长UUID
     * @param level 公会等级
     * @param experience 公会经验
     * @param isPublic 是否公开
     * @param createdAt 创建时间
     */
    public Guild(int id, String name, String tag, String description, String announcement,
                UUID ownerUuid, int level, int experience, boolean isPublic, Date createdAt) {
        this(id, name, tag, description, announcement, ownerUuid, level, experience, isPublic, createdAt, "7");
    }

    /**
     * 创建一个新的公会对象（用于新建公会）
     * @param name 公会名称
     * @param tag 公会标签
     * @param description 公会描述
     * @param ownerUuid 会长UUID
     */
    public Guild(String name, String tag, String description, UUID ownerUuid) {
        this.id = 0; // 未保存到数据库
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.announcement = "";
        this.ownerUuid = ownerUuid;
        this.level = 1;
        this.experience = 0;
        this.isPublic = true;
        this.createdAt = new Date();
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 增加公会经验
     * @param amount 经验数量
     * @return 是否升级
     */
    public boolean addExperience(int amount) {
        this.experience += amount;

        // 检查是否可以升级
        int nextLevelExp = getNextLevelExperience();
        if (this.experience >= nextLevelExp && this.level < 10) {
            this.level++;
            return true;
        }

        return false;
    }

    /**
     * 获取下一级所需经验
     * @return 下一级所需经验
     */
    public int getNextLevelExperience() {
        // 基础经验 + 每级增加的经验 * (当前等级 - 1)
        return 1000 + 500 * (this.level - 1);
    }

    /**
     * 获取标签颜色
     * @return 标签颜色代码
     */
    public String getTagColor() {
        return tagColor;
    }

    /**
     * 设置标签颜色
     * @param tagColor 标签颜色代码
     */
    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    /**
     * 获取当前等级的最大成员数量
     * 注意：此方法需要GuildManager的支持，不能直接在Guild类中使用
     * @param guildManager 公会管理器
     * @return 最大成员数量
     */
    public int getMaxMembers(cn.i7mc.sagaguild.managers.GuildManager guildManager) {
        return guildManager.getMaxMembersByLevel(this.level);
    }
}
