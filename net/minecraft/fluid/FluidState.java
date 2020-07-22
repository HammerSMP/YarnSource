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
    public static final Codec<FluidState> CODEC = FluidState.createCodec(Registry.FLUID, Fluid::getDefaultState).stable();

    public FluidState(Fluid fluid, ImmutableMap<Property<?>, Comparable<?>> propertiesMap, MapCodec<FluidState> mapCodec) {
        super(fluid, propertiesMap, mapCodec);
    }

    public Fluid getFluid() {
        return (Fluid)this.owner;
    }

    public boolean isStill() {
        return this.getFluid().isStill(this);
    }

    public boolean isEmpty() {
        return this.getFluid().isEmpty();
    }

    public float getHeight(BlockView world, BlockPos pos) {
        return this.getFluid().getHeight(this, world, pos);
    }

    public float getHeight() {
        return this.getFluid().getHeight(this);
    }

    public int getLevel() {
        return this.getFluid().getLevel(this);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_15756(BlockView world, BlockPos pos) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPos lv = pos.add(i, 0, j);
                FluidState lv2 = world.getFluidState(lv);
                if (lv2.getFluid().matchesType(this.getFluid()) || world.getBlockState(lv).isOpaqueFullCube(world, lv)) continue;
                return true;
            }
        }
        return false;
    }

    public void onScheduledTick(World world, BlockPos pos) {
        this.getFluid().onScheduledTick(world, pos, this);
    }

    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, Random random) {
        this.getFluid().randomDisplayTick(world, pos, this, random);
    }

    public boolean hasRandomTicks() {
        return this.getFluid().hasRandomTicks();
    }

    public void onRandomTick(World world, BlockPos pos, Random random) {
        this.getFluid().onRandomTick(world, pos, this, random);
    }

    public Vec3d getVelocity(BlockView world, BlockPos pos) {
        return this.getFluid().getVelocity(world, pos, this);
    }

    public BlockState getBlockState() {
        return this.getFluid().toBlockState(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return this.getFluid().getParticle();
    }

    public boolean isIn(Tag<Fluid> tag) {
        return this.getFluid().isIn(tag);
    }

    public float getBlastResistance() {
        return this.getFluid().getBlastResistance();
    }

    public boolean canBeReplacedWith(BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return this.getFluid().canBeReplacedWith(this, world, pos, fluid, direction);
    }

    public VoxelShape getShape(BlockView world, BlockPos pos) {
        return this.getFluid().getShape(this, world, pos);
    }
}

