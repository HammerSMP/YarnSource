/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public class ComparatorBlock
extends AbstractRedstoneGateBlock
implements BlockEntityProvider {
    public static final EnumProperty<ComparatorMode> MODE = Properties.COMPARATOR_MODE;

    public ComparatorBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(MODE, ComparatorMode.COMPARE));
    }

    @Override
    protected int getUpdateDelayInternal(BlockState arg) {
        return 2;
    }

    @Override
    protected int getOutputLevel(BlockView arg, BlockPos arg2, BlockState arg3) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof ComparatorBlockEntity) {
            return ((ComparatorBlockEntity)lv).getOutputSignal();
        }
        return 0;
    }

    private int calculateOutputSignal(World arg, BlockPos arg2, BlockState arg3) {
        if (arg3.get(MODE) == ComparatorMode.SUBTRACT) {
            return Math.max(this.getPower(arg, arg2, arg3) - this.getMaxInputLevelSides(arg, arg2, arg3), 0);
        }
        return this.getPower(arg, arg2, arg3);
    }

    @Override
    protected boolean hasPower(World arg, BlockPos arg2, BlockState arg3) {
        int i = this.getPower(arg, arg2, arg3);
        if (i == 0) {
            return false;
        }
        int j = this.getMaxInputLevelSides(arg, arg2, arg3);
        if (i > j) {
            return true;
        }
        return i == j && arg3.get(MODE) == ComparatorMode.COMPARE;
    }

    @Override
    protected int getPower(World arg, BlockPos arg2, BlockState arg3) {
        int i = super.getPower(arg, arg2, arg3);
        Direction lv = arg3.get(FACING);
        BlockPos lv2 = arg2.offset(lv);
        BlockState lv3 = arg.getBlockState(lv2);
        if (lv3.hasComparatorOutput()) {
            i = lv3.getComparatorOutput(arg, lv2);
        } else if (i < 15 && lv3.isSolidBlock(arg, lv2)) {
            lv2 = lv2.offset(lv);
            lv3 = arg.getBlockState(lv2);
            ItemFrameEntity lv4 = this.getAttachedItemFrame(arg, lv, lv2);
            int j = Math.max(lv4 == null ? Integer.MIN_VALUE : lv4.getComparatorPower(), lv3.hasComparatorOutput() ? lv3.getComparatorOutput(arg, lv2) : Integer.MIN_VALUE);
            if (j != Integer.MIN_VALUE) {
                i = j;
            }
        }
        return i;
    }

    @Nullable
    private ItemFrameEntity getAttachedItemFrame(World arg, Direction arg22, BlockPos arg3) {
        List<ItemFrameEntity> list = arg.getEntities(ItemFrameEntity.class, new Box(arg3.getX(), arg3.getY(), arg3.getZ(), arg3.getX() + 1, arg3.getY() + 1, arg3.getZ() + 1), arg2 -> arg2 != null && arg2.getHorizontalFacing() == arg22);
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (!arg4.abilities.allowModifyWorld) {
            return ActionResult.PASS;
        }
        float f = (arg = (BlockState)arg.method_28493(MODE)).get(MODE) == ComparatorMode.SUBTRACT ? 0.55f : 0.5f;
        arg2.playSound(arg4, arg3, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, f);
        arg2.setBlockState(arg3, arg, 2);
        this.update(arg2, arg3, arg);
        return ActionResult.SUCCESS;
    }

    @Override
    protected void updatePowered(World arg, BlockPos arg2, BlockState arg3) {
        int j;
        if (arg.getBlockTickScheduler().isTicking(arg2, this)) {
            return;
        }
        int i = this.calculateOutputSignal(arg, arg2, arg3);
        BlockEntity lv = arg.getBlockEntity(arg2);
        int n = j = lv instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)lv).getOutputSignal() : 0;
        if (i != j || arg3.get(POWERED).booleanValue() != this.hasPower(arg, arg2, arg3)) {
            TickPriority lv2 = this.isTargetNotAligned(arg, arg2, arg3) ? TickPriority.HIGH : TickPriority.NORMAL;
            arg.getBlockTickScheduler().schedule(arg2, this, 2, lv2);
        }
    }

    private void update(World arg, BlockPos arg2, BlockState arg3) {
        int i = this.calculateOutputSignal(arg, arg2, arg3);
        BlockEntity lv = arg.getBlockEntity(arg2);
        int j = 0;
        if (lv instanceof ComparatorBlockEntity) {
            ComparatorBlockEntity lv2 = (ComparatorBlockEntity)lv;
            j = lv2.getOutputSignal();
            lv2.setOutputSignal(i);
        }
        if (j != i || arg3.get(MODE) == ComparatorMode.COMPARE) {
            boolean bl = this.hasPower(arg, arg2, arg3);
            boolean bl2 = arg3.get(POWERED);
            if (bl2 && !bl) {
                arg.setBlockState(arg2, (BlockState)arg3.with(POWERED, false), 2);
            } else if (!bl2 && bl) {
                arg.setBlockState(arg2, (BlockState)arg3.with(POWERED, true), 2);
            }
            this.updateTarget(arg, arg2, arg3);
        }
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        this.update(arg2, arg3, arg);
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState arg, World arg2, BlockPos arg3, int i, int j) {
        super.onSyncedBlockEvent(arg, arg2, arg3, i, j);
        BlockEntity lv = arg2.getBlockEntity(arg3);
        return lv != null && lv.onSyncedBlockEvent(i, j);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new ComparatorBlockEntity();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, MODE, POWERED);
    }
}

