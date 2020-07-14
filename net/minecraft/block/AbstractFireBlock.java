/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.AreaHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractFireBlock
extends Block {
    private final float damage;
    protected static final VoxelShape field_22497 = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape field_22498 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    protected static final VoxelShape field_22499 = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    protected static final VoxelShape field_22500 = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape field_22501 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    protected static final VoxelShape field_22502 = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    public AbstractFireBlock(AbstractBlock.Settings settings, float damage) {
        super(settings);
        this.damage = damage;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return AbstractFireBlock.getState(ctx.getWorld(), ctx.getBlockPos());
    }

    public static BlockState getState(BlockView world, BlockPos pos) {
        BlockPos lv = pos.down();
        BlockState lv2 = world.getBlockState(lv);
        if (SoulFireBlock.isSoulBase(lv2.getBlock())) {
            return Blocks.SOUL_FIRE.getDefaultState();
        }
        return ((FireBlock)Blocks.FIRE).getStateForPosition(world, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return field_22498;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        block12: {
            block11: {
                BlockPos lv;
                BlockState lv2;
                if (random.nextInt(24) == 0) {
                    world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
                }
                if (!this.isFlammable(lv2 = world.getBlockState(lv = pos.down())) && !lv2.isSideSolidFullSquare(world, lv, Direction.UP)) break block11;
                for (int i = 0; i < 3; ++i) {
                    double d = (double)pos.getX() + random.nextDouble();
                    double e = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
                    double f = (double)pos.getZ() + random.nextDouble();
                    world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
                break block12;
            }
            if (this.isFlammable(world.getBlockState(pos.west()))) {
                for (int j = 0; j < 2; ++j) {
                    double g = (double)pos.getX() + random.nextDouble() * (double)0.1f;
                    double h = (double)pos.getY() + random.nextDouble();
                    double k = (double)pos.getZ() + random.nextDouble();
                    world.addParticle(ParticleTypes.LARGE_SMOKE, g, h, k, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.east()))) {
                for (int l = 0; l < 2; ++l) {
                    double m = (double)(pos.getX() + 1) - random.nextDouble() * (double)0.1f;
                    double n = (double)pos.getY() + random.nextDouble();
                    double o = (double)pos.getZ() + random.nextDouble();
                    world.addParticle(ParticleTypes.LARGE_SMOKE, m, n, o, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.north()))) {
                for (int p = 0; p < 2; ++p) {
                    double q = (double)pos.getX() + random.nextDouble();
                    double r = (double)pos.getY() + random.nextDouble();
                    double s = (double)pos.getZ() + random.nextDouble() * (double)0.1f;
                    world.addParticle(ParticleTypes.LARGE_SMOKE, q, r, s, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(world.getBlockState(pos.south()))) {
                for (int t = 0; t < 2; ++t) {
                    double u = (double)pos.getX() + random.nextDouble();
                    double v = (double)pos.getY() + random.nextDouble();
                    double w = (double)(pos.getZ() + 1) - random.nextDouble() * (double)0.1f;
                    world.addParticle(ParticleTypes.LARGE_SMOKE, u, v, w, 0.0, 0.0, 0.0);
                }
            }
            if (!this.isFlammable(world.getBlockState(pos.up()))) break block12;
            for (int x = 0; x < 2; ++x) {
                double y = (double)pos.getX() + random.nextDouble();
                double z = (double)(pos.getY() + 1) - random.nextDouble() * (double)0.1f;
                double aa = (double)pos.getZ() + random.nextDouble();
                world.addParticle(ParticleTypes.LARGE_SMOKE, y, z, aa, 0.0, 0.0, 0.0);
            }
        }
    }

    protected abstract boolean isFlammable(BlockState var1);

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.isFireImmune()) {
            entity.setFireTicks(entity.getFireTicks() + 1);
            if (entity.getFireTicks() == 0) {
                entity.setOnFireFor(8);
            }
            entity.damage(DamageSource.IN_FIRE, this.damage);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        Optional<AreaHelper> optional;
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        if (AbstractFireBlock.method_30366(world) && (optional = AreaHelper.method_30485(world, pos, Direction.Axis.X)).isPresent()) {
            optional.get().createPortal();
            return;
        }
        if (!state.canPlaceAt(world, pos)) {
            world.removeBlock(pos, false);
        }
    }

    private static boolean method_30366(World arg) {
        return arg.getRegistryKey() == World.OVERWORLD || arg.getRegistryKey() == World.NETHER;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            world.syncWorldEvent(null, 1009, pos, 0);
        }
    }

    public static boolean method_30032(World arg, BlockPos arg2, Direction arg3) {
        BlockState lv = arg.getBlockState(arg2);
        if (!lv.isAir()) {
            return false;
        }
        return AbstractFireBlock.getState(arg, arg2).canPlaceAt(arg, arg2) || AbstractFireBlock.method_30033(arg, arg2, arg3);
    }

    private static boolean method_30033(World arg, BlockPos arg2, Direction arg3) {
        if (!AbstractFireBlock.method_30366(arg)) {
            return false;
        }
        BlockPos.Mutable lv = arg2.mutableCopy();
        boolean bl = false;
        for (Direction lv2 : Direction.values()) {
            if (!arg.getBlockState(lv.set(arg2).move(lv2)).isOf(Blocks.OBSIDIAN)) continue;
            bl = true;
            break;
        }
        return bl && AreaHelper.method_30485(arg, arg2, arg3.rotateYCounterclockwise().getAxis()).isPresent();
    }
}

