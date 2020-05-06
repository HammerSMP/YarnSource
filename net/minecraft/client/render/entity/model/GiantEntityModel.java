/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.entity.mob.GiantEntity;

@Environment(value=EnvType.CLIENT)
public class GiantEntityModel
extends AbstractZombieModel<GiantEntity> {
    public GiantEntityModel() {
        this(0.0f, false);
    }

    public GiantEntityModel(float f, boolean bl) {
        super(f, 0.0f, 64, bl ? 32 : 64);
    }

    @Override
    public boolean isAttacking(GiantEntity arg) {
        return false;
    }
}

