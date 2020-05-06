/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class CampfireBlock
extends BlockWithEntity
implements Waterloggable {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty SIGNAL_FIRE = Properties.SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape field_21580 = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    private final boolean field_23881;

    public CampfireBlock(boolean bl, AbstractBlock.Settings arg) {
        super(arg);
        this.field_23881 = bl;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LIT, true)).with(SIGNAL_FIRE, false)).with(WATERLOGGED, false)).with(FACING, Direction.NORTH));
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        ItemStack lv3;
        CampfireBlockEntity lv2;
        Optional<CampfireCookingRecipe> optional;
        BlockEntity lv;
        if (arg.get(LIT).booleanValue() && (lv = arg2.getBlockEntity(arg3)) instanceof CampfireBlockEntity && (optional = (lv2 = (CampfireBlockEntity)lv).getRecipeFor(lv3 = arg4.getStackInHand(arg5))).isPresent()) {
            if (!arg2.isClient && lv2.addItem(arg4.abilities.creativeMode ? lv3.copy() : lv3, optional.get().getCookTime())) {
                arg4.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
                return ActionResult.SUCCESS;
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (!arg4.isFireImmune() && arg.get(LIT).booleanValue() && arg4 instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)arg4)) {
            arg4.damage(DamageSource.IN_FIRE, 1.0f);
        }
        super.onEntityCollision(arg, arg2, arg3, arg4);
    }

    @Override
    public void onBlockRemoved(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.isOf(arg4.getBlock())) {
            return;
        }
        BlockEntity lv = arg2.getBlockEntity(arg3);
        if (lv instanceof CampfireBlockEntity) {
            ItemScatterer.spawn(arg2, arg3, ((CampfireBlockEntity)lv).getItemsBeingCooked());
        }
        super.onBlockRemoved(arg, arg2, arg3, arg4, bl);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        BlockPos lv2;
        World lv = arg.getWorld();
        boolean bl = lv.getFluidState(lv2 = arg.getBlockPos()).getFluid() == Fluids.WATER;
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, bl)).with(SIGNAL_FIRE, this.doesBlockCauseSignalFire(lv.getBlockState(lv2.down())))).with(LIT, !bl)).with(FACING, arg.getPlayerFacing());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, IWorld arg4, BlockPos arg5, BlockPos arg6) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            arg4.getFluidTickScheduler().schedule(arg5, Fluids.WATER, Fluids.WATER.getTickRate(arg4));
        }
        if (arg2 == Direction.DOWN) {
            return (BlockState)arg.with(SIGNAL_FIRE, this.doesBlockCauseSignalFire(arg3));
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    private boolean doesBlockCauseSignalFire(BlockState arg) {
        return arg.isOf(Blocks.HAY_BLOCK);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (!arg.get(LIT).booleanValue()) {
            return;
        }
        if (random.nextInt(10) == 0) {
            arg2.playSound((float)arg3.getX() + 0.5f, (double)((float)arg3.getY() + 0.5f), (double)((float)arg3.getZ() + 0.5f), SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f + random.nextFloat(), random.nextFloat() * 0.7f + 0.6f, false);
        }
        if (this.field_23881 && random.nextInt(5) == 0) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                arg2.addParticle(ParticleTypes.LAVA, (float)arg3.getX() + 0.5f, (float)arg3.getY() + 0.5f, (float)arg3.getZ() + 0.5f, random.nextFloat() / 2.0f, 5.0E-5, random.nextFloat() / 2.0f);
            }
        }
    }

    @Override
    public boolean tryFillWithFluid(IWorld arg, BlockPos arg2, BlockState arg3, FluidState arg4) {
        if (!arg3.get(Properties.WATERLOGGED).booleanValue() && arg4.getFluid() == Fluids.WATER) {
            boolean bl = arg3.get(LIT);
            if (bl) {
                if (arg.isClient()) {
                    for (int i = 0; i < 20; ++i) {
                        CampfireBlock.spawnSmokeParticle(arg.getWorld(), arg2, arg3.get(SIGNAL_FIRE), true);
                    }
                } else {
                    arg.playSound(null, arg2, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                BlockEntity lv = arg.getBlockEntity(arg2);
                if (lv instanceof CampfireBlockEntity) {
                    ((CampfireBlockEntity)lv).spawnItemsBeingCooked();
                }
            }
            arg.setBlockState(arg2, (BlockState)((BlockState)arg3.with(WATERLOGGED, true)).with(LIT, false), 3);
            arg.getFluidTickScheduler().schedule(arg2, arg4.getFluid(), arg4.getFluid().getTickRate(arg));
            return true;
        }
        return false;
    }

    @Override
    public void onProjectileHit(World arg, BlockState arg2, BlockHitResult arg3, ProjectileEntity arg4) {
        if (!arg.isClient && arg4.isOnFire()) {
            boolean bl;
            Entity lv = arg4.getOwner();
            boolean bl2 = bl = lv == null || lv instanceof PlayerEntity || arg.getGameRules().getBoolean(GameRules.MOB_GRIEFING);
            if (bl && !arg2.get(LIT).booleanValue() && !arg2.get(WATERLOGGED).booleanValue()) {
                BlockPos lv2 = arg3.getBlockPos();
                arg.setBlockState(lv2, (BlockState)arg2.with(Properties.LIT, true), 11);
            }
        }
    }

    public static void spawnSmokeParticle(World arg, BlockPos arg2, boolean bl, boolean bl2) {
        Random random = arg.getRandom();
        DefaultParticleType lv = bl ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        arg.addImportantParticle(lv, true, (double)arg2.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), (double)arg2.getY() + random.nextDouble() + random.nextDouble(), (double)arg2.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (bl2) {
            arg.addParticle(ParticleTypes.SMOKE, (double)arg2.getX() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1), (double)arg2.getY() + 0.4, (double)arg2.getZ() + 0.25 + random.nextDouble() / 2.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }

    public static boolean isLitCampfireInRange(World arg, BlockPos arg2, int i) {
        for (int j = 1; j <= i; ++j) {
            BlockPos lv = arg2.down(j);
            BlockState lv2 = arg.getBlockState(lv);
            if (CampfireBlock.isLitCampfire(lv2)) {
                return true;
            }
            boolean bl = VoxelShapes.matchesAnywhere(field_21580, lv2.getCollisionShape(arg, arg2, ShapeContext.absent()), BooleanBiFunction.AND);
            if (!bl) continue;
            BlockState lv3 = arg.getBlockState(lv.down());
            return CampfireBlock.isLitCampfire(lv3);
        }
        return false;
    }

    public static boolean isLitCampfire(BlockState arg) {
        return arg.getBlock().isIn(BlockTags.CAMPFIRES) && arg.contains(LIT) && arg.get(LIT) != false;
    }

    @Override
    public FluidState getFluidState(BlockState arg) {
        if (arg.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(arg);
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return (BlockState)arg.with(FACING, arg2.rotate(arg.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return arg.rotate(arg2.getRotation(arg.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new CampfireBlockEntity();
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

