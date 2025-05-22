package cn.i7mc.sagaguild.data.dao;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.DatabaseManager;
import cn.i7mc.sagaguild.data.models.GuildLand;
import org.bukkit.Chunk;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 公会领地数据访问对象
 * 提供公会领地数据的CRUD操作
 */
public class LandDAO {
    private final SagaGuild plugin;
    private final DatabaseManager databaseManager;
    
    public LandDAO(SagaGuild plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }
    
    /**
     * 声明领地
     * @param land 领地对象
     * @return 创建的领地ID，失败返回-1
     */
    public int claimLand(GuildLand land) {
        String sql = "INSERT INTO lands (guild_id, world, chunk_x, chunk_z) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, land.getGuildId());
            stmt.setString(2, land.getWorld());
            stmt.setInt(3, land.getChunkX());
            stmt.setInt(4, land.getChunkZ());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    land.setId(id);
                    return id;
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("声明领地失败: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * 取消领地声明
     * @param id 领地ID
     * @return 是否成功
     */
    public boolean unclaimLand(int id) {
        String sql = "DELETE FROM lands WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("取消领地声明失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 取消公会所有领地声明
     * @param guildId 公会ID
     * @return 是否成功
     */
    public boolean unclaimAllLands(int guildId) {
        String sql = "DELETE FROM lands WHERE guild_id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("取消公会所有领地声明失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 根据ID获取领地
     * @param id 领地ID
     * @return 领地对象，不存在返回null
     */
    public GuildLand getLandById(int id) {
        String sql = "SELECT * FROM lands WHERE id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractLandFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取领地失败: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 根据区块获取领地
     * @param chunk 区块
     * @return 领地对象，不存在返回null
     */
    public GuildLand getLandByChunk(Chunk chunk) {
        String sql = "SELECT * FROM lands WHERE world = ? AND chunk_x = ? AND chunk_z = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, chunk.getWorld().getName());
            stmt.setInt(2, chunk.getX());
            stmt.setInt(3, chunk.getZ());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractLandFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取领地失败: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 获取公会所有领地
     * @param guildId 公会ID
     * @return 领地列表
     */
    public List<GuildLand> getGuildLands(int guildId) {
        List<GuildLand> lands = new ArrayList<>();
        String sql = "SELECT * FROM lands WHERE guild_id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lands.add(extractLandFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会领地列表失败: " + e.getMessage());
        }
        
        return lands;
    }
    
    /**
     * 获取公会领地数量
     * @param guildId 公会ID
     * @return 领地数量
     */
    public int getGuildLandCount(int guildId) {
        String sql = "SELECT COUNT(*) FROM lands WHERE guild_id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会领地数量失败: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * 从结果集中提取领地对象
     * @param rs 结果集
     * @return 领地对象
     * @throws SQLException SQL异常
     */
    private GuildLand extractLandFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int guildId = rs.getInt("guild_id");
        String world = rs.getString("world");
        int chunkX = rs.getInt("chunk_x");
        int chunkZ = rs.getInt("chunk_z");
        Date claimedAt = new Date(rs.getTimestamp("claimed_at").getTime());
        
        return new GuildLand(id, guildId, world, chunkX, chunkZ, claimedAt);
    }
}
