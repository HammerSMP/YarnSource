/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.structure;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;

public abstract class StructurePieceWithDimensions
extends StructurePiece {
    protected final int width;
    protected final int height;
    protected final int depth;
    protected int hPos = -1;

    protected StructurePieceWithDimensions(StructurePieceType arg, Random random, int i, int j, int k, int l, int m, int n) {
        super(arg, 0);
        this.width = l;
        this.height = m;
        this.depth = n;
        this.setOrientation(Direction.Type.HORIZONTAL.random(random));
        this.boundingBox = this.getFacing().getAxis() == Direction.Axis.Z ? new BlockBox(i, j, k, i + l - 1, j + m - 1, k + n - 1) : new BlockBox(i, j, k, i + n - 1, j + m - 1, k + l - 1);
    }

    protected StructurePieceWithDimensions(StructurePieceType arg, CompoundTag arg2) {
        super(arg, arg2);
        this.width = arg2.getInt("Width");
        this.height = arg2.getInt("Height");
        this.depth = arg2.getInt("Depth");
        this.hPos = arg2.getInt("HPos");
    }

    @Override
    protected void toNbt(CompoundTag arg) {
        arg.putInt("Width", this.width);
        arg.putInt("Height", this.height);
        arg.putInt("Depth", this.depth);
        arg.putInt("HPos", this.hPos);
    }

    protected boolean method_14839(WorldAccess arg, BlockBox arg2, int i) {
        if (this.hPos >= 0) {
            return true;
        }
        int j = 0;
        int k = 0;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int l = this.boundingBox.minZ; l <= this.boundingBox.maxZ; ++l) {
            for (int m = this.boundingBox.minX; m <= this.boundingBox.maxX; ++m) {
                lv.set(m, 64, l);
                if (!arg2.contains(lv)) continue;
                j += arg.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lv).getY();
                ++k;
            }
        }
        if (k == 0) {
            return false;
        }
        this.hPos = j / k;
        this.boundingBox.offset(0, this.hPos - this.boundingBox.minY + i, 0);
        return true;
    }
}

