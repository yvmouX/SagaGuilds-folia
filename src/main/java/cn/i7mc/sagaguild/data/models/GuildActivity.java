package cn.i7mc.sagaguild.data.models;

import java.util.Date;
import java.util.UUID;

/**
 * 公会活动数据模型
 */
public class GuildActivity {
    /**
     * 活动类型枚举
     */
    public enum Type {
        MEETING("会议"),
        DUNGEON("副本"),
        PVP("PVP"),
        RESOURCE_GATHERING("资源收集"),
        BUILDING("建筑"),
        CUSTOM("自定义");
        
        private final String displayName;
        
        Type(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 活动状态枚举
     */
    public enum Status {
        PLANNED,    // 计划中
        ONGOING,    // 进行中
        COMPLETED,  // 已完成
        CANCELLED   // 已取消
    }
    
    private int id;
    private int guildId;
    private String name;
    private String description;
    private Type type;
    private UUID creatorUuid;
    private Date startTime;
    private Date endTime;
    private String location;
    private int maxParticipants;
    private Date createdAt;
    private Status status;
    
    /**
     * 创建一个新的公会活动对象
     * @param id 活动ID
     * @param guildId 公会ID
     * @param name 活动名称
     * @param description 活动描述
     * @param type 活动类型
     * @param creatorUuid 创建者UUID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param location 活动地点
     * @param maxParticipants 最大参与人数
     * @param createdAt 创建时间
     * @param status 活动状态
     */
    public GuildActivity(int id, int guildId, String name, String description, Type type, UUID creatorUuid,
                        Date startTime, Date endTime, String location, int maxParticipants, Date createdAt, Status status) {
        this.id = id;
        this.guildId = guildId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.creatorUuid = creatorUuid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.createdAt = createdAt;
        this.status = status;
    }
    
    /**
     * 创建一个新的公会活动对象（用于新建活动）
     * @param guildId 公会ID
     * @param name 活动名称
     * @param description 活动描述
     * @param type 活动类型
     * @param creatorUuid 创建者UUID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param location 活动地点
     * @param maxParticipants 最大参与人数
     */
    public GuildActivity(int guildId, String name, String description, Type type, UUID creatorUuid,
                        Date startTime, Date endTime, String location, int maxParticipants) {
        this.id = 0; // 未保存到数据库
        this.guildId = guildId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.creatorUuid = creatorUuid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.createdAt = new Date();
        this.status = Status.PLANNED;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public UUID getCreatorUuid() {
        return creatorUuid;
    }
    
    public void setCreatorUuid(UUID creatorUuid) {
        this.creatorUuid = creatorUuid;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public int getMaxParticipants() {
        return maxParticipants;
    }
    
    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    /**
     * 检查活动是否已开始
     * @return 是否已开始
     */
    public boolean hasStarted() {
        return startTime != null && startTime.before(new Date());
    }
    
    /**
     * 检查活动是否已结束
     * @return 是否已结束
     */
    public boolean hasEnded() {
        return endTime != null && endTime.before(new Date());
    }
    
    /**
     * 获取活动状态
     * @return 活动状态
     */
    public Status getCurrentStatus() {
        if (status == Status.CANCELLED) {
            return Status.CANCELLED;
        }
        
        if (hasEnded()) {
            return Status.COMPLETED;
        }
        
        if (hasStarted()) {
            return Status.ONGOING;
        }
        
        return Status.PLANNED;
    }
}
