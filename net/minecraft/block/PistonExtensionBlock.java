/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PistonExtensionBlock
extends BlockWithEntity {
    public static final DirectionProperty FACING = PistonHeadBlock.FACING;
    public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.TYPE;

    public PistonExtensionBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(TYPE, PistonType.DEFAULT));
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView arg) {
        return null;
    }

    public static BlockEntity createBlockEntityPiston(BlockState arg, Direction arg2, boolean bl, boolean bl2) {
        return new PistonBlockEntity(arg, arg2, bl, bl2);
    }

    @Override
    public void onBlockRemoved(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof PistonBlockEntity) {
            ((PistonBlockEntity)lv).finish();
        }
    }

    @Override
    public void onBroken(IWorld arg, BlockPos arg2, BlockState arg3) {
        BlockPos lv = arg2.offset(arg3.get(FACING).getOpposite());
        BlockState lv2 = arg.getBlockState(lv);
        if (lv2.getBlock() instanceof PistonBlock && lv2.get(PistonBlock.EXTENDED).booleanValue()) {
            arg.removeBlock(lv, false);
        }
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (!arg2.isClient && arg2.getBlockEntity(arg3) == null) {
            arg2.removeBlock(arg3, false);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState arg, LootContext.Builder arg2) {
        PistonBlockEntity lv = this.getPistonBlockEntity(arg2.getWorld(), arg2.get(LootContextParameters.POSITION));
        if (lv == null) {
            return Collections.emptyList();
        }
        return lv.getPushedBlock().getDroppedStacks(arg2);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        PistonBlockEntity lv = this.getPistonBlockEntity(arg2, arg3);
        if (lv != null) {
            return lv.getCollisionShape(arg2, arg3);
        }
        return VoxelShapes.empty();
    }

    @Nullable
    private PistonBlockEntity getPistonBlockEntity(BlockView arg, BlockPos arg2) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof PistonBlockEntity) {
            return (PistonBlockEntity)lv;
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(FACING, arg2.rotate(arg.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return arg.rotate(arg2.getRotation(arg.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, TYPE);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

