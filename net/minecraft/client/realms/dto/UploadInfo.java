/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class UploadInfo
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean worldClosed;
    private String token = "";
    private String uploadEndpoint = "";
    private int port;

    public static UploadInfo parse(String json) {
        UploadInfo lv = new UploadInfo();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            lv.worldClosed = JsonUtils.getBooleanOr("worldClosed", jsonObject, false);
            lv.token = JsonUtils.getStringOr("token", jsonObject, null);
            lv.uploadEndpoint = JsonUtils.getStringOr("uploadEndpoint", jsonObject, null);
            lv.port = JsonUtils.getIntOr("port", jsonObject, 8080);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse UploadInfo: " + exception.getMessage());
        }
        return lv;
    }

    public String getToken() {
        return this.token;
    }

    public String getUploadEndpoint() {
        return this.uploadEndpoint;
    }

    public boolean isWorldClosed() {
        return this.worldClosed;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPort() {
        return this.port;
    }
}

