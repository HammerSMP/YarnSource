/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.ValueObject;

@Environment(value=EnvType.CLIENT)
public class PlayerInfo
extends ValueObject
implements RealmsSerializable {
    @SerializedName(value="name")
    private String name;
    @SerializedName(value="uuid")
    private String uuid;
    @SerializedName(value="operator")
    private boolean operator;
    @SerializedName(value="accepted")
    private boolean accepted;
    @SerializedName(value="online")
    private boolean online;

    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String string) {
        this.uuid = string;
    }

    public boolean isOperator() {
        return this.operator;
    }

    public void setOperator(boolean bl) {
        this.operator = bl;
    }

    public boolean getAccepted() {
        return this.accepted;
    }

    public void setAccepted(boolean bl) {
        this.accepted = bl;
    }

    public boolean getOnline() {
        return this.online;
    }

    public void setOnline(boolean bl) {
        this.online = bl;
    }
}

