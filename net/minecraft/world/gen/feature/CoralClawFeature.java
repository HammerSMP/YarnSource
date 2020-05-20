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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.CoralFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class CoralClawFeature
extends CoralFeature {
    public CoralClawFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    protected boolean spawnCoral(WorldAccess arg, Random random, BlockPos arg2, BlockState arg3) {
        if (!this.spawnCoralPiece(arg, random, arg2, arg3)) {
            return false;
        }
        Direction lv = Direction.Type.HORIZONTAL.random(random);
        int i = random.nextInt(2) + 2;
        ArrayList list = Lists.newArrayList((Object[])new Direction[]{lv, lv.rotateYClockwise(), lv.rotateYCounterclockwise()});
        Collections.shuffle(list, random);
        List list2 = list.subList(0, i);
        block0: for (Direction lv2 : list2) {
            int l;
            Direction lv5;
            BlockPos.Mutable lv3 = arg2.mutableCopy();
            int j = random.nextInt(2) + 1;
            lv3.move(lv2);
            if (lv2 == lv) {
                Direction lv4 = lv;
                int k = random.nextInt(3) + 2;
            } else {
                lv3.move(Direction.UP);
                Direction[] lvs = new Direction[]{lv2, Direction.UP};
                lv5 = Util.getRandom(lvs, random);
                l = random.nextInt(3) + 3;
            }
            for (int m = 0; m < j && this.spawnCoralPiece(arg, random, lv3, arg3); ++m) {
                lv3.move(lv5);
            }
            lv3.move(lv5.getOpposite());
            lv3.move(Direction.UP);
            for (int n = 0; n < l; ++n) {
                lv3.move(lv);
                if (!this.spawnCoralPiece(arg, random, lv3, arg3)) continue block0;
                if (!(random.nextFloat() < 0.25f)) continue;
                lv3.move(Direction.UP);
            }
        }
        return true;
    }
}

