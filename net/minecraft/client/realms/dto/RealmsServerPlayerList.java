/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsServerPlayerList
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final JsonParser jsonParser = new JsonParser();
    public long serverId;
    public List<String> players;

    public static RealmsServerPlayerList parse(JsonObject node) {
        RealmsServerPlayerList lv = new RealmsServerPlayerList();
        try {
            JsonElement jsonElement;
            lv.serverId = JsonUtils.getLongOr("serverId", node, -1L);
            String string = JsonUtils.getStringOr("playerList", node, null);
            lv.players = string != null ? ((jsonElement = jsonParser.parse(string)).isJsonArray() ? RealmsServerPlayerList.parsePlayers(jsonElement.getAsJsonArray()) : Lists.newArrayList()) : Lists.newArrayList();
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerPlayerList: " + exception.getMessage());
        }
        return lv;
    }

    private static List<String> parsePlayers(JsonArray jsonArray) {
        ArrayList list = Lists.newArrayList();
        for (JsonElement jsonElement : jsonArray) {
            try {
                list.add(jsonElement.getAsString());
            }
            catch (Exception exception) {}
        }
        return list;
    }
}

