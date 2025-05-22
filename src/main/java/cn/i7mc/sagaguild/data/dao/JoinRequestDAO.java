package cn.i7mc.sagaguild.data.dao;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.DatabaseManager;
import cn.i7mc.sagaguild.data.models.JoinRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 公会加入请求数据访问对象
 * 提供公会加入请求数据的CRUD操作
 */
public class JoinRequestDAO {
    private final SagaGuild plugin;
    private final DatabaseManager databaseManager;

    public JoinRequestDAO(SagaGuild plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }

    /**
     * 创建加入请求
     * @param request 请求对象
     * @return 创建的请求ID，失败返回-1
     */
    public int createJoinRequest(JoinRequest request) {
        String sql = "INSERT INTO join_requests (player_uuid, player_name, guild_id, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, request.getPlayerUuid().toString());
            stmt.setString(2, request.getPlayerName());
            stmt.setInt(3, request.getGuildId());
            stmt.setString(4, request.getStatus().name());



            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                plugin.getLogger().warning("创建加入请求失败: 没有行被插入");
                return -1;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    request.setId(id);
                    return id;
                } else {
                    plugin.getLogger().warning("创建加入请求失败: 未获取到ID");
                    return -1;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("创建加入请求失败: " + e.getMessage());
            return -1;
        }
    }

    /**
     * 获取公会的所有加入请求
     * @param guildId 公会ID
     * @return 请求列表
     */
    public List<JoinRequest> getGuildJoinRequests(int guildId) {
        String sql = "SELECT * FROM join_requests WHERE guild_id = ? AND status = 'PENDING'";
        List<JoinRequest> requests = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guildId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(extractJoinRequestFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会加入请求失败: " + e.getMessage());
        }

        return requests;
    }

    /**
     * 根据ID获取加入请求
     * @param requestId 请求ID
     * @return 请求对象，不存在返回null
     */
    public JoinRequest getJoinRequestById(int requestId) {
        String sql = "SELECT * FROM join_requests WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = databaseManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, requestId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                JoinRequest request = extractJoinRequestFromResultSet(rs);
                return request;
            } else {
                plugin.getLogger().warning("未找到ID为 " + requestId + " 的加入请求");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取加入请求失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 只关闭语句和结果集，不关闭连接
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("关闭资源失败: " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * 获取玩家的所有加入请求
     * @param playerUuid 玩家UUID
     * @return 请求列表
     */
    public List<JoinRequest> getPlayerJoinRequests(UUID playerUuid) {
        String sql = "SELECT * FROM join_requests WHERE player_uuid = ? AND status = 'PENDING'";
        List<JoinRequest> requests = new ArrayList<>();

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerUuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(extractJoinRequestFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取玩家加入请求失败: " + e.getMessage());
        }

        return requests;
    }

    /**
     * 更新加入请求状态
     * @param requestId 请求ID
     * @param status 新状态
     * @return 是否成功
     */
    public boolean updateJoinRequestStatus(int requestId, JoinRequest.Status status) {
        String sql = "UPDATE join_requests SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = databaseManager.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, status.name());
            stmt.setInt(2, requestId);

            int result = stmt.executeUpdate();
            boolean success = result > 0;

            return success;
        } catch (SQLException e) {
            plugin.getLogger().severe("更新加入请求状态失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // 只关闭语句，不关闭连接
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("关闭语句失败: " + e.getMessage());
            }
        }
    }

    /**
     * 删除玩家的所有加入请求
     * @param playerUuid 玩家UUID
     * @return 是否成功
     */
    public boolean deletePlayerJoinRequests(UUID playerUuid) {
        String sql = "DELETE FROM join_requests WHERE player_uuid = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerUuid.toString());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("删除玩家加入请求失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 从结果集中提取加入请求对象
     * @param rs 结果集
     * @return 加入请求对象
     * @throws SQLException SQL异常
     */
    private JoinRequest extractJoinRequestFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
        String playerName = rs.getString("player_name");
        int guildId = rs.getInt("guild_id");
        Timestamp requestedAt = rs.getTimestamp("requested_at");
        JoinRequest.Status status = JoinRequest.Status.valueOf(rs.getString("status"));

        return new JoinRequest(id, playerUuid, playerName, guildId, requestedAt, status);
    }
}
