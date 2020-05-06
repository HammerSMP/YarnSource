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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherRoseBlock
extends FlowerBlock {
    public WitherRoseBlock(StatusEffect arg, AbstractBlock.Settings arg2) {
        super(arg, 8, arg2);
    }

    @Override
    protected boolean canPlantOnTop(BlockState arg, BlockView arg2, BlockPos arg3) {
        return super.canPlantOnTop(arg, arg2, arg3) || arg.isOf(Blocks.NETHERRACK) || arg.isOf(Blocks.SOUL_SAND) || arg.isOf(Blocks.SOUL_SOIL);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        VoxelShape lv = this.getOutlineShape(arg, arg2, arg3, ShapeContext.absent());
        Vec3d lv2 = lv.getBoundingBox().getCenter();
        double d = (double)arg3.getX() + lv2.x;
        double e = (double)arg3.getZ() + lv2.z;
        for (int i = 0; i < 3; ++i) {
            if (!random.nextBoolean()) continue;
            arg2.addParticle(ParticleTypes.SMOKE, d + (double)(random.nextFloat() / 5.0f), (double)arg3.getY() + (0.5 - (double)random.nextFloat()), e + (double)(random.nextFloat() / 5.0f), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        LivingEntity lv;
        if (arg2.isClient || arg2.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (arg4 instanceof LivingEntity && !(lv = (LivingEntity)arg4).isInvulnerableTo(DamageSource.WITHER)) {
            lv.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 40));
        }
    }
}

