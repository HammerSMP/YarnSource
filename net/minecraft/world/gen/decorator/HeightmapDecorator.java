/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.class_5438;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public class HeightmapDecorator
extends class_5438<NopeDecoratorConfig> {
    public HeightmapDecorator(Codec<NopeDecoratorConfig> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type method_30463(NopeDecoratorConfig arg) {
        return Heightmap.Type.OCEAN_FLOOR_WG;
    }
}

