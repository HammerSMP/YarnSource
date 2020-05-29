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

    public static BlockHitResult createMissed(Vec3d arg, Direction arg2, BlockPos arg3) {
        return new BlockHitResult(true, arg, arg2, arg3, false);
    }

    public BlockHitResult(Vec3d arg, Direction arg2, BlockPos arg3, boolean bl) {
        this(false, arg, arg2, arg3, bl);
    }

    private BlockHitResult(boolean bl, Vec3d arg, Direction arg2, BlockPos arg3, boolean bl2) {
        super(arg);
        this.missed = bl;
        this.side = arg2;
        this.blockPos = arg3;
        this.insideBlock = bl2;
    }

    public BlockHitResult withSide(Direction arg) {
        return new BlockHitResult(this.missed, this.pos, arg, this.blockPos, this.insideBlock);
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

