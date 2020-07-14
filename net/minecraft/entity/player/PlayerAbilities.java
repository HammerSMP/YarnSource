/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;

public class PlayerAbilities {
    public boolean invulnerable;
    public boolean flying;
    public boolean allowFlying;
    public boolean creativeMode;
    public boolean allowModifyWorld = true;
    private float flySpeed = 0.05f;
    private float walkSpeed = 0.1f;

    public void serialize(CompoundTag arg) {
        CompoundTag lv = new CompoundTag();
        lv.putBoolean("invulnerable", this.invulnerable);
        lv.putBoolean("flying", this.flying);
        lv.putBoolean("mayfly", this.allowFlying);
        lv.putBoolean("instabuild", this.creativeMode);
        lv.putBoolean("mayBuild", this.allowModifyWorld);
        lv.putFloat("flySpeed", this.flySpeed);
        lv.putFloat("walkSpeed", this.walkSpeed);
        arg.put("abilities", lv);
    }

    public void deserialize(CompoundTag arg) {
        if (arg.contains("abilities", 10)) {
            CompoundTag lv = arg.getCompound("abilities");
            this.invulnerable = lv.getBoolean("invulnerable");
            this.flying = lv.getBoolean("flying");
            this.allowFlying = lv.getBoolean("mayfly");
            this.creativeMode = lv.getBoolean("instabuild");
            if (lv.contains("flySpeed", 99)) {
                this.flySpeed = lv.getFloat("flySpeed");
                this.walkSpeed = lv.getFloat("walkSpeed");
            }
            if (lv.contains("mayBuild", 1)) {
                this.allowModifyWorld = lv.getBoolean("mayBuild");
            }
        }
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    @Environment(value=EnvType.CLIENT)
    public void setFlySpeed(float flySpeed) {
        this.flySpeed = flySpeed;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    @Environment(value=EnvType.CLIENT)
    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }
}

