package cn.i7mc.sagaguild.data.dao;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.DatabaseManager;
import cn.i7mc.sagaguild.data.models.ActivityParticipant;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 活动参与者数据访问对象
 * 提供活动参与者数据的CRUD操作
 */
public class ParticipantDAO {
    private final SagaGuild plugin;
    private final DatabaseManager databaseManager;
    
    public ParticipantDAO(SagaGuild plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }
    
    /**
     * 添加参与者
     * @param participant 参与者对象
     * @return 创建的参与者ID，失败返回-1
     */
    public int addParticipant(ActivityParticipant participant) {
        String sql = "INSERT INTO activity_participants (activity_id, player_uuid, player_name, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, participant.getActivityId());
            stmt.setString(2, participant.getPlayerUuid().toString());
            stmt.setString(3, participant.getPlayerName());
            stmt.setString(4, participant.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    participant.setId(id);
                    return id;
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("添加活动参与者失败: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * 更新参与者状态
     * @param participantId 参与者ID
     * @param status 状态
     * @return 是否成功
     */
    public boolean updateParticipantStatus(int participantId, ActivityParticipant.Status status) {
        String sql = "UPDATE activity_participants SET status = ? WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, participantId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("更新参与者状态失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 删除参与者
     * @param participantId 参与者ID
     * @return 是否成功
     */
    public boolean deleteParticipant(int participantId) {
        String sql = "DELETE FROM activity_participants WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, participantId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("删除参与者失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取活动所有参与者
     * @param activityId 活动ID
     * @return 参与者列表
     */
    public List<ActivityParticipant> getActivityParticipants(int activityId) {
        List<ActivityParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM activity_participants WHERE activity_id = ? ORDER BY registered_at ASC";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participants.add(extractParticipantFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取活动参与者列表失败: " + e.getMessage());
        }
        
        return participants;
    }
    
    /**
     * 获取玩家参与的活动
     * @param playerUuid 玩家UUID
     * @return 参与者列表
     */
    public List<ActivityParticipant> getPlayerParticipations(UUID playerUuid) {
        List<ActivityParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM activity_participants WHERE player_uuid = ? ORDER BY registered_at DESC";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUuid.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participants.add(extractParticipantFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取玩家参与活动列表失败: " + e.getMessage());
        }
        
        return participants;
    }
    
    /**
     * 检查玩家是否已参与活动
     * @param activityId 活动ID
     * @param playerUuid 玩家UUID
     * @return 是否已参与
     */
    public boolean isPlayerParticipating(int activityId, UUID playerUuid) {
        String sql = "SELECT COUNT(*) FROM activity_participants WHERE activity_id = ? AND player_uuid = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            stmt.setString(2, playerUuid.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("检查玩家是否参与活动失败: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * 获取活动参与者数量
     * @param activityId 活动ID
     * @return 参与者数量
     */
    public int getParticipantCount(int activityId) {
        String sql = "SELECT COUNT(*) FROM activity_participants WHERE activity_id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取活动参与者数量失败: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * 从结果集中提取参与者对象
     * @param rs 结果集
     * @return 参与者对象
     * @throws SQLException SQL异常
     */
    private ActivityParticipant extractParticipantFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int activityId = rs.getInt("activity_id");
        UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
        String playerName = rs.getString("player_name");
        ActivityParticipant.Status status = ActivityParticipant.Status.valueOf(rs.getString("status"));
        Date registeredAt = new Date(rs.getTimestamp("registered_at").getTime());
        
        return new ActivityParticipant(id, activityId, playerUuid, playerName, status, registeredAt);
    }
}
