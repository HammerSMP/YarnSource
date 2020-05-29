/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class JukeboxBlock
extends BlockWithEntity {
    public static final BooleanProperty HAS_RECORD = Properties.HAS_RECORD;

    protected JukeboxBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HAS_RECORD, false));
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg.get(HAS_RECORD).booleanValue()) {
            this.removeRecord(arg2, arg3);
            arg = (BlockState)arg.with(HAS_RECORD, false);
            arg2.setBlockState(arg3, arg, 2);
            return ActionResult.method_29236(arg2.isClient);
        }
        return ActionResult.PASS;
    }

    public void setRecord(WorldAccess arg, BlockPos arg2, BlockState arg3, ItemStack arg4) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (!(lv instanceof JukeboxBlockEntity)) {
            return;
        }
        ((JukeboxBlockEntity)lv).setRecord(arg4.copy());
        arg.setBlockState(arg2, (BlockState)arg3.with(HAS_RECORD, true), 2);
    }

    private void removeRecord(World arg, BlockPos arg2) {
        if (arg.isClient) {
            return;
        }
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (!(lv instanceof JukeboxBlockEntity)) {
            return;
        }
        JukeboxBlockEntity lv2 = (JukeboxBlockEntity)lv;
        ItemStack lv3 = lv2.getRecord();
        if (lv3.isEmpty()) {
            return;
        }
        arg.syncWorldEvent(1010, arg2, 0);
        lv2.clear();
        float f = 0.7f;
        double d = (double)(arg.random.nextFloat() * 0.7f) + (double)0.15f;
        double e = (double)(arg.random.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
        double g = (double)(arg.random.nextFloat() * 0.7f) + (double)0.15f;
        ItemStack lv4 = lv3.copy();
        ItemEntity lv5 = new ItemEntity(arg, (double)arg2.getX() + d, (double)arg2.getY() + e, (double)arg2.getZ() + g, lv4);
        lv5.setToDefaultPickupDelay();
        arg.spawnEntity(lv5);
    }

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        this.removeRecord(arg2, arg3);
        super.onStateReplaced(arg, arg2, arg3, arg4, bl);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new JukeboxBlockEntity();
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        Item lv2;
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof JukeboxBlockEntity && (lv2 = ((JukeboxBlockEntity)lv).getRecord().getItem()) instanceof MusicDiscItem) {
            return ((MusicDiscItem)lv2).getComparatorOutput();
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(HAS_RECORD);
    }
}

