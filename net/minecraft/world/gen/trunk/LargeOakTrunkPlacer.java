/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.trunk;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
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
    public LargeOakTrunkPlacer(int i, int j, int k) {
        this(i, j, k, (TrunkPlacerType<? extends LargeOakTrunkPlacer>)TrunkPlacerType.FANCY_TRUNK_PLACER);
    }

    public LargeOakTrunkPlacer(int i, int j, int k, TrunkPlacerType<? extends LargeOakTrunkPlacer> arg) {
        super(i, j, k, arg);
    }

    public <T> LargeOakTrunkPlacer(Dynamic<T> dynamic) {
        this(dynamic.get("base_height").asInt(0), dynamic.get("height_rand_a").asInt(0), dynamic.get("height_rand_b").asInt(0));
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld arg, Random random, int i, BlockPos arg2, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        int o;
        int j = 5;
        int k = i + 2;
        int l = MathHelper.floor((double)k * 0.618);
        LargeOakTrunkPlacer.method_27400(arg, arg2.down());
        double d = 1.0;
        int m = Math.min(1, MathHelper.floor(1.382 + Math.pow(1.0 * (double)k / 13.0, 2.0)));
        int n = arg2.getY() + l;
        ArrayList list = Lists.newArrayList();
        list.add(new BranchPosition(arg2.up(o), n));
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
                BlockPos lv = arg2.add(q, (double)(o - 1), r = g * Math.cos(h) + 0.5);
                if (!this.makeOrCheckBranch(arg, random, lv, lv2 = lv.up(5), false, set, arg3, arg4)) continue;
                int s = arg2.getX() - lv.getX();
                int t = arg2.getZ() - lv.getZ();
                double u = (double)lv.getY() - Math.sqrt(s * s + t * t) * 0.381;
                int v = u > (double)n ? n : (int)u;
                BlockPos lv3 = new BlockPos(arg2.getX(), v, arg2.getZ());
                if (!this.makeOrCheckBranch(arg, random, lv3, lv, false, set, arg3, arg4)) continue;
                list.add(new BranchPosition(lv, lv3.getY()));
            }
        }
        this.makeOrCheckBranch(arg, random, arg2, arg2.up(l), true, set, arg3, arg4);
        this.makeBranches(arg, random, k, arg2, list, set, arg3, arg4);
        ArrayList list2 = Lists.newArrayList();
        for (BranchPosition lv4 : list) {
            if (!this.isHighEnough(k, lv4.getEndY() - arg2.getY())) continue;
            list2.add(lv4.node);
        }
        return list2;
    }

    private boolean makeOrCheckBranch(ModifiableTestableWorld arg, Random random, BlockPos arg2, BlockPos arg3, boolean bl, Set<BlockPos> set, BlockBox arg4, TreeFeatureConfig arg5) {
        if (!bl && Objects.equals(arg2, arg3)) {
            return true;
        }
        BlockPos lv = arg3.add(-arg2.getX(), -arg2.getY(), -arg2.getZ());
        int i = this.getLongestSide(lv);
        float f = (float)lv.getX() / (float)i;
        float g = (float)lv.getY() / (float)i;
        float h = (float)lv.getZ() / (float)i;
        for (int j = 0; j <= i; ++j) {
            BlockPos lv2 = arg2.add(0.5f + (float)j * f, 0.5f + (float)j * g, 0.5f + (float)j * h);
            if (bl) {
                LargeOakTrunkPlacer.method_27404(arg, lv2, (BlockState)arg5.trunkProvider.getBlockState(random, lv2).with(PillarBlock.AXIS, this.getLogAxis(arg2, lv2)), arg4);
                set.add(lv2.toImmutable());
                continue;
            }
            if (TreeFeature.canTreeReplace(arg, lv2)) continue;
            return false;
        }
        return true;
    }

    private int getLongestSide(BlockPos arg) {
        int i = MathHelper.abs(arg.getX());
        int j = MathHelper.abs(arg.getY());
        int k = MathHelper.abs(arg.getZ());
        return Math.max(i, Math.max(j, k));
    }

    private Direction.Axis getLogAxis(BlockPos arg, BlockPos arg2) {
        int j;
        Direction.Axis lv = Direction.Axis.Y;
        int i = Math.abs(arg2.getX() - arg.getX());
        int k = Math.max(i, j = Math.abs(arg2.getZ() - arg.getZ()));
        if (k > 0) {
            lv = i == k ? Direction.Axis.X : Direction.Axis.Z;
        }
        return lv;
    }

    private boolean isHighEnough(int i, int j) {
        return (double)j >= (double)i * 0.2;
    }

    private void makeBranches(ModifiableTestableWorld arg, Random random, int i, BlockPos arg2, List<BranchPosition> list, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        for (BranchPosition lv : list) {
            int j = lv.getEndY();
            BlockPos lv2 = new BlockPos(arg2.getX(), j, arg2.getZ());
            if (lv2.equals(lv.node.getCenter()) || !this.isHighEnough(i, j - arg2.getY())) continue;
            this.makeOrCheckBranch(arg, random, lv2, lv.node.getCenter(), true, set, arg3, arg4);
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

        public BranchPosition(BlockPos arg, int i) {
            this.node = new FoliagePlacer.TreeNode(arg, 0, false);
            this.endY = i;
        }

        public int getEndY() {
            return this.endY;
        }
    }
}

