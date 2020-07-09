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
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalBlock
extends BlockWithEntity {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    protected EndPortalBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new EndPortalBlockEntity();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (arg2 instanceof ServerWorld && !arg4.hasVehicle() && !arg4.hasPassengers() && arg4.canUsePortals() && VoxelShapes.matchesAnywhere(VoxelShapes.cuboid(arg4.getBoundingBox().offset(-arg3.getX(), -arg3.getY(), -arg3.getZ())), arg.getOutlineShape(arg2, arg3), BooleanBiFunction.AND)) {
            RegistryKey<World> lv = arg2.getRegistryKey() == World.END ? World.OVERWORLD : World.END;
            ServerWorld lv2 = ((ServerWorld)arg2).getServer().getWorld(lv);
            if (lv2 == null) {
                return;
            }
            arg4.moveToWorld(lv2);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        double d = (double)arg3.getX() + random.nextDouble();
        double e = (double)arg3.getY() + 0.8;
        double f = (double)arg3.getZ() + random.nextDouble();
        arg2.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canBucketPlace(BlockState arg, Fluid arg2) {
        return false;
    }
}

