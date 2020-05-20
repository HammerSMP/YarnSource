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
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class DarkOakTrunkPlacer
extends TrunkPlacer {
    public DarkOakTrunkPlacer(int i, int j, int k) {
        this(i, j, k, (TrunkPlacerType<? extends DarkOakTrunkPlacer>)TrunkPlacerType.DARK_OAK_TRUNK_PLACER);
    }

    public DarkOakTrunkPlacer(int i, int j, int k, TrunkPlacerType<? extends DarkOakTrunkPlacer> arg) {
        super(i, j, k, arg);
    }

    public <T> DarkOakTrunkPlacer(Dynamic<T> dynamic) {
        this(dynamic.get("base_height").asInt(0), dynamic.get("height_rand_a").asInt(0), dynamic.get("height_rand_b").asInt(0));
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld arg, Random random, int i, BlockPos arg2, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        ArrayList list = Lists.newArrayList();
        BlockPos lv = arg2.down();
        DarkOakTrunkPlacer.method_27400(arg, lv);
        DarkOakTrunkPlacer.method_27400(arg, lv.east());
        DarkOakTrunkPlacer.method_27400(arg, lv.south());
        DarkOakTrunkPlacer.method_27400(arg, lv.south().east());
        Direction lv2 = Direction.Type.HORIZONTAL.random(random);
        int j = i - random.nextInt(4);
        int k = 2 - random.nextInt(3);
        int l = arg2.getX();
        int m = arg2.getY();
        int n = arg2.getZ();
        int o = l;
        int p = n;
        int q = m + i - 1;
        for (int r = 0; r < i; ++r) {
            int s;
            BlockPos lv3;
            if (r >= j && k > 0) {
                o += lv2.getOffsetX();
                p += lv2.getOffsetZ();
                --k;
            }
            if (!TreeFeature.isAirOrLeaves(arg, lv3 = new BlockPos(o, s = m + r, p))) continue;
            DarkOakTrunkPlacer.method_27402(arg, random, lv3, set, arg3, arg4);
            DarkOakTrunkPlacer.method_27402(arg, random, lv3.east(), set, arg3, arg4);
            DarkOakTrunkPlacer.method_27402(arg, random, lv3.south(), set, arg3, arg4);
            DarkOakTrunkPlacer.method_27402(arg, random, lv3.east().south(), set, arg3, arg4);
        }
        list.add(new FoliagePlacer.TreeNode(new BlockPos(o, q, p), 0, true));
        for (int t = -1; t <= 2; ++t) {
            for (int u = -1; u <= 2; ++u) {
                if (t >= 0 && t <= 1 && u >= 0 && u <= 1 || random.nextInt(3) > 0) continue;
                int v = random.nextInt(3) + 2;
                for (int w = 0; w < v; ++w) {
                    DarkOakTrunkPlacer.method_27402(arg, random, new BlockPos(l + t, q - w - 1, n + u), set, arg3, arg4);
                }
                list.add(new FoliagePlacer.TreeNode(new BlockPos(o + t, q, p + u), 0, false));
            }
        }
        return list;
    }
}

