/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.util.hit;

import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlockHitResult
extends HitResult {
    private final Direction side;
    private final BlockPos blockPos;
    private final boolean missed;
    private final boolean insideBlock;

    public static BlockHitResult createMissed(Vec3d pos, Direction side, BlockPos blockPos) {
        return new BlockHitResult(true, pos, side, blockPos, false);
    }

    public BlockHitResult(Vec3d pos, Direction side, BlockPos blockPos, boolean insideBlock) {
        this(false, pos, side, blockPos, insideBlock);
    }

    private BlockHitResult(boolean missed, Vec3d pos, Direction side, BlockPos blockPos, boolean insideBlock) {
        super(pos);
        this.missed = missed;
        this.side = side;
        this.blockPos = blockPos;
        this.insideBlock = insideBlock;
    }

    public BlockHitResult withSide(Direction side) {
        return new BlockHitResult(this.missed, this.pos, side, this.blockPos, this.insideBlock);
    }

    public BlockHitResult method_29328(BlockPos arg) {
        return new BlockHitResult(this.missed, this.pos, this.side, arg, this.insideBlock);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public Direction getSide() {
        return this.side;
    }

    @Override
    public HitResult.Type getType() {
        return this.missed ? HitResult.Type.MISS : HitResult.Type.BLOCK;
    }

    public boolean isInsideBlock() {
        return this.insideBlock;
    }
}

