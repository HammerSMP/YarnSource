/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BeehiveBlock
extends BlockWithEntity {
    private static final Direction[] GENERATE_DIRECTIONS = new Direction[]{Direction.WEST, Direction.EAST, Direction.SOUTH};
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final IntProperty HONEY_LEVEL = Properties.HONEY_LEVEL;

    public BeehiveBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HONEY_LEVEL, 0)).with(FACING, Direction.NORTH));
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        return arg.get(HONEY_LEVEL);
    }

    @Override
    public void afterBreak(World arg, PlayerEntity arg2, BlockPos arg3, BlockState arg4, @Nullable BlockEntity arg5, ItemStack arg6) {
        super.afterBreak(arg, arg2, arg3, arg4, arg5, arg6);
        if (!arg.isClient && arg5 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity lv = (BeehiveBlockEntity)arg5;
            if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, arg6) == 0) {
                lv.angerBees(arg2, arg4, BeehiveBlockEntity.BeeState.EMERGENCY);
                arg.updateComparators(arg3, this);
                this.angerNearbyBees(arg, arg3);
            }
            Criteria.BEE_NEST_DESTROYED.test((ServerPlayerEntity)arg2, arg4.getBlock(), arg6, lv.getBeeCount());
        }
    }

    private void angerNearbyBees(World arg, BlockPos arg2) {
        List<BeeEntity> list = arg.getNonSpectatingEntities(BeeEntity.class, new Box(arg2).expand(8.0, 6.0, 8.0));
        if (!list.isEmpty()) {
            List<PlayerEntity> list2 = arg.getNonSpectatingEntities(PlayerEntity.class, new Box(arg2).expand(8.0, 6.0, 8.0));
            int i = list2.size();
            for (BeeEntity lv : list) {
                if (lv.getTarget() != null) continue;
                lv.setTarget(list2.get(arg.random.nextInt(i)));
            }
        }
    }

    public static void dropHoneycomb(World arg, BlockPos arg2) {
        BeehiveBlock.dropStack(arg, arg2, new ItemStack(Items.HONEYCOMB, 3));
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg22, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        ItemStack lv = arg4.getStackInHand(arg5);
        int i = arg.get(HONEY_LEVEL);
        boolean bl = false;
        if (i >= 5) {
            if (lv.getItem() == Items.SHEARS) {
                arg22.playSound(arg4, arg4.getX(), arg4.getY(), arg4.getZ(), SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                BeehiveBlock.dropHoneycomb(arg22, arg3);
                lv.damage(1, arg4, arg2 -> arg2.sendToolBreakStatus(arg5));
                bl = true;
            } else if (lv.getItem() == Items.GLASS_BOTTLE) {
                lv.decrement(1);
                arg22.playSound(arg4, arg4.getX(), arg4.getY(), arg4.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                if (lv.isEmpty()) {
                    arg4.setStackInHand(arg5, new ItemStack(Items.HONEY_BOTTLE));
                } else if (!arg4.inventory.insertStack(new ItemStack(Items.HONEY_BOTTLE))) {
                    arg4.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
                }
                bl = true;
            }
        }
        if (bl) {
            if (!CampfireBlock.isLitCampfireInRange(arg22, arg3)) {
                if (this.hasBees(arg22, arg3)) {
                    this.angerNearbyBees(arg22, arg3);
                }
                this.takeHoney(arg22, arg, arg3, arg4, BeehiveBlockEntity.BeeState.EMERGENCY);
            } else {
                this.takeHoney(arg22, arg, arg3);
            }
            return ActionResult.method_29236(arg22.isClient);
        }
        return super.onUse(arg, arg22, arg3, arg4, arg5, arg6);
    }

    private boolean hasBees(World arg, BlockPos arg2) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity lv2 = (BeehiveBlockEntity)lv;
            return !lv2.hasNoBees();
        }
        return false;
    }

    public void takeHoney(World arg, BlockState arg2, BlockPos arg3, @Nullable PlayerEntity arg4, BeehiveBlockEntity.BeeState arg5) {
        this.takeHoney(arg, arg2, arg3);
        BlockEntity lv = arg.getBlockEntity(arg3);
        if (lv instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity lv2 = (BeehiveBlockEntity)lv;
            lv2.angerBees(arg4, arg2, arg5);
        }
    }

    public void takeHoney(World arg, BlockState arg2, BlockPos arg3) {
        arg.setBlockState(arg3, (BlockState)arg2.with(HONEY_LEVEL, 0), 3);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (arg.get(HONEY_LEVEL) >= 5) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.spawnHoneyParticles(arg2, arg3, arg);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    private void spawnHoneyParticles(World arg, BlockPos arg2, BlockState arg3) {
        if (!arg3.getFluidState().isEmpty() || arg.random.nextFloat() < 0.3f) {
            return;
        }
        VoxelShape lv = arg3.getCollisionShape(arg, arg2);
        double d = lv.getMax(Direction.Axis.Y);
        if (d >= 1.0 && !arg3.isIn(BlockTags.IMPERMEABLE)) {
            double e = lv.getMin(Direction.Axis.Y);
            if (e > 0.0) {
                this.addHoneyParticle(arg, arg2, lv, (double)arg2.getY() + e - 0.05);
            } else {
                BlockPos lv2 = arg2.down();
                BlockState lv3 = arg.getBlockState(lv2);
                VoxelShape lv4 = lv3.getCollisionShape(arg, lv2);
                double f = lv4.getMax(Direction.Axis.Y);
                if ((f < 1.0 || !lv3.isFullCube(arg, lv2)) && lv3.getFluidState().isEmpty()) {
                    this.addHoneyParticle(arg, arg2, lv, (double)arg2.getY() - 0.05);
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    private void addHoneyParticle(World arg, BlockPos arg2, VoxelShape arg3, double d) {
        this.addHoneyParticle(arg, (double)arg2.getX() + arg3.getMin(Direction.Axis.X), (double)arg2.getX() + arg3.getMax(Direction.Axis.X), (double)arg2.getZ() + arg3.getMin(Direction.Axis.Z), (double)arg2.getZ() + arg3.getMax(Direction.Axis.Z), d);
    }

    @Environment(value=EnvType.CLIENT)
    private void addHoneyParticle(World arg, double d, double e, double f, double g, double h) {
        arg.addParticle(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(arg.random.nextDouble(), d, e), h, MathHelper.lerp(arg.random.nextDouble(), f, g), 0.0, 0.0, 0.0);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return (BlockState)this.getDefaultState().with(FACING, arg.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(HONEY_LEVEL, FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockView arg) {
        return new BeehiveBlockEntity();
    }

    @Override
    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        BlockEntity lv;
        if (!arg.isClient && arg4.isCreative() && arg.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && (lv = arg.getBlockEntity(arg2)) instanceof BeehiveBlockEntity) {
            boolean bl;
            BeehiveBlockEntity lv2 = (BeehiveBlockEntity)lv;
            ItemStack lv3 = new ItemStack(this);
            int i = arg3.get(HONEY_LEVEL);
            boolean bl2 = bl = !lv2.hasNoBees();
            if (!bl && i == 0) {
                return;
            }
            if (bl) {
                CompoundTag lv4 = new CompoundTag();
                lv4.put("Bees", lv2.getBees());
                lv3.putSubTag("BlockEntityTag", lv4);
            }
            CompoundTag lv5 = new CompoundTag();
            lv5.putInt("honey_level", i);
            lv3.putSubTag("BlockStateTag", lv5);
            ItemEntity lv6 = new ItemEntity(arg, arg2.getX(), arg2.getY(), arg2.getZ(), lv3);
            lv6.setToDefaultPickupDelay();
            arg.spawnEntity(lv6);
        }
        super.onBreak(arg, arg2, arg3, arg4);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState arg, LootContext.Builder arg2) {
        BlockEntity lv2;
        Entity lv = arg2.getNullable(LootContextParameters.THIS_ENTITY);
        if ((lv instanceof TntEntity || lv instanceof CreeperEntity || lv instanceof WitherSkullEntity || lv instanceof WitherEntity || lv instanceof TntMinecartEntity) && (lv2 = arg2.getNullable(LootContextParameters.BLOCK_ENTITY)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity lv3 = (BeehiveBlockEntity)lv2;
            lv3.angerBees(null, arg, BeehiveBlockEntity.BeeState.EMERGENCY);
        }
        return super.getDroppedStacks(arg, arg2);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        BlockEntity lv;
        if (arg4.getBlockState(arg6).getBlock() instanceof FireBlock && (lv = arg4.getBlockEntity(arg5)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity lv2 = (BeehiveBlockEntity)lv;
            lv2.angerBees(null, arg, BeehiveBlockEntity.BeeState.EMERGENCY);
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    public static Direction getRandomGenerationDirection(Random random) {
        return Util.getRandom(GENERATE_DIRECTIONS, random);
    }
}

