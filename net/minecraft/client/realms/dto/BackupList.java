/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.client.realms.dto.ValueObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BackupList
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public List<Backup> backups;

    public static BackupList parse(String json) {
        JsonParser jsonParser = new JsonParser();
        BackupList lv = new BackupList();
        lv.backups = Lists.newArrayList();
        try {
            JsonElement jsonElement = jsonParser.parse(json).getAsJsonObject().get("backups");
            if (jsonElement.isJsonArray()) {
                Iterator iterator = jsonElement.getAsJsonArray().iterator();
                while (iterator.hasNext()) {
                    lv.backups.add(Backup.parse((JsonElement)iterator.next()));
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse BackupList: " + exception.getMessage());
        }
        return lv;
    }
}

