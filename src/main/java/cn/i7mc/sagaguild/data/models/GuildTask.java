package cn.i7mc.sagaguild.data.models;

import java.util.Date;

/**
 * 公会任务数据模型
 */
public class GuildTask {
    /**
     * 任务类型枚举
     */
    public enum Type {
        KILL_MOBS("击杀怪物"),
        BREAK_BLOCKS("破坏方块"),
        PLACE_BLOCKS("放置方块"),
        FISH("钓鱼"),
        CRAFT("合成物品");
        
        private final String displayName;
        
        Type(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 任务状态枚举
     */
    public enum Status {
        ACTIVE,     // 进行中
        COMPLETED,  // 已完成
        EXPIRED     // 已过期
    }
    
    private int id;
    private int guildId;
    private Type type;
    private String description;
    private int target;
    private int progress;
    private int rewardExp;
    private double rewardMoney;
    private Date createdAt;
    private Date expiresAt;
    private Date completedAt;
    
    /**
     * 创建一个新的公会任务对象
     * @param id 任务ID
     * @param guildId 公会ID
     * @param type 任务类型
     * @param description 任务描述
     * @param target 目标数量
     * @param progress 当前进度
     * @param rewardExp 奖励经验
     * @param rewardMoney 奖励金钱
     * @param createdAt 创建时间
     * @param expiresAt 过期时间
     * @param completedAt 完成时间
     */
    public GuildTask(int id, int guildId, Type type, String description, int target, int progress, 
                    int rewardExp, double rewardMoney, Date createdAt, Date expiresAt, Date completedAt) {
        this.id = id;
        this.guildId = guildId;
        this.type = type;
        this.description = description;
        this.target = target;
        this.progress = progress;
        this.rewardExp = rewardExp;
        this.rewardMoney = rewardMoney;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.completedAt = completedAt;
    }
    
    /**
     * 创建一个新的公会任务对象（用于新建任务）
     * @param guildId 公会ID
     * @param type 任务类型
     * @param description 任务描述
     * @param target 目标数量
     * @param rewardExp 奖励经验
     * @param rewardMoney 奖励金钱
     * @param expiresAt 过期时间
     */
    public GuildTask(int guildId, Type type, String description, int target, int rewardExp, double rewardMoney, Date expiresAt) {
        this.id = 0; // 未保存到数据库
        this.guildId = guildId;
        this.type = type;
        this.description = description;
        this.target = target;
        this.progress = 0;
        this.rewardExp = rewardExp;
        this.rewardMoney = rewardMoney;
        this.createdAt = new Date();
        this.expiresAt = expiresAt;
        this.completedAt = null;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getGuildId() {
        return guildId;
    }
    
    public void setGuildId(int guildId) {
        this.guildId = guildId;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getTarget() {
        return target;
    }
    
    public void setTarget(int target) {
        this.target = target;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public void setProgress(int progress) {
        this.progress = progress;
    }
    
    public int getRewardExp() {
        return rewardExp;
    }
    
    public void setRewardExp(int rewardExp) {
        this.rewardExp = rewardExp;
    }
    
    public double getRewardMoney() {
        return rewardMoney;
    }
    
    public void setRewardMoney(double rewardMoney) {
        this.rewardMoney = rewardMoney;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public Date getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }
    
    /**
     * 增加进度
     * @param amount 增加数量
     * @return 是否完成
     */
    public boolean addProgress(int amount) {
        this.progress += amount;
        
        // 检查是否完成
        if (this.progress >= this.target && this.completedAt == null) {
            this.completedAt = new Date();
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取任务状态
     * @return 任务状态
     */
    public Status getStatus() {
        if (completedAt != null) {
            return Status.COMPLETED;
        } else if (expiresAt != null && expiresAt.before(new Date())) {
            return Status.EXPIRED;
        } else {
            return Status.ACTIVE;
        }
    }
    
    /**
     * 获取进度百分比
     * @return 进度百分比
     */
    public int getProgressPercentage() {
        return (int) ((double) progress / target * 100);
    }
}
