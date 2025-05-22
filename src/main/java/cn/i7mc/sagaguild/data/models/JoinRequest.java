package cn.i7mc.sagaguild.data.models;

import java.util.Date;
import java.util.UUID;

/**
 * 公会加入请求模型
 * 表示一个玩家向公会发起的加入请求
 */
public class JoinRequest {
    /**
     * 请求状态枚举
     */
    public enum Status {
        PENDING,    // 等待中
        ACCEPTED,   // 已接受
        REJECTED,   // 已拒绝
        EXPIRED     // 已过期
    }
    
    private int id;
    private UUID playerUuid;
    private String playerName;
    private int guildId;
    private Date requestedAt;
    private Status status;
    
    /**
     * 默认构造函数
     */
    public JoinRequest() {
    }
    
    /**
     * 创建加入请求的构造函数
     * @param playerUuid 玩家UUID
     * @param playerName 玩家名称
     * @param guildId 目标公会ID
     */
    public JoinRequest(UUID playerUuid, String playerName, int guildId) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.guildId = guildId;
        this.requestedAt = new Date();
        this.status = Status.PENDING;
    }
    
    /**
     * 完整构造函数
     * @param id 请求ID
     * @param playerUuid 玩家UUID
     * @param playerName 玩家名称
     * @param guildId 目标公会ID
     * @param requestedAt 请求时间
     * @param status 请求状态
     */
    public JoinRequest(int id, UUID playerUuid, String playerName, int guildId, Date requestedAt, Status status) {
        this.id = id;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.guildId = guildId;
        this.requestedAt = requestedAt;
        this.status = status;
    }
    
    /**
     * 获取请求ID
     * @return 请求ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * 设置请求ID
     * @param id 请求ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * 获取玩家UUID
     * @return 玩家UUID
     */
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    
    /**
     * 设置玩家UUID
     * @param playerUuid 玩家UUID
     */
    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }
    
    /**
     * 获取玩家名称
     * @return 玩家名称
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * 设置玩家名称
     * @param playerName 玩家名称
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    /**
     * 获取目标公会ID
     * @return 目标公会ID
     */
    public int getGuildId() {
        return guildId;
    }
    
    /**
     * 设置目标公会ID
     * @param guildId 目标公会ID
     */
    public void setGuildId(int guildId) {
        this.guildId = guildId;
    }
    
    /**
     * 获取请求时间
     * @return 请求时间
     */
    public Date getRequestedAt() {
        return requestedAt;
    }
    
    /**
     * 设置请求时间
     * @param requestedAt 请求时间
     */
    public void setRequestedAt(Date requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    /**
     * 获取请求状态
     * @return 请求状态
     */
    public Status getStatus() {
        return status;
    }
    
    /**
     * 设置请求状态
     * @param status 请求状态
     */
    public void setStatus(Status status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "JoinRequest{" +
                "id=" + id +
                ", playerUuid=" + playerUuid +
                ", playerName='" + playerName + '\'' +
                ", guildId=" + guildId +
                ", requestedAt=" + requestedAt +
                ", status=" + status +
                '}';
    }
}
