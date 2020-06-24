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
    public static final Codec<TreeFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter(arg -> arg.trunkProvider), (App)BlockStateProvider.CODEC.fieldOf("leaves_provider").forGetter(arg -> arg.leavesProvider), (App)FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter(arg -> arg.foliagePlacer), (App)TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter(arg -> arg.trunkPlacer), (App)FeatureSize.CODEC.fieldOf("minimum_size").forGetter(arg -> arg.minimumSize), (App)TreeDecorator.field_24962.listOf().fieldOf("decorators").forGetter(arg -> arg.decorators), (App)Codec.INT.fieldOf("max_water_depth").withDefault((Object)0).forGetter(arg -> arg.maxWaterDepth), (App)Codec.BOOL.fieldOf("ignore_vines").withDefault((Object)false).forGetter(arg -> arg.ignoreVines), (App)Heightmap.Type.field_24772.fieldOf("heightmap").forGetter(arg -> arg.heightmap)).apply((Applicative)instance, TreeFeatureConfig::new));
    public final BlockStateProvider trunkProvider;
    public final BlockStateProvider leavesProvider;
    public final List<TreeDecorator> decorators;
    public transient boolean skipFluidCheck;
    public final FoliagePlacer foliagePlacer;
    public final TrunkPlacer trunkPlacer;
    public final FeatureSize minimumSize;
    public final int maxWaterDepth;
    public final boolean ignoreVines;
    public final Heightmap.Type heightmap;

    protected TreeFeatureConfig(BlockStateProvider arg, BlockStateProvider arg2, FoliagePlacer arg3, TrunkPlacer arg4, FeatureSize arg5, List<TreeDecorator> list, int i, boolean bl, Heightmap.Type arg6) {
        this.trunkProvider = arg;
        this.leavesProvider = arg2;
        this.decorators = list;
        this.foliagePlacer = arg3;
        this.minimumSize = arg5;
        this.trunkPlacer = arg4;
        this.maxWaterDepth = i;
        this.ignoreVines = bl;
        this.heightmap = arg6;
    }

    public void ignoreFluidCheck() {
        this.skipFluidCheck = true;
    }

    public TreeFeatureConfig setTreeDecorators(List<TreeDecorator> list) {
        return new TreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.minimumSize, list, this.maxWaterDepth, this.ignoreVines, this.heightmap);
    }

    public static class Builder {
        public final BlockStateProvider trunkProvider;
        public final BlockStateProvider leavesProvider;
        private final FoliagePlacer foliagePlacer;
        private final TrunkPlacer trunkPlacer;
        private final FeatureSize minimumSize;
        private List<TreeDecorator> decorators = ImmutableList.of();
        private int maxWaterDepth;
        private boolean ignoreVines;
        private Heightmap.Type heightmap = Heightmap.Type.OCEAN_FLOOR;

        public Builder(BlockStateProvider arg, BlockStateProvider arg2, FoliagePlacer arg3, TrunkPlacer arg4, FeatureSize arg5) {
            this.trunkProvider = arg;
            this.leavesProvider = arg2;
            this.foliagePlacer = arg3;
            this.trunkPlacer = arg4;
            this.minimumSize = arg5;
        }

        public Builder decorators(List<TreeDecorator> list) {
            this.decorators = list;
            return this;
        }

        public Builder maxWaterDepth(int i) {
            this.maxWaterDepth = i;
            return this;
        }

        public Builder ignoreVines() {
            this.ignoreVines = true;
            return this;
        }

        public Builder heightmap(Heightmap.Type arg) {
            this.heightmap = arg;
            return this;
        }

        public TreeFeatureConfig build() {
            return new TreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.minimumSize, this.decorators, this.maxWaterDepth, this.ignoreVines, this.heightmap);
        }
    }
}

