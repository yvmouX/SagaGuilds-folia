package cn.i7mc.sagaguild.data.dao;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.DatabaseManager;
import cn.i7mc.sagaguild.data.models.GuildTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 公会任务数据访问对象
 * 提供公会任务数据的CRUD操作
 */
public class TaskDAO {
    private final SagaGuild plugin;
    private final DatabaseManager databaseManager;
    
    public TaskDAO(SagaGuild plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }
    
    /**
     * 创建任务
     * @param task 任务对象
     * @return 创建的任务ID，失败返回-1
     */
    public int createTask(GuildTask task) {
        String sql = "INSERT INTO tasks (guild_id, type, description, target, progress, reward_exp, reward_money, expires_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, task.getGuildId());
            stmt.setString(2, task.getType().name());
            stmt.setString(3, task.getDescription());
            stmt.setInt(4, task.getTarget());
            stmt.setInt(5, task.getProgress());
            stmt.setInt(6, task.getRewardExp());
            stmt.setDouble(7, task.getRewardMoney());
            
            if (task.getExpiresAt() != null) {
                stmt.setTimestamp(8, new Timestamp(task.getExpiresAt().getTime()));
            } else {
                stmt.setNull(8, Types.TIMESTAMP);
            }
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    task.setId(id);
                    return id;
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("创建任务失败: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * 更新任务进度
     * @param taskId 任务ID
     * @param progress 进度
     * @param completedAt 完成时间，未完成为null
     * @return 是否成功
     */
    public boolean updateTaskProgress(int taskId, int progress, Date completedAt) {
        String sql = "UPDATE tasks SET progress = ?, completed_at = ? WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, progress);
            
            if (completedAt != null) {
                stmt.setTimestamp(2, new Timestamp(completedAt.getTime()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            
            stmt.setInt(3, taskId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("更新任务进度失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 删除任务
     * @param taskId 任务ID
     * @return 是否成功
     */
    public boolean deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, taskId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("删除任务失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 根据ID获取任务
     * @param id 任务ID
     * @return 任务对象，不存在返回null
     */
    public GuildTask getTaskById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractTaskFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取任务失败: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 获取公会所有任务
     * @param guildId 公会ID
     * @return 任务列表
     */
    public List<GuildTask> getGuildTasks(int guildId) {
        List<GuildTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE guild_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会任务列表失败: " + e.getMessage());
        }
        
        return tasks;
    }
    
    /**
     * 获取公会活跃任务
     * @param guildId 公会ID
     * @return 任务列表
     */
    public List<GuildTask> getActiveGuildTasks(int guildId) {
        List<GuildTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE guild_id = ? AND completed_at IS NULL AND (expires_at IS NULL OR expires_at > ?) ORDER BY created_at DESC";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会活跃任务列表失败: " + e.getMessage());
        }
        
        return tasks;
    }
    
    /**
     * 获取公会已完成任务
     * @param guildId 公会ID
     * @return 任务列表
     */
    public List<GuildTask> getCompletedGuildTasks(int guildId) {
        List<GuildTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE guild_id = ? AND completed_at IS NOT NULL ORDER BY completed_at DESC";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(extractTaskFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会已完成任务列表失败: " + e.getMessage());
        }
        
        return tasks;
    }
    
    /**
     * 从结果集中提取任务对象
     * @param rs 结果集
     * @return 任务对象
     * @throws SQLException SQL异常
     */
    private GuildTask extractTaskFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int guildId = rs.getInt("guild_id");
        GuildTask.Type type = GuildTask.Type.valueOf(rs.getString("type"));
        String description = rs.getString("description");
        int target = rs.getInt("target");
        int progress = rs.getInt("progress");
        int rewardExp = rs.getInt("reward_exp");
        double rewardMoney = rs.getDouble("reward_money");
        Date createdAt = new Date(rs.getTimestamp("created_at").getTime());
        
        Timestamp expiresAtTimestamp = rs.getTimestamp("expires_at");
        Date expiresAt = expiresAtTimestamp != null ? new Date(expiresAtTimestamp.getTime()) : null;
        
        Timestamp completedAtTimestamp = rs.getTimestamp("completed_at");
        Date completedAt = completedAtTimestamp != null ? new Date(completedAtTimestamp.getTime()) : null;
        
        return new GuildTask(id, guildId, type, description, target, progress, rewardExp, rewardMoney, createdAt, expiresAt, completedAt);
    }
}
