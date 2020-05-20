/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.command.arguments;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface PosArgument {
    public Vec3d toAbsolutePos(ServerCommandSource var1);

    public Vec2f toAbsoluteRotation(ServerCommandSource var1);

    default public BlockPos toAbsoluteBlockPos(ServerCommandSource arg) {
        return new BlockPos(this.toAbsolutePos(arg));
    }

    public boolean isXRelative();

    public boolean isYRelative();

    public boolean isZRelative();
}
