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

    public static void registerBehavior(ItemConvertible arg, DispenserBehavior arg2) {
        BEHAVIORS.put(arg.asItem(), arg2);
    }

    protected DispenserBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(TRIGGERED, false));
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg2.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof DispenserBlockEntity) {
            arg4.openHandledScreen((DispenserBlockEntity)lv);
            if (lv instanceof DropperBlockEntity) {
                arg4.incrementStat(Stats.INSPECT_DROPPER);
            } else {
                arg4.incrementStat(Stats.INSPECT_DISPENSER);
            }
        }
        return ActionResult.SUCCESS;
    }

    protected void dispense(World arg, BlockPos arg2) {
        BlockPointerImpl lv = new BlockPointerImpl(arg, arg2);
        DispenserBlockEntity lv2 = (DispenserBlockEntity)lv.getBlockEntity();
        int i = lv2.chooseNonEmptySlot();
        if (i < 0) {
            arg.syncWorldEvent(1001, arg2, 0);
            return;
        }
        ItemStack lv3 = lv2.getStack(i);
        DispenserBehavior lv4 = this.getBehaviorForItem(lv3);
        if (lv4 != DispenserBehavior.NOOP) {
            lv2.setStack(i, lv4.dispense(lv, lv3));
        }
    }

    protected DispenserBehavior getBehaviorForItem(ItemStack arg) {
        return BEHAVIORS.get(arg.getItem());
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        boolean bl2 = arg2.isReceivingRedstonePower(arg3) || arg2.isReceivingRedstonePower(arg3.up());
        boolean bl3 = arg.get(TRIGGERED);
        if (bl2 && !bl3) {
            arg2.getBlockTickScheduler().schedule(arg3, this, 4);
            arg2.setBlockState(arg3, (BlockState)arg.with(TRIGGERED, true), 4);
        } else if (!bl2 && bl3) {
            arg2.setBlockState(arg3, (BlockState)arg.with(TRIGGERED, false), 4);
        }
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        this.dispense(arg2, arg3);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new DispenserBlockEntity();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(FACING, arg.getPlayerLookDirection().getOpposite());
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, LivingEntity arg4, ItemStack arg5) {
        BlockEntity lv;
        if (arg5.hasCustomName() && (lv = arg.getBlockEntity(arg2)) instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)lv).setCustomName(arg5.getName());
        }
    }

    @Override
    public void onBlockRemoved(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof DispenserBlockEntity) {
            ItemScatterer.spawn(arg2, arg3, (Inventory)((DispenserBlockEntity)lv));
            arg2.updateComparators(arg3, this);
        }
        super.onBlockRemoved(arg, arg2, arg3, arg4, bl);
    }

    public static Position getOutputLocation(BlockPointer arg) {
        Direction lv = arg.getBlockState().get(FACING);
        double d = arg.getX() + 0.7 * (double)lv.getOffsetX();
        double e = arg.getY() + 0.7 * (double)lv.getOffsetY();
        double f = arg.getZ() + 0.7 * (double)lv.getOffsetZ();
        return new PositionImpl(d, e, f);
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        return ScreenHandler.calculateComparatorOutput(arg2.getBlockEntity(arg3));
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
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
        arg.add(FACING, TRIGGERED);
    }
}

