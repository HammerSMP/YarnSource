/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.Dynamic
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.CoralFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class CoralTreeFeature
extends CoralFeature {
    public CoralTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    protected boolean spawnCoral(IWorld arg, Random random, BlockPos arg2, BlockState arg3) {
        BlockPos.Mutable lv = arg2.mutableCopy();
        int i = random.nextInt(3) + 1;
        for (int j = 0; j < i; ++j) {
            if (!this.spawnCoralPiece(arg, random, lv, arg3)) {
                return true;
            }
            lv.move(Direction.UP);
        }
        BlockPos lv2 = lv.toImmutable();
        int k = random.nextInt(3) + 2;
        ArrayList list = Lists.newArrayList((Iterable)Direction.Type.HORIZONTAL);
        Collections.shuffle(list, random);
        List list2 = list.subList(0, k);
        for (Direction lv3 : list2) {
            lv.set(lv2);
            lv.move(lv3);
            int l = random.nextInt(5) + 2;
            int m = 0;
            for (int n = 0; n < l && this.spawnCoralPiece(arg, random, lv, arg3); ++n) {
                lv.move(Direction.UP);
                if (n != 0 && (++m < 2 || !(random.nextFloat() < 0.25f))) continue;
                lv.move(lv3);
                m = 0;
            }
        }
        return true;
    }
}

