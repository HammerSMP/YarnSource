/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlock
extends BlockWithEntity {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final BooleanProperty CONDITIONAL = Properties.CONDITIONAL;

    public CommandBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(CONDITIONAL, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        CommandBlockBlockEntity lv = new CommandBlockBlockEntity();
        lv.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
        return lv;
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg2.isClient) {
            return;
        }
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (!(lv instanceof CommandBlockBlockEntity)) {
            return;
        }
        CommandBlockBlockEntity lv2 = (CommandBlockBlockEntity)lv;
        boolean bl2 = arg2.isReceivingRedstonePower(arg3);
        boolean bl3 = lv2.isPowered();
        lv2.setPowered(bl2);
        if (bl3 || lv2.isAuto() || lv2.getCommandBlockType() == CommandBlockBlockEntity.Type.SEQUENCE) {
            return;
        }
        if (bl2) {
            lv2.updateConditionMet();
            arg2.getBlockTickScheduler().schedule(arg3, this, 1);
        }
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof CommandBlockBlockEntity) {
            CommandBlockBlockEntity lv2 = (CommandBlockBlockEntity)lv;
            CommandBlockExecutor lv3 = lv2.getCommandExecutor();
            boolean bl = !ChatUtil.isEmpty(lv3.getCommand());
            CommandBlockBlockEntity.Type lv4 = lv2.getCommandBlockType();
            boolean bl2 = lv2.isConditionMet();
            if (lv4 == CommandBlockBlockEntity.Type.AUTO) {
                lv2.updateConditionMet();
                if (bl2) {
                    this.execute(arg, arg2, arg3, lv3, bl);
                } else if (lv2.isConditionalCommandBlock()) {
                    lv3.setSuccessCount(0);
                }
                if (lv2.isPowered() || lv2.isAuto()) {
                    arg2.getBlockTickScheduler().schedule(arg3, this, 1);
                }
            } else if (lv4 == CommandBlockBlockEntity.Type.REDSTONE) {
                if (bl2) {
                    this.execute(arg, arg2, arg3, lv3, bl);
                } else if (lv2.isConditionalCommandBlock()) {
                    lv3.setSuccessCount(0);
                }
            }
            arg2.updateComparators(arg3, this);
        }
    }

    private void execute(BlockState arg, World arg2, BlockPos arg3, CommandBlockExecutor arg4, boolean bl) {
        if (bl) {
            arg4.execute(arg2);
        } else {
            arg4.setSuccessCount(0);
        }
        CommandBlock.executeCommandChain(arg2, arg3, arg.get(FACING));
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof CommandBlockBlockEntity && arg4.isCreativeLevelTwoOp()) {
            arg4.openCommandBlockScreen((CommandBlockBlockEntity)lv);
            return ActionResult.method_29236(arg2.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof CommandBlockBlockEntity) {
            return ((CommandBlockBlockEntity)lv).getCommandExecutor().getSuccessCount();
        }
        return 0;
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, LivingEntity arg4, ItemStack arg5) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (!(lv instanceof CommandBlockBlockEntity)) {
            return;
        }
        CommandBlockBlockEntity lv2 = (CommandBlockBlockEntity)lv;
        CommandBlockExecutor lv3 = lv2.getCommandExecutor();
        if (arg5.hasCustomName()) {
            lv3.setCustomName(arg5.getName());
        }
        if (!arg.isClient) {
            if (arg5.getSubTag("BlockEntityTag") == null) {
                lv3.shouldTrackOutput(arg.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK));
                lv2.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
            }
            if (lv2.getCommandBlockType() == CommandBlockBlockEntity.Type.SEQUENCE) {
                boolean bl = arg.isReceivingRedstonePower(arg2);
                lv2.setPowered(bl);
            }
        }
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
        arg.add(FACING, CONDITIONAL);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(FACING, arg.getPlayerLookDirection().getOpposite());
    }

    private static void executeCommandChain(World arg, BlockPos arg2, Direction arg3) {
        BlockPos.Mutable lv = arg2.mutableCopy();
        GameRules lv2 = arg.getGameRules();
        int i = lv2.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
        while (i-- > 0) {
            CommandBlockBlockEntity lv6;
            BlockEntity lv5;
            lv.move(arg3);
            BlockState lv3 = arg.getBlockState(lv);
            Block lv4 = lv3.getBlock();
            if (!lv3.isOf(Blocks.CHAIN_COMMAND_BLOCK) || !((lv5 = arg.getBlockEntity(lv)) instanceof CommandBlockBlockEntity) || (lv6 = (CommandBlockBlockEntity)lv5).getCommandBlockType() != CommandBlockBlockEntity.Type.SEQUENCE) break;
            if (lv6.isPowered() || lv6.isAuto()) {
                CommandBlockExecutor lv7 = lv6.getCommandExecutor();
                if (lv6.updateConditionMet()) {
                    if (!lv7.execute(arg)) break;
                    arg.updateComparators(lv, lv4);
                } else if (lv6.isConditionalCommandBlock()) {
                    lv7.setSuccessCount(0);
                }
            }
            arg3 = lv3.get(FACING);
        }
        if (i <= 0) {
            int j = Math.max(lv2.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH), 0);
            LOGGER.warn("Command Block chain tried to execute more than {} steps!", (Object)j);
        }
    }
}

