package cn.i7mc.sagaguild.data.dao;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 公会银行数据访问对象
 * 提供公会银行数据的CRUD操作
 */
public class BankDAO {
    private final SagaGuild plugin;
    private final DatabaseManager databaseManager;
    
    public BankDAO(SagaGuild plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }
    
    /**
     * 获取公会银行余额
     * @param guildId 公会ID
     * @return 银行余额
     */
    public double getBalance(int guildId) {
        String sql = "SELECT balance FROM banks WHERE guild_id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会银行余额失败: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * 获取公会银行容量
     * @param guildId 公会ID
     * @return 银行容量
     */
    public double getCapacity(int guildId) {
        String sql = "SELECT capacity FROM banks WHERE guild_id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guildId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("capacity");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("获取公会银行容量失败: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * 设置公会银行余额
     * @param guildId 公会ID
     * @param balance 银行余额
     * @return 是否成功
     */
    public boolean setBalance(int guildId, double balance) {
        String sql = "UPDATE banks SET balance = ? WHERE guild_id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, balance);
            stmt.setInt(2, guildId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("设置公会银行余额失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 设置公会银行容量
     * @param guildId 公会ID
     * @param capacity 银行容量
     * @return 是否成功
     */
    public boolean setCapacity(int guildId, double capacity) {
        String sql = "UPDATE banks SET capacity = ? WHERE guild_id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, capacity);
            stmt.setInt(2, guildId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("设置公会银行容量失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 存款
     * @param guildId 公会ID
     * @param amount 金额
     * @return 是否成功
     */
    public boolean deposit(int guildId, double amount) {
        String sql = "UPDATE banks SET balance = balance + ? WHERE guild_id = ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, amount);
            stmt.setInt(2, guildId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("公会银行存款失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 取款
     * @param guildId 公会ID
     * @param amount 金额
     * @return 是否成功
     */
    public boolean withdraw(int guildId, double amount) {
        String sql = "UPDATE banks SET balance = balance - ? WHERE guild_id = ? AND balance >= ?";
        
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, amount);
            stmt.setInt(2, guildId);
            stmt.setDouble(3, amount);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("公会银行取款失败: " + e.getMessage());
            return false;
        }
    }
}
