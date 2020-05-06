/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.math.IntMath
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.util.shape;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.PairList;
import net.minecraft.util.shape.VoxelShapes;

public final class FractionalPairList
implements PairList {
    private final FractionalDoubleList mergedList;
    private final int firstSectionCount;
    private final int secondSectionCount;
    private final int gcd;

    FractionalPairList(int i, int j) {
        this.mergedList = new FractionalDoubleList((int)VoxelShapes.lcm(i, j));
        this.firstSectionCount = i;
        this.secondSectionCount = j;
        this.gcd = IntMath.gcd((int)i, (int)j);
    }

    @Override
    public boolean forEachPair(PairList.Consumer arg) {
        int i = this.firstSectionCount / this.gcd;
        int j = this.secondSectionCount / this.gcd;
        for (int k = 0; k <= this.mergedList.size(); ++k) {
            if (arg.merge(k / j, k / i, k)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList getPairs() {
        return this.mergedList;
    }
}

