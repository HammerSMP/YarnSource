/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class TreeFeatureConfig
implements FeatureConfig {
    public final BlockStateProvider trunkProvider;
    public final BlockStateProvider leavesProvider;
    public final List<TreeDecorator> decorators;
    public transient boolean skipFluidCheck;
    public final FoliagePlacer foliagePlacer;
    public final TrunkPlacer trunkPlacer;
    public final FeatureSize featureSize;
    public final int baseHeight;
    public final boolean ignoreVines;
    public final Heightmap.Type heightmap;

    protected TreeFeatureConfig(BlockStateProvider arg, BlockStateProvider arg2, FoliagePlacer arg3, TrunkPlacer arg4, FeatureSize arg5, List<TreeDecorator> list, int i, boolean bl, Heightmap.Type arg6) {
        this.trunkProvider = arg;
        this.leavesProvider = arg2;
        this.decorators = list;
        this.foliagePlacer = arg3;
        this.featureSize = arg5;
        this.trunkPlacer = arg4;
        this.baseHeight = i;
        this.ignoreVines = bl;
        this.heightmap = arg6;
    }

    public void ignoreFluidCheck() {
        this.skipFluidCheck = true;
    }

    public TreeFeatureConfig setTreeDecorators(List<TreeDecorator> list) {
        return new TreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.featureSize, list, this.baseHeight, this.ignoreVines, this.heightmap);
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("trunk_provider"), this.trunkProvider.serialize(dynamicOps)).put(dynamicOps.createString("leaves_provider"), this.leavesProvider.serialize(dynamicOps)).put(dynamicOps.createString("decorators"), dynamicOps.createList(this.decorators.stream().map(arg -> arg.serialize(dynamicOps)))).put(dynamicOps.createString("foliage_placer"), this.foliagePlacer.serialize(dynamicOps)).put(dynamicOps.createString("trunk_placer"), this.trunkPlacer.serialize(dynamicOps)).put(dynamicOps.createString("minimum_size"), this.featureSize.serialize(dynamicOps)).put(dynamicOps.createString("max_water_depth"), dynamicOps.createInt(this.baseHeight)).put(dynamicOps.createString("ignore_vines"), dynamicOps.createBoolean(this.ignoreVines)).put(dynamicOps.createString("heightmap"), dynamicOps.createString(this.heightmap.getName()));
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)builder.build()));
    }

    public static <T> TreeFeatureConfig deserialize(Dynamic<T> dynamic2) {
        BlockStateProviderType<?> lv = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier((String)dynamic2.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        BlockStateProviderType<?> lv2 = Registry.BLOCK_STATE_PROVIDER_TYPE.get(new Identifier((String)dynamic2.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));
        FoliagePlacerType<?> lv3 = Registry.FOLIAGE_PLACER_TYPE.get(new Identifier((String)dynamic2.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
        TrunkPlacerType<?> lv4 = Registry.TRUNK_PLACER_TYPE.get(new Identifier((String)dynamic2.get("trunk_placer").get("type").asString().orElseThrow(RuntimeException::new)));
        FeatureSizeType<?> lv5 = Registry.FEATURE_SIZE_TYPE.get(new Identifier((String)dynamic2.get("minimum_size").get("type").asString().orElseThrow(RuntimeException::new)));
        return new TreeFeatureConfig((BlockStateProvider)lv.deserialize(dynamic2.get("trunk_provider").orElseEmptyMap()), (BlockStateProvider)lv2.deserialize(dynamic2.get("leaves_provider").orElseEmptyMap()), (FoliagePlacer)lv3.deserialize(dynamic2.get("foliage_placer").orElseEmptyMap()), (TrunkPlacer)lv4.deserialize(dynamic2.get("trunk_placer").orElseEmptyMap()), (FeatureSize)lv5.method_27381(dynamic2.get("minimum_size").orElseEmptyMap()), dynamic2.get("decorators").asList(dynamic -> Registry.TREE_DECORATOR_TYPE.get(new Identifier((String)dynamic.get("type").asString().orElseThrow(RuntimeException::new))).method_23472((Dynamic<?>)dynamic)), dynamic2.get("max_water_depth").asInt(0), dynamic2.get("ignore_vines").asBoolean(false), Heightmap.Type.byName(dynamic2.get("heightmap").asString("")));
    }

    public static class Builder {
        public final BlockStateProvider trunkProvider;
        public final BlockStateProvider leavesProvider;
        private final FoliagePlacer field_24140;
        private final TrunkPlacer field_24141;
        private final FeatureSize field_24142;
        private List<TreeDecorator> decorators = ImmutableList.of();
        private int baseHeight;
        private boolean field_24143;
        private Heightmap.Type heightmap = Heightmap.Type.OCEAN_FLOOR;

        public Builder(BlockStateProvider arg, BlockStateProvider arg2, FoliagePlacer arg3, TrunkPlacer arg4, FeatureSize arg5) {
            this.trunkProvider = arg;
            this.leavesProvider = arg2;
            this.field_24140 = arg3;
            this.field_24141 = arg4;
            this.field_24142 = arg5;
        }

        public Builder method_27376(List<TreeDecorator> list) {
            this.decorators = list;
            return this;
        }

        public Builder baseHeight(int i) {
            this.baseHeight = i;
            return this;
        }

        public Builder method_27374() {
            this.field_24143 = true;
            return this;
        }

        public Builder method_27375(Heightmap.Type arg) {
            this.heightmap = arg;
            return this;
        }

        public TreeFeatureConfig build() {
            return new TreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.field_24140, this.field_24141, this.field_24142, this.decorators, this.baseHeight, this.field_24143, this.heightmap);
        }
    }
}

