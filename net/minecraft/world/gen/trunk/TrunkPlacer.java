/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.gen.trunk;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public abstract class TrunkPlacer {
    public static final Codec<TrunkPlacer> CODEC = Registry.TRUNK_PLACER_TYPE.dispatch(TrunkPlacer::getType, TrunkPlacerType::getCodec);
    protected final int baseHeight;
    protected final int firstRandomHeight;
    protected final int secondRandomHeight;

    protected static <P extends TrunkPlacer> Products.P3<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer> method_28904(RecordCodecBuilder.Instance<P> instance) {
        return instance.group((App)Codec.intRange((int)0, (int)32).fieldOf("base_height").forGetter(arg -> arg.baseHeight), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_a").forGetter(arg -> arg.firstRandomHeight), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_b").forGetter(arg -> arg.secondRandomHeight));
    }

    public TrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        this.baseHeight = baseHeight;
        this.firstRandomHeight = firstRandomHeight;
        this.secondRandomHeight = secondRandomHeight;
    }

    protected abstract TrunkPlacerType<?> getType();

    public abstract List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BlockBox var6, TreeFeatureConfig var7);

    public int getHeight(Random random) {
        return this.baseHeight + random.nextInt(this.firstRandomHeight + 1) + random.nextInt(this.secondRandomHeight + 1);
    }

    protected static void method_27404(ModifiableWorld arg, BlockPos arg2, BlockState arg3, BlockBox arg4) {
        TreeFeature.setBlockStateWithoutUpdatingNeighbors(arg, arg2, arg3);
        arg4.encompass(new BlockBox(arg2, arg2));
    }

    private static boolean method_27403(TestableWorld arg2, BlockPos arg22) {
        return arg2.testBlockState(arg22, arg -> {
            Block lv = arg.getBlock();
            return Feature.isSoil(lv) && !arg.isOf(Blocks.GRASS_BLOCK) && !arg.isOf(Blocks.MYCELIUM);
        });
    }

    protected static void method_27400(ModifiableTestableWorld arg, BlockPos arg2) {
        if (!TrunkPlacer.method_27403(arg, arg2)) {
            TreeFeature.setBlockStateWithoutUpdatingNeighbors(arg, arg2, Blocks.DIRT.getDefaultState());
        }
    }

    protected static boolean method_27402(ModifiableTestableWorld arg, Random random, BlockPos arg2, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        if (TreeFeature.canReplace(arg, arg2)) {
            TrunkPlacer.method_27404(arg, arg2, arg4.trunkProvider.getBlockState(random, arg2), arg3);
            set.add(arg2.toImmutable());
            return true;
        }
        return false;
    }

    protected static void method_27401(ModifiableTestableWorld arg, Random random, BlockPos.Mutable arg2, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        if (TreeFeature.canTreeReplace(arg, arg2)) {
            TrunkPlacer.method_27402(arg, random, arg2, set, arg3, arg4);
        }
    }
}

