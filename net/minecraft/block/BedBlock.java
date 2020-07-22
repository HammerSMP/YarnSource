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

    public BedBlock(DyeColor color, AbstractBlock.Settings settings) {
        super(settings);
        this.color = color;
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(PART, BedPart.FOOT)).with(OCCUPIED, false));
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Direction getDirection(BlockView world, BlockPos pos) {
        BlockState lv = world.getBlockState(pos);
        return lv.getBlock() instanceof BedBlock ? lv.get(FACING) : null;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.CONSUME;
        }
        if (state.get(PART) != BedPart.HEAD && !(state = world.getBlockState(pos = pos.offset(state.get(FACING)))).isOf(this)) {
            return ActionResult.CONSUME;
        }
        if (!BedBlock.isOverworld(world)) {
            world.removeBlock(pos, false);
            BlockPos lv = pos.offset(state.get(FACING).getOpposite());
            if (world.getBlockState(lv).isOf(this)) {
                world.removeBlock(lv, false);
            }
            world.createExplosion(null, DamageSource.badRespawnPoint(), null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0f, true, Explosion.DestructionType.DESTROY);
            return ActionResult.SUCCESS;
        }
        if (state.get(OCCUPIED).booleanValue()) {
            if (!this.isFree(world, pos)) {
                player.sendMessage(new TranslatableText("block.minecraft.bed.occupied"), true);
            }
            return ActionResult.SUCCESS;
        }
        player.trySleep(pos).ifLeft(arg2 -> {
            if (arg2 != null) {
                player.sendMessage(arg2.toText(), true);
            }
        });
        return ActionResult.SUCCESS;
    }

    public static boolean isOverworld(World world) {
        return world.getDimension().isBedWorking();
    }

    private boolean isFree(World world, BlockPos pos) {
        List<VillagerEntity> list = world.getEntitiesByClass(VillagerEntity.class, new Box(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        }
        list.get(0).wakeUp();
        return true;
    }

    @Override
    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        super.onLandedUpon(world, pos, entity, distance * 0.5f);
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        if (entity.bypassesLandingEffects()) {
            super.onEntityLand(world, entity);
        } else {
            this.bounceEntity(entity);
        }
    }

    private void bounceEntity(Entity entity) {
        Vec3d lv = entity.getVelocity();
        if (lv.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setVelocity(lv.x, -lv.y * (double)0.66f * d, lv.z);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == BedBlock.getDirectionTowardsOtherPart(state.get(PART), state.get(FACING))) {
            if (newState.isOf(this) && newState.get(PART) != state.get(PART)) {
                return (BlockState)state.with(OCCUPIED, newState.get(OCCUPIED));
            }
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    private static Direction getDirectionTowardsOtherPart(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos lv2;
        BlockState lv3;
        BedPart lv;
        if (!world.isClient && player.isCreative() && (lv = state.get(PART)) == BedPart.FOOT && (lv3 = world.getBlockState(lv2 = pos.offset(BedBlock.getDirectionTowardsOtherPart(lv, state.get(FACING))))).getBlock() == this && lv3.get(PART) == BedPart.HEAD) {
            world.setBlockState(lv2, Blocks.AIR.getDefaultState(), 35);
            world.syncWorldEvent(player, 2001, lv2, Block.getRawIdFromState(lv3));
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction lv = ctx.getPlayerFacing();
        BlockPos lv2 = ctx.getBlockPos();
        BlockPos lv3 = lv2.offset(lv);
        if (ctx.getWorld().getBlockState(lv3).canReplace(ctx)) {
            return (BlockState)this.getDefaultState().with(FACING, lv);
        }
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction lv = BedBlock.getOppositePartDirection(state).getOpposite();
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

    public static Direction getOppositePartDirection(BlockState state) {
        Direction lv = state.get(FACING);
        return state.get(PART) == BedPart.HEAD ? lv.getOpposite() : lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static DoubleBlockProperties.Type getBedPart(BlockState state) {
        BedPart lv = state.get(PART);
        if (lv == BedPart.HEAD) {
            return DoubleBlockProperties.Type.FIRST;
        }
        return DoubleBlockProperties.Type.SECOND;
    }

    public static Optional<Vec3d> findWakeUpPosition(EntityType<?> type, WorldView world, BlockPos pos, int index) {
        Direction lv = world.getBlockState(pos).get(FACING);
        int j = pos.getX();
        int k = pos.getY();
        int l = pos.getZ();
        for (int m = 0; m <= 1; ++m) {
            int n = j - lv.getOffsetX() * m - 1;
            int o = l - lv.getOffsetZ() * m - 1;
            int p = n + 2;
            int q = o + 2;
            for (int r = n; r <= p; ++r) {
                for (int s = o; s <= q; ++s) {
                    BlockPos lv2 = new BlockPos(r, k, s);
                    Optional<Vec3d> optional = BedBlock.canWakeUpAt(type, world, lv2);
                    if (!optional.isPresent()) continue;
                    if (index > 0) {
                        --index;
                        continue;
                    }
                    return optional;
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<Vec3d> canWakeUpAt(EntityType<?> type, WorldView world, BlockPos pos) {
        VoxelShape lv = world.getBlockState(pos).getCollisionShape(world, pos);
        if (lv.getMax(Direction.Axis.Y) > 0.4375) {
            return Optional.empty();
        }
        BlockPos.Mutable lv2 = pos.mutableCopy();
        while (lv2.getY() >= 0 && pos.getY() - lv2.getY() <= 2 && world.getBlockState(lv2).getCollisionShape(world, lv2).isEmpty()) {
            lv2.move(Direction.DOWN);
        }
        VoxelShape lv3 = world.getBlockState(lv2).getCollisionShape(world, lv2);
        if (lv3.isEmpty()) {
            return Optional.empty();
        }
        double d = (double)lv2.getY() + lv3.getMax(Direction.Axis.Y) + 2.0E-7;
        if ((double)pos.getY() - d > 2.0) {
            return Optional.empty();
        }
        Vec3d lv4 = new Vec3d((double)lv2.getX() + 0.5, d, (double)lv2.getZ() + 0.5);
        Box lv5 = type.createSimpleBoundingBox(lv4.x, lv4.y, lv4.z);
        if (world.doesNotCollide(lv5)) {
            if (world.method_29546(lv5.stretch(0.0, -0.2f, 0.0)).noneMatch(type::isInvalidSpawn)) {
                return Optional.of(lv4);
            }
        }
        return Optional.empty();
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BedBlockEntity(this.color);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            BlockPos lv = pos.offset(state.get(FACING));
            world.setBlockState(lv, (BlockState)state.with(PART, BedPart.HEAD), 3);
            world.updateNeighbors(pos, Blocks.AIR);
            state.method_30101(world, pos, 3);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public DyeColor getColor() {
        return this.color;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        BlockPos lv = pos.offset(state.get(FACING), state.get(PART) == BedPart.HEAD ? 0 : 1);
        return MathHelper.hashCode(lv.getX(), pos.getY(), lv.getZ());
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}

