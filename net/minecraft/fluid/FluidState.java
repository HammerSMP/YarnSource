/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public interface FluidState
extends State<FluidState> {
    public Fluid getFluid();

    default public boolean isStill() {
        return this.getFluid().isStill(this);
    }

    default public boolean isEmpty() {
        return this.getFluid().isEmpty();
    }

    default public float getHeight(BlockView arg, BlockPos arg2) {
        return this.getFluid().getHeight(this, arg, arg2);
    }

    default public float getHeight() {
        return this.getFluid().getHeight(this);
    }

    default public int getLevel() {
        return this.getFluid().getLevel(this);
    }

    @Environment(value=EnvType.CLIENT)
    default public boolean method_15756(BlockView arg, BlockPos arg2) {
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

    default public void onScheduledTick(World arg, BlockPos arg2) {
        this.getFluid().onScheduledTick(arg, arg2, this);
    }

    @Environment(value=EnvType.CLIENT)
    default public void randomDisplayTick(World arg, BlockPos arg2, Random random) {
        this.getFluid().randomDisplayTick(arg, arg2, this, random);
    }

    default public boolean hasRandomTicks() {
        return this.getFluid().hasRandomTicks();
    }

    default public void onRandomTick(World arg, BlockPos arg2, Random random) {
        this.getFluid().onRandomTick(arg, arg2, this, random);
    }

    default public Vec3d getVelocity(BlockView arg, BlockPos arg2) {
        return this.getFluid().getVelocity(arg, arg2, this);
    }

    default public BlockState getBlockState() {
        return this.getFluid().toBlockState(this);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    default public ParticleEffect getParticle() {
        return this.getFluid().getParticle();
    }

    default public boolean matches(Tag<Fluid> arg) {
        return this.getFluid().isIn(arg);
    }

    default public float getBlastResistance() {
        return this.getFluid().getBlastResistance();
    }

    default public boolean canBeReplacedWith(BlockView arg, BlockPos arg2, Fluid arg3, Direction arg4) {
        return this.getFluid().canBeReplacedWith(this, arg, arg2, arg3, arg4);
    }

    public static <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps, FluidState arg) {
        Object object2;
        ImmutableMap<Property<?>, Comparable<?>> immutableMap = arg.getEntries();
        if (immutableMap.isEmpty()) {
            Object object = dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("Name"), (Object)dynamicOps.createString(Registry.FLUID.getId(arg.getFluid()).toString())));
        } else {
            object2 = dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("Name"), (Object)dynamicOps.createString(Registry.FLUID.getId(arg.getFluid()).toString()), (Object)dynamicOps.createString("Properties"), (Object)dynamicOps.createMap(immutableMap.entrySet().stream().map(entry -> Pair.of((Object)dynamicOps.createString(((Property)entry.getKey()).getName()), (Object)dynamicOps.createString(State.nameValue((Property)entry.getKey(), (Comparable)entry.getValue())))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        }
        return new Dynamic(dynamicOps, object2);
    }

    public static <T> FluidState deserialize(Dynamic<T> dynamic2) {
        Fluid lv = Registry.FLUID.get(new Identifier(dynamic2.getElement("Name").flatMap(((DynamicOps)dynamic2.getOps())::getStringValue).orElse("minecraft:empty")));
        Map map = dynamic2.get("Properties").asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asString(""));
        FluidState lv2 = lv.getDefaultState();
        StateManager<Fluid, FluidState> lv3 = lv.getStateManager();
        for (Map.Entry entry : map.entrySet()) {
            String string = (String)entry.getKey();
            Property<?> lv4 = lv3.getProperty(string);
            if (lv4 == null) continue;
            lv2 = State.tryRead(lv2, lv4, string, dynamic2.toString(), (String)entry.getValue());
        }
        return lv2;
    }

    default public VoxelShape getShape(BlockView arg, BlockPos arg2) {
        return this.getFluid().getShape(this, arg, arg2);
    }
}

