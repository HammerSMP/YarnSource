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
    public BlockEntity createBlockEntity(BlockView world) {
        CommandBlockBlockEntity lv = new CommandBlockBlockEntity();
        lv.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
        return lv;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (world.isClient) {
            return;
        }
        BlockEntity lv = world.getBlockEntity(pos);
        if (!(lv instanceof CommandBlockBlockEntity)) {
            return;
        }
        CommandBlockBlockEntity lv2 = (CommandBlockBlockEntity)lv;
        boolean bl2 = world.isReceivingRedstonePower(pos);
        boolean bl3 = lv2.isPowered();
        lv2.setPowered(bl2);
        if (bl3 || lv2.isAuto() || lv2.getCommandBlockType() == CommandBlockBlockEntity.Type.SEQUENCE) {
            return;
        }
        if (bl2) {
            lv2.updateConditionMet();
            world.getBlockTickScheduler().schedule(pos, this, 1);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof CommandBlockBlockEntity) {
            CommandBlockBlockEntity lv2 = (CommandBlockBlockEntity)lv;
            CommandBlockExecutor lv3 = lv2.getCommandExecutor();
            boolean bl = !ChatUtil.isEmpty(lv3.getCommand());
            CommandBlockBlockEntity.Type lv4 = lv2.getCommandBlockType();
            boolean bl2 = lv2.isConditionMet();
            if (lv4 == CommandBlockBlockEntity.Type.AUTO) {
                lv2.updateConditionMet();
                if (bl2) {
                    this.execute(state, world, pos, lv3, bl);
                } else if (lv2.isConditionalCommandBlock()) {
                    lv3.setSuccessCount(0);
                }
                if (lv2.isPowered() || lv2.isAuto()) {
                    world.getBlockTickScheduler().schedule(pos, this, 1);
                }
            } else if (lv4 == CommandBlockBlockEntity.Type.REDSTONE) {
                if (bl2) {
                    this.execute(state, world, pos, lv3, bl);
                } else if (lv2.isConditionalCommandBlock()) {
                    lv3.setSuccessCount(0);
                }
            }
            world.updateComparators(pos, this);
        }
    }

    private void execute(BlockState state, World world, BlockPos pos, CommandBlockExecutor executor, boolean hasCommand) {
        if (hasCommand) {
            executor.execute(world);
        } else {
            executor.setSuccessCount(0);
        }
        CommandBlock.executeCommandChain(world, pos, state.get(FACING));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof CommandBlockBlockEntity && player.isCreativeLevelTwoOp()) {
            player.openCommandBlockScreen((CommandBlockBlockEntity)lv);
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (lv instanceof CommandBlockBlockEntity) {
            return ((CommandBlockBlockEntity)lv).getCommandExecutor().getSuccessCount();
        }
        return 0;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity lv = world.getBlockEntity(pos);
        if (!(lv instanceof CommandBlockBlockEntity)) {
            return;
        }
        CommandBlockBlockEntity lv2 = (CommandBlockBlockEntity)lv;
        CommandBlockExecutor lv3 = lv2.getCommandExecutor();
        if (itemStack.hasCustomName()) {
            lv3.setCustomName(itemStack.getName());
        }
        if (!world.isClient) {
            if (itemStack.getSubTag("BlockEntityTag") == null) {
                lv3.shouldTrackOutput(world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK));
                lv2.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
            }
            if (lv2.getCommandBlockType() == CommandBlockBlockEntity.Type.SEQUENCE) {
                boolean bl = world.isReceivingRedstonePower(pos);
                lv2.setPowered(bl);
            }
        }
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
        builder.add(FACING, CONDITIONAL);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    private static void executeCommandChain(World world, BlockPos pos, Direction facing) {
        BlockPos.Mutable lv = pos.mutableCopy();
        GameRules lv2 = world.getGameRules();
        int i = lv2.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
        while (i-- > 0) {
            CommandBlockBlockEntity lv6;
            BlockEntity lv5;
            lv.move(facing);
            BlockState lv3 = world.getBlockState(lv);
            Block lv4 = lv3.getBlock();
            if (!lv3.isOf(Blocks.CHAIN_COMMAND_BLOCK) || !((lv5 = world.getBlockEntity(lv)) instanceof CommandBlockBlockEntity) || (lv6 = (CommandBlockBlockEntity)lv5).getCommandBlockType() != CommandBlockBlockEntity.Type.SEQUENCE) break;
            if (lv6.isPowered() || lv6.isAuto()) {
                CommandBlockExecutor lv7 = lv6.getCommandExecutor();
                if (lv6.updateConditionMet()) {
                    if (!lv7.execute(world)) break;
                    world.updateComparators(lv, lv4);
                } else if (lv6.isConditionalCommandBlock()) {
                    lv7.setSuccessCount(0);
                }
            }
            facing = lv3.get(FACING);
        }
        if (i <= 0) {
            int j = Math.max(lv2.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH), 0);
            LOGGER.warn("Command Block chain tried to execute more than {} steps!", (Object)j);
        }
    }
}

