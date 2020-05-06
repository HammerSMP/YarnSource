/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 */
package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccessType;
import net.minecraft.world.biome.source.HorizontalVoronoiBiomeAccessType;
import net.minecraft.world.biome.source.VoronoiBiomeAccessType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;

public class DimensionType
implements DynamicSerializable {
    public static final DimensionType OVERWORLD = DimensionType.register("overworld", new DimensionType(1, "", "", OverworldDimension::new, true, HorizontalVoronoiBiomeAccessType.INSTANCE));
    public static final DimensionType THE_NETHER = DimensionType.register("the_nether", new DimensionType(0, "_nether", "DIM-1", TheNetherDimension::new, false, VoronoiBiomeAccessType.INSTANCE));
    public static final DimensionType THE_END = DimensionType.register("the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::new, false, VoronoiBiomeAccessType.INSTANCE));
    private final int id;
    private final String suffix;
    private final String saveDir;
    private final BiFunction<World, DimensionType, ? extends Dimension> factory;
    private final boolean hasSkyLight;
    private final BiomeAccessType biomeAccessType;

    private static DimensionType register(String string, DimensionType arg) {
        return Registry.register(Registry.DIMENSION_TYPE, arg.id, string, arg);
    }

    protected DimensionType(int i, String string, String string2, BiFunction<World, DimensionType, ? extends Dimension> biFunction, boolean bl, BiomeAccessType arg) {
        this.id = i;
        this.suffix = string;
        this.saveDir = string2;
        this.factory = biFunction;
        this.hasSkyLight = bl;
        this.biomeAccessType = arg;
    }

    public static DimensionType deserialize(Dynamic<?> dynamic) {
        return Registry.DIMENSION_TYPE.get(new Identifier(dynamic.asString("")));
    }

    public static Iterable<DimensionType> getAll() {
        return Registry.DIMENSION_TYPE;
    }

    public int getRawId() {
        return this.id + -1;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public File getSaveDirectory(File file) {
        if (this.saveDir.isEmpty()) {
            return file;
        }
        return new File(file, this.saveDir);
    }

    public Dimension create(World arg) {
        return this.factory.apply(arg, this);
    }

    public String toString() {
        return DimensionType.getId(this).toString();
    }

    @Nullable
    public static DimensionType byRawId(int i) {
        return (DimensionType)Registry.DIMENSION_TYPE.get(i - -1);
    }

    @Nullable
    public static DimensionType byId(Identifier arg) {
        return Registry.DIMENSION_TYPE.get(arg);
    }

    @Nullable
    public static Identifier getId(DimensionType arg) {
        return Registry.DIMENSION_TYPE.getId(arg);
    }

    public boolean hasSkyLight() {
        return this.hasSkyLight;
    }

    public BiomeAccessType getBiomeAccessType() {
        return this.biomeAccessType;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createString(Registry.DIMENSION_TYPE.getId(this).toString());
    }
}

