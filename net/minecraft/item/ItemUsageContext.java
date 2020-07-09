/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemUsageContext {
    @Nullable
    private final PlayerEntity player;
    private final Hand hand;
    private final BlockHitResult hit;
    private final World world;
    private final ItemStack stack;

    public ItemUsageContext(PlayerEntity arg, Hand arg2, BlockHitResult arg3) {
        this(arg.world, arg, arg2, arg.getStackInHand(arg2), arg3);
    }

    protected ItemUsageContext(World arg, @Nullable PlayerEntity arg2, Hand arg3, ItemStack arg4, BlockHitResult arg5) {
        this.player = arg2;
        this.hand = arg3;
        this.hit = arg5;
        this.stack = arg4;
        this.world = arg;
    }

    protected final BlockHitResult method_30344() {
        return this.hit;
    }

    public BlockPos getBlockPos() {
        return this.hit.getBlockPos();
    }

    public Direction getSide() {
        return this.hit.getSide();
    }

    public Vec3d getHitPos() {
        return this.hit.getPos();
    }

    public boolean hitsInsideBlock() {
        return this.hit.isInsideBlock();
    }

    public ItemStack getStack() {
        return this.stack;
    }

    @Nullable
    public PlayerEntity getPlayer() {
        return this.player;
    }

    public Hand getHand() {
        return this.hand;
    }

    public World getWorld() {
        return this.world;
    }

    public Direction getPlayerFacing() {
        return this.player == null ? Direction.NORTH : this.player.getHorizontalFacing();
    }

    public boolean shouldCancelInteraction() {
        return this.player != null && this.player.shouldCancelInteraction();
    }

    public float getPlayerYaw() {
        return this.player == null ? 0.0f : this.player.yaw;
    }
}

