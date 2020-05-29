/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class NoteBlock
extends Block {
    public static final EnumProperty<Instrument> INSTRUMENT = Properties.INSTRUMENT;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final IntProperty NOTE = Properties.NOTE;

    public NoteBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(INSTRUMENT, Instrument.HARP)).with(NOTE, 0)).with(POWERED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(INSTRUMENT, Instrument.fromBlockState(arg.getWorld().getBlockState(arg.getBlockPos().down())));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == Direction.DOWN) {
            return (BlockState)arg.with(INSTRUMENT, Instrument.fromBlockState(arg3));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        boolean bl2 = arg2.isReceivingRedstonePower(arg3);
        if (bl2 != arg.get(POWERED)) {
            if (bl2) {
                this.playNote(arg2, arg3);
            }
            arg2.setBlockState(arg3, (BlockState)arg.with(POWERED, bl2), 3);
        }
    }

    private void playNote(World arg, BlockPos arg2) {
        if (arg.getBlockState(arg2.up()).isAir()) {
            arg.addSyncedBlockEvent(arg2, this, 0, 0);
        }
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg2.isClient) {
            return ActionResult.SUCCESS;
        }
        arg = (BlockState)arg.cycle(NOTE);
        arg2.setBlockState(arg3, arg, 3);
        this.playNote(arg2, arg3);
        arg4.incrementStat(Stats.TUNE_NOTEBLOCK);
        return ActionResult.CONSUME;
    }

    @Override
    public void onBlockBreakStart(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4) {
        if (arg2.isClient) {
            return;
        }
        this.playNote(arg2, arg3);
        arg4.incrementStat(Stats.PLAY_NOTEBLOCK);
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState arg, World arg2, BlockPos arg3, int i, int j) {
        int k = arg.get(NOTE);
        float f = (float)Math.pow(2.0, (double)(k - 12) / 12.0);
        arg2.playSound(null, arg3, arg.get(INSTRUMENT).getSound(), SoundCategory.RECORDS, 3.0f, f);
        arg2.addParticle(ParticleTypes.NOTE, (double)arg3.getX() + 0.5, (double)arg3.getY() + 1.2, (double)arg3.getZ() + 0.5, (double)k / 24.0, 0.0, 0.0);
        return true;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(INSTRUMENT, POWERED, NOTE);
    }
}

