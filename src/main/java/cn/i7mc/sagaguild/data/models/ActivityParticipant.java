package cn.i7mc.sagaguild.data.models;

import java.util.Date;
import java.util.UUID;

/**
 * 活动参与者数据模型
 */
public class ActivityParticipant {
    /**
     * 参与状态枚举
     */
    public enum Status {
        REGISTERED,  // 已报名
        CONFIRMED,   // 已确认
        ATTENDED,    // 已参加
        ABSENT       // 缺席
    }
    
    private int id;
    private int activityId;
    private UUID playerUuid;
    private String playerName;
    private Status status;
    private Date registeredAt;
    
    /**
     * 创建一个新的活动参与者对象
     * @param id ID
     * @param activityId 活动ID
     * @param playerUuid 玩家UUID
     * @param playerName 玩家名称
     * @param status 参与状态
     * @param registeredAt 报名时间
     */
    public ActivityParticipant(int id, int activityId, UUID playerUuid, String playerName, Status status, Date registeredAt) {
        this.id = id;
        this.activityId = activityId;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.status = status;
        this.registeredAt = registeredAt;
    }
    
    /**
     * 创建一个新的活动参与者对象（用于新建参与者）
     * @param activityId 活动ID
     * @param playerUuid 玩家UUID
     * @param playerName 玩家名称
     */
    public ActivityParticipant(int activityId, UUID playerUuid, String playerName) {
        this.id = 0; // 未保存到数据库
        this.activityId = activityId;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.status = Status.REGISTERED;
        this.registeredAt = new Date();
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getActivityId() {
        return activityId;
    }
    
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }
    
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    
    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Date getRegisteredAt() {
        return registeredAt;
    }
    
    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }
}
