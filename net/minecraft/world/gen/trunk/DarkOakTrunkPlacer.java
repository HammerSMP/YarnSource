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
    public static final Codec<DarkOakTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> DarkOakTrunkPlacer.method_28904(instance).apply((Applicative)instance, DarkOakTrunkPlacer::new));

    public DarkOakTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld world, Random random, int trunkHeight, BlockPos pos, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        ArrayList list = Lists.newArrayList();
        BlockPos lv = pos.down();
        DarkOakTrunkPlacer.method_27400(world, lv);
        DarkOakTrunkPlacer.method_27400(world, lv.east());
        DarkOakTrunkPlacer.method_27400(world, lv.south());
        DarkOakTrunkPlacer.method_27400(world, lv.south().east());
        Direction lv2 = Direction.Type.HORIZONTAL.random(random);
        int j = trunkHeight - random.nextInt(4);
        int k = 2 - random.nextInt(3);
        int l = pos.getX();
        int m = pos.getY();
        int n = pos.getZ();
        int o = l;
        int p = n;
        int q = m + trunkHeight - 1;
        for (int r = 0; r < trunkHeight; ++r) {
            int s;
            BlockPos lv3;
            if (r >= j && k > 0) {
                o += lv2.getOffsetX();
                p += lv2.getOffsetZ();
                --k;
            }
            if (!TreeFeature.isAirOrLeaves(world, lv3 = new BlockPos(o, s = m + r, p))) continue;
            DarkOakTrunkPlacer.method_27402(world, random, lv3, set, arg3, arg4);
            DarkOakTrunkPlacer.method_27402(world, random, lv3.east(), set, arg3, arg4);
            DarkOakTrunkPlacer.method_27402(world, random, lv3.south(), set, arg3, arg4);
            DarkOakTrunkPlacer.method_27402(world, random, lv3.east().south(), set, arg3, arg4);
        }
        list.add(new FoliagePlacer.TreeNode(new BlockPos(o, q, p), 0, true));
        for (int t = -1; t <= 2; ++t) {
            for (int u = -1; u <= 2; ++u) {
                if (t >= 0 && t <= 1 && u >= 0 && u <= 1 || random.nextInt(3) > 0) continue;
                int v = random.nextInt(3) + 2;
                for (int w = 0; w < v; ++w) {
                    DarkOakTrunkPlacer.method_27402(world, random, new BlockPos(l + t, q - w - 1, n + u), set, arg3, arg4);
                }
                list.add(new FoliagePlacer.TreeNode(new BlockPos(o + t, q, p + u), 0, false));
            }
        }
        return list;
    }
}

