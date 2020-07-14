/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

public class EndPortalBlockEntity
extends BlockEntity {
    public EndPortalBlockEntity(BlockEntityType<?> arg) {
        super(arg);
    }

    public EndPortalBlockEntity() {
        this(BlockEntityType.END_PORTAL);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldDrawSide(Direction direction) {
        return direction == Direction.UP;
    }
}

