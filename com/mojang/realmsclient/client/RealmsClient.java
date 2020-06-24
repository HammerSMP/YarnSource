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
package com.mojang.realmsclient.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.realmsclient.client.RealmsClientConfig;
import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.client.Request;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.CheckedGson;
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

    public RealmsClient(String string, String string2, Proxy proxy) {
        this.sessionId = string;
        this.username = string2;
        RealmsClientConfig.setProxy(proxy);
    }

    public RealmsServerList listWorlds() throws RealmsServiceException {
        String string = this.url("worlds");
        String string2 = this.execute(Request.get(string));
        return RealmsServerList.parse(string2);
    }

    public RealmsServer getOwnWorld(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(l)));
        String string2 = this.execute(Request.get(string));
        return RealmsServer.parse(string2);
    }

    public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
        String string = this.url("activities/liveplayerlist");
        String string2 = this.execute(Request.get(string));
        return RealmsServerPlayerLists.parse(string2);
    }

    public RealmsServerAddress join(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + l));
        String string2 = this.execute(Request.get(string, 5000, 30000));
        return RealmsServerAddress.parse(string2);
    }

    public void initializeWorld(long l, String string, String string2) throws RealmsServiceException {
        RealmsDescriptionDto lv = new RealmsDescriptionDto(string, string2);
        String string3 = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(l)));
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

    public void uninvite(long l, String string) throws RealmsServiceException {
        String string2 = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(l)).replace("$UUID", string));
        this.execute(Request.delete(string2));
    }

    public void uninviteMyselfFrom(long l) throws RealmsServiceException {
        String string = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
        this.execute(Request.delete(string));
    }

    public RealmsServer invite(long l, String string) throws RealmsServiceException {
        PlayerInfo lv = new PlayerInfo();
        lv.setName(string);
        String string2 = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
        String string3 = this.execute(Request.post(string2, JSON.toJson(lv)));
        return RealmsServer.parse(string3);
    }

    public BackupList backupsFor(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(l)));
        String string2 = this.execute(Request.get(string));
        return BackupList.parse(string2);
    }

    public void update(long l, String string, String string2) throws RealmsServiceException {
        RealmsDescriptionDto lv = new RealmsDescriptionDto(string, string2);
        String string3 = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
        this.execute(Request.post(string3, JSON.toJson(lv)));
    }

    public void updateSlot(long l, int i, RealmsWorldOptions arg) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(l)).replace("$SLOT_ID", String.valueOf(i)));
        String string2 = arg.toJson();
        this.execute(Request.post(string, string2));
    }

    public boolean switchSlot(long l, int i) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(l)).replace("$SLOT_ID", String.valueOf(i)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public void restoreWorld(long l, String string) throws RealmsServiceException {
        String string2 = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(l)), "backupId=" + string);
        this.execute(Request.put(string2, "", 40000, 600000));
    }

    public WorldTemplatePaginatedList fetchWorldTemplates(int i, int j, RealmsServer.WorldType arg) throws RealmsServiceException {
        String string = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", arg.toString()), String.format("page=%d&pageSize=%d", i, j));
        String string2 = this.execute(Request.get(string));
        return WorldTemplatePaginatedList.parse(string2);
    }

    public Boolean putIntoMinigameMode(long l, String string) throws RealmsServiceException {
        String string2 = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", string).replace("$WORLD_ID", String.valueOf(l));
        String string3 = this.url("worlds" + string2);
        return Boolean.valueOf(this.execute(Request.put(string3, "")));
    }

    public Ops op(long l, String string) throws RealmsServiceException {
        String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(l)).replace("$PROFILE_UUID", string);
        String string3 = this.url("ops" + string2);
        return Ops.parse(this.execute(Request.post(string3, "")));
    }

    public Ops deop(long l, String string) throws RealmsServiceException {
        String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(l)).replace("$PROFILE_UUID", string);
        String string3 = this.url("ops" + string2);
        return Ops.parse(this.execute(Request.delete(string3)));
    }

    public Boolean open(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(l)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public Boolean close(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(l)));
        String string2 = this.execute(Request.put(string, ""));
        return Boolean.valueOf(string2);
    }

    public Boolean resetWorldWithSeed(long l, String string, Integer integer, boolean bl) throws RealmsServiceException {
        RealmsWorldResetDto lv = new RealmsWorldResetDto(string, -1L, integer, bl);
        String string2 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(l)));
        String string3 = this.execute(Request.post(string2, JSON.toJson(lv), 30000, 80000));
        return Boolean.valueOf(string3);
    }

    public Boolean resetWorldWithTemplate(long l, String string) throws RealmsServiceException {
        RealmsWorldResetDto lv = new RealmsWorldResetDto(null, Long.valueOf(string), -1, false);
        String string2 = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(l)));
        String string3 = this.execute(Request.post(string2, JSON.toJson(lv), 30000, 80000));
        return Boolean.valueOf(string3);
    }

    public Subscription subscriptionFor(long l) throws RealmsServiceException {
        String string = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
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

    public void acceptInvitation(String string) throws RealmsServiceException {
        String string2 = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", string));
        this.execute(Request.put(string2, ""));
    }

    public WorldDownload download(long l, int i) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(l)).replace("$SLOT_ID", String.valueOf(i)));
        String string2 = this.execute(Request.get(string));
        return WorldDownload.parse(string2);
    }

    public UploadInfo upload(long l, String string) throws RealmsServiceException {
        String string2 = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(l)));
        UploadInfo lv = new UploadInfo();
        if (string != null) {
            lv.setToken(string);
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = gsonBuilder.create();
        String string3 = gson.toJson((Object)lv);
        return UploadInfo.parse(this.execute(Request.put(string2, string3)));
    }

    public void rejectInvitation(String string) throws RealmsServiceException {
        String string2 = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", string));
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

    public void sendPingResults(PingResult arg) throws RealmsServiceException {
        String string = this.url("regions/ping/stat");
        this.execute(Request.post(string, JSON.toJson(arg)));
    }

    public Boolean trialAvailable() throws RealmsServiceException {
        String string = this.url("trial");
        String string2 = this.execute(Request.get(string));
        return Boolean.valueOf(string2);
    }

    public void deleteWorld(long l) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
        this.execute(Request.delete(string));
    }

    @Nullable
    private String url(String string) {
        return this.url(string, null);
    }

    @Nullable
    private String url(String string, @Nullable String string2) {
        try {
            return new URI(RealmsClient.currentEnvironment.protocol, RealmsClient.currentEnvironment.baseUrl, "/" + string, string2, null).toASCIIString();
        }
        catch (URISyntaxException uRISyntaxException) {
            uRISyntaxException.printStackTrace();
            return null;
        }
    }

    private String execute(Request<?> arg) throws RealmsServiceException {
        arg.cookie("sid", this.sessionId);
        arg.cookie("user", this.username);
        arg.cookie("version", SharedConstants.getGameVersion().getName());
        try {
            int i = arg.responseCode();
            if (i == 503) {
                int j = arg.getRetryAfterHeader();
                throw new RetryCallException(j);
            }
            String string = arg.text();
            if (i < 200 || i >= 300) {
                if (i == 401) {
                    String string2 = arg.getHeader("WWW-Authenticate");
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

        private Environment(String string2, String string3) {
            this.baseUrl = string2;
            this.protocol = string3;
        }
    }
}

