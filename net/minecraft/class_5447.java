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
import net.minecraft.world.gen.decorator.DecoratorConfig;

public class class_5447<DC extends DecoratorConfig>
extends class_5438<DC> {
    public class_5447(Codec<DC> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Type method_30463(DC arg) {
        return Heightmap.Type.MOTION_BLOCKING;
    }
}

