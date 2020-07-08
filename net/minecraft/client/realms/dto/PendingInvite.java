/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PendingInvite
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public String invitationId;
    public String worldName;
    public String worldOwnerName;
    public String worldOwnerUuid;
    public Date date;

    public static PendingInvite parse(JsonObject jsonObject) {
        PendingInvite lv = new PendingInvite();
        try {
            lv.invitationId = JsonUtils.getStringOr("invitationId", jsonObject, "");
            lv.worldName = JsonUtils.getStringOr("worldName", jsonObject, "");
            lv.worldOwnerName = JsonUtils.getStringOr("worldOwnerName", jsonObject, "");
            lv.worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", jsonObject, "");
            lv.date = JsonUtils.getDateOr("date", jsonObject);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse PendingInvite: " + exception.getMessage());
        }
        return lv;
    }
}

