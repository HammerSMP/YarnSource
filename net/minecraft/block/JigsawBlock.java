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
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class JigsawBlock
extends Block
implements BlockEntityProvider {
    public static final EnumProperty<JigsawOrientation> ORIENTATION = Properties.ORIENTATION;

    protected JigsawBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(ORIENTATION, JigsawOrientation.NORTH_UP));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(ORIENTATION);
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(ORIENTATION, arg2.getDirectionTransformation().mapJigsawOrientation(arg.get(ORIENTATION)));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return (BlockState)arg.with(ORIENTATION, arg2.getDirectionTransformation().mapJigsawOrientation(arg.get(ORIENTATION)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        Direction lv3;
        Direction lv = arg.getSide();
        if (lv.getAxis() == Direction.Axis.Y) {
            Direction lv2 = arg.getPlayerFacing().getOpposite();
        } else {
            lv3 = Direction.UP;
        }
        return (BlockState)this.getDefaultState().with(ORIENTATION, JigsawOrientation.byDirections(lv, lv3));
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView arg) {
        return new JigsawBlockEntity();
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof JigsawBlockEntity && arg4.isCreativeLevelTwoOp()) {
            arg4.openJigsawScreen((JigsawBlockEntity)lv);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static boolean attachmentMatches(Structure.StructureBlockInfo arg, Structure.StructureBlockInfo arg2) {
        Direction lv = JigsawBlock.getFacing(arg.state);
        Direction lv2 = JigsawBlock.getFacing(arg2.state);
        Direction lv3 = JigsawBlock.getRotation(arg.state);
        Direction lv4 = JigsawBlock.getRotation(arg2.state);
        JigsawBlockEntity.Joint lv5 = JigsawBlockEntity.Joint.byName(arg.tag.getString("joint")).orElseGet(() -> lv.getAxis().isHorizontal() ? JigsawBlockEntity.Joint.ALIGNED : JigsawBlockEntity.Joint.ROLLABLE);
        boolean bl = lv5 == JigsawBlockEntity.Joint.ROLLABLE;
        return lv == lv2.getOpposite() && (bl || lv3 == lv4) && arg.tag.getString("target").equals(arg2.tag.getString("name"));
    }

    public static Direction getFacing(BlockState arg) {
        return arg.get(ORIENTATION).getFacing();
    }

    public static Direction getRotation(BlockState arg) {
        return arg.get(ORIENTATION).getRotation();
    }
}

