package cn.i7mc.sagaguild.data.models;

import java.util.Date;

/**
 * 停战请求模型
 * 表示一个公会向另一个公会发起的停战请求
 */
public class CeasefireRequest {
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
    private int requesterId;
    private int targetId;
    private int warId;
    private Date requestedAt;
    private Status status;
    
    /**
     * 默认构造函数
     */
    public CeasefireRequest() {
    }
    
    /**
     * 创建停战请求的构造函数
     * @param requesterId 请求方公会ID
     * @param targetId 目标公会ID
     * @param warId 战争ID
     */
    public CeasefireRequest(int requesterId, int targetId, int warId) {
        this.requesterId = requesterId;
        this.targetId = targetId;
        this.warId = warId;
        this.requestedAt = new Date();
        this.status = Status.PENDING;
    }
    
    /**
     * 完整构造函数
     * @param id 请求ID
     * @param requesterId 请求方公会ID
     * @param targetId 目标公会ID
     * @param warId 战争ID
     * @param requestedAt 请求时间
     * @param status 请求状态
     */
    public CeasefireRequest(int id, int requesterId, int targetId, int warId, Date requestedAt, Status status) {
        this.id = id;
        this.requesterId = requesterId;
        this.targetId = targetId;
        this.warId = warId;
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
     * 获取请求方公会ID
     * @return 请求方公会ID
     */
    public int getRequesterId() {
        return requesterId;
    }
    
    /**
     * 设置请求方公会ID
     * @param requesterId 请求方公会ID
     */
    public void setRequesterId(int requesterId) {
        this.requesterId = requesterId;
    }
    
    /**
     * 获取目标公会ID
     * @return 目标公会ID
     */
    public int getTargetId() {
        return targetId;
    }
    
    /**
     * 设置目标公会ID
     * @param targetId 目标公会ID
     */
    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
    
    /**
     * 获取战争ID
     * @return 战争ID
     */
    public int getWarId() {
        return warId;
    }
    
    /**
     * 设置战争ID
     * @param warId 战争ID
     */
    public void setWarId(int warId) {
        this.warId = warId;
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
        return "CeasefireRequest{" +
                "id=" + id +
                ", requesterId=" + requesterId +
                ", targetId=" + targetId +
                ", warId=" + warId +
                ", requestedAt=" + requestedAt +
                ", status=" + status +
                '}';
    }
}
