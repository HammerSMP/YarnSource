/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsSerializable;

@Environment(value=EnvType.CLIENT)
public class PingResult
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="pingResults")
    public List<RegionPingResult> pingResults = Lists.newArrayList();
    @SerializedName(value="worldIds")
    public List<Long> worldIds = Lists.newArrayList();
}

