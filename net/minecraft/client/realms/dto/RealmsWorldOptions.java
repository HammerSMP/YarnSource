/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
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

    public RealmsWorldOptions(Boolean pvp, Boolean spawnAnimals, Boolean spawnMonsters, Boolean spawnNPCs, Integer spawnProtection, Boolean commandBlocks, Integer difficulty, Integer gameMode, Boolean forceGameMode, String slotName) {
        this.pvp = pvp;
        this.spawnAnimals = spawnAnimals;
        this.spawnMonsters = spawnMonsters;
        this.spawnNPCs = spawnNPCs;
        this.spawnProtection = spawnProtection;
        this.commandBlocks = commandBlocks;
        this.difficulty = difficulty;
        this.gameMode = gameMode;
        this.forceGameMode = forceGameMode;
        this.slotName = slotName;
    }

    public static RealmsWorldOptions getDefaults() {
        return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
    }

    public static RealmsWorldOptions getEmptyDefaults() {
        RealmsWorldOptions lv = RealmsWorldOptions.getDefaults();
        lv.setEmpty(true);
        return lv;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public static RealmsWorldOptions parse(JsonObject json) {
        RealmsWorldOptions lv = new RealmsWorldOptions(JsonUtils.getBooleanOr("pvp", json, true), JsonUtils.getBooleanOr("spawnAnimals", json, true), JsonUtils.getBooleanOr("spawnMonsters", json, true), JsonUtils.getBooleanOr("spawnNPCs", json, true), JsonUtils.getIntOr("spawnProtection", json, 0), JsonUtils.getBooleanOr("commandBlocks", json, false), JsonUtils.getIntOr("difficulty", json, 2), JsonUtils.getIntOr("gameMode", json, 0), JsonUtils.getBooleanOr("forceGameMode", json, false), JsonUtils.getStringOr("slotName", json, ""));
        lv.templateId = JsonUtils.getLongOr("worldTemplateId", json, -1L);
        lv.templateImage = JsonUtils.getStringOr("worldTemplateImage", json, DEFAULT_WORLD_TEMPLATE_IMAGE);
        lv.adventureMap = JsonUtils.getBooleanOr("adventureMap", json, false);
        return lv;
    }

    public String getSlotName(int index) {
        if (this.slotName == null || this.slotName.isEmpty()) {
            if (this.empty) {
                return I18n.translate("mco.configure.world.slot.empty", new Object[0]);
            }
            return this.getDefaultSlotName(index);
        }
        return this.slotName;
    }

    public String getDefaultSlotName(int index) {
        return I18n.translate("mco.configure.world.slot", index);
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

