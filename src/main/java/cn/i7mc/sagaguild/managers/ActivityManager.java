package cn.i7mc.sagaguild.managers;

import cn.i7mc.sagaguild.SagaGuild;
import cn.i7mc.sagaguild.data.dao.ActivityDAO;
import cn.i7mc.sagaguild.data.dao.ParticipantDAO;
import cn.i7mc.sagaguild.data.models.ActivityParticipant;
import cn.i7mc.sagaguild.data.models.Guild;
import cn.i7mc.sagaguild.data.models.GuildActivity;
import cn.i7mc.sagaguild.data.models.GuildMember;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;

/**
 * 活动管理器
 * 负责公会活动的创建和管理
 */
public class ActivityManager {
    private final SagaGuild plugin;
    private final ActivityDAO activityDAO;
    private final ParticipantDAO participantDAO;

    // 缓存公会活动
    private final Map<Integer, List<GuildActivity>> guildActivities;

    // 活动通知任务
    private BukkitTask notificationTask;

    // 活动通知时间（分钟）
    private final int[] notificationTimes = {60, 30, 15, 5, 1};

    // 活动通知记录
    private final Map<Integer, Set<Integer>> activityNotifications;

    public ActivityManager(SagaGuild plugin) {
        this.plugin = plugin;
        this.activityDAO = new ActivityDAO(plugin);
        this.participantDAO = new ParticipantDAO(plugin);

        this.guildActivities = new HashMap<>();
        this.activityNotifications = new HashMap<>();

        // 加载所有公会的活动
        loadActivities();

        // 启动活动通知任务
        startNotificationTask();
    }

    /**
     * 从数据库加载所有公会的活动
     */
    private void loadActivities() {
        // 清空缓存
        guildActivities.clear();

        // 加载所有公会的活动
        for (Guild guild : plugin.getGuildManager().getAllGuilds()) {
            List<GuildActivity> activities = activityDAO.getGuildActivities(guild.getId());
            guildActivities.put(guild.getId(), activities);
        }

        plugin.getLogger().info("已加载 " + guildActivities.size() + " 个公会的活动数据！");
    }

    /**
     * 启动活动通知任务
     */
    private void startNotificationTask() {
        // 每分钟检查一次活动状态
        notificationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkActivities, 20 * 60, 20 * 60);
    }

    /**
     * 检查活动状态
     */
    private void checkActivities() {
        // 当前时间
        long currentTime = System.currentTimeMillis();

        // 检查所有公会的活动
        for (Map.Entry<Integer, List<GuildActivity>> entry : guildActivities.entrySet()) {
            int guildId = entry.getKey();
            List<GuildActivity> activities = entry.getValue();

            // 检查每个活动
            for (GuildActivity activity : activities) {
                // 检查活动状态
                if (activity.getStatus() == GuildActivity.Status.PLANNED && activity.getStartTime() != null) {
                    // 计算距离活动开始的时间（分钟）
                    long minutesUntilStart = (activity.getStartTime().getTime() - currentTime) / (1000 * 60);

                    // 检查是否需要发送通知
                    for (int notificationTime : notificationTimes) {
                        if (minutesUntilStart <= notificationTime && minutesUntilStart > notificationTime - 1) {
                            // 检查是否已经发送过该时间点的通知
                            Set<Integer> sentNotifications = activityNotifications.computeIfAbsent(activity.getId(), k -> new HashSet<>());
                            if (!sentNotifications.contains(notificationTime)) {
                                // 发送通知
                                sendActivityNotification(activity, notificationTime);
                                // 记录已发送通知
                                sentNotifications.add(notificationTime);
                            }
                        }
                    }

                    // 检查活动是否已开始
                    if (minutesUntilStart <= 0 && activity.getStatus() == GuildActivity.Status.PLANNED) {
                        // 更新活动状态为进行中
                        activity.setStatus(GuildActivity.Status.ONGOING);
                        activityDAO.updateActivity(activity);

                        // 发送活动开始通知
                        sendActivityStartNotification(activity);
                    }
                }

                // 检查活动是否已结束
                if (activity.getStatus() == GuildActivity.Status.ONGOING && activity.getEndTime() != null && activity.getEndTime().getTime() <= currentTime) {
                    // 更新活动状态为已完成
                    activity.setStatus(GuildActivity.Status.COMPLETED);
                    activityDAO.updateActivity(activity);

                    // 发送活动结束通知
                    sendActivityEndNotification(activity);
                }
            }
        }
    }

    /**
     * 发送活动通知
     * @param activity 活动对象
     * @param minutesUntilStart 距离开始的分钟数
     */
    private void sendActivityNotification(GuildActivity activity, int minutesUntilStart) {
        Guild guild = plugin.getGuildManager().getGuildById(activity.getGuildId());
        if (guild == null) {
            return;
        }

        // 获取公会成员
        List<GuildMember> members = plugin.getGuildManager().getGuildMembers(guild.getId());

        // 通知所有在线成员
        for (GuildMember member : members) {
            Player player = Bukkit.getPlayer(member.getPlayerUuid());
            if (player != null && player.isOnline()) {
                // 发送消息
                player.sendMessage(Component.text("公会活动提醒: ", NamedTextColor.GOLD)
                        .append(Component.text(activity.getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" 将在 ", NamedTextColor.GOLD))
                        .append(Component.text(minutesUntilStart, NamedTextColor.RED))
                        .append(Component.text(" 分钟后开始！", NamedTextColor.GOLD)));

                // 播放声音
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
    }

    /**
     * 发送活动开始通知
     * @param activity 活动对象
     */
    private void sendActivityStartNotification(GuildActivity activity) {
        Guild guild = plugin.getGuildManager().getGuildById(activity.getGuildId());
        if (guild == null) {
            return;
        }

        // 获取公会成员
        List<GuildMember> members = plugin.getGuildManager().getGuildMembers(guild.getId());

        // 通知所有在线成员
        for (GuildMember member : members) {
            Player player = Bukkit.getPlayer(member.getPlayerUuid());
            if (player != null && player.isOnline()) {
                // 发送标题
                Title title = Title.title(
                        Component.text("活动开始", NamedTextColor.GREEN),
                        Component.text(activity.getName(), NamedTextColor.YELLOW),
                        Title.Times.of(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
                );
                player.showTitle(title);

                // 发送消息
                player.sendMessage(Component.text("公会活动 ", NamedTextColor.GOLD)
                        .append(Component.text(activity.getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" 已经开始！", NamedTextColor.GOLD)));

                // 播放声音
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
        }
    }

    /**
     * 发送活动结束通知
     * @param activity 活动对象
     */
    private void sendActivityEndNotification(GuildActivity activity) {
        Guild guild = plugin.getGuildManager().getGuildById(activity.getGuildId());
        if (guild == null) {
            return;
        }

        // 获取公会成员
        List<GuildMember> members = plugin.getGuildManager().getGuildMembers(guild.getId());

        // 通知所有在线成员
        for (GuildMember member : members) {
            Player player = Bukkit.getPlayer(member.getPlayerUuid());
            if (player != null && player.isOnline()) {
                // 发送消息
                player.sendMessage(Component.text("公会活动 ", NamedTextColor.GOLD)
                        .append(Component.text(activity.getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" 已经结束！", NamedTextColor.GOLD)));

                // 播放声音
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.5f);
            }
        }
    }

    /**
     * 创建公会活动
     * @param guildId 公会ID
     * @param name 活动名称
     * @param description 活动描述
     * @param type 活动类型
     * @param creatorUuid 创建者UUID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param location 活动地点
     * @param maxParticipants 最大参与人数
     * @return 创建的活动对象，失败返回null
     */
    public GuildActivity createActivity(int guildId, String name, String description, GuildActivity.Type type,
                                      UUID creatorUuid, Date startTime, Date endTime, String location, int maxParticipants) {
        // 创建活动对象
        GuildActivity activity = new GuildActivity(guildId, name, description, type, creatorUuid, startTime, endTime, location, maxParticipants);

        // 保存到数据库
        int activityId = activityDAO.createActivity(activity);
        if (activityId == -1) {
            return null;
        }

        // 添加到缓存
        List<GuildActivity> activities = guildActivities.computeIfAbsent(guildId, k -> new ArrayList<>());
        activities.add(activity);

        return activity;
    }

    /**
     * 更新公会活动
     * @param activity 活动对象
     * @return 是否成功
     */
    public boolean updateActivity(GuildActivity activity) {
        // 更新数据库
        boolean success = activityDAO.updateActivity(activity);
        if (!success) {
            return false;
        }

        return true;
    }

    /**
     * 取消公会活动
     * @param activityId 活动ID
     * @return 是否成功
     */
    public boolean cancelActivity(int activityId) {
        // 获取活动
        GuildActivity activity = getActivityById(activityId);
        if (activity == null) {
            return false;
        }

        // 更新活动状态
        activity.setStatus(GuildActivity.Status.CANCELLED);

        // 更新数据库
        boolean success = activityDAO.updateActivity(activity);
        if (!success) {
            return false;
        }

        return true;
    }

    /**
     * 删除公会活动
     * @param activityId 活动ID
     * @return 是否成功
     */
    public boolean deleteActivity(int activityId) {
        // 获取活动
        GuildActivity activity = getActivityById(activityId);
        if (activity == null) {
            return false;
        }

        // 删除数据库中的活动
        boolean success = activityDAO.deleteActivity(activityId);
        if (!success) {
            return false;
        }

        // 从缓存中移除
        List<GuildActivity> activities = guildActivities.get(activity.getGuildId());
        if (activities != null) {
            activities.removeIf(a -> a.getId() == activityId);
        }

        return true;
    }

    /**
     * 参与公会活动
     * @param activityId 活动ID
     * @param playerUuid 玩家UUID
     * @param playerName 玩家名称
     * @return 是否成功
     */
    public boolean joinActivity(int activityId, UUID playerUuid, String playerName) {
        // 获取活动
        GuildActivity activity = getActivityById(activityId);
        if (activity == null) {
            return false;
        }

        // 检查活动状态
        if (activity.getStatus() != GuildActivity.Status.PLANNED) {
            return false;
        }

        // 检查是否已参与
        if (participantDAO.isPlayerParticipating(activityId, playerUuid)) {
            return false;
        }

        // 检查参与人数是否已满
        int participantCount = participantDAO.getParticipantCount(activityId);
        if (activity.getMaxParticipants() > 0 && participantCount >= activity.getMaxParticipants()) {
            return false;
        }

        // 创建参与者对象
        ActivityParticipant participant = new ActivityParticipant(activityId, playerUuid, playerName);

        // 保存到数据库
        int participantId = participantDAO.addParticipant(participant);

        return participantId != -1;
    }

    /**
     * 退出公会活动
     * @param activityId 活动ID
     * @param playerUuid 玩家UUID
     * @return 是否成功
     */
    public boolean leaveActivity(int activityId, UUID playerUuid) {
        // 获取活动
        GuildActivity activity = getActivityById(activityId);
        if (activity == null) {
            return false;
        }

        // 检查活动状态
        if (activity.getStatus() != GuildActivity.Status.PLANNED) {
            return false;
        }

        // 获取参与者列表
        List<ActivityParticipant> participants = participantDAO.getActivityParticipants(activityId);

        // 查找玩家的参与记录
        for (ActivityParticipant participant : participants) {
            if (participant.getPlayerUuid().equals(playerUuid)) {
                // 删除参与记录
                return participantDAO.deleteParticipant(participant.getId());
            }
        }

        return false;
    }

    /**
     * 获取活动
     * @param activityId 活动ID
     * @return 活动对象，不存在返回null
     */
    public GuildActivity getActivityById(int activityId) {
        // 先从缓存中查找
        for (List<GuildActivity> activities : guildActivities.values()) {
            for (GuildActivity activity : activities) {
                if (activity.getId() == activityId) {
                    return activity;
                }
            }
        }

        // 从数据库中查找
        return activityDAO.getActivityById(activityId);
    }

    /**
     * 获取公会活动列表
     * @param guildId 公会ID
     * @return 活动列表
     */
    public List<GuildActivity> getGuildActivities(int guildId) {
        return guildActivities.getOrDefault(guildId, new ArrayList<>());
    }

    /**
     * 获取公会即将开始的活动
     * @param guildId 公会ID
     * @return 活动列表
     */
    public List<GuildActivity> getUpcomingGuildActivities(int guildId) {
        return activityDAO.getUpcomingGuildActivities(guildId);
    }

    /**
     * 获取活动参与者列表
     * @param activityId 活动ID
     * @return 参与者列表
     */
    public List<ActivityParticipant> getActivityParticipants(int activityId) {
        return participantDAO.getActivityParticipants(activityId);
    }

    /**
     * 获取玩家参与的活动
     * @param playerUuid 玩家UUID
     * @return 参与者列表
     */
    public List<ActivityParticipant> getPlayerParticipations(UUID playerUuid) {
        return participantDAO.getPlayerParticipations(playerUuid);
    }
}
