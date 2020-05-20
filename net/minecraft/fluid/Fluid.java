/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class Fluid {
    public static final IdList<FluidState> STATE_IDS = new IdList();
    protected final StateManager<Fluid, FluidState> stateManager;
    private FluidState defaultState;

    protected Fluid() {
        StateManager.Builder<Fluid, FluidState> lv = new StateManager.Builder<Fluid, FluidState>(this);
        this.appendProperties(lv);
        this.stateManager = lv.build(Fluid::getDefaultState, FluidState::new);
        this.setDefaultState(this.stateManager.getDefaultState());
    }

    protected void appendProperties(StateManager.Builder<Fluid, FluidState> arg) {
    }

    public StateManager<Fluid, FluidState> getStateManager() {
        return this.stateManager;
    }

    protected final void setDefaultState(FluidState arg) {
        this.defaultState = arg;
    }

    public final FluidState getDefaultState() {
        return this.defaultState;
    }

    public abstract Item getBucketItem();

    @Environment(value=EnvType.CLIENT)
    protected void randomDisplayTick(World arg, BlockPos arg2, FluidState arg3, Random random) {
    }

    protected void onScheduledTick(World arg, BlockPos arg2, FluidState arg3) {
    }

    protected void onRandomTick(World arg, BlockPos arg2, FluidState arg3, Random random) {
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    protected ParticleEffect getParticle() {
        return null;
    }

    protected abstract boolean canBeReplacedWith(FluidState var1, BlockView var2, BlockPos var3, Fluid var4, Direction var5);

    protected abstract Vec3d getVelocity(BlockView var1, BlockPos var2, FluidState var3);

    public abstract int getTickRate(WorldView var1);

    protected boolean hasRandomTicks() {
        return false;
    }

    protected boolean isEmpty() {
        return false;
    }

    protected abstract float getBlastResistance();

    public abstract float getHeight(FluidState var1, BlockView var2, BlockPos var3);

    public abstract float getHeight(FluidState var1);

    protected abstract BlockState toBlockState(FluidState var1);

    public abstract boolean isStill(FluidState var1);

    public abstract int getLevel(FluidState var1);

    public boolean matchesType(Fluid arg) {
        return arg == this;
    }

    public boolean isIn(Tag<Fluid> arg) {
        return arg.contains(this);
    }

    public abstract VoxelShape getShape(FluidState var1, BlockView var2, BlockPos var3);
}

