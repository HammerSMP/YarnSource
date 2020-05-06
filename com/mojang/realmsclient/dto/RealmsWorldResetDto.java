/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.ValueObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsSerializable;

@Environment(value=EnvType.CLIENT)
public class RealmsWorldResetDto
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="seed")
    private final String seed;
    @SerializedName(value="worldTemplateId")
    private final long worldTemplateId;
    @SerializedName(value="levelType")
    private final int levelType;
    @SerializedName(value="generateStructures")
    private final boolean generateStructures;

    public RealmsWorldResetDto(String string, long l, int i, boolean bl) {
        this.seed = string;
        this.worldTemplateId = l;
        this.levelType = i;
        this.generateStructures = bl;
    }
}

