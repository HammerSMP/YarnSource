/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;

public interface DecoratorConfig {
    public static final NopeDecoratorConfig DEFAULT = new NopeDecoratorConfig();

    public <T> Dynamic<T> serialize(DynamicOps<T> var1);
}

