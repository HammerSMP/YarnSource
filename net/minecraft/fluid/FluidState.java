/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public final class FluidState
extends State<Fluid, FluidState> {
    public static final Codec<FluidState> field_25018 = FluidState.method_28494(Registry.FLUID, Fluid::getDefaultState).stable();

    public FluidState(Fluid arg, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<FluidState> mapCodec) {
        super(arg, immutableMap, mapCodec);
    }

    public Fluid getFluid() {
        return (Fluid)this.field_24739;
    }

    public boolean isStill() {
        return this.getFluid().isStill(this);
    }

    public boolean isEmpty() {
        return this.getFluid().isEmpty();
    }

    public float getHeight(BlockView arg, BlockPos arg2) {
        return this.getFluid().getHeight(this, arg, arg2);
    }

    public float getHeight() {
        return this.getFluid().getHeight(this);
    }

    public int getLevel() {
        return this.getFluid().getLevel(this);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_15756(BlockView arg, BlockPos arg2) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPos lv = arg2.add(i, 0, j);
                FluidState lv2 = arg.getFluidState(lv);
                if (lv2.getFluid().matchesType(this.getFluid()) || arg.getBlockState(lv).isOpaqueFullCube(arg, lv)) continue;
                return true;
            }
        }
        return false;
    }

    public void onScheduledTick(World arg, BlockPos arg2) {
        this.getFluid().onScheduledTick(arg, arg2, this);
    }

    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(World arg, BlockPos arg2, Random random) {
        this.getFluid().randomDisplayTick(arg, arg2, this, random);
    }

    public boolean hasRandomTicks() {
        return this.getFluid().hasRandomTicks();
    }

    public void onRandomTick(World arg, BlockPos arg2, Random random) {
        this.getFluid().onRandomTick(arg, arg2, this, random);
    }

    public Vec3d getVelocity(BlockView arg, BlockPos arg2) {
        return this.getFluid().getVelocity(arg, arg2, this);
    }

    public BlockState getBlockState() {
        return this.getFluid().toBlockState(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return this.getFluid().getParticle();
    }

    public boolean matches(Tag<Fluid> arg) {
        return this.getFluid().isIn(arg);
    }

    public float getBlastResistance() {
        return this.getFluid().getBlastResistance();
    }

    public boolean canBeReplacedWith(BlockView arg, BlockPos arg2, Fluid arg3, Direction arg4) {
        return this.getFluid().canBeReplacedWith(this, arg, arg2, arg3, arg4);
    }

    public VoxelShape getShape(BlockView arg, BlockPos arg2) {
        return this.getFluid().getShape(this, arg, arg2);
    }
}

