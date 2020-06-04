/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldOptions
extends ValueObject {
    public Boolean pvp;
    public Boolean spawnAnimals;
    public Boolean spawnMonsters;
    public Boolean spawnNPCs;
    public Integer spawnProtection;
    public Boolean commandBlocks;
    public Boolean forceGameMode;
    public Integer difficulty;
    public Integer gameMode;
    public String slotName;
    public long templateId;
    public String templateImage;
    public boolean adventureMap;
    public boolean empty;
    private static final String DEFAULT_WORLD_TEMPLATE_IMAGE = null;

    public RealmsWorldOptions(Boolean boolean_, Boolean boolean2, Boolean boolean3, Boolean boolean4, Integer integer, Boolean boolean5, Integer integer2, Integer integer3, Boolean boolean6, String string) {
        this.pvp = boolean_;
        this.spawnAnimals = boolean2;
        this.spawnMonsters = boolean3;
        this.spawnNPCs = boolean4;
        this.spawnProtection = integer;
        this.commandBlocks = boolean5;
        this.difficulty = integer2;
        this.gameMode = integer3;
        this.forceGameMode = boolean6;
        this.slotName = string;
    }

    public static RealmsWorldOptions getDefaults() {
        return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
    }

    public static RealmsWorldOptions getEmptyDefaults() {
        RealmsWorldOptions lv = RealmsWorldOptions.getDefaults();
        lv.setEmpty(true);
        return lv;
    }

    public void setEmpty(boolean bl) {
        this.empty = bl;
    }

    public static RealmsWorldOptions parse(JsonObject jsonObject) {
        RealmsWorldOptions lv = new RealmsWorldOptions(JsonUtils.getBooleanOr("pvp", jsonObject, true), JsonUtils.getBooleanOr("spawnAnimals", jsonObject, true), JsonUtils.getBooleanOr("spawnMonsters", jsonObject, true), JsonUtils.getBooleanOr("spawnNPCs", jsonObject, true), JsonUtils.getIntOr("spawnProtection", jsonObject, 0), JsonUtils.getBooleanOr("commandBlocks", jsonObject, false), JsonUtils.getIntOr("difficulty", jsonObject, 2), JsonUtils.getIntOr("gameMode", jsonObject, 0), JsonUtils.getBooleanOr("forceGameMode", jsonObject, false), JsonUtils.getStringOr("slotName", jsonObject, ""));
        lv.templateId = JsonUtils.getLongOr("worldTemplateId", jsonObject, -1L);
        lv.templateImage = JsonUtils.getStringOr("worldTemplateImage", jsonObject, DEFAULT_WORLD_TEMPLATE_IMAGE);
        lv.adventureMap = JsonUtils.getBooleanOr("adventureMap", jsonObject, false);
        return lv;
    }

    public String getSlotName(int i) {
        if (this.slotName == null || this.slotName.isEmpty()) {
            if (this.empty) {
                return I18n.translate("mco.configure.world.slot.empty", new Object[0]);
            }
            return this.getDefaultSlotName(i);
        }
        return this.slotName;
    }

    public String getDefaultSlotName(int i) {
        return I18n.translate("mco.configure.world.slot", i);
    }

    public String toJson() {
        JsonObject jsonObject = new JsonObject();
        if (!this.pvp.booleanValue()) {
            jsonObject.addProperty("pvp", this.pvp);
        }
        if (!this.spawnAnimals.booleanValue()) {
            jsonObject.addProperty("spawnAnimals", this.spawnAnimals);
        }
        if (!this.spawnMonsters.booleanValue()) {
            jsonObject.addProperty("spawnMonsters", this.spawnMonsters);
        }
        if (!this.spawnNPCs.booleanValue()) {
            jsonObject.addProperty("spawnNPCs", this.spawnNPCs);
        }
        if (this.spawnProtection != 0) {
            jsonObject.addProperty("spawnProtection", (Number)this.spawnProtection);
        }
        if (this.commandBlocks.booleanValue()) {
            jsonObject.addProperty("commandBlocks", this.commandBlocks);
        }
        if (this.difficulty != 2) {
            jsonObject.addProperty("difficulty", (Number)this.difficulty);
        }
        if (this.gameMode != 0) {
            jsonObject.addProperty("gameMode", (Number)this.gameMode);
        }
        if (this.forceGameMode.booleanValue()) {
            jsonObject.addProperty("forceGameMode", this.forceGameMode);
        }
        if (!Objects.equals(this.slotName, "")) {
            jsonObject.addProperty("slotName", this.slotName);
        }
        return jsonObject.toString();
    }

    public RealmsWorldOptions clone() {
        return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
    }

    public /* synthetic */ Object clone() throws CloneNotSupportedException {
        return this.clone();
    }
}

