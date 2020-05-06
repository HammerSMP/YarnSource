/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldTemplate
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String id = "";
    public String name = "";
    public String version = "";
    public String author = "";
    public String link = "";
    @Nullable
    public String image;
    public String trailer = "";
    public String recommendedPlayers = "";
    public WorldTemplateType type = WorldTemplateType.WORLD_TEMPLATE;

    public static WorldTemplate parse(JsonObject jsonObject) {
        WorldTemplate lv = new WorldTemplate();
        try {
            lv.id = JsonUtils.getStringOr("id", jsonObject, "");
            lv.name = JsonUtils.getStringOr("name", jsonObject, "");
            lv.version = JsonUtils.getStringOr("version", jsonObject, "");
            lv.author = JsonUtils.getStringOr("author", jsonObject, "");
            lv.link = JsonUtils.getStringOr("link", jsonObject, "");
            lv.image = JsonUtils.getStringOr("image", jsonObject, null);
            lv.trailer = JsonUtils.getStringOr("trailer", jsonObject, "");
            lv.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", jsonObject, "");
            lv.type = WorldTemplateType.valueOf(JsonUtils.getStringOr("type", jsonObject, WorldTemplateType.WORLD_TEMPLATE.name()));
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplate: " + exception.getMessage());
        }
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum WorldTemplateType {
        WORLD_TEMPLATE,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;

    }
}

