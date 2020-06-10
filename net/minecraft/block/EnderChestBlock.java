/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class EnderChestBlock
extends AbstractChestBlock<EnderChestBlockEntity>
implements Waterloggable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    public static final TranslatableText CONTAINER_NAME = new TranslatableText("container.enderchest");

    protected EnderChestBlock(AbstractBlock.Settings arg) {
        super(arg, () -> BlockEntityType.ENDER_CHEST);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState arg, World arg2, BlockPos arg3, boolean bl) {
        return DoubleBlockProperties.PropertyRetriever::getFallback;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        FluidState lv;
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, arg.getPlayerFacing().getOpposite())).with(WATERLOGGED, (lv = arg.getWorld().getFluidState(arg.getBlockPos())).getFluid() == Fluids.WATER);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg22, BlockPos arg32, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        EnderChestInventory lv = arg4.getEnderChestInventory();
        BlockEntity lv2 = arg22.getBlockEntity(arg32);
        if (lv == null || !(lv2 instanceof EnderChestBlockEntity)) {
            return ActionResult.success(arg22.isClient);
        }
        BlockPos lv3 = arg32.up();
        if (arg22.getBlockState(lv3).isSolidBlock(arg22, lv3)) {
            return ActionResult.success(arg22.isClient);
        }
        if (arg22.isClient) {
            return ActionResult.SUCCESS;
        }
        EnderChestBlockEntity lv4 = (EnderChestBlockEntity)lv2;
        lv.setActiveBlockEntity(lv4);
        arg4.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, arg2, arg3) -> GenericContainerScreenHandler.createGeneric9x3(i, arg2, lv), CONTAINER_NAME));
        arg4.incrementStat(Stats.OPEN_ENDERCHEST);
        PiglinBrain.onGoldBlockBroken(arg4, true);
        return ActionResult.CONSUME;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new EnderChestBlockEntity();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        for (int i = 0; i < 3; ++i) {
            int j = random.nextInt(2) * 2 - 1;
            int k = random.nextInt(2) * 2 - 1;
            double d = (double)arg3.getX() + 0.5 + 0.25 * (double)j;
            double e = (float)arg3.getY() + random.nextFloat();
            double f = (double)arg3.getZ() + 0.5 + 0.25 * (double)k;
            double g = random.nextFloat() * (float)j;
            double h = ((double)random.nextFloat() - 0.5) * 0.125;
            double l = random.nextFloat() * (float)k;
            arg2.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, l);
        }
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
        arg.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

