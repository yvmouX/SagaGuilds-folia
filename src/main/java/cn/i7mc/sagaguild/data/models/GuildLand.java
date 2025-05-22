package cn.i7mc.sagaguild.data.models;

import java.util.Date;

/**
 * 公会领地数据模型
 */
public class GuildLand {
    private int id;
    private int guildId;
    private String world;
    private int chunkX;
    private int chunkZ;
    private Date claimedAt;
    
    /**
     * 创建一个新的公会领地对象
     * @param id 领地ID
     * @param guildId 公会ID
     * @param world 世界名称
     * @param chunkX 区块X坐标
     * @param chunkZ 区块Z坐标
     * @param claimedAt 声明时间
     */
    public GuildLand(int id, int guildId, String world, int chunkX, int chunkZ, Date claimedAt) {
        this.id = id;
        this.guildId = guildId;
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.claimedAt = claimedAt;
    }
    
    /**
     * 创建一个新的公会领地对象（用于新声明领地）
     * @param guildId 公会ID
     * @param world 世界名称
     * @param chunkX 区块X坐标
     * @param chunkZ 区块Z坐标
     */
    public GuildLand(int guildId, String world, int chunkX, int chunkZ) {
        this.id = 0; // 未保存到数据库
        this.guildId = guildId;
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.claimedAt = new Date();
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
    
    public String getWorld() {
        return world;
    }
    
    public void setWorld(String world) {
        this.world = world;
    }
    
    public int getChunkX() {
        return chunkX;
    }
    
    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }
    
    public int getChunkZ() {
        return chunkZ;
    }
    
    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }
    
    public Date getClaimedAt() {
        return claimedAt;
    }
    
    public void setClaimedAt(Date claimedAt) {
        this.claimedAt = claimedAt;
    }
    
    /**
     * 获取区块坐标字符串
     * @return 区块坐标字符串，格式为 "x,z"
     */
    public String getChunkCoords() {
        return chunkX + "," + chunkZ;
    }
    
    /**
     * 获取区块唯一标识
     * @return 区块唯一标识，格式为 "world:x:z"
     */
    public String getChunkKey() {
        return world + ":" + chunkX + ":" + chunkZ;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        GuildLand land = (GuildLand) obj;
        
        if (chunkX != land.chunkX) return false;
        if (chunkZ != land.chunkZ) return false;
        return world.equals(land.world);
    }
    
    @Override
    public int hashCode() {
        int result = world.hashCode();
        result = 31 * result + chunkX;
        result = 31 * result + chunkZ;
        return result;
    }
}
