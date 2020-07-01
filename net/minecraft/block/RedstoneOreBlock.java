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
    public void onBlockBreakStart(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4) {
        RedstoneOreBlock.light(arg, arg2, arg3);
        super.onBlockBreakStart(arg, arg2, arg3, arg4);
    }

    @Override
    public void onSteppedOn(World arg, BlockPos arg2, Entity arg3) {
        RedstoneOreBlock.light(arg.getBlockState(arg2), arg, arg2);
        super.onSteppedOn(arg, arg2, arg3);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg2.isClient) {
            RedstoneOreBlock.spawnParticles(arg2, arg3);
        } else {
            RedstoneOreBlock.light(arg, arg2, arg3);
        }
        ItemStack lv = arg4.getStackInHand(arg5);
        if (lv.getItem() instanceof BlockItem && new ItemPlacementContext(arg4, arg5, lv, arg6).canPlace()) {
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    private static void light(BlockState arg, World arg2, BlockPos arg3) {
        RedstoneOreBlock.spawnParticles(arg2, arg3);
        if (!arg.get(LIT).booleanValue()) {
            arg2.setBlockState(arg3, (BlockState)arg.with(LIT, true), 3);
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState arg) {
        return arg.get(LIT);
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg.get(LIT).booleanValue()) {
            arg2.setBlockState(arg3, (BlockState)arg.with(LIT, false), 3);
        }
    }

    @Override
    public void onStacksDropped(BlockState arg, ServerWorld arg2, BlockPos arg3, ItemStack arg4) {
        super.onStacksDropped(arg, arg2, arg3, arg4);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, arg4) == 0) {
            int i = 1 + arg2.random.nextInt(5);
            this.dropExperience(arg2, arg3, i);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (arg.get(LIT).booleanValue()) {
            RedstoneOreBlock.spawnParticles(arg2, arg3);
        }
    }

    private static void spawnParticles(World arg, BlockPos arg2) {
        double d = 0.5625;
        Random random = arg.random;
        for (Direction lv : Direction.values()) {
            BlockPos lv2 = arg2.offset(lv);
            if (arg.getBlockState(lv2).isOpaqueFullCube(arg, lv2)) continue;
            Direction.Axis lv3 = lv.getAxis();
            double e = lv3 == Direction.Axis.X ? 0.5 + 0.5625 * (double)lv.getOffsetX() : (double)random.nextFloat();
            double f = lv3 == Direction.Axis.Y ? 0.5 + 0.5625 * (double)lv.getOffsetY() : (double)random.nextFloat();
            double g = lv3 == Direction.Axis.Z ? 0.5 + 0.5625 * (double)lv.getOffsetZ() : (double)random.nextFloat();
            arg.addParticle(DustParticleEffect.RED, (double)arg2.getX() + e, (double)arg2.getY() + f, (double)arg2.getZ() + g, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(LIT);
    }
}

