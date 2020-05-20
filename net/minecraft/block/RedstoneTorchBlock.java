/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RedstoneTorchBlock
extends TorchBlock {
    public static final BooleanProperty LIT = Properties.LIT;
    private static final Map<BlockView, List<BurnoutEntry>> BURNOUT_MAP = new WeakHashMap<BlockView, List<BurnoutEntry>>();

    protected RedstoneTorchBlock(AbstractBlock.Settings arg) {
        super(arg, DustParticleEffect.RED);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LIT, true));
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        for (Direction lv : Direction.values()) {
            arg2.updateNeighborsAlways(arg3.offset(lv), this);
        }
    }

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (bl) {
            return;
        }
        for (Direction lv : Direction.values()) {
            arg2.updateNeighborsAlways(arg3.offset(lv), this);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (arg.get(LIT).booleanValue() && Direction.UP != arg4) {
            return 15;
        }
        return 0;
    }

    protected boolean shouldUnpower(World arg, BlockPos arg2, BlockState arg3) {
        return arg.isEmittingRedstonePower(arg2.down(), Direction.DOWN);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        boolean bl = this.shouldUnpower(arg2, arg3, arg);
        List<BurnoutEntry> list = BURNOUT_MAP.get(arg2);
        while (list != null && !list.isEmpty() && arg2.getTime() - list.get(0).time > 60L) {
            list.remove(0);
        }
        if (arg.get(LIT).booleanValue()) {
            if (bl) {
                arg2.setBlockState(arg3, (BlockState)arg.with(LIT, false), 3);
                if (RedstoneTorchBlock.isBurnedOut(arg2, arg3, true)) {
                    arg2.syncWorldEvent(1502, arg3, 0);
                    arg2.getBlockTickScheduler().schedule(arg3, arg2.getBlockState(arg3).getBlock(), 160);
                }
            }
        } else if (!bl && !RedstoneTorchBlock.isBurnedOut(arg2, arg3, false)) {
            arg2.setBlockState(arg3, (BlockState)arg.with(LIT, true), 3);
        }
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg.get(LIT).booleanValue() == this.shouldUnpower(arg2, arg3, arg) && !arg2.getBlockTickScheduler().isTicking(arg3, this)) {
            arg2.getBlockTickScheduler().schedule(arg3, this, 2);
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (arg4 == Direction.DOWN) {
            return arg.getWeakRedstonePower(arg2, arg3, arg4);
        }
        return 0;
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (!arg.get(LIT).booleanValue()) {
            return;
        }
        double d = (double)arg3.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double e = (double)arg3.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
        double f = (double)arg3.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        arg2.addParticle(this.particle, d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(LIT);
    }

    private static boolean isBurnedOut(World arg2, BlockPos arg22, boolean bl) {
        List list = BURNOUT_MAP.computeIfAbsent(arg2, arg -> Lists.newArrayList());
        if (bl) {
            list.add(new BurnoutEntry(arg22.toImmutable(), arg2.getTime()));
        }
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            BurnoutEntry lv = (BurnoutEntry)list.get(j);
            if (!lv.pos.equals(arg22) || ++i < 8) continue;
            return true;
        }
        return false;
    }

    public static class BurnoutEntry {
        private final BlockPos pos;
        private final long time;

        public BurnoutEntry(BlockPos arg, long l) {
            this.pos = arg;
            this.time = l;
        }
    }
}

