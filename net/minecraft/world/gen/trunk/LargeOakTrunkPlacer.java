/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class LargeOakTrunkPlacer
extends TrunkPlacer {
    public static final Codec<LargeOakTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> LargeOakTrunkPlacer.method_28904(instance).apply((Applicative)instance, LargeOakTrunkPlacer::new));

    public LargeOakTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.FANCY_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        int o;
        int j = 5;
        int k = trunkHeight + 2;
        int l = MathHelper.floor((double)k * 0.618);
        if (!arg4.skipFluidCheck) {
            LargeOakTrunkPlacer.method_27400(world, pos.down());
        }
        double d = 1.0;
        int m = Math.min(1, MathHelper.floor(1.382 + Math.pow(1.0 * (double)k / 13.0, 2.0)));
        int n = pos.getY() + l;
        ArrayList list = Lists.newArrayList();
        list.add(new BranchPosition(pos.up(o), n));
        for (o = k - 5; o >= 0; --o) {
            float f = this.method_27396(k, o);
            if (f < 0.0f) continue;
            for (int p = 0; p < m; ++p) {
                BlockPos lv2;
                double r;
                double h;
                double e = 1.0;
                double g = 1.0 * (double)f * ((double)random.nextFloat() + 0.328);
                double q = g * Math.sin(h = (double)(random.nextFloat() * 2.0f) * Math.PI) + 0.5;
                BlockPos lv = pos.add(q, (double)(o - 1), r = g * Math.cos(h) + 0.5);
                if (!this.makeOrCheckBranch(world, random, lv, lv2 = lv.up(5), false, set, arg3, arg4)) continue;
                int s = pos.getX() - lv.getX();
                int t = pos.getZ() - lv.getZ();
                double u = (double)lv.getY() - Math.sqrt(s * s + t * t) * 0.381;
                int v = u > (double)n ? n : (int)u;
                BlockPos lv3 = new BlockPos(pos.getX(), v, pos.getZ());
                if (!this.makeOrCheckBranch(world, random, lv3, lv, false, set, arg3, arg4)) continue;
                list.add(new BranchPosition(lv, lv3.getY()));
            }
        }
        this.makeOrCheckBranch(world, random, pos, pos.up(l), true, set, arg3, arg4);
        this.makeBranches(world, random, k, pos, list, set, arg3, arg4);
        ArrayList list2 = Lists.newArrayList();
        for (BranchPosition lv4 : list) {
            if (!this.isHighEnough(k, lv4.getEndY() - pos.getY())) continue;
            list2.add(lv4.node);
        }
        return list2;
    }

    private boolean makeOrCheckBranch(ModifiableTestableWorld world, Random random, BlockPos start, BlockPos end, boolean make, Set<BlockPos> set, BlockBox arg4, TreeFeatureConfig config) {
        if (!make && Objects.equals(start, end)) {
            return true;
        }
        BlockPos lv = end.add(-start.getX(), -start.getY(), -start.getZ());
        int i = this.getLongestSide(lv);
        float f = (float)lv.getX() / (float)i;
        float g = (float)lv.getY() / (float)i;
        float h = (float)lv.getZ() / (float)i;
        for (int j = 0; j <= i; ++j) {
            BlockPos lv2 = start.add(0.5f + (float)j * f, 0.5f + (float)j * g, 0.5f + (float)j * h);
            if (make) {
                LargeOakTrunkPlacer.method_27404(world, lv2, (BlockState)config.trunkProvider.getBlockState(random, lv2).with(PillarBlock.AXIS, this.getLogAxis(start, lv2)), arg4);
                set.add(lv2.toImmutable());
                continue;
            }
            if (TreeFeature.canTreeReplace(world, lv2)) continue;
            return false;
        }
        return true;
    }

    private int getLongestSide(BlockPos offset) {
        int i = MathHelper.abs(offset.getX());
        int j = MathHelper.abs(offset.getY());
        int k = MathHelper.abs(offset.getZ());
        return Math.max(i, Math.max(j, k));
    }

    private Direction.Axis getLogAxis(BlockPos branchStart, BlockPos branchEnd) {
        int j;
        Direction.Axis lv = Direction.Axis.Y;
        int i = Math.abs(branchEnd.getX() - branchStart.getX());
        int k = Math.max(i, j = Math.abs(branchEnd.getZ() - branchStart.getZ()));
        if (k > 0) {
            lv = i == k ? Direction.Axis.X : Direction.Axis.Z;
        }
        return lv;
    }

    private boolean isHighEnough(int treeHeight, int height) {
        return (double)height >= (double)treeHeight * 0.2;
    }

    private void makeBranches(ModifiableTestableWorld world, Random random, int treeHeight, BlockPos treePos, List<BranchPosition> branches, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig config) {
        for (BranchPosition lv : branches) {
            int j = lv.getEndY();
            BlockPos lv2 = new BlockPos(treePos.getX(), j, treePos.getZ());
            if (lv2.equals(lv.node.getCenter()) || !this.isHighEnough(treeHeight, j - treePos.getY())) continue;
            this.makeOrCheckBranch(world, random, lv2, lv.node.getCenter(), true, set, arg3, config);
        }
    }

    private float method_27396(int i, int j) {
        if ((float)j < (float)i * 0.3f) {
            return -1.0f;
        }
        float f = (float)i / 2.0f;
        float g = f - (float)j;
        float h = MathHelper.sqrt(f * f - g * g);
        if (g == 0.0f) {
            h = f;
        } else if (Math.abs(g) >= f) {
            return 0.0f;
        }
        return h * 0.5f;
    }

    static class BranchPosition {
        private final FoliagePlacer.TreeNode node;
        private final int endY;

        public BranchPosition(BlockPos pos, int width) {
            this.node = new FoliagePlacer.TreeNode(pos, 0, false);
            this.endY = width;
        }

        public int getEndY() {
            return this.endY;
        }
    }
}

