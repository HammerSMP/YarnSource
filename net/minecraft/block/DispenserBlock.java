/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DispenserBlock
extends BlockWithEntity {
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
    private static final Map<Item, DispenserBehavior> BEHAVIORS = (Map)Util.make(new Object2ObjectOpenHashMap(), object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue((Object)new ItemDispenserBehavior()));

    public static void registerBehavior(ItemConvertible provider, DispenserBehavior behavior) {
        BEHAVIORS.put(provider.asItem(), behavior);
    }

    protected DispenserBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(TRIGGERED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof DispenserBlockEntity) {
            player.openHandledScreen((DispenserBlockEntity)lv);
            if (lv instanceof DropperBlockEntity) {
                player.incrementStat(Stats.INSPECT_DROPPER);
            } else {
                player.incrementStat(Stats.INSPECT_DISPENSER);
            }
        }
        return ActionResult.CONSUME;
    }

    protected void dispense(ServerWorld arg, BlockPos pos) {
        BlockPointerImpl lv = new BlockPointerImpl(arg, pos);
        DispenserBlockEntity lv2 = (DispenserBlockEntity)lv.getBlockEntity();
        int i = lv2.chooseNonEmptySlot();
        if (i < 0) {
            arg.syncWorldEvent(1001, pos, 0);
            return;
        }
        ItemStack lv3 = lv2.getStack(i);
        DispenserBehavior lv4 = this.getBehaviorForItem(lv3);
        if (lv4 != DispenserBehavior.NOOP) {
            lv2.setStack(i, lv4.dispense(lv, lv3));
        }
    }

    protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
        return BEHAVIORS.get(stack.getItem());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        boolean bl2 = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean bl3 = state.get(TRIGGERED);
        if (bl2 && !bl3) {
            world.getBlockTickScheduler().schedule(pos, this, 4);
            world.setBlockState(pos, (BlockState)state.with(TRIGGERED, true), 4);
        } else if (!bl2 && bl3) {
            world.setBlockState(pos, (BlockState)state.with(TRIGGERED, false), 4);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.dispense(world, pos);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new DispenserBlockEntity();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity lv;
        if (itemStack.hasCustomName() && (lv = world.getBlockEntity(pos)) instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)lv).setCustomName(itemStack.getName());
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof DispenserBlockEntity) {
            ItemScatterer.spawn(world, pos, (Inventory)((DispenserBlockEntity)lv));
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public static Position getOutputLocation(BlockPointer pointer) {
        Direction lv = pointer.getBlockState().get(FACING);
        double d = pointer.getX() + 0.7 * (double)lv.getOffsetX();
        double e = pointer.getY() + 0.7 * (double)lv.getOffsetY();
        double f = pointer.getZ() + 0.7 * (double)lv.getOffsetZ();
        return new PositionImpl(d, e, f);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }
}

