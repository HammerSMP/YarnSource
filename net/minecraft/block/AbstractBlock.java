/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AbstractBlock {
    protected static final Direction[] FACINGS = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
    protected final Material material;
    protected final boolean collidable;
    protected final float resistance;
    protected final boolean randomTicks;
    protected final BlockSoundGroup soundGroup;
    protected final float slipperiness;
    protected final float velocityMultiplier;
    protected final float jumpVelocityMultiplier;
    protected final boolean dynamicBounds;
    protected final Settings settings;
    @Nullable
    protected Identifier lootTableId;

    public AbstractBlock(Settings settings) {
        this.material = settings.material;
        this.collidable = settings.collidable;
        this.lootTableId = settings.lootTableId;
        this.resistance = settings.resistance;
        this.randomTicks = settings.randomTicks;
        this.soundGroup = settings.soundGroup;
        this.slipperiness = settings.slipperiness;
        this.velocityMultiplier = settings.velocityMultiplier;
        this.jumpVelocityMultiplier = settings.jumpVelocityMultiplier;
        this.dynamicBounds = settings.dynamicBounds;
        this.settings = settings;
    }

    @Deprecated
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int j) {
    }

    @Deprecated
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        switch (type) {
            case LAND: {
                return !state.isFullCube(world, pos);
            }
            case WATER: {
                return world.getFluidState(pos).isIn(FluidTags.WATER);
            }
            case AIR: {
                return !state.isFullCube(world, pos);
            }
        }
        return false;
    }

    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return state;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return false;
    }

    @Deprecated
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        DebugInfoSender.sendNeighborUpdate(world, pos);
    }

    @Deprecated
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
    }

    @Deprecated
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (this.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            world.removeBlockEntity(pos);
        }
    }

    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }

    @Deprecated
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        return false;
    }

    @Deprecated
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Deprecated
    public boolean hasSidedTransparency(BlockState state) {
        return false;
    }

    @Deprecated
    public boolean emitsRedstonePower(BlockState state) {
        return false;
    }

    @Deprecated
    public PistonBehavior getPistonBehavior(BlockState state) {
        return this.material.getPistonBehavior();
    }

    @Deprecated
    public FluidState getFluidState(BlockState state) {
        return Fluids.EMPTY.getDefaultState();
    }

    @Deprecated
    public boolean hasComparatorOutput(BlockState state) {
        return false;
    }

    public OffsetType getOffsetType() {
        return OffsetType.NONE;
    }

    @Deprecated
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state;
    }

    @Deprecated
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state;
    }

    @Deprecated
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return this.material.isReplaceable() && (context.getStack().isEmpty() || context.getStack().getItem() != this.asItem());
    }

    @Deprecated
    public boolean canBucketPlace(BlockState state, Fluid fluid) {
        return this.material.isReplaceable() || !this.material.isSolid();
    }

    @Deprecated
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        Identifier lv = this.getLootTableId();
        if (lv == LootTables.EMPTY) {
            return Collections.emptyList();
        }
        LootContext lv2 = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
        ServerWorld lv3 = lv2.getWorld();
        LootTable lv4 = lv3.getServer().getLootManager().getTable(lv);
        return lv4.generateLoot(lv2);
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos);
    }

    @Deprecated
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return state.getOutlineShape(world, pos);
    }

    @Deprecated
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, ShapeContext.absent());
    }

    @Deprecated
    public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Deprecated
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        if (state.isOpaqueFullCube(world, pos)) {
            return world.getMaxLightLevel();
        }
        return state.isTranslucent(world, pos) ? 0 : 1;
    }

    @Nullable
    @Deprecated
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return null;
    }

    @Deprecated
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return state.isFullCube(world, pos) ? 0.2f : 1.0f;
    }

    @Deprecated
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 0;
    }

    @Deprecated
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.collidable ? state.getOutlineShape(world, pos) : VoxelShapes.empty();
    }

    @Deprecated
    public VoxelShape getVisualShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getCollisionShape(state, world, pos, context);
    }

    @Deprecated
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.scheduledTick(state, world, pos, random);
    }

    @Deprecated
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    }

    @Deprecated
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        float f = state.getHardness(world, pos);
        if (f == -1.0f) {
            return 0.0f;
        }
        int i = player.isUsingEffectiveTool(state) ? 30 : 100;
        return player.getBlockBreakingSpeed(state) / f / (float)i;
    }

    @Deprecated
    public void onStacksDropped(BlockState state, ServerWorld arg2, BlockPos pos, ItemStack stack) {
    }

    @Deprecated
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
    }

    @Deprecated
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    @Deprecated
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    }

    @Deprecated
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return 0;
    }

    public final boolean hasBlockEntity() {
        return this instanceof BlockEntityProvider;
    }

    public final Identifier getLootTableId() {
        if (this.lootTableId == null) {
            Identifier lv = Registry.BLOCK.getId(this.asBlock());
            this.lootTableId = new Identifier(lv.getNamespace(), "blocks/" + lv.getPath());
        }
        return this.lootTableId;
    }

    @Deprecated
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
    }

    public abstract Item asItem();

    protected abstract Block asBlock();

    public MaterialColor getDefaultMaterialColor() {
        return (MaterialColor)this.settings.materialColorFactory.apply(this.asBlock().getDefaultState());
    }

    public static interface TypedContextPredicate<A> {
        public boolean test(BlockState var1, BlockView var2, BlockPos var3, A var4);
    }

    public static interface ContextPredicate {
        public boolean test(BlockState var1, BlockView var2, BlockPos var3);
    }

    public static abstract class AbstractBlockState
    extends State<Block, BlockState> {
        private final int luminance;
        private final boolean hasSidedTransparency;
        private final boolean isAir;
        private final Material material;
        private final MaterialColor materialColor;
        private final float hardness;
        private final boolean toolRequired;
        private final boolean opaque;
        private final ContextPredicate solidBlockPredicate;
        private final ContextPredicate suffocationPredicate;
        private final ContextPredicate blockVisionPredicate;
        private final ContextPredicate postProcessPredicate;
        private final ContextPredicate emissiveLightingPredicate;
        @Nullable
        protected ShapeCache shapeCache;

        protected AbstractBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> mapCodec) {
            super(block, propertyMap, mapCodec);
            Settings lv = block.settings;
            this.luminance = lv.luminance.applyAsInt(this.asBlockState());
            this.hasSidedTransparency = block.hasSidedTransparency(this.asBlockState());
            this.isAir = lv.isAir;
            this.material = lv.material;
            this.materialColor = (MaterialColor)lv.materialColorFactory.apply(this.asBlockState());
            this.hardness = lv.hardness;
            this.toolRequired = lv.toolRequired;
            this.opaque = lv.opaque;
            this.solidBlockPredicate = lv.solidBlockPredicate;
            this.suffocationPredicate = lv.suffocationPredicate;
            this.blockVisionPredicate = lv.blockVisionPredicate;
            this.postProcessPredicate = lv.postProcessPredicate;
            this.emissiveLightingPredicate = lv.emissiveLightingPredicate;
        }

        public void initShapeCache() {
            if (!this.getBlock().hasDynamicBounds()) {
                this.shapeCache = new ShapeCache(this.asBlockState());
            }
        }

        public Block getBlock() {
            return (Block)this.owner;
        }

        public Material getMaterial() {
            return this.material;
        }

        public boolean allowsSpawning(BlockView world, BlockPos pos, EntityType<?> type) {
            return this.getBlock().settings.allowsSpawningPredicate.test(this.asBlockState(), world, pos, type);
        }

        public boolean isTranslucent(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.translucent;
            }
            return this.getBlock().isTranslucent(this.asBlockState(), world, pos);
        }

        public int getOpacity(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.lightSubtracted;
            }
            return this.getBlock().getOpacity(this.asBlockState(), world, pos);
        }

        public VoxelShape getCullingFace(BlockView world, BlockPos pos, Direction direction) {
            if (this.shapeCache != null && this.shapeCache.extrudedFaces != null) {
                return this.shapeCache.extrudedFaces[direction.ordinal()];
            }
            return VoxelShapes.extrudeFace(this.getCullingShape(world, pos), direction);
        }

        public VoxelShape getCullingShape(BlockView world, BlockPos pos) {
            return this.getBlock().getCullingShape(this.asBlockState(), world, pos);
        }

        public boolean exceedsCube() {
            return this.shapeCache == null || this.shapeCache.exceedsCube;
        }

        public boolean hasSidedTransparency() {
            return this.hasSidedTransparency;
        }

        public int getLuminance() {
            return this.luminance;
        }

        public boolean isAir() {
            return this.isAir;
        }

        public MaterialColor getTopMaterialColor(BlockView world, BlockPos pos) {
            return this.materialColor;
        }

        public BlockState rotate(BlockRotation rotation) {
            return this.getBlock().rotate(this.asBlockState(), rotation);
        }

        public BlockState mirror(BlockMirror mirror) {
            return this.getBlock().mirror(this.asBlockState(), mirror);
        }

        public BlockRenderType getRenderType() {
            return this.getBlock().getRenderType(this.asBlockState());
        }

        @Environment(value=EnvType.CLIENT)
        public boolean hasEmissiveLighting(BlockView world, BlockPos pos) {
            return this.emissiveLightingPredicate.test(this.asBlockState(), world, pos);
        }

        @Environment(value=EnvType.CLIENT)
        public float getAmbientOcclusionLightLevel(BlockView world, BlockPos pos) {
            return this.getBlock().getAmbientOcclusionLightLevel(this.asBlockState(), world, pos);
        }

        public boolean isSolidBlock(BlockView world, BlockPos pos) {
            return this.solidBlockPredicate.test(this.asBlockState(), world, pos);
        }

        public boolean emitsRedstonePower() {
            return this.getBlock().emitsRedstonePower(this.asBlockState());
        }

        public int getWeakRedstonePower(BlockView world, BlockPos pos, Direction direction) {
            return this.getBlock().getWeakRedstonePower(this.asBlockState(), world, pos, direction);
        }

        public boolean hasComparatorOutput() {
            return this.getBlock().hasComparatorOutput(this.asBlockState());
        }

        public int getComparatorOutput(World world, BlockPos pos) {
            return this.getBlock().getComparatorOutput(this.asBlockState(), world, pos);
        }

        public float getHardness(BlockView world, BlockPos pos) {
            return this.hardness;
        }

        public float calcBlockBreakingDelta(PlayerEntity player, BlockView world, BlockPos pos) {
            return this.getBlock().calcBlockBreakingDelta(this.asBlockState(), player, world, pos);
        }

        public int getStrongRedstonePower(BlockView world, BlockPos pos, Direction direction) {
            return this.getBlock().getStrongRedstonePower(this.asBlockState(), world, pos, direction);
        }

        public PistonBehavior getPistonBehavior() {
            return this.getBlock().getPistonBehavior(this.asBlockState());
        }

        public boolean isOpaqueFullCube(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.fullOpaque;
            }
            BlockState lv = this.asBlockState();
            if (lv.isOpaque()) {
                return Block.isShapeFullCube(lv.getCullingShape(world, pos));
            }
            return false;
        }

        public boolean isOpaque() {
            return this.opaque;
        }

        @Environment(value=EnvType.CLIENT)
        public boolean isSideInvisible(BlockState state, Direction direction) {
            return this.getBlock().isSideInvisible(this.asBlockState(), state, direction);
        }

        public VoxelShape getOutlineShape(BlockView world, BlockPos pos) {
            return this.getOutlineShape(world, pos, ShapeContext.absent());
        }

        public VoxelShape getOutlineShape(BlockView world, BlockPos pos, ShapeContext context) {
            return this.getBlock().getOutlineShape(this.asBlockState(), world, pos, context);
        }

        public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.collisionShape;
            }
            return this.getCollisionShape(world, pos, ShapeContext.absent());
        }

        public VoxelShape getCollisionShape(BlockView world, BlockPos pos, ShapeContext context) {
            return this.getBlock().getCollisionShape(this.asBlockState(), world, pos, context);
        }

        public VoxelShape getSidesShape(BlockView world, BlockPos pos) {
            return this.getBlock().getSidesShape(this.asBlockState(), world, pos);
        }

        public VoxelShape getVisualShape(BlockView world, BlockPos pos, ShapeContext context) {
            return this.getBlock().getVisualShape(this.asBlockState(), world, pos, context);
        }

        public VoxelShape getRayTraceShape(BlockView world, BlockPos pos) {
            return this.getBlock().getRayTraceShape(this.asBlockState(), world, pos);
        }

        public final boolean hasSolidTopSurface(BlockView world, BlockPos pos, Entity entity) {
            return this.hasSolidTopSurface(world, pos, entity, Direction.UP);
        }

        public final boolean hasSolidTopSurface(BlockView world, BlockPos pos, Entity entity, Direction direction) {
            return Block.isFaceFullSquare(this.getCollisionShape(world, pos, ShapeContext.of(entity)), direction);
        }

        public Vec3d getModelOffset(BlockView world, BlockPos pos) {
            OffsetType lv = this.getBlock().getOffsetType();
            if (lv == OffsetType.NONE) {
                return Vec3d.ZERO;
            }
            long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
            return new Vec3d(((double)((float)(l & 0xFL) / 15.0f) - 0.5) * 0.5, lv == OffsetType.XYZ ? ((double)((float)(l >> 4 & 0xFL) / 15.0f) - 1.0) * 0.2 : 0.0, ((double)((float)(l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5);
        }

        public boolean onSyncedBlockEvent(World world, BlockPos pos, int type, int data) {
            return this.getBlock().onSyncedBlockEvent(this.asBlockState(), world, pos, type, data);
        }

        public void neighborUpdate(World world, BlockPos pos, Block block, BlockPos posFrom, boolean notify) {
            this.getBlock().neighborUpdate(this.asBlockState(), world, pos, block, posFrom, notify);
        }

        public final void method_30101(WorldAccess arg, BlockPos arg2, int i) {
            this.updateNeighbors(arg, arg2, i, 512);
        }

        public final void updateNeighbors(WorldAccess world, BlockPos pos, int flags, int j) {
            this.getBlock();
            BlockPos.Mutable lv = new BlockPos.Mutable();
            for (Direction lv2 : FACINGS) {
                lv.set(pos, lv2);
                BlockState lv3 = world.getBlockState(lv);
                BlockState lv4 = lv3.getStateForNeighborUpdate(lv2.getOpposite(), this.asBlockState(), world, lv, pos);
                Block.replace(lv3, lv4, world, lv, flags, j);
            }
        }

        public final void method_30102(WorldAccess arg, BlockPos arg2, int i) {
            this.prepare(arg, arg2, i, 512);
        }

        public void prepare(WorldAccess world, BlockPos pos, int flags, int j) {
            this.getBlock().prepare(this.asBlockState(), world, pos, flags, j);
        }

        public void onBlockAdded(World world, BlockPos pos, BlockState state, boolean notify) {
            this.getBlock().onBlockAdded(this.asBlockState(), world, pos, state, notify);
        }

        public void onStateReplaced(World world, BlockPos pos, BlockState state, boolean moved) {
            this.getBlock().onStateReplaced(this.asBlockState(), world, pos, state, moved);
        }

        public void scheduledTick(ServerWorld world, BlockPos pos, Random random) {
            this.getBlock().scheduledTick(this.asBlockState(), world, pos, random);
        }

        public void randomTick(ServerWorld world, BlockPos pos, Random random) {
            this.getBlock().randomTick(this.asBlockState(), world, pos, random);
        }

        public void onEntityCollision(World world, BlockPos pos, Entity entity) {
            this.getBlock().onEntityCollision(this.asBlockState(), world, pos, entity);
        }

        public void onStacksDropped(ServerWorld arg, BlockPos pos, ItemStack stack) {
            this.getBlock().onStacksDropped(this.asBlockState(), arg, pos, stack);
        }

        public List<ItemStack> getDroppedStacks(LootContext.Builder builder) {
            return this.getBlock().getDroppedStacks(this.asBlockState(), builder);
        }

        public ActionResult onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
            return this.getBlock().onUse(this.asBlockState(), world, hit.getBlockPos(), player, hand, hit);
        }

        public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
            this.getBlock().onBlockBreakStart(this.asBlockState(), world, pos, player);
        }

        public boolean shouldSuffocate(BlockView world, BlockPos pos) {
            return this.suffocationPredicate.test(this.asBlockState(), world, pos);
        }

        @Environment(value=EnvType.CLIENT)
        public boolean shouldBlockVision(BlockView world, BlockPos pos) {
            return this.blockVisionPredicate.test(this.asBlockState(), world, pos);
        }

        public BlockState getStateForNeighborUpdate(Direction direction, BlockState state, WorldAccess world, BlockPos pos, BlockPos fromPos) {
            return this.getBlock().getStateForNeighborUpdate(this.asBlockState(), direction, state, world, pos, fromPos);
        }

        public boolean canPathfindThrough(BlockView world, BlockPos pos, NavigationType type) {
            return this.getBlock().canPathfindThrough(this.asBlockState(), world, pos, type);
        }

        public boolean canReplace(ItemPlacementContext context) {
            return this.getBlock().canReplace(this.asBlockState(), context);
        }

        public boolean canBucketPlace(Fluid fluid) {
            return this.getBlock().canBucketPlace(this.asBlockState(), fluid);
        }

        public boolean canPlaceAt(WorldView world, BlockPos pos) {
            return this.getBlock().canPlaceAt(this.asBlockState(), world, pos);
        }

        public boolean shouldPostProcess(BlockView world, BlockPos pos) {
            return this.postProcessPredicate.test(this.asBlockState(), world, pos);
        }

        @Nullable
        public NamedScreenHandlerFactory createScreenHandlerFactory(World world, BlockPos pos) {
            return this.getBlock().createScreenHandlerFactory(this.asBlockState(), world, pos);
        }

        public boolean isIn(Tag<Block> tag) {
            return this.getBlock().isIn(tag);
        }

        public boolean method_27851(Tag<Block> arg, Predicate<AbstractBlockState> predicate) {
            return this.getBlock().isIn(arg) && predicate.test(this);
        }

        public boolean isOf(Block block) {
            return this.getBlock().is(block);
        }

        public FluidState getFluidState() {
            return this.getBlock().getFluidState(this.asBlockState());
        }

        public boolean hasRandomTicks() {
            return this.getBlock().hasRandomTicks(this.asBlockState());
        }

        @Environment(value=EnvType.CLIENT)
        public long getRenderingSeed(BlockPos pos) {
            return this.getBlock().getRenderingSeed(this.asBlockState(), pos);
        }

        public BlockSoundGroup getSoundGroup() {
            return this.getBlock().getSoundGroup(this.asBlockState());
        }

        public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
            this.getBlock().onProjectileHit(world, state, hit, projectile);
        }

        public boolean isSideSolidFullSquare(BlockView world, BlockPos pos, Direction direction) {
            return this.isSideSolid(world, pos, direction, SideShapeType.FULL);
        }

        public boolean isSideSolid(BlockView world, BlockPos pos, Direction direction, SideShapeType shapeType) {
            if (this.shapeCache != null) {
                return this.shapeCache.isSideSolid(direction, shapeType);
            }
            return shapeType.matches(this.asBlockState(), world, pos, direction);
        }

        public boolean isFullCube(BlockView world, BlockPos pos) {
            if (this.shapeCache != null) {
                return this.shapeCache.isFullCube;
            }
            return Block.isShapeFullCube(this.getCollisionShape(world, pos));
        }

        protected abstract BlockState asBlockState();

        public boolean isToolRequired() {
            return this.toolRequired;
        }

        static final class ShapeCache {
            private static final Direction[] DIRECTIONS = Direction.values();
            private static final int SHAPE_TYPE_LENGTH = SideShapeType.values().length;
            protected final boolean fullOpaque;
            private final boolean translucent;
            private final int lightSubtracted;
            @Nullable
            private final VoxelShape[] extrudedFaces;
            protected final VoxelShape collisionShape;
            protected final boolean exceedsCube;
            private final boolean[] solidSides;
            protected final boolean isFullCube;

            private ShapeCache(BlockState state) {
                Block lv = state.getBlock();
                this.fullOpaque = state.isOpaqueFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                this.translucent = lv.isTranslucent(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                this.lightSubtracted = lv.getOpacity(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                if (!state.isOpaque()) {
                    this.extrudedFaces = null;
                } else {
                    this.extrudedFaces = new VoxelShape[DIRECTIONS.length];
                    VoxelShape lv2 = lv.getCullingShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                    Direction[] arrdirection = DIRECTIONS;
                    int n = arrdirection.length;
                    for (int i = 0; i < n; ++i) {
                        Direction lv3 = arrdirection[i];
                        this.extrudedFaces[lv3.ordinal()] = VoxelShapes.extrudeFace(lv2, lv3);
                    }
                }
                this.collisionShape = lv.getCollisionShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, ShapeContext.absent());
                this.exceedsCube = Arrays.stream(Direction.Axis.values()).anyMatch(arg -> this.collisionShape.getMin((Direction.Axis)arg) < 0.0 || this.collisionShape.getMax((Direction.Axis)arg) > 1.0);
                this.solidSides = new boolean[DIRECTIONS.length * SHAPE_TYPE_LENGTH];
                for (Direction lv4 : DIRECTIONS) {
                    for (SideShapeType lv5 : SideShapeType.values()) {
                        this.solidSides[ShapeCache.indexSolidSide((Direction)lv4, (SideShapeType)lv5)] = lv5.matches(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, lv4);
                    }
                }
                this.isFullCube = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
            }

            public boolean isSideSolid(Direction direction, SideShapeType shapeType) {
                return this.solidSides[ShapeCache.indexSolidSide(direction, shapeType)];
            }

            private static int indexSolidSide(Direction direction, SideShapeType shapeType) {
                return direction.ordinal() * SHAPE_TYPE_LENGTH + shapeType.ordinal();
            }
        }
    }

    public static class Settings {
        private Material material;
        private Function<BlockState, MaterialColor> materialColorFactory;
        private boolean collidable = true;
        private BlockSoundGroup soundGroup = BlockSoundGroup.STONE;
        private ToIntFunction<BlockState> luminance = state -> 0;
        private float resistance;
        private float hardness;
        private boolean toolRequired;
        private boolean randomTicks;
        private float slipperiness = 0.6f;
        private float velocityMultiplier = 1.0f;
        private float jumpVelocityMultiplier = 1.0f;
        private Identifier lootTableId;
        private boolean opaque = true;
        private boolean isAir;
        private TypedContextPredicate<EntityType<?>> allowsSpawningPredicate = (state, world, pos, type) -> state.isSideSolidFullSquare(world, pos, Direction.UP) && state.getLuminance() < 14;
        private ContextPredicate solidBlockPredicate = (state, world, pos) -> state.getMaterial().blocksLight() && state.isFullCube(world, pos);
        private ContextPredicate suffocationPredicate;
        private ContextPredicate blockVisionPredicate = this.suffocationPredicate = (state, world, pos) -> this.material.blocksMovement() && state.isFullCube(world, pos);
        private ContextPredicate postProcessPredicate = (state, world, pos) -> false;
        private ContextPredicate emissiveLightingPredicate = (state, world, pos) -> false;
        private boolean dynamicBounds;

        private Settings(Material material, MaterialColor materialColorFactory) {
            this(material, (BlockState state) -> materialColorFactory);
        }

        private Settings(Material material, Function<BlockState, MaterialColor> materialColorFactory) {
            this.material = material;
            this.materialColorFactory = materialColorFactory;
        }

        public static Settings of(Material material) {
            return Settings.of(material, material.getColor());
        }

        public static Settings of(Material material, DyeColor color) {
            return Settings.of(material, color.getMaterialColor());
        }

        public static Settings of(Material material, MaterialColor color) {
            return new Settings(material, color);
        }

        public static Settings of(Material material, Function<BlockState, MaterialColor> materialColor) {
            return new Settings(material, materialColor);
        }

        public static Settings copy(AbstractBlock block) {
            Settings lv = new Settings(block.material, block.settings.materialColorFactory);
            lv.material = block.settings.material;
            lv.hardness = block.settings.hardness;
            lv.resistance = block.settings.resistance;
            lv.collidable = block.settings.collidable;
            lv.randomTicks = block.settings.randomTicks;
            lv.luminance = block.settings.luminance;
            lv.materialColorFactory = block.settings.materialColorFactory;
            lv.soundGroup = block.settings.soundGroup;
            lv.slipperiness = block.settings.slipperiness;
            lv.velocityMultiplier = block.settings.velocityMultiplier;
            lv.dynamicBounds = block.settings.dynamicBounds;
            lv.opaque = block.settings.opaque;
            lv.isAir = block.settings.isAir;
            lv.toolRequired = block.settings.toolRequired;
            return lv;
        }

        public Settings noCollision() {
            this.collidable = false;
            this.opaque = false;
            return this;
        }

        public Settings nonOpaque() {
            this.opaque = false;
            return this;
        }

        public Settings slipperiness(float slipperiness) {
            this.slipperiness = slipperiness;
            return this;
        }

        public Settings velocityMultiplier(float velocityMultiplier) {
            this.velocityMultiplier = velocityMultiplier;
            return this;
        }

        public Settings jumpVelocityMultiplier(float jumpVelocityMultiplier) {
            this.jumpVelocityMultiplier = jumpVelocityMultiplier;
            return this;
        }

        public Settings sounds(BlockSoundGroup soundGroup) {
            this.soundGroup = soundGroup;
            return this;
        }

        public Settings lightLevel(ToIntFunction<BlockState> levelFunction) {
            this.luminance = levelFunction;
            return this;
        }

        public Settings strength(float hardness, float resistance) {
            this.hardness = hardness;
            this.resistance = Math.max(0.0f, resistance);
            return this;
        }

        public Settings breakInstantly() {
            return this.strength(0.0f);
        }

        public Settings strength(float strength) {
            this.strength(strength, strength);
            return this;
        }

        public Settings ticksRandomly() {
            this.randomTicks = true;
            return this;
        }

        public Settings dynamicBounds() {
            this.dynamicBounds = true;
            return this;
        }

        public Settings dropsNothing() {
            this.lootTableId = LootTables.EMPTY;
            return this;
        }

        public Settings dropsLike(Block source) {
            this.lootTableId = source.getLootTableId();
            return this;
        }

        public Settings air() {
            this.isAir = true;
            return this;
        }

        public Settings allowsSpawning(TypedContextPredicate<EntityType<?>> predicate) {
            this.allowsSpawningPredicate = predicate;
            return this;
        }

        public Settings solidBlock(ContextPredicate predicate) {
            this.solidBlockPredicate = predicate;
            return this;
        }

        public Settings suffocates(ContextPredicate predicate) {
            this.suffocationPredicate = predicate;
            return this;
        }

        public Settings blockVision(ContextPredicate predicate) {
            this.blockVisionPredicate = predicate;
            return this;
        }

        public Settings postProcess(ContextPredicate predicate) {
            this.postProcessPredicate = predicate;
            return this;
        }

        public Settings emissiveLighting(ContextPredicate predicate) {
            this.emissiveLightingPredicate = predicate;
            return this;
        }

        public Settings requiresTool() {
            this.toolRequired = true;
            return this;
        }
    }

    public static enum OffsetType {
        NONE,
        XZ,
        XYZ;

    }
}

