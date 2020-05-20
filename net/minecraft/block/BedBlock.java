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
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;

public class BedBlock
extends HorizontalFacingBlock
implements BlockEntityProvider {
    public static final EnumProperty<BedPart> PART = Properties.BED_PART;
    public static final BooleanProperty OCCUPIED = Properties.OCCUPIED;
    protected static final VoxelShape TOP_SHAPE = Block.createCuboidShape(0.0, 3.0, 0.0, 16.0, 9.0, 16.0);
    protected static final VoxelShape LEG_1_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
    protected static final VoxelShape LEG_2_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 3.0, 3.0, 16.0);
    protected static final VoxelShape LEG_3_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 3.0, 3.0);
    protected static final VoxelShape LEG_4_SHAPE = Block.createCuboidShape(13.0, 0.0, 13.0, 16.0, 3.0, 16.0);
    protected static final VoxelShape NORTH_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_1_SHAPE, LEG_3_SHAPE);
    protected static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_2_SHAPE, LEG_4_SHAPE);
    protected static final VoxelShape WEST_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_1_SHAPE, LEG_2_SHAPE);
    protected static final VoxelShape EAST_SHAPE = VoxelShapes.union(TOP_SHAPE, LEG_3_SHAPE, LEG_4_SHAPE);
    private final DyeColor color;

    public BedBlock(DyeColor arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.color = arg;
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(PART, BedPart.FOOT)).with(OCCUPIED, false));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Direction getDirection(BlockView arg, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        return lv.getBlock() instanceof BedBlock ? lv.get(FACING) : null;
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg22, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg22.isClient) {
            return ActionResult.CONSUME;
        }
        if (arg.get(PART) != BedPart.HEAD && !(arg = arg22.getBlockState(arg3 = arg3.offset(arg.get(FACING)))).isOf(this)) {
            return ActionResult.CONSUME;
        }
        if (!BedBlock.isOverworld(arg22, arg3)) {
            arg22.removeBlock(arg3, false);
            BlockPos lv = arg3.offset(arg.get(FACING).getOpposite());
            if (arg22.getBlockState(lv).isOf(this)) {
                arg22.removeBlock(lv, false);
            }
            arg22.createExplosion(null, DamageSource.netherBed(), (double)arg3.getX() + 0.5, (double)arg3.getY() + 0.5, (double)arg3.getZ() + 0.5, 5.0f, true, Explosion.DestructionType.DESTROY);
            return ActionResult.SUCCESS;
        }
        if (arg.get(OCCUPIED).booleanValue()) {
            if (!this.isFree(arg22, arg3)) {
                arg4.sendMessage(new TranslatableText("block.minecraft.bed.occupied"), true);
            }
            return ActionResult.SUCCESS;
        }
        arg4.trySleep(arg3).ifLeft(arg2 -> {
            if (arg2 != null) {
                arg4.sendMessage(arg2.toText(), true);
            }
        });
        return ActionResult.SUCCESS;
    }

    public static boolean isOverworld(World arg, BlockPos arg2) {
        return arg.getDimension().method_28541();
    }

    private boolean isFree(World arg, BlockPos arg2) {
        List<VillagerEntity> list = arg.getEntities(VillagerEntity.class, new Box(arg2), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        }
        list.get(0).wakeUp();
        return true;
    }

    @Override
    public void onLandedUpon(World arg, BlockPos arg2, Entity arg3, float f) {
        super.onLandedUpon(arg, arg2, arg3, f * 0.5f);
    }

    @Override
    public void onEntityLand(BlockView arg, Entity arg2) {
        if (arg2.bypassesLandingEffects()) {
            super.onEntityLand(arg, arg2);
        } else {
            this.bounceEntity(arg2);
        }
    }

    private void bounceEntity(Entity arg) {
        Vec3d lv = arg.getVelocity();
        if (lv.y < 0.0) {
            double d = arg instanceof LivingEntity ? 1.0 : 0.8;
            arg.setVelocity(lv.x, -lv.y * (double)0.66f * d, lv.z);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == BedBlock.getDirectionTowardsOtherPart(arg.get(PART), arg.get(FACING))) {
            if (arg3.isOf(this) && arg3.get(PART) != arg.get(PART)) {
                return (BlockState)arg.with(OCCUPIED, arg3.get(OCCUPIED));
            }
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
    }

    private static Direction getDirectionTowardsOtherPart(BedPart arg, Direction arg2) {
        return arg == BedPart.FOOT ? arg2 : arg2.getOpposite();
    }

    @Override
    public void afterBreak(World arg, PlayerEntity arg2, BlockPos arg3, BlockState arg4, @Nullable BlockEntity arg5, ItemStack arg6) {
        super.afterBreak(arg, arg2, arg3, Blocks.AIR.getDefaultState(), arg5, arg6);
    }

    @Override
    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        BedPart lv = arg3.get(PART);
        BlockPos lv2 = arg2.offset(BedBlock.getDirectionTowardsOtherPart(lv, arg3.get(FACING)));
        BlockState lv3 = arg.getBlockState(lv2);
        if (lv3.isOf(this) && lv3.get(PART) != lv) {
            arg.setBlockState(lv2, Blocks.AIR.getDefaultState(), 35);
            arg.syncWorldEvent(arg4, 2001, lv2, Block.getRawIdFromState(lv3));
            if (!arg.isClient && !arg4.isCreative()) {
                ItemStack lv4 = arg4.getMainHandStack();
                BedBlock.dropStacks(arg3, arg, arg2, null, arg4, lv4);
                BedBlock.dropStacks(lv3, arg, lv2, null, arg4, lv4);
            }
            arg4.incrementStat(Stats.MINED.getOrCreateStat(this));
        }
        super.onBreak(arg, arg2, arg3, arg4);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        Direction lv = arg.getPlayerFacing();
        BlockPos lv2 = arg.getBlockPos();
        BlockPos lv3 = lv2.offset(lv);
        if (arg.getWorld().getBlockState(lv3).canReplace(arg)) {
            return (BlockState)this.getDefaultState().with(FACING, lv);
        }
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        Direction lv = BedBlock.getOppositePartDirection(arg).getOpposite();
        switch (lv) {
            case NORTH: {
                return NORTH_SHAPE;
            }
            case SOUTH: {
                return SOUTH_SHAPE;
            }
            case WEST: {
                return WEST_SHAPE;
            }
        }
        return EAST_SHAPE;
    }

    public static Direction getOppositePartDirection(BlockState arg) {
        Direction lv = arg.get(FACING);
        return arg.get(PART) == BedPart.HEAD ? lv.getOpposite() : lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static DoubleBlockProperties.Type getBedPart(BlockState arg) {
        BedPart lv = arg.get(PART);
        if (lv == BedPart.HEAD) {
            return DoubleBlockProperties.Type.FIRST;
        }
        return DoubleBlockProperties.Type.SECOND;
    }

    public static Optional<Vec3d> findWakeUpPosition(EntityType<?> arg, WorldView arg2, BlockPos arg3, int i) {
        Direction lv = arg2.getBlockState(arg3).get(FACING);
        int j = arg3.getX();
        int k = arg3.getY();
        int l = arg3.getZ();
        for (int m = 0; m <= 1; ++m) {
            int n = j - lv.getOffsetX() * m - 1;
            int o = l - lv.getOffsetZ() * m - 1;
            int p = n + 2;
            int q = o + 2;
            for (int r = n; r <= p; ++r) {
                for (int s = o; s <= q; ++s) {
                    BlockPos lv2 = new BlockPos(r, k, s);
                    Optional<Vec3d> optional = BedBlock.canWakeUpAt(arg, arg2, lv2);
                    if (!optional.isPresent()) continue;
                    if (i > 0) {
                        --i;
                        continue;
                    }
                    return optional;
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<Vec3d> canWakeUpAt(EntityType<?> arg, WorldView arg2, BlockPos arg3) {
        VoxelShape lv = arg2.getBlockState(arg3).getCollisionShape(arg2, arg3);
        if (lv.getMaximum(Direction.Axis.Y) > 0.4375) {
            return Optional.empty();
        }
        BlockPos.Mutable lv2 = arg3.mutableCopy();
        while (lv2.getY() >= 0 && arg3.getY() - lv2.getY() <= 2 && arg2.getBlockState(lv2).getCollisionShape(arg2, lv2).isEmpty()) {
            lv2.move(Direction.DOWN);
        }
        VoxelShape lv3 = arg2.getBlockState(lv2).getCollisionShape(arg2, lv2);
        if (lv3.isEmpty()) {
            return Optional.empty();
        }
        double d = (double)lv2.getY() + lv3.getMaximum(Direction.Axis.Y) + 2.0E-7;
        if ((double)arg3.getY() - d > 2.0) {
            return Optional.empty();
        }
        float f = arg.getWidth() / 2.0f;
        Vec3d lv4 = new Vec3d((double)lv2.getX() + 0.5, d, (double)lv2.getZ() + 0.5);
        if (arg2.doesNotCollide(new Box(lv4.x - (double)f, lv4.y, lv4.z - (double)f, lv4.x + (double)f, lv4.y + (double)arg.getHeight(), lv4.z + (double)f))) {
            return Optional.of(lv4);
        }
        return Optional.empty();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState arg) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(FACING, PART, OCCUPIED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView arg) {
        return new BedBlockEntity(this.color);
    }

    @Override
    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, @Nullable LivingEntity arg4, ItemStack arg5) {
        super.onPlaced(arg, arg2, arg3, arg4, arg5);
        if (!arg.isClient) {
            BlockPos lv = arg2.offset(arg3.get(FACING));
            arg.setBlockState(lv, (BlockState)arg3.with(PART, BedPart.HEAD), 3);
            arg.updateNeighbors(arg2, Blocks.AIR);
            arg3.updateNeighbors(arg, arg2, 3);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public long getRenderingSeed(BlockState arg, BlockPos arg2) {
        BlockPos lv = arg2.offset(arg.get(FACING), arg.get(PART) == BedPart.HEAD ? 0 : 1);
        return MathHelper.hashCode(lv.getX(), arg2.getY(), lv.getZ());
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }
}

