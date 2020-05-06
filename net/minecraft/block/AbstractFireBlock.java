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
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.dimension.DimensionType;

public abstract class AbstractFireBlock
extends Block {
    private final float damage;
    protected static final VoxelShape field_22497 = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape field_22498 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    protected static final VoxelShape field_22499 = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    protected static final VoxelShape field_22500 = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape field_22501 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    protected static final VoxelShape field_22502 = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);

    public AbstractFireBlock(AbstractBlock.Settings arg, float f) {
        super(arg);
        this.damage = f;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return AbstractFireBlock.getState(arg.getWorld(), arg.getBlockPos());
    }

    public static BlockState getState(BlockView arg, BlockPos arg2) {
        BlockPos lv = arg2.down();
        BlockState lv2 = arg.getBlockState(lv);
        if (SoulFireBlock.isSoulBase(lv2.getBlock())) {
            return Blocks.SOUL_FIRE.getDefaultState();
        }
        return ((FireBlock)Blocks.FIRE).getStateForPosition(arg, arg2);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return field_22498;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        block12: {
            block11: {
                BlockPos lv;
                BlockState lv2;
                if (random.nextInt(24) == 0) {
                    arg2.playSound((float)arg3.getX() + 0.5f, (double)((float)arg3.getY() + 0.5f), (double)((float)arg3.getZ() + 0.5f), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
                }
                if (!this.isFlammable(lv2 = arg2.getBlockState(lv = arg3.down())) && !lv2.isSideSolidFullSquare(arg2, lv, Direction.UP)) break block11;
                for (int i = 0; i < 3; ++i) {
                    double d = (double)arg3.getX() + random.nextDouble();
                    double e = (double)arg3.getY() + random.nextDouble() * 0.5 + 0.5;
                    double f = (double)arg3.getZ() + random.nextDouble();
                    arg2.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
                }
                break block12;
            }
            if (this.isFlammable(arg2.getBlockState(arg3.west()))) {
                for (int j = 0; j < 2; ++j) {
                    double g = (double)arg3.getX() + random.nextDouble() * (double)0.1f;
                    double h = (double)arg3.getY() + random.nextDouble();
                    double k = (double)arg3.getZ() + random.nextDouble();
                    arg2.addParticle(ParticleTypes.LARGE_SMOKE, g, h, k, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(arg2.getBlockState(arg3.east()))) {
                for (int l = 0; l < 2; ++l) {
                    double m = (double)(arg3.getX() + 1) - random.nextDouble() * (double)0.1f;
                    double n = (double)arg3.getY() + random.nextDouble();
                    double o = (double)arg3.getZ() + random.nextDouble();
                    arg2.addParticle(ParticleTypes.LARGE_SMOKE, m, n, o, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(arg2.getBlockState(arg3.north()))) {
                for (int p = 0; p < 2; ++p) {
                    double q = (double)arg3.getX() + random.nextDouble();
                    double r = (double)arg3.getY() + random.nextDouble();
                    double s = (double)arg3.getZ() + random.nextDouble() * (double)0.1f;
                    arg2.addParticle(ParticleTypes.LARGE_SMOKE, q, r, s, 0.0, 0.0, 0.0);
                }
            }
            if (this.isFlammable(arg2.getBlockState(arg3.south()))) {
                for (int t = 0; t < 2; ++t) {
                    double u = (double)arg3.getX() + random.nextDouble();
                    double v = (double)arg3.getY() + random.nextDouble();
                    double w = (double)(arg3.getZ() + 1) - random.nextDouble() * (double)0.1f;
                    arg2.addParticle(ParticleTypes.LARGE_SMOKE, u, v, w, 0.0, 0.0, 0.0);
                }
            }
            if (!this.isFlammable(arg2.getBlockState(arg3.up()))) break block12;
            for (int x = 0; x < 2; ++x) {
                double y = (double)arg3.getX() + random.nextDouble();
                double z = (double)(arg3.getY() + 1) - random.nextDouble() * (double)0.1f;
                double aa = (double)arg3.getZ() + random.nextDouble();
                arg2.addParticle(ParticleTypes.LARGE_SMOKE, y, z, aa, 0.0, 0.0, 0.0);
            }
        }
    }

    protected abstract boolean isFlammable(BlockState var1);

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (!(arg4.isFireImmune() || arg4 instanceof LivingEntity && EnchantmentHelper.hasFrostWalker((LivingEntity)arg4))) {
            arg4.setFireTicks(arg4.getFireTicks() + 1);
            if (arg4.getFireTicks() == 0) {
                arg4.setOnFireFor(8);
            }
            arg4.damage(DamageSource.IN_FIRE, this.damage);
        }
        super.onEntityCollision(arg, arg2, arg3, arg4);
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg4.isOf(arg.getBlock())) {
            return;
        }
        if ((arg2.dimension.getType() == DimensionType.OVERWORLD || arg2.dimension.getType() == DimensionType.THE_NETHER) && NetherPortalBlock.createPortalAt(arg2, arg3)) {
            return;
        }
        if (!arg.canPlaceAt(arg2, arg3)) {
            arg2.removeBlock(arg3, false);
        }
    }

    @Override
    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        if (!arg.isClient()) {
            arg.syncWorldEvent(null, 1009, arg2, 0);
        }
    }
}

