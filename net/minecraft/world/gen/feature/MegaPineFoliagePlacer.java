/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class MegaPineFoliagePlacer
extends FoliagePlacer {
    public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> MegaPineFoliagePlacer.method_28846(instance).and(instance.group((App)Codec.INT.fieldOf("height_random").forGetter(arg -> arg.heightRange), (App)Codec.INT.fieldOf("crown_height").forGetter(arg -> arg.crownHeight))).apply((Applicative)instance, MegaPineFoliagePlacer::new));
    private final int heightRange;
    private final int crownHeight;

    public MegaPineFoliagePlacer(int i, int j, int k, int l, int m, int n) {
        super(i, j, k, l);
        this.heightRange = m;
        this.crownHeight = n;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(ModifiableTestableWorld arg, Random random, TreeFeatureConfig arg2, int i, FoliagePlacer.TreeNode arg3, int j, int k, Set<BlockPos> set, int l, BlockBox arg4) {
        BlockPos lv = arg3.getCenter();
        int m = 0;
        for (int n = lv.getY() - j + l; n <= lv.getY() + l; ++n) {
            int r;
            int o = lv.getY() - n;
            int p = k + arg3.getFoliageRadius() + MathHelper.floor((float)o / (float)j * 3.5f);
            if (o > 0 && p == m && (n & 1) == 0) {
                int q = p + 1;
            } else {
                r = p;
            }
            this.generate(arg, random, arg2, new BlockPos(lv.getX(), n, lv.getZ()), r, set, 0, arg3.isGiantTrunk(), arg4);
            m = p;
        }
    }

    @Override
    public int getHeight(Random random, int i, TreeFeatureConfig arg) {
        return random.nextInt(this.heightRange + 1) + this.crownHeight;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int i, int j, int k, int l, boolean bl) {
        if (i + k >= 7) {
            return true;
        }
        return i * i + k * k > l * l;
    }
}

