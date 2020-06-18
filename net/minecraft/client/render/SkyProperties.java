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
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
public abstract class SkyProperties {
    private static final Object2ObjectMap<RegistryKey<DimensionType>, SkyProperties> BY_DIMENSION_TYPE = (Object2ObjectMap)Util.make(new Object2ObjectArrayMap(), object2ObjectArrayMap -> {
        Overworld lv = new Overworld();
        object2ObjectArrayMap.defaultReturnValue((Object)lv);
        object2ObjectArrayMap.put(DimensionType.OVERWORLD_REGISTRY_KEY, (Object)lv);
        object2ObjectArrayMap.put(DimensionType.THE_NETHER_REGISTRY_KEY, (Object)new Nether());
        object2ObjectArrayMap.put(DimensionType.THE_END_REGISTRY_KEY, (Object)new End());
    });
    private final float[] rgba = new float[4];
    private final float cloudsHeight;
    private final boolean alternateSkyColor;
    private final SkyType skyType;
    private final boolean shouldRenderSky;
    private final boolean darkened;

    public SkyProperties(float f, boolean bl, SkyType arg, boolean bl2, boolean bl3) {
        this.cloudsHeight = f;
        this.alternateSkyColor = bl;
        this.skyType = arg;
        this.shouldRenderSky = bl2;
        this.darkened = bl3;
    }

    public static SkyProperties byDimensionType(Optional<RegistryKey<DimensionType>> optional) {
        return (SkyProperties)BY_DIMENSION_TYPE.get(optional.orElse(DimensionType.OVERWORLD_REGISTRY_KEY));
    }

    @Nullable
    public float[] getSkyColor(float f, float g) {
        float h = 0.4f;
        float i = MathHelper.cos(f * ((float)Math.PI * 2)) - 0.0f;
        float j = -0.0f;
        if (i >= -0.4f && i <= 0.4f) {
            float k = (i - -0.0f) / 0.4f * 0.5f + 0.5f;
            float l = 1.0f - (1.0f - MathHelper.sin(k * (float)Math.PI)) * 0.99f;
            l *= l;
            this.rgba[0] = k * 0.3f + 0.7f;
            this.rgba[1] = k * k * 0.7f + 0.2f;
            this.rgba[2] = k * k * 0.0f + 0.2f;
            this.rgba[3] = l;
            return this.rgba;
        }
        return null;
    }

    public float getCloudsHeight() {
        return this.cloudsHeight;
    }

    public boolean isAlternateSkyColor() {
        return this.alternateSkyColor;
    }

    public abstract Vec3d adjustSkyColor(Vec3d var1, float var2);

    public abstract boolean useThickFog(int var1, int var2);

    public SkyType getSkyType() {
        return this.skyType;
    }

    public boolean shouldRenderSky() {
        return this.shouldRenderSky;
    }

    public boolean isDarkened() {
        return this.darkened;
    }

    @Environment(value=EnvType.CLIENT)
    public static class End
    extends SkyProperties {
        public End() {
            super(Float.NaN, false, SkyType.END, true, false);
        }

        @Override
        public Vec3d adjustSkyColor(Vec3d arg, float f) {
            return arg.multiply(0.15f);
        }

        @Override
        public boolean useThickFog(int i, int j) {
            return false;
        }

        @Override
        @Nullable
        public float[] getSkyColor(float f, float g) {
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Overworld
    extends SkyProperties {
        public Overworld() {
            super(128.0f, true, SkyType.NORMAL, false, false);
        }

        @Override
        public Vec3d adjustSkyColor(Vec3d arg, float f) {
            return arg.multiply(f * 0.94f + 0.06f, f * 0.94f + 0.06f, f * 0.91f + 0.09f);
        }

        @Override
        public boolean useThickFog(int i, int j) {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Nether
    extends SkyProperties {
        public Nether() {
            super(Float.NaN, true, SkyType.NONE, false, true);
        }

        @Override
        public Vec3d adjustSkyColor(Vec3d arg, float f) {
            return arg;
        }

        @Override
        public boolean useThickFog(int i, int j) {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum SkyType {
        NONE,
        NORMAL,
        END;

    }
}

