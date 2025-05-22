package cn.i7mc.sagaguild.data.dao;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.DatabaseManager;
import cn.i7mc.sagaguild.data.models.GuildMember;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 公会成员数据访问对象
 * 提供公会成员数据的CRUD操作
 */
public class MemberDAO {
    private final SagaGuild plugin;
    private final DatabaseManager databaseManager;

    public MemberDAO(SagaGuild plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }

    /**
     * 添加公会成员
     * @param member 成员对象
     * @return 创建的成员ID，失败返回-1
     */
    public int addMember(GuildMember member) {
        String sql = "INSERT INTO members (guild_id, player_uuid, player_name, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, member.getGuildId());
            stmt.setString(2, member.getPlayerUuid().toString());
            stmt.setString(3, member.getPlayerName());
            stmt.setString(4, member.getRole().name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    member.setId(id);
                    return id;
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("添加公会成员失败: " + e.getMessage());
            return -1;
        }
    }

    /**
     * 根据ID获取成员
     * @param id 成员ID
     * @return 成员对象，不存在返回null
     */
    public GuildMember getMemberById(int id) {
        String sql = "SELECT * FROM members WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMemberFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会成员失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * 根据玩家UUID获取成员
     * @param playerUuid 玩家UUID
     * @return 成员对象，不存在返回null
     */
    public GuildMember getMemberByUuid(UUID playerUuid) {
        String sql = "SELECT * FROM members WHERE player_uuid = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerUuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMemberFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会成员失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * 根据玩家UUID和公会ID获取成员
     * @param guildId 公会ID
     * @param playerUuid 玩家UUID
     * @return 成员对象，不存在返回null
     */
    public GuildMember getMemberByGuildAndUuid(int guildId, UUID playerUuid) {
        String sql = "SELECT * FROM members WHERE guild_id = ? AND player_uuid = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guildId);
            stmt.setString(2, playerUuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMemberFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会成员失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * 获取公会所有成员
     * @param guildId 公会ID
     * @return 成员列表
     */
    public List<GuildMember> getGuildMembers(int guildId) {
        List<GuildMember> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE guild_id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guildId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(extractMemberFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会成员列表失败: " + e.getMessage());
        }

        return members;
    }

    /**
     * 获取公会成员数量
     * @param guildId 公会ID
     * @return 成员数量
     */
    public int getGuildMemberCount(int guildId) {
        String sql = "SELECT COUNT(*) FROM members WHERE guild_id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guildId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会成员数量失败: " + e.getMessage());
        }

        return 0;
    }

    /**
     * 更新成员信息
     * @param member 成员对象
     * @return 是否成功
     */
    public boolean updateMember(GuildMember member) {
        String sql = "UPDATE members SET guild_id = ?, player_name = ?, role = ? WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, member.getGuildId());
            stmt.setString(2, member.getPlayerName());
            stmt.setString(3, member.getRole().name());
            stmt.setInt(4, member.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("更新公会成员失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除成员
     * @param id 成员ID
     * @return 是否成功
     */
    public boolean deleteMember(int id) {
        String sql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("删除公会成员失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除公会所有成员
     * @param guildId 公会ID
     * @return 是否成功
     */
    public boolean deleteAllGuildMembers(int guildId) {
        String sql = "DELETE FROM members WHERE guild_id = ?";

        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guildId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("删除公会所有成员失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 从结果集中提取成员对象
     * @param rs 结果集
     * @return 成员对象
     * @throws SQLException SQL异常
     */
    private GuildMember extractMemberFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int guildId = rs.getInt("guild_id");
        UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
        String playerName = rs.getString("player_name");
        GuildMember.Role role = GuildMember.Role.valueOf(rs.getString("role"));
        Date joinedAt = new Date(rs.getTimestamp("joined_at").getTime());

        return new GuildMember(id, guildId, playerUuid, playerName, role, joinedAt);
    }
}
