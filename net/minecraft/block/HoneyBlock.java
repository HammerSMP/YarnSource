/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HoneyBlock
extends TransparentBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public HoneyBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    private static boolean hasHoneyBlockEffects(Entity arg) {
        return arg instanceof LivingEntity || arg instanceof AbstractMinecartEntity || arg instanceof TntEntity || arg instanceof BoatEntity;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public void onLandedUpon(World arg, BlockPos arg2, Entity arg3, float f) {
        arg3.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0f, 1.0f);
        if (!arg.isClient) {
            arg.sendEntityStatus(arg3, (byte)54);
        }
        if (arg3.handleFallDamage(f, 0.2f)) {
            arg3.playSound(this.soundGroup.getFallSound(), this.soundGroup.getVolume() * 0.5f, this.soundGroup.getPitch() * 0.75f);
        }
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (this.isSliding(arg3, arg4)) {
            this.triggerAdvancement(arg4, arg3);
            this.updateSlidingVelocity(arg4);
            this.addCollisionEffects(arg2, arg4);
        }
        super.onEntityCollision(arg, arg2, arg3, arg4);
    }

    private boolean isSliding(BlockPos arg, Entity arg2) {
        if (arg2.isOnGround()) {
            return false;
        }
        if (arg2.getY() > (double)arg.getY() + 0.9375 - 1.0E-7) {
            return false;
        }
        if (arg2.getVelocity().y >= -0.08) {
            return false;
        }
        double d = Math.abs((double)arg.getX() + 0.5 - arg2.getX());
        double e = Math.abs((double)arg.getZ() + 0.5 - arg2.getZ());
        double f = 0.4375 + (double)(arg2.getWidth() / 2.0f);
        return d + 1.0E-7 > f || e + 1.0E-7 > f;
    }

    private void triggerAdvancement(Entity arg, BlockPos arg2) {
        if (arg instanceof ServerPlayerEntity && arg.world.getTime() % 20L == 0L) {
            Criteria.SLIDE_DOWN_BLOCK.test((ServerPlayerEntity)arg, arg.world.getBlockState(arg2));
        }
    }

    private void updateSlidingVelocity(Entity arg) {
        Vec3d lv = arg.getVelocity();
        if (lv.y < -0.13) {
            double d = -0.05 / lv.y;
            arg.setVelocity(new Vec3d(lv.x * d, -0.05, lv.z * d));
        } else {
            arg.setVelocity(new Vec3d(lv.x, -0.05, lv.z));
        }
        arg.fallDistance = 0.0f;
    }

    private void addCollisionEffects(World arg, Entity arg2) {
        if (HoneyBlock.hasHoneyBlockEffects(arg2)) {
            if (arg.random.nextInt(5) == 0) {
                arg2.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0f, 1.0f);
            }
            if (!arg.isClient && arg.random.nextInt(5) == 0) {
                arg.sendEntityStatus(arg2, (byte)53);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static void addRegularParticles(Entity arg) {
        HoneyBlock.addParticles(arg, 5);
    }

    @Environment(value=EnvType.CLIENT)
    public static void addRichParticles(Entity arg) {
        HoneyBlock.addParticles(arg, 10);
    }

    @Environment(value=EnvType.CLIENT)
    private static void addParticles(Entity arg, int i) {
        if (!arg.world.isClient) {
            return;
        }
        BlockState lv = Blocks.HONEY_BLOCK.getDefaultState();
        for (int j = 0; j < i; ++j) {
            arg.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, lv), arg.getX(), arg.getY(), arg.getZ(), 0.0, 0.0, 0.0);
        }
    }
}

