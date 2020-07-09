/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;

public abstract class class_5445<DC extends DecoratorConfig>
extends Decorator<DC> {
    public class_5445(Codec<DC> codec) {
        super(codec);
    }

    protected abstract Heightmap.Type method_30463(DC var1);
}

