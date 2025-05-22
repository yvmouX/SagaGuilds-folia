package cn.i7mc.sagaguild.data.models;

import java.util.Date;
import java.util.UUID;

/**
 * 公会成员数据模型
 */
public class GuildMember {
    // 成员角色枚举
    public enum Role {
        OWNER("会长"),
        ADMIN("副会长"),
        ELDER("长老"),
        MEMBER("成员");
        
        private final String displayName;
        
        Role(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private int id;
    private int guildId;
    private UUID playerUuid;
    private String playerName;
    private Role role;
    private Date joinedAt;
    
    /**
     * 创建一个新的公会成员对象
     * @param id 成员ID
     * @param guildId 公会ID
     * @param playerUuid 玩家UUID
     * @param playerName 玩家名称
     * @param role 成员角色
     * @param joinedAt 加入时间
     */
    public GuildMember(int id, int guildId, UUID playerUuid, String playerName, Role role, Date joinedAt) {
        this.id = id;
        this.guildId = guildId;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.role = role;
        this.joinedAt = joinedAt;
    }
    
    /**
     * 创建一个新的公会成员对象（用于新加入成员）
     * @param guildId 公会ID
     * @param playerUuid 玩家UUID
     * @param playerName 玩家名称
     * @param role 成员角色
     */
    public GuildMember(int guildId, UUID playerUuid, String playerName, Role role) {
        this.id = 0; // 未保存到数据库
        this.guildId = guildId;
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.role = role;
        this.joinedAt = new Date();
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
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Date getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }
    
    /**
     * 检查是否是会长
     * @return 是否是会长
     */
    public boolean isOwner() {
        return role == Role.OWNER;
    }
    
    /**
     * 检查是否是管理员或更高职位
     * @return 是否是管理员或更高职位
     */
    public boolean isAdmin() {
        return role == Role.OWNER || role == Role.ADMIN;
    }
    
    /**
     * 检查是否是长老或更高职位
     * @return 是否是长老或更高职位
     */
    public boolean isElder() {
        return role == Role.OWNER || role == Role.ADMIN || role == Role.ELDER;
    }
    
    /**
     * 检查是否有权限提升指定角色
     * @param targetRole 目标角色
     * @return 是否有权限提升
     */
    public boolean canPromote(Role targetRole) {
        // 只有会长可以提升到副会长
        if (targetRole == Role.ADMIN) {
            return role == Role.OWNER;
        }
        
        // 会长和副会长可以提升到长老
        if (targetRole == Role.ELDER) {
            return role == Role.OWNER || role == Role.ADMIN;
        }
        
        return false;
    }
    
    /**
     * 检查是否有权限降级指定角色
     * @param targetRole 目标角色
     * @return 是否有权限降级
     */
    public boolean canDemote(Role targetRole) {
        // 只有会长可以降级副会长
        if (targetRole == Role.ADMIN) {
            return role == Role.OWNER;
        }
        
        // 会长和副会长可以降级长老
        if (targetRole == Role.ELDER) {
            return role == Role.OWNER || role == Role.ADMIN;
        }
        
        // 会长、副会长和长老可以降级普通成员
        if (targetRole == Role.MEMBER) {
            return role == Role.OWNER || role == Role.ADMIN || role == Role.ELDER;
        }
        
        return false;
    }
    
    /**
     * 检查是否有权限踢出指定角色
     * @param targetRole 目标角色
     * @return 是否有权限踢出
     */
    public boolean canKick(Role targetRole) {
        // 不能踢出会长
        if (targetRole == Role.OWNER) {
            return false;
        }
        
        // 只有会长可以踢出副会长
        if (targetRole == Role.ADMIN) {
            return role == Role.OWNER;
        }
        
        // 会长和副会长可以踢出长老
        if (targetRole == Role.ELDER) {
            return role == Role.OWNER || role == Role.ADMIN;
        }
        
        // 会长、副会长和长老可以踢出普通成员
        return role == Role.OWNER || role == Role.ADMIN || role == Role.ELDER;
    }
}
