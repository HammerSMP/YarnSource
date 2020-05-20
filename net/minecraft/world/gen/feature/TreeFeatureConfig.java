/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class TreeFeatureConfig
implements FeatureConfig {
    public static final Codec<TreeFeatureConfig> field_24921 = RecordCodecBuilder.create(instance -> instance.group((App)BlockStateProvider.field_24937.fieldOf("trunk_provider").forGetter(arg -> arg.trunkProvider), (App)BlockStateProvider.field_24937.fieldOf("leaves_provider").forGetter(arg -> arg.leavesProvider), (App)FoliagePlacer.field_24931.fieldOf("foliage_placer").forGetter(arg -> arg.foliagePlacer), (App)TrunkPlacer.field_24972.fieldOf("trunk_placer").forGetter(arg -> arg.trunkPlacer), (App)FeatureSize.field_24922.fieldOf("minimum_size").forGetter(arg -> arg.featureSize), (App)TreeDecorator.field_24962.listOf().fieldOf("decorators").forGetter(arg -> arg.decorators), (App)Codec.INT.fieldOf("max_water_depth").withDefault((Object)0).forGetter(arg -> arg.baseHeight), (App)Codec.BOOL.fieldOf("ignore_vines").withDefault((Object)false).forGetter(arg -> arg.ignoreVines), (App)Heightmap.Type.field_24772.fieldOf("heightmap").forGetter(arg -> arg.heightmap)).apply((Applicative)instance, TreeFeatureConfig::new));
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

