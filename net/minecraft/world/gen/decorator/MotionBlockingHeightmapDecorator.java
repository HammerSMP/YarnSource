/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.HeightmapDecorator;

public class MotionBlockingHeightmapDecorator<DC extends DecoratorConfig>
extends HeightmapDecorator<DC> {
    public MotionBlockingHeightmapDecorator(Codec<DC> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type getHeightmapType(DC config) {
        return Heightmap.Type.MOTION_BLOCKING;
    }
}

