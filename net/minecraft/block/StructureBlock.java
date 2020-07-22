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
import net.minecraft.server.world.ServerWorld;
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
    public BlockEntity createBlockEntity(BlockView world) {
        return new StructureBlockBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof StructureBlockBlockEntity) {
            return ((StructureBlockBlockEntity)lv).openScreen(player) ? ActionResult.success(world.isClient) : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity lv;
        if (world.isClient) {
            return;
        }
        if (placer != null && (lv = world.getBlockEntity(pos)) instanceof StructureBlockBlockEntity) {
            ((StructureBlockBlockEntity)lv).setAuthor(placer);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(MODE, StructureBlockMode.DATA);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MODE);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!(world instanceof ServerWorld)) {
            return;
        }
        BlockEntity lv = world.getBlockEntity(pos);
        if (!(lv instanceof StructureBlockBlockEntity)) {
            return;
        }
        StructureBlockBlockEntity lv2 = (StructureBlockBlockEntity)lv;
        boolean bl2 = world.isReceivingRedstonePower(pos);
        boolean bl3 = lv2.isPowered();
        if (bl2 && !bl3) {
            lv2.setPowered(true);
            this.doAction((ServerWorld)world, lv2);
        } else if (!bl2 && bl3) {
            lv2.setPowered(false);
        }
    }

    private void doAction(ServerWorld arg, StructureBlockBlockEntity arg2) {
        switch (arg2.getMode()) {
            case SAVE: {
                arg2.saveStructure(false);
                break;
            }
            case LOAD: {
                arg2.loadStructure(arg, false);
                break;
            }
            case CORNER: {
                arg2.unloadStructure();
                break;
            }
            case DATA: {
                break;
            }
        }
    }
}

