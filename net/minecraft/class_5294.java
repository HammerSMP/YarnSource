/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5321;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
public abstract class class_5294 {
    private static final Object2ObjectMap<class_5321<DimensionType>, class_5294> field_24609 = (Object2ObjectMap)Util.make(new Object2ObjectArrayMap(), object2ObjectArrayMap -> {
        class_5297 lv = new class_5297();
        object2ObjectArrayMap.defaultReturnValue((Object)lv);
        object2ObjectArrayMap.put(DimensionType.field_24753, (Object)lv);
        object2ObjectArrayMap.put(DimensionType.field_24754, (Object)new class_5296());
        object2ObjectArrayMap.put(DimensionType.field_24755, (Object)new class_5295());
    });
    private final float[] field_24610 = new float[4];
    private final float field_24611;
    private final boolean field_24612;
    private final boolean field_24613;

    public class_5294(float f, boolean bl, boolean bl2) {
        this.field_24611 = f;
        this.field_24612 = bl;
        this.field_24613 = bl2;
    }

    public static class_5294 method_28111(@Nullable class_5321<DimensionType> arg) {
        return (class_5294)field_24609.get(arg);
    }

    @Nullable
    public float[] method_28109(float f, float g) {
        float h = 0.4f;
        float i = MathHelper.cos(f * ((float)Math.PI * 2)) - 0.0f;
        float j = -0.0f;
        if (i >= -0.4f && i <= 0.4f) {
            float k = (i - -0.0f) / 0.4f * 0.5f + 0.5f;
            float l = 1.0f - (1.0f - MathHelper.sin(k * (float)Math.PI)) * 0.99f;
            l *= l;
            this.field_24610[0] = k * 0.3f + 0.7f;
            this.field_24610[1] = k * k * 0.7f + 0.2f;
            this.field_24610[2] = k * k * 0.0f + 0.2f;
            this.field_24610[3] = l;
            return this.field_24610;
        }
        return null;
    }

    public float method_28108() {
        return this.field_24611;
    }

    public boolean method_28113() {
        return this.field_24612;
    }

    public abstract Vec3d method_28112(Vec3d var1, float var2);

    public abstract boolean method_28110(int var1, int var2);

    public boolean method_28114() {
        return this.field_24613;
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5295
    extends class_5294 {
        public class_5295() {
            super(Float.NaN, false, false);
        }

        @Override
        public Vec3d method_28112(Vec3d arg, float f) {
            return arg.multiply(0.15f);
        }

        @Override
        public boolean method_28110(int i, int j) {
            return false;
        }

        @Override
        @Nullable
        public float[] method_28109(float f, float g) {
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5297
    extends class_5294 {
        public class_5297() {
            super(128.0f, true, true);
        }

        @Override
        public Vec3d method_28112(Vec3d arg, float f) {
            return arg.multiply(f * 0.94f + 0.06f, f * 0.94f + 0.06f, f * 0.91f + 0.09f);
        }

        @Override
        public boolean method_28110(int i, int j) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_5296
    extends class_5294 {
        public class_5296() {
            super(Float.NaN, true, false);
        }

        @Override
        public Vec3d method_28112(Vec3d arg, float f) {
            return arg;
        }

        @Override
        public boolean method_28110(int i, int j) {
            return true;
        }
    }
}

