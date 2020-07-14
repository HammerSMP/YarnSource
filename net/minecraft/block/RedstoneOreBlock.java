/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneOreBlock
extends Block {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedstoneOreBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        RedstoneOreBlock.light(state, world, pos);
        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        RedstoneOreBlock.light(world.getBlockState(pos), world, pos);
        super.onSteppedOn(world, pos, entity);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            RedstoneOreBlock.spawnParticles(world, pos);
        } else {
            RedstoneOreBlock.light(state, world, pos);
        }
        ItemStack lv = player.getStackInHand(hand);
        if (lv.getItem() instanceof BlockItem && new ItemPlacementContext(player, hand, lv, hit).canPlace()) {
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    private static void light(BlockState state, World world, BlockPos pos) {
        RedstoneOreBlock.spawnParticles(world, pos);
        if (!state.get(LIT).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with(LIT, true), 3);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(LIT);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT).booleanValue()) {
            world.setBlockState(pos, (BlockState)state.with(LIT, false), 3);
        }
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld arg2, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, arg2, pos, stack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            int i = 1 + arg2.random.nextInt(5);
            this.dropExperience(arg2, pos, i);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT).booleanValue()) {
            RedstoneOreBlock.spawnParticles(world, pos);
        }
    }

    private static void spawnParticles(World world, BlockPos pos) {
        double d = 0.5625;
        Random random = world.random;
        for (Direction lv : Direction.values()) {
            BlockPos lv2 = pos.offset(lv);
            if (world.getBlockState(lv2).isOpaqueFullCube(world, lv2)) continue;
            Direction.Axis lv3 = lv.getAxis();
            double e = lv3 == Direction.Axis.X ? 0.5 + 0.5625 * (double)lv.getOffsetX() : (double)random.nextFloat();
            double f = lv3 == Direction.Axis.Y ? 0.5 + 0.5625 * (double)lv.getOffsetY() : (double)random.nextFloat();
            double g = lv3 == Direction.Axis.Z ? 0.5 + 0.5625 * (double)lv.getOffsetZ() : (double)random.nextFloat();
            world.addParticle(DustParticleEffect.RED, (double)pos.getX() + e, (double)pos.getY() + f, (double)pos.getZ() + g, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}

