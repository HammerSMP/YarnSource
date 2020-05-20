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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;

public class RespawnAnchorBlock
extends Block {
    public static final IntProperty CHARGES = Properties.CHARGES;

    public RespawnAnchorBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(CHARGES, 0));
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        ItemStack lv = arg4.getStackInHand(arg5);
        if (lv.getItem() == Items.GLOWSTONE && arg.get(CHARGES) < 4) {
            RespawnAnchorBlock.charge(arg2, arg3, arg);
            if (!arg4.abilities.creativeMode) {
                lv.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        if (arg.get(CHARGES) == 0) {
            return ActionResult.PASS;
        }
        if (RespawnAnchorBlock.method_27353(arg2)) {
            ServerPlayerEntity lv2;
            if (!(arg2.isClient || (lv2 = (ServerPlayerEntity)arg4).getSpawnPointDimension() == arg2.method_27983() && lv2.getSpawnPointPosition().equals(arg3))) {
                lv2.setSpawnPoint(arg2.method_27983(), arg3, false, true);
                arg2.playSound(null, (double)arg3.getX() + 0.5, (double)arg3.getY() + 0.5, (double)arg3.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
            return arg.get(CHARGES) < 4 ? ActionResult.PASS : ActionResult.CONSUME;
        }
        if (!arg2.isClient) {
            arg2.removeBlock(arg3, false);
            arg2.createExplosion(null, DamageSource.netherBed(), (double)arg3.getX() + 0.5, (double)arg3.getY() + 0.5, (double)arg3.getZ() + 0.5, 5.0f, true, Explosion.DestructionType.DESTROY);
        }
        return ActionResult.SUCCESS;
    }

    public static boolean method_27353(World arg) {
        return arg.method_27983() == DimensionType.THE_NETHER;
    }

    public static void charge(World arg, BlockPos arg2, BlockState arg3) {
        arg.setBlockState(arg2, (BlockState)arg3.with(CHARGES, arg3.get(CHARGES) + 1), 3);
        arg.playSound(null, (double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (arg.get(CHARGES) == 0) {
            return;
        }
        if (random.nextInt(100) == 0) {
            arg2.playSound(null, (double)arg3.getX() + 0.5, (double)arg3.getY() + 0.5, (double)arg3.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        double d = (double)arg3.getX() + 0.5 + (double)(0.5f - random.nextFloat());
        double e = (double)arg3.getY() + 1.0;
        double f = (double)arg3.getZ() + 0.5 + (double)(0.5f - random.nextFloat());
        double g = (double)random.nextFloat() * 0.04;
        arg2.addParticle(ParticleTypes.REVERSE_PORTAL, d, e, f, 0.0, g, 0.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(CHARGES);
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    public static int getLightLevel(BlockState arg, int i) {
        return MathHelper.floor((float)(arg.get(CHARGES) - 0) / 4.0f * (float)i);
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        return RespawnAnchorBlock.getLightLevel(arg, 15);
    }

    public static Optional<Vec3d> findRespawnPosition(EntityType<?> arg, WorldView arg2, BlockPos arg3) {
        for (BlockPos lv : BlockPos.iterate(arg3.add(-1, -1, -1), arg3.add(1, 1, 1))) {
            Optional<Vec3d> optional = BedBlock.canWakeUpAt(arg, arg2, lv);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public boolean hasSidedTransparency(BlockState arg) {
        return true;
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}
