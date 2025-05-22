package cn.i7mc.sagaguild.data.models;

import java.util.Date;

/**
 * 联盟请求模型
 * 表示一个公会向另一个公会发起的联盟请求
 */
public class AllianceRequest {
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
    private Date requestedAt;
    private Status status;
    
    /**
     * 默认构造函数
     */
    public AllianceRequest() {
    }
    
    /**
     * 创建联盟请求的构造函数
     * @param requesterId 请求方公会ID
     * @param targetId 目标公会ID
     */
    public AllianceRequest(int requesterId, int targetId) {
        this.requesterId = requesterId;
        this.targetId = targetId;
        this.requestedAt = new Date();
        this.status = Status.PENDING;
    }
    
    /**
     * 完整构造函数
     * @param id 请求ID
     * @param requesterId 请求方公会ID
     * @param targetId 目标公会ID
     * @param requestedAt 请求时间
     * @param status 请求状态
     */
    public AllianceRequest(int id, int requesterId, int targetId, Date requestedAt, Status status) {
        this.id = id;
        this.requesterId = requesterId;
        this.targetId = targetId;
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
        return "AllianceRequest{" +
                "id=" + id +
                ", requesterId=" + requesterId +
                ", targetId=" + targetId +
                ", requestedAt=" + requestedAt +
                ", status=" + status +
                '}';
    }
}
