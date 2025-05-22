package cn.i7mc.sagaguild.data.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 公会战数据模型
 */
public class GuildWar {
    /**
     * 公会战状态枚举
     */
    public enum Status {
        PENDING,    // 等待中
        PREPARING,  // 准备中
        ONGOING,    // 进行中
        FINISHED    // 已结束
    }
    
    private int id;
    private int attackerId;
    private int defenderId;
    private Date startTime;
    private Date endTime;
    private Integer winnerId;
    private Status status;
    
    // 战斗数据
    private final Map<UUID, Integer> kills;
    private final Map<UUID, Integer> deaths;
    
    /**
     * 创建一个新的公会战对象
     * @param id 公会战ID
     * @param attackerId 攻击方公会ID
     * @param defenderId 防守方公会ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param winnerId 胜利方公会ID
     * @param status 状态
     */
    public GuildWar(int id, int attackerId, int defenderId, Date startTime, Date endTime, Integer winnerId, Status status) {
        this.id = id;
        this.attackerId = attackerId;
        this.defenderId = defenderId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.winnerId = winnerId;
        this.status = status;
        
        this.kills = new HashMap<>();
        this.deaths = new HashMap<>();
    }
    
    /**
     * 创建一个新的公会战对象（用于新建公会战）
     * @param attackerId 攻击方公会ID
     * @param defenderId 防守方公会ID
     */
    public GuildWar(int attackerId, int defenderId) {
        this.id = 0; // 未保存到数据库
        this.attackerId = attackerId;
        this.defenderId = defenderId;
        this.startTime = new Date();
        this.endTime = null;
        this.winnerId = null;
        this.status = Status.PENDING;
        
        this.kills = new HashMap<>();
        this.deaths = new HashMap<>();
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getAttackerId() {
        return attackerId;
    }
    
    public void setAttackerId(int attackerId) {
        this.attackerId = attackerId;
    }
    
    public int getDefenderId() {
        return defenderId;
    }
    
    public void setDefenderId(int defenderId) {
        this.defenderId = defenderId;
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
    
    public Integer getWinnerId() {
        return winnerId;
    }
    
    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    /**
     * 增加玩家击杀数
     * @param playerUuid 玩家UUID
     */
    public void addKill(UUID playerUuid) {
        kills.put(playerUuid, kills.getOrDefault(playerUuid, 0) + 1);
    }
    
    /**
     * 增加玩家死亡数
     * @param playerUuid 玩家UUID
     */
    public void addDeath(UUID playerUuid) {
        deaths.put(playerUuid, deaths.getOrDefault(playerUuid, 0) + 1);
    }
    
    /**
     * 获取玩家击杀数
     * @param playerUuid 玩家UUID
     * @return 击杀数
     */
    public int getKills(UUID playerUuid) {
        return kills.getOrDefault(playerUuid, 0);
    }
    
    /**
     * 获取玩家死亡数
     * @param playerUuid 玩家UUID
     * @return 死亡数
     */
    public int getDeaths(UUID playerUuid) {
        return deaths.getOrDefault(playerUuid, 0);
    }
    
    /**
     * 获取公会总击杀数
     * @param guildId 公会ID
     * @param guildMembers 公会成员列表
     * @return 总击杀数
     */
    public int getTotalKills(int guildId, Map<UUID, Integer> guildMembers) {
        int total = 0;
        for (UUID playerUuid : guildMembers.keySet()) {
            if (guildMembers.get(playerUuid) == guildId) {
                total += getKills(playerUuid);
            }
        }
        return total;
    }
    
    /**
     * 检查公会是否参与战争
     * @param guildId 公会ID
     * @return 是否参与
     */
    public boolean isParticipant(int guildId) {
        return attackerId == guildId || defenderId == guildId;
    }
    
    /**
     * 获取对方公会ID
     * @param guildId 公会ID
     * @return 对方公会ID，如果不是参与者则返回-1
     */
    public int getOpponentId(int guildId) {
        if (attackerId == guildId) {
            return defenderId;
        } else if (defenderId == guildId) {
            return attackerId;
        } else {
            return -1;
        }
    }
    
    /**
     * 检查是否是攻击方
     * @param guildId 公会ID
     * @return 是否是攻击方
     */
    public boolean isAttacker(int guildId) {
        return attackerId == guildId;
    }
    
    /**
     * 检查是否是防守方
     * @param guildId 公会ID
     * @return 是否是防守方
     */
    public boolean isDefender(int guildId) {
        return defenderId == guildId;
    }
}
