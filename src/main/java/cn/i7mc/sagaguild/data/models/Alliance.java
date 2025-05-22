package cn.i7mc.sagaguild.data.models;

import java.util.Date;

/**
 * 公会联盟模型
 * 表示两个公会之间的联盟关系
 */
public class Alliance {
    private int id;
    private int guild1Id;
    private int guild2Id;
    private Date formedAt;

    /**
     * 默认构造函数
     */
    public Alliance() {
    }

    /**
     * 创建联盟的构造函数
     * @param guild1Id 第一个公会ID
     * @param guild2Id 第二个公会ID
     */
    public Alliance(int guild1Id, int guild2Id) {
        this.guild1Id = guild1Id;
        this.guild2Id = guild2Id;
        this.formedAt = new Date();
    }

    /**
     * 完整构造函数
     * @param id 联盟ID
     * @param guild1Id 第一个公会ID
     * @param guild2Id 第二个公会ID
     * @param formedAt 联盟形成时间
     */
    public Alliance(int id, int guild1Id, int guild2Id, Date formedAt) {
        this.id = id;
        this.guild1Id = guild1Id;
        this.guild2Id = guild2Id;
        this.formedAt = formedAt;
    }

    /**
     * 获取联盟ID
     * @return 联盟ID
     */
    public int getId() {
        return id;
    }

    /**
     * 设置联盟ID
     * @param id 联盟ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取第一个公会ID
     * @return 第一个公会ID
     */
    public int getGuild1Id() {
        return guild1Id;
    }

    /**
     * 设置第一个公会ID
     * @param guild1Id 第一个公会ID
     */
    public void setGuild1Id(int guild1Id) {
        this.guild1Id = guild1Id;
    }

    /**
     * 获取第二个公会ID
     * @return 第二个公会ID
     */
    public int getGuild2Id() {
        return guild2Id;
    }

    /**
     * 设置第二个公会ID
     * @param guild2Id 第二个公会ID
     */
    public void setGuild2Id(int guild2Id) {
        this.guild2Id = guild2Id;
    }

    /**
     * 获取联盟形成时间
     * @return 联盟形成时间
     */
    public Date getFormedAt() {
        return formedAt;
    }

    /**
     * 设置联盟形成时间
     * @param formedAt 联盟形成时间
     */
    public void setFormedAt(Date formedAt) {
        this.formedAt = formedAt;
    }

    /**
     * 检查指定公会是否在此联盟中
     * @param guildId 公会ID
     * @return 是否在联盟中
     */
    public boolean containsGuild(int guildId) {
        return guild1Id == guildId || guild2Id == guildId;
    }

    /**
     * 获取联盟中的另一个公会ID
     * @param guildId 当前公会ID
     * @return 另一个公会ID，如果当前公会不在联盟中则返回-1
     */
    public int getOtherGuildId(int guildId) {
        if (guild1Id == guildId) {
            return guild2Id;
        } else if (guild2Id == guildId) {
            return guild1Id;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Alliance{" +
                "id=" + id +
                ", guild1Id=" + guild1Id +
                ", guild2Id=" + guild2Id +
                ", formedAt=" + formedAt +
                '}';
    }
}
