/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.class_5439;
import net.minecraft.class_5440;
import net.minecraft.class_5441;
import net.minecraft.class_5442;
import net.minecraft.class_5443;
import net.minecraft.class_5444;
import net.minecraft.class_5446;
import net.minecraft.class_5447;
import net.minecraft.class_5448;
import net.minecraft.class_5449;
import net.minecraft.class_5450;
import net.minecraft.class_5451;
import net.minecraft.class_5452;
import net.minecraft.class_5453;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.CarvingMaskDecorator;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ChancePassthroughDecorator;
import net.minecraft.world.gen.decorator.ChorusPlantDecorator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountDepthAverageDecorator;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraHeightmapDecorator;
import net.minecraft.world.gen.decorator.DarkOakTreeDecorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.EmeraldOreDecorator;
import net.minecraft.world.gen.decorator.EndGatewayDecorator;
import net.minecraft.world.gen.decorator.EndIslandDecorator;
import net.minecraft.world.gen.decorator.HeightmapDecorator;
import net.minecraft.world.gen.decorator.HeightmapNoiseBiasedDecorator;
import net.minecraft.world.gen.decorator.HellFireDecorator;
import net.minecraft.world.gen.decorator.IcebergDecorator;
import net.minecraft.world.gen.decorator.LavaLakeDecorator;
import net.minecraft.world.gen.decorator.MagmaDecorator;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.NopeDecorator;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.WaterLakeDecorator;
import net.minecraft.world.gen.feature.SeaPickleFeatureConfig;

public abstract class Decorator<DC extends DecoratorConfig> {
    public static final Decorator<NopeDecoratorConfig> NOPE = Decorator.register("nope", new NopeDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<ChanceDecoratorConfig> CHANCE = Decorator.register("chance", new ChancePassthroughDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<SeaPickleFeatureConfig> COUNT = Decorator.register("count", new class_5440(SeaPickleFeatureConfig.CODEC));
    public static final Decorator<NoiseHeightmapDecoratorConfig> COUNT_NOISE = Decorator.register("count_noise", new class_5441(NoiseHeightmapDecoratorConfig.CODEC));
    public static final Decorator<TopSolidHeightmapNoiseBiasedDecoratorConfig> COUNT_NOISE_BIASED = Decorator.register("count_noise_biased", new HeightmapNoiseBiasedDecorator(TopSolidHeightmapNoiseBiasedDecoratorConfig.CODEC));
    public static final Decorator<CountExtraChanceDecoratorConfig> COUNT_EXTRA = Decorator.register("count_extra", new CountExtraHeightmapDecorator(CountExtraChanceDecoratorConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> SQUARE = Decorator.register("square", new class_5450(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> HEIGHTMAP = Decorator.register("heightmap", new class_5447<NopeDecoratorConfig>(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = Decorator.register("heightmap_spread_double", new class_5448<NopeDecoratorConfig>(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> TOP_SOLID_HEIGHTMAP = Decorator.register("top_solid_heightmap", new HeightmapDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> HEIGHTMAP_WORLD_SURFACE = Decorator.register("heightmap_world_surface", new class_5446(NopeDecoratorConfig.field_24891));
    public static final Decorator<RangeDecoratorConfig> RANGE = Decorator.register("range", new class_5449(RangeDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> RANGE_BIASED = Decorator.register("range_biased", new class_5439(RangeDecoratorConfig.CODEC));
    public static final Decorator<RangeDecoratorConfig> RANGE_VERY_BIASED = Decorator.register("range_very_biased", new class_5451(RangeDecoratorConfig.CODEC));
    public static final Decorator<CountDepthDecoratorConfig> DEPTH_AVERAGE = Decorator.register("depth_average", new CountDepthAverageDecorator(CountDepthDecoratorConfig.field_24982));
    public static final Decorator<NopeDecoratorConfig> SPREAD_32_ABOVE = Decorator.register("spread_32_above", new ChorusPlantDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<CarvingMaskDecoratorConfig> CARVING_MASK = Decorator.register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfig.CODEC));
    public static final Decorator<SeaPickleFeatureConfig> FIRE = Decorator.register("fire", new HellFireDecorator(SeaPickleFeatureConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> MAGMA = Decorator.register("magma", new MagmaDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> EMERALD_ORE = Decorator.register("emerald_ore", new EmeraldOreDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<ChanceDecoratorConfig> LAVA_LAKE = Decorator.register("lava_lake", new LavaLakeDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<ChanceDecoratorConfig> WATER_LAKE = Decorator.register("water_lake", new WaterLakeDecorator(ChanceDecoratorConfig.field_24980));
    public static final Decorator<SeaPickleFeatureConfig> GLOWSTONE = Decorator.register("glowstone", new class_5453(SeaPickleFeatureConfig.CODEC));
    public static final Decorator<NopeDecoratorConfig> END_GATEWAY = Decorator.register("end_gateway", new EndGatewayDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> DARK_OAK_TREE = Decorator.register("dark_oak_tree", new DarkOakTreeDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> ICEBERG = Decorator.register("iceberg", new IcebergDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<NopeDecoratorConfig> END_ISLAND = Decorator.register("end_island", new EndIslandDecorator(NopeDecoratorConfig.field_24891));
    public static final Decorator<class_5443> DECORATED = Decorator.register("decorated", new class_5442(class_5443.field_25854));
    public static final Decorator<SeaPickleFeatureConfig> COUNT_MULTILAYER = Decorator.register("count_multilayer", new class_5452(SeaPickleFeatureConfig.CODEC));
    private final Codec<ConfiguredDecorator<DC>> codec;

    private static <T extends DecoratorConfig, G extends Decorator<T>> G register(String string, G arg) {
        return (G)Registry.register(Registry.DECORATOR, string, arg);
    }

    public Decorator(Codec<DC> codec) {
        this.codec = codec.fieldOf("config").xmap(arg -> new ConfiguredDecorator<DecoratorConfig>(this, (DecoratorConfig)arg), ConfiguredDecorator::method_30445).codec();
    }

    public ConfiguredDecorator<DC> configure(DC arg) {
        return new ConfiguredDecorator<DC>(this, arg);
    }

    public Codec<ConfiguredDecorator<DC>> getCodec() {
        return this.codec;
    }

    public abstract Stream<BlockPos> getPositions(class_5444 var1, Random var2, DC var3, BlockPos var4);

    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
    }
}

