package cn.i7mc.sagaguild.data.dao;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.DatabaseManager;
import cn.i7mc.sagaguild.data.models.ActivityParticipant;
import cn.i7mc.sagaguild.data.models.GuildActivity;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 公会活动数据访问对象
 * 提供公会活动数据的CRUD操作
 */
public class ActivityDAO {
    private final SagaGuild plugin;
    private final DatabaseManager databaseManager;
    
    public ActivityDAO(SagaGuild plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }
    
    /**
     * 创建活动
     * @param activity 活动对象
     * @return 创建的活动ID，失败返回-1
     */
    public int createActivity(GuildActivity activity) {
        String sql = "INSERT INTO activities (guild_id, name, description, type, creator_uuid, start_time, end_time, location, max_participants, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, activity.getGuildId());
            stmt.setString(2, activity.getName());
            stmt.setString(3, activity.getDescription());
            stmt.setString(4, activity.getType().name());
            stmt.setString(5, activity.getCreatorUuid().toString());
            
            if (activity.getStartTime() != null) {
                stmt.setTimestamp(6, new Timestamp(activity.getStartTime().getTime()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            
            if (activity.getEndTime() != null) {
                stmt.setTimestamp(7, new Timestamp(activity.getEndTime().getTime()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }
            
            stmt.setString(8, activity.getLocation());
            stmt.setInt(9, activity.getMaxParticipants());
            stmt.setString(10, activity.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    activity.setId(id);
                    return id;
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("创建活动失败: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * 更新活动
     * @param activity 活动对象
     * @return 是否成功
     */
    public boolean updateActivity(GuildActivity activity) {
        String sql = "UPDATE activities SET name = ?, description = ?, type = ?, start_time = ?, end_time = ?, " +
                     "location = ?, max_participants = ?, status = ? WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, activity.getName());
            stmt.setString(2, activity.getDescription());
            stmt.setString(3, activity.getType().name());
            
            if (activity.getStartTime() != null) {
                stmt.setTimestamp(4, new Timestamp(activity.getStartTime().getTime()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            if (activity.getEndTime() != null) {
                stmt.setTimestamp(5, new Timestamp(activity.getEndTime().getTime()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            stmt.setString(6, activity.getLocation());
            stmt.setInt(7, activity.getMaxParticipants());
            stmt.setString(8, activity.getStatus().name());
            stmt.setInt(9, activity.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("更新活动失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 删除活动
     * @param activityId 活动ID
     * @return 是否成功
     */
    public boolean deleteActivity(int activityId) {
        String sql = "DELETE FROM activities WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, activityId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("删除活动失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 根据ID获取活动
     * @param id 活动ID
     * @return 活动对象，不存在返回null
     */
    public GuildActivity getActivityById(int id) {
        String sql = "SELECT * FROM activities WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractActivityFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取活动失败: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 获取公会所有活动
     * @param guildId 公会ID
     * @return 活动列表
     */
    public List<GuildActivity> getGuildActivities(int guildId) {
        List<GuildActivity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities WHERE guild_id = ? ORDER BY start_time DESC";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(extractActivityFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会活动列表失败: " + e.getMessage());
        }
        
        return activities;
    }
    
    /**
     * 获取公会即将开始的活动
     * @param guildId 公会ID
     * @return 活动列表
     */
    public List<GuildActivity> getUpcomingGuildActivities(int guildId) {
        List<GuildActivity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities WHERE guild_id = ? AND status = ? AND start_time > ? ORDER BY start_time ASC";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            stmt.setString(2, GuildActivity.Status.PLANNED.name());
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(extractActivityFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会即将开始的活动列表失败: " + e.getMessage());
        }
        
        return activities;
    }
    
    /**
     * 从结果集中提取活动对象
     * @param rs 结果集
     * @return 活动对象
     * @throws SQLException SQL异常
     */
    private GuildActivity extractActivityFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int guildId = rs.getInt("guild_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        GuildActivity.Type type = GuildActivity.Type.valueOf(rs.getString("type"));
        UUID creatorUuid = UUID.fromString(rs.getString("creator_uuid"));
        
        Timestamp startTimeTimestamp = rs.getTimestamp("start_time");
        Date startTime = startTimeTimestamp != null ? new Date(startTimeTimestamp.getTime()) : null;
        
        Timestamp endTimeTimestamp = rs.getTimestamp("end_time");
        Date endTime = endTimeTimestamp != null ? new Date(endTimeTimestamp.getTime()) : null;
        
        String location = rs.getString("location");
        int maxParticipants = rs.getInt("max_participants");
        Date createdAt = new Date(rs.getTimestamp("created_at").getTime());
        GuildActivity.Status status = GuildActivity.Status.valueOf(rs.getString("status"));
        
        return new GuildActivity(id, guildId, name, description, type, creatorUuid, startTime, endTime, location, maxParticipants, createdAt, status);
    }
}
