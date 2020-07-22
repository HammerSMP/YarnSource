/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsClientConfig;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.Request;
import net.minecraft.client.realms.dto.BackupList;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PendingInvitesList;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsDescriptionDto;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.dto.RealmsServerPlayerLists;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.RealmsWorldResetDto;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsHttpException;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@net.fabricmc.api.Environment(value=EnvType.CLIENT)
public class RealmsClient {
    public static Environment currentEnvironment = Environment.PRODUCTION;
    private static boolean initialized;
    private static final Logger LOGGER;
    private final String sessionId;
    private final String username;
    private static final CheckedGson JSON;

    public static RealmsClient createRealmsClient() {
        MinecraftClient lv = MinecraftClient.getInstance();
        String string = lv.getSession().getUsername();
        String string2 = lv.getSession().getSessionId();
        if (!initialized) {
            initialized = true;
            String string3 = System.getenv("realms.environment");
            if (string3 == null) {
                string3 = System.getProperty("realms.environment");
            }
            if (string3 != null) {
                if ("LOCAL".equals(string3)) {
                    RealmsClient.switchToLocal();
                } else if ("STAGE".equals(string3)) {
                    RealmsClient.switchToStage();
                }
            }
        }
        return new RealmsClient(string2, string, lv.getNetworkProxy());
    }

    public static void switchToStage() {
        currentEnvironment = Environment.STAGE;
    }

    public static void switchToProd() {
        currentEnvironment = Environment.PRODUCTION;
    }

    public static void switchToLocal() {
        currentEnvironment = Environment.LOCAL;
    }

    public RealmsClient(String sessionId, String username, Proxy proxy) {
        this.sessionId = sessionId;
        this.username = username;
        RealmsClientConfig.setProxy(proxy);
    }

    public RealmsServerList listWorlds() throws RealmsServiceException {
        String string = this.url("worlds");
        String string2 = this.execute(Request.get(string));
        return RealmsServerList.parse(string2);
    }

    public RealmsServer getOwnWorld(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get(string));
        return RealmsServer.parse(string2);
    }

    public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
        String string = this.url("activities/liveplayerlist");
        String string2 = this.execute(Request.get(string));
        return RealmsServerPlayerLists.parse(string2);
    }

    public RealmsServerAddress join(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + worldId));
        String string2 = this.execute(Request.get(string, 5000, 30000));
        return RealmsServerAddress.parse(string2);
    }

    public void initializeWorld(long worldId, String name, String motd) throws RealmsServiceException {
        RealmsDescriptionDto lv = new RealmsDescriptionDto(name, motd);
        String string3 = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(worldId)));
        String string4 = JSON.toJson(lv);
        this.execute(Request.post(string3, string4, 5000, 10000));
    }

    public Boolean mcoEnabled() throws RealmsServiceException {
        String string = this.url("mco/available");
        String string2 = this.execute(Request.get(string));
        return Boolean.valueOf(string2);
    }

    public Boolean stageAvailable() throws RealmsServiceException {
        String string = this.url("mco/stageAvailable");
        String string2 = this.execute(Request.get(string));
        return Boolean.valueOf(string2);
    }

    /*
     * WARNING - void declaration
     */
    public CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
        void lv2;
        String string = this.url("mco/client/compatible");
        String string2 = this.execute(Request.get(string));
        try {
            CompatibleVersionResponse lv = CompatibleVersionResponse.valueOf(string2);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new RealmsServiceException(500, "Could not check compatible version, got response: " + string2, -1, "");
        }
        return lv2;
    }

    public void uninvite(long worldId, String profileUuid) throws RealmsServiceException {
        String string2 = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$UUID", profileUuid));
        this.execute(Request.delete(string2));
    }

    public void uninviteMyselfFrom(long worldId) throws RealmsServiceException {
        String string = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        this.execute(Request.delete(string));
    }

    public RealmsServer invite(long worldId, String profileName) throws RealmsServiceException {
        PlayerInfo lv = new PlayerInfo();
        lv.setName(profileName);
        String string2 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        String string3 = this.execute(Request.post(string2, JSON.toJson(lv)));
        return RealmsServer.parse(string3);
    }

    public BackupList backupsFor(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get(string));
        return BackupList.parse(string2);
    }

    public void update(long worldId, String name, String motd) throws RealmsServiceException {
        RealmsDescriptionDto lv = new RealmsDescriptionDto(name, motd);
        String string3 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        this.execute(Request.post(string3, JSON.toJson(lv)));
    }

    public void updateSlot(long worldId, int slot, RealmsWorldOptions options) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slot)));
        String string2 = options.toJson();
        this.execute(Request.post(string, string2));
    }

    public boolean switchSlot(long worldId, int slot) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slot)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public void restoreWorld(long worldId, String backupId) throws RealmsServiceException {
        String string2 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(worldId)), "backupId=" + backupId);
        this.execute(Request.put(string2, "", 40000, 600000));
    }

    public WorldTemplatePaginatedList fetchWorldTemplates(int page, int pageSize, RealmsServer.WorldType type) throws RealmsServiceException {
        String string = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", type.toString()), String.format("page=%d&pageSize=%d", page, pageSize));
        String string2 = this.execute(Request.get(string));
        return WorldTemplatePaginatedList.parse(string2);
    }

    public Boolean putIntoMinigameMode(long worldId, String minigameId) throws RealmsServiceException {
        String string2 = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", minigameId).replace("$WORLD_ID", String.valueOf(worldId));
        String string3 = this.url("worlds" + string2);
        return Boolean.valueOf(this.execute(Request.put(string3, "")));
    }

    public Ops op(long worldId, String profileUuid) throws RealmsServiceException {
        String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", profileUuid);
        String string3 = this.url("ops" + string2);
        return Ops.parse(this.execute(Request.post(string3, "")));
    }

    public Ops deop(long worldId, String profileUuid) throws RealmsServiceException {
        String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", profileUuid);
        String string3 = this.url("ops" + string2);
        return Ops.parse(this.execute(Request.delete(string3)));
    }

    public Boolean open(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public Boolean close(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public Boolean resetWorldWithSeed(long worldId, String seed, Integer levelType, boolean generateStructures) throws RealmsServiceException {
        RealmsWorldResetDto lv = new RealmsWorldResetDto(seed, -1L, levelType, generateStructures);
        String string2 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(worldId)));
        String string3 = this.execute(Request.post(string2, JSON.toJson(lv), 30000, 80000));
        return Boolean.valueOf(string3);
    }

    public Boolean resetWorldWithTemplate(long worldId, String worldTemplateId) throws RealmsServiceException {
        RealmsWorldResetDto lv = new RealmsWorldResetDto(null, Long.valueOf(worldTemplateId), -1, false);
        String string2 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(worldId)));
        String string3 = this.execute(Request.post(string2, JSON.toJson(lv), 30000, 80000));
        return Boolean.valueOf(string3);
    }

    public Subscription subscriptionFor(long worldId) throws RealmsServiceException {
        String string = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get(string));
        return Subscription.parse(string2);
    }

    public int pendingInvitesCount() throws RealmsServiceException {
        String string = this.url("invites/count/pending");
        String string2 = this.execute(Request.get(string));
        return Integer.parseInt(string2);
    }

    public PendingInvitesList pendingInvites() throws RealmsServiceException {
        String string = this.url("invites/pending");
        String string2 = this.execute(Request.get(string));
        return PendingInvitesList.parse(string2);
    }

    public void acceptInvitation(String invitationId) throws RealmsServiceException {
        String string2 = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", invitationId));
        this.execute(Request.put(string2, ""));
    }

    public WorldDownload download(long worldId, int slotId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slotId)));
        String string2 = this.execute(Request.get(string));
        return WorldDownload.parse(string2);
    }

    public UploadInfo upload(long worldId, String uploadToken) throws RealmsServiceException {
        String string2 = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(worldId)));
        UploadInfo lv = new UploadInfo();
        if (uploadToken != null) {
            lv.setToken(uploadToken);
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = gsonBuilder.create();
        String string3 = gson.toJson((Object)lv);
        return UploadInfo.parse(this.execute(Request.put(string2, string3)));
    }

    public void rejectInvitation(String invitationId) throws RealmsServiceException {
        String string2 = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", invitationId));
        this.execute(Request.put(string2, ""));
    }

    public void agreeToTos() throws RealmsServiceException {
        String string = this.url("mco/tos/agreed");
        this.execute(Request.post(string, ""));
    }

    public RealmsNews getNews() throws RealmsServiceException {
        String string = this.url("mco/v1/news");
        String string2 = this.execute(Request.get(string, 5000, 10000));
        return RealmsNews.parse(string2);
    }

    public void sendPingResults(PingResult pingResult) throws RealmsServiceException {
        String string = this.url("regions/ping/stat");
        this.execute(Request.post(string, JSON.toJson(pingResult)));
    }

    public Boolean trialAvailable() throws RealmsServiceException {
        String string = this.url("trial");
        String string2 = this.execute(Request.get(string));
        return Boolean.valueOf(string2);
    }

    public void deleteWorld(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        this.execute(Request.delete(string));
    }

    @Nullable
    private String url(String path) {
        return this.url(path, null);
    }

    @Nullable
    private String url(String path, @Nullable String queryString) {
        try {
            return new URI(RealmsClient.currentEnvironment.protocol, RealmsClient.currentEnvironment.baseUrl, "/" + path, queryString, null).toASCIIString();
        }
        catch (URISyntaxException uRISyntaxException) {
            uRISyntaxException.printStackTrace();
            return null;
        }
    }

    private String execute(Request<?> r) throws RealmsServiceException {
        r.cookie("sid", this.sessionId);
        r.cookie("user", this.username);
        r.cookie("version", SharedConstants.getGameVersion().getName());
        try {
            int i = r.responseCode();
            if (i == 503) {
                int j = r.getRetryAfterHeader();
                throw new RetryCallException(j);
            }
            String string = r.text();
            if (i < 200 || i >= 300) {
                if (i == 401) {
                    String string2 = r.getHeader("WWW-Authenticate");
                    LOGGER.info("Could not authorize you against Realms server: " + string2);
                    throw new RealmsServiceException(i, string2, -1, string2);
                }
                if (string == null || string.length() == 0) {
                    LOGGER.error("Realms error code: " + i + " message: " + string);
                    throw new RealmsServiceException(i, string, i, "");
                }
                RealmsError lv = RealmsError.method_30162(string);
                LOGGER.error("Realms http code: " + i + " -  error code: " + lv.getErrorCode() + " -  message: " + lv.getErrorMessage() + " - raw body: " + string);
                throw new RealmsServiceException(i, string, lv);
            }
            return string;
        }
        catch (RealmsHttpException lv2) {
            throw new RealmsServiceException(500, "Could not connect to Realms: " + lv2.getMessage(), -1, "");
        }
    }

    static {
        LOGGER = LogManager.getLogger();
        JSON = new CheckedGson();
    }

    @net.fabricmc.api.Environment(value=EnvType.CLIENT)
    public static enum CompatibleVersionResponse {
        COMPATIBLE,
        OUTDATED,
        OTHER;

    }

    @net.fabricmc.api.Environment(value=EnvType.CLIENT)
    public static enum Environment {
        PRODUCTION("pc.realms.minecraft.net", "https"),
        STAGE("pc-stage.realms.minecraft.net", "https"),
        LOCAL("localhost:8080", "http");

        public String baseUrl;
        public String protocol;

        private Environment(String baseUrl, String protocol) {
            this.baseUrl = baseUrl;
            this.protocol = protocol;
        }
    }
}

