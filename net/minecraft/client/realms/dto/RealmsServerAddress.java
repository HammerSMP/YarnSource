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
public class RealmsServerAddress
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String address;
    public String resourcePackUrl;
    public String resourcePackHash;

    public static RealmsServerAddress parse(String json) {
        JsonParser jsonParser = new JsonParser();
        RealmsServerAddress lv = new RealmsServerAddress();
        try {
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            lv.address = JsonUtils.getStringOr("address", jsonObject, null);
            lv.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", jsonObject, null);
            lv.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", jsonObject, null);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerAddress: " + exception.getMessage());
        }
        return lv;
    }
}

