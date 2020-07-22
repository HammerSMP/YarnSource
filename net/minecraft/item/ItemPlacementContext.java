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
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemPlacementContext
extends ItemUsageContext {
    private final BlockPos placementPos;
    protected boolean canReplaceExisting = true;

    public ItemPlacementContext(PlayerEntity arg, Hand arg2, ItemStack arg3, BlockHitResult arg4) {
        this(arg.world, arg, arg2, arg3, arg4);
    }

    public ItemPlacementContext(ItemUsageContext context) {
        this(context.getWorld(), context.getPlayer(), context.getHand(), context.getStack(), context.method_30344());
    }

    protected ItemPlacementContext(World arg, @Nullable PlayerEntity arg2, Hand arg3, ItemStack arg4, BlockHitResult arg5) {
        super(arg, arg2, arg3, arg4, arg5);
        this.placementPos = arg5.getBlockPos().offset(arg5.getSide());
        this.canReplaceExisting = arg.getBlockState(arg5.getBlockPos()).canReplace(this);
    }

    public static ItemPlacementContext offset(ItemPlacementContext context, BlockPos pos, Direction side) {
        return new ItemPlacementContext(context.getWorld(), context.getPlayer(), context.getHand(), context.getStack(), new BlockHitResult(new Vec3d((double)pos.getX() + 0.5 + (double)side.getOffsetX() * 0.5, (double)pos.getY() + 0.5 + (double)side.getOffsetY() * 0.5, (double)pos.getZ() + 0.5 + (double)side.getOffsetZ() * 0.5), side, pos, false));
    }

    @Override
    public BlockPos getBlockPos() {
        return this.canReplaceExisting ? super.getBlockPos() : this.placementPos;
    }

    public boolean canPlace() {
        return this.canReplaceExisting || this.getWorld().getBlockState(this.getBlockPos()).canReplace(this);
    }

    public boolean canReplaceExisting() {
        return this.canReplaceExisting;
    }

    public Direction getPlayerLookDirection() {
        return Direction.getEntityFacingOrder(this.getPlayer())[0];
    }

    public Direction[] getPlacementDirections() {
        int i;
        Direction[] lvs = Direction.getEntityFacingOrder(this.getPlayer());
        if (this.canReplaceExisting) {
            return lvs;
        }
        Direction lv = this.getSide();
        for (i = 0; i < lvs.length && lvs[i] != lv.getOpposite(); ++i) {
        }
        if (i > 0) {
            System.arraycopy(lvs, 0, lvs, 1, i);
            lvs[0] = lv.getOpposite();
        }
        return lvs;
    }
}

