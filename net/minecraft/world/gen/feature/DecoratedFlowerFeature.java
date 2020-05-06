/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;

public class DecoratedFlowerFeature
extends DecoratedFeature {
    public DecoratedFlowerFeature(Function<Dynamic<?>, ? extends DecoratedFeatureConfig> function) {
        super(function);
    }
}

