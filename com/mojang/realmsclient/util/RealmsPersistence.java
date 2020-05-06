/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 */
package com.mojang.realmsclient.util;

import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.realms.CheckedGson;
import net.minecraft.realms.RealmsSerializable;
import org.apache.commons.io.FileUtils;

@Environment(value=EnvType.CLIENT)
public class RealmsPersistence {
    private static final CheckedGson CHECKED_GSON = new CheckedGson();

    public static RealmsPersistenceData readFile() {
        File file = RealmsPersistence.getFile();
        try {
            return CHECKED_GSON.fromJson(FileUtils.readFileToString((File)file, (Charset)StandardCharsets.UTF_8), RealmsPersistenceData.class);
        }
        catch (IOException iOException) {
            return new RealmsPersistenceData();
        }
    }

    public static void writeFile(RealmsPersistenceData arg) {
        File file = RealmsPersistence.getFile();
        try {
            FileUtils.writeStringToFile((File)file, (String)CHECKED_GSON.toJson(arg), (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static File getFile() {
        return new File(MinecraftClient.getInstance().runDirectory, "realms_persistence.json");
    }

    @Environment(value=EnvType.CLIENT)
    public static class RealmsPersistenceData
    implements RealmsSerializable {
        @SerializedName(value="newsLink")
        public String newsLink;
        @SerializedName(value="hasUnreadNews")
        public boolean hasUnreadNews;
    }
}

