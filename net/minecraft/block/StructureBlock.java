/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StructureBlock
extends BlockWithEntity {
    public static final EnumProperty<StructureBlockMode> MODE = Properties.STRUCTURE_BLOCK_MODE;

    protected StructureBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new StructureBlockBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof StructureBlockBlockEntity) {
            return ((StructureBlockBlockEntity)lv).openScreen(arg4) ? ActionResult.success(arg2.isClient) : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, @Nullable LivingEntity arg4, ItemStack arg5) {
        BlockEntity lv;
        if (arg.isClient) {
            return;
        }
        if (arg4 != null && (lv = arg.getBlockEntity(arg2)) instanceof StructureBlockBlockEntity) {
            ((StructureBlockBlockEntity)lv).setAuthor(arg4);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(MODE, StructureBlockMode.DATA);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(MODE);
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg2.isClient) {
            return;
        }
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (!(lv instanceof StructureBlockBlockEntity)) {
            return;
        }
        StructureBlockBlockEntity lv2 = (StructureBlockBlockEntity)lv;
        boolean bl2 = arg2.isReceivingRedstonePower(arg3);
        boolean bl3 = lv2.isPowered();
        if (bl2 && !bl3) {
            lv2.setPowered(true);
            this.doAction(lv2);
        } else if (!bl2 && bl3) {
            lv2.setPowered(false);
        }
    }

    private void doAction(StructureBlockBlockEntity arg) {
        switch (arg.getMode()) {
            case SAVE: {
                arg.saveStructure(false);
                break;
            }
            case LOAD: {
                arg.loadStructure(false);
                break;
            }
            case CORNER: {
                arg.unloadStructure();
                break;
            }
            case DATA: {
                break;
            }
        }
    }
}

