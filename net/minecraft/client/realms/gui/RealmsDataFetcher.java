/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerLists;
import net.minecraft.client.realms.util.RealmsPersistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsDataFetcher {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private volatile boolean stopped = true;
    private final Runnable serverListUpdateTask = new ServerListUpdateTask();
    private final Runnable pendingInviteUpdateTask = new PendingInviteUpdateTask();
    private final Runnable trialAvailabilityTask = new TrialAvailabilityTask();
    private final Runnable liveStatsTask = new LiveStatsTask();
    private final Runnable unreadNewsTask = new UnreadNewsTask();
    private final Set<RealmsServer> removedServers = Sets.newHashSet();
    private List<RealmsServer> servers = Lists.newArrayList();
    private RealmsServerPlayerLists livestats;
    private int pendingInvitesCount;
    private boolean trialAvailable;
    private boolean hasUnreadNews;
    private String newsLink;
    private ScheduledFuture<?> serverListScheduledFuture;
    private ScheduledFuture<?> pendingInviteScheduledFuture;
    private ScheduledFuture<?> trialAvailableScheduledFuture;
    private ScheduledFuture<?> liveStatsScheduledFuture;
    private ScheduledFuture<?> unreadNewsScheduledFuture;
    private final Map<Task, Boolean> fetchStatus = new ConcurrentHashMap<Task, Boolean>(Task.values().length);

    public boolean isStopped() {
        return this.stopped;
    }

    public synchronized void init() {
        if (this.stopped) {
            this.stopped = false;
            this.cancelTasks();
            this.scheduleTasks();
        }
    }

    public synchronized void initWithSpecificTaskList() {
        if (this.stopped) {
            this.stopped = false;
            this.cancelTasks();
            this.fetchStatus.put(Task.PENDING_INVITE, false);
            this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
            this.fetchStatus.put(Task.TRIAL_AVAILABLE, false);
            this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
            this.fetchStatus.put(Task.UNREAD_NEWS, false);
            this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
        }
    }

    public boolean isFetchedSinceLastTry(Task task) {
        Boolean boolean_ = this.fetchStatus.get((Object)task);
        return boolean_ == null ? false : boolean_;
    }

    public void markClean() {
        for (Task lv : this.fetchStatus.keySet()) {
            this.fetchStatus.put(lv, false);
        }
    }

    public synchronized void forceUpdate() {
        this.stop();
        this.init();
    }

    public synchronized List<RealmsServer> getServers() {
        return Lists.newArrayList(this.servers);
    }

    public synchronized int getPendingInvitesCount() {
        return this.pendingInvitesCount;
    }

    public synchronized boolean isTrialAvailable() {
        return this.trialAvailable;
    }

    public synchronized RealmsServerPlayerLists getLivestats() {
        return this.livestats;
    }

    public synchronized boolean hasUnreadNews() {
        return this.hasUnreadNews;
    }

    public synchronized String newsLink() {
        return this.newsLink;
    }

    public synchronized void stop() {
        this.stopped = true;
        this.cancelTasks();
    }

    private void scheduleTasks() {
        for (Task lv : Task.values()) {
            this.fetchStatus.put(lv, false);
        }
        this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate(this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
        this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
        this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
        this.liveStatsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.liveStatsTask, 0L, 10L, TimeUnit.SECONDS);
        this.unreadNewsScheduledFuture = this.scheduler.scheduleAtFixedRate(this.unreadNewsTask, 0L, 300L, TimeUnit.SECONDS);
    }

    private void cancelTasks() {
        try {
            if (this.serverListScheduledFuture != null) {
                this.serverListScheduledFuture.cancel(false);
            }
            if (this.pendingInviteScheduledFuture != null) {
                this.pendingInviteScheduledFuture.cancel(false);
            }
            if (this.trialAvailableScheduledFuture != null) {
                this.trialAvailableScheduledFuture.cancel(false);
            }
            if (this.liveStatsScheduledFuture != null) {
                this.liveStatsScheduledFuture.cancel(false);
            }
            if (this.unreadNewsScheduledFuture != null) {
                this.unreadNewsScheduledFuture.cancel(false);
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to cancel Realms tasks", (Throwable)exception);
        }
    }

    private synchronized void setServers(List<RealmsServer> newServers) {
        int i = 0;
        for (RealmsServer lv : this.removedServers) {
            if (!newServers.remove(lv)) continue;
            ++i;
        }
        if (i == 0) {
            this.removedServers.clear();
        }
        this.servers = newServers;
    }

    public synchronized void removeItem(RealmsServer server) {
        this.servers.remove(server);
        this.removedServers.add(server);
    }

    private boolean isActive() {
        return !this.stopped;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Task {
        SERVER_LIST,
        PENDING_INVITE,
        TRIAL_AVAILABLE,
        LIVE_STATS,
        UNREAD_NEWS;

    }

    @Environment(value=EnvType.CLIENT)
    class UnreadNewsTask
    implements Runnable {
        private UnreadNewsTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getUnreadNews();
            }
        }

        private void getUnreadNews() {
            try {
                String string;
                RealmsClient lv = RealmsClient.createRealmsClient();
                RealmsNews lv2 = null;
                try {
                    lv2 = lv.getNews();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                RealmsPersistence.RealmsPersistenceData lv3 = RealmsPersistence.readFile();
                if (lv2 != null && (string = lv2.newsLink) != null && !string.equals(lv3.newsLink)) {
                    lv3.hasUnreadNews = true;
                    lv3.newsLink = string;
                    RealmsPersistence.writeFile(lv3);
                }
                RealmsDataFetcher.this.hasUnreadNews = lv3.hasUnreadNews;
                RealmsDataFetcher.this.newsLink = lv3.newsLink;
                RealmsDataFetcher.this.fetchStatus.put(Task.UNREAD_NEWS, true);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't get unread news", (Throwable)exception);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class LiveStatsTask
    implements Runnable {
        private LiveStatsTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getLiveStats();
            }
        }

        private void getLiveStats() {
            try {
                RealmsClient lv = RealmsClient.createRealmsClient();
                RealmsDataFetcher.this.livestats = lv.getLiveStats();
                RealmsDataFetcher.this.fetchStatus.put(Task.LIVE_STATS, true);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't get live stats", (Throwable)exception);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class TrialAvailabilityTask
    implements Runnable {
        private TrialAvailabilityTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getTrialAvailable();
            }
        }

        private void getTrialAvailable() {
            try {
                RealmsClient lv = RealmsClient.createRealmsClient();
                RealmsDataFetcher.this.trialAvailable = lv.trialAvailable();
                RealmsDataFetcher.this.fetchStatus.put(Task.TRIAL_AVAILABLE, true);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't get trial availability", (Throwable)exception);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PendingInviteUpdateTask
    implements Runnable {
        private PendingInviteUpdateTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.updatePendingInvites();
            }
        }

        private void updatePendingInvites() {
            try {
                RealmsClient lv = RealmsClient.createRealmsClient();
                RealmsDataFetcher.this.pendingInvitesCount = lv.pendingInvitesCount();
                RealmsDataFetcher.this.fetchStatus.put(Task.PENDING_INVITE, true);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't get pending invite count", (Throwable)exception);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class ServerListUpdateTask
    implements Runnable {
        private ServerListUpdateTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.updateServersList();
            }
        }

        private void updateServersList() {
            try {
                RealmsClient lv = RealmsClient.createRealmsClient();
                List<RealmsServer> list = lv.listWorlds().servers;
                if (list != null) {
                    list.sort(new RealmsServer.McoServerComparator(MinecraftClient.getInstance().getSession().getUsername()));
                    RealmsDataFetcher.this.setServers(list);
                    RealmsDataFetcher.this.fetchStatus.put(Task.SERVER_LIST, true);
                } else {
                    LOGGER.warn("Realms server list was null or empty");
                }
            }
            catch (Exception exception) {
                RealmsDataFetcher.this.fetchStatus.put(Task.SERVER_LIST, true);
                LOGGER.error("Couldn't get server list", (Throwable)exception);
            }
        }
    }
}

