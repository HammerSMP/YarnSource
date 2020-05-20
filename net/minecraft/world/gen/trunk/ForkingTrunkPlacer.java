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
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class ForkingTrunkPlacer
extends TrunkPlacer {
    public static final Codec<ForkingTrunkPlacer> field_24968 = RecordCodecBuilder.create(instance -> ForkingTrunkPlacer.method_28904(instance).apply((Applicative)instance, ForkingTrunkPlacer::new));

    public ForkingTrunkPlacer(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected TrunkPlacerType<?> method_28903() {
        return TrunkPlacerType.FORKING_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(ModifiableTestableWorld arg, Random random, int i, BlockPos arg2, Set<BlockPos> set, BlockBox arg3, TreeFeatureConfig arg4) {
        ForkingTrunkPlacer.method_27400(arg, arg2.down());
        ArrayList list = Lists.newArrayList();
        Direction lv = Direction.Type.HORIZONTAL.random(random);
        int j = i - random.nextInt(4) - 1;
        int k = 3 - random.nextInt(3);
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        int l = arg2.getX();
        int m = arg2.getZ();
        int n = 0;
        for (int o = 0; o < i; ++o) {
            int p = arg2.getY() + o;
            if (o >= j && k > 0) {
                l += lv.getOffsetX();
                m += lv.getOffsetZ();
                --k;
            }
            if (!ForkingTrunkPlacer.method_27402(arg, random, lv2.set(l, p, m), set, arg3, arg4)) continue;
            n = p + 1;
        }
        list.add(new FoliagePlacer.TreeNode(new BlockPos(l, n, m), 1, false));
        l = arg2.getX();
        m = arg2.getZ();
        Direction lv3 = Direction.Type.HORIZONTAL.random(random);
        if (lv3 != lv) {
            int q = j - random.nextInt(2) - 1;
            int r = 1 + random.nextInt(3);
            n = 0;
            for (int s = q; s < i && r > 0; ++s, --r) {
                if (s < 1) continue;
                int t = arg2.getY() + s;
                if (!ForkingTrunkPlacer.method_27402(arg, random, lv2.set(l += lv3.getOffsetX(), t, m += lv3.getOffsetZ()), set, arg3, arg4)) continue;
                n = t + 1;
            }
            if (n > 1) {
                list.add(new FoliagePlacer.TreeNode(new BlockPos(l, n, m), 0, false));
            }
        }
        return list;
    }
}

