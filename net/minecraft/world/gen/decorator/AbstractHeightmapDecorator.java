/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public abstract class AbstractHeightmapDecorator<DC extends DecoratorConfig>
extends Decorator<DC> {
    public AbstractHeightmapDecorator(Codec<DC> codec) {
        super(codec);
    }

    protected abstract Heightmap.Type getHeightmapType(DC var1);
}

