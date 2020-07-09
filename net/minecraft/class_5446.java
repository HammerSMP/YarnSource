/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import net.minecraft.class_5438;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class class_5446
extends class_5438<NopeDecoratorConfig> {
    public class_5446(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type method_30463(NopeDecoratorConfig arg) {
        return Heightmap.Type.WORLD_SURFACE_WG;
    }
}

