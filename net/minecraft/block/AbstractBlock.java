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

    public AbstractBlock(Settings arg) {
        this.material = arg.material;
        this.collidable = arg.collidable;
        this.lootTableId = arg.lootTableId;
        this.resistance = arg.resistance;
        this.randomTicks = arg.randomTicks;
        this.soundGroup = arg.soundGroup;
        this.slipperiness = arg.slipperiness;
        this.velocityMultiplier = arg.velocityMultiplier;
        this.jumpVelocityMultiplier = arg.jumpVelocityMultiplier;
        this.dynamicBounds = arg.dynamicBounds;
        this.settings = arg;
    }

    @Deprecated
    public void prepare(BlockState arg, WorldAccess arg2, BlockPos arg3, int i) {
    }

    @Deprecated
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        switch (arg4) {
            case LAND: {
                return !arg.isFullCube(arg2, arg3);
            }
            case WATER: {
                return arg2.getFluidState(arg3).matches(FluidTags.WATER);
            }
            case AIR: {
                return !arg.isFullCube(arg2, arg3);
            }
        }
        return false;
    }

    @Deprecated
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        return arg;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public boolean isSideInvisible(BlockState arg, BlockState arg2, Direction arg3) {
        return false;
    }

    @Deprecated
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        DebugInfoSender.sendNeighborUpdate(arg2, arg3);
    }

    @Deprecated
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
    }

    @Deprecated
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (this.hasBlockEntity() && !arg.isOf(arg4.getBlock())) {
            arg2.removeBlockEntity(arg3);
        }
    }

    @Deprecated
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        return ActionResult.PASS;
    }

    @Deprecated
    public boolean onSyncedBlockEvent(BlockState arg, World arg2, BlockPos arg3, int i, int j) {
        return false;
    }

    @Deprecated
    public BlockRenderType getRenderType(BlockState arg) {
        return BlockRenderType.MODEL;
    }

    @Deprecated
    public boolean hasSidedTransparency(BlockState arg) {
        return false;
    }

    @Deprecated
    public boolean emitsRedstonePower(BlockState arg) {
        return false;
    }

    @Deprecated
    public PistonBehavior getPistonBehavior(BlockState arg) {
        return this.material.getPistonBehavior();
    }

    @Deprecated
    public FluidState getFluidState(BlockState arg) {
        return Fluids.EMPTY.getDefaultState();
    }

    @Deprecated
    public boolean hasComparatorOutput(BlockState arg) {
        return false;
    }

    public OffsetType getOffsetType() {
        return OffsetType.NONE;
    }

    @Deprecated
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        return arg;
    }

    @Deprecated
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        return arg;
    }

    @Deprecated
    public boolean canReplace(BlockState arg, ItemPlacementContext arg2) {
        return this.material.isReplaceable() && (arg2.getStack().isEmpty() || arg2.getStack().getItem() != this.asItem());
    }

    @Deprecated
    public boolean canBucketPlace(BlockState arg, Fluid arg2) {
        return this.material.isReplaceable() || !this.material.isSolid();
    }

    @Deprecated
    public List<ItemStack> getDroppedStacks(BlockState arg, LootContext.Builder arg2) {
        Identifier lv = this.getLootTableId();
        if (lv == LootTables.EMPTY) {
            return Collections.emptyList();
        }
        LootContext lv2 = arg2.parameter(LootContextParameters.BLOCK_STATE, arg).build(LootContextTypes.BLOCK);
        ServerWorld lv3 = lv2.getWorld();
        LootTable lv4 = lv3.getServer().getLootManager().getTable(lv);
        return lv4.generateLoot(lv2);
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public long getRenderingSeed(BlockState arg, BlockPos arg2) {
        return MathHelper.hashCode(arg2);
    }

    @Deprecated
    public VoxelShape getCullingShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.getOutlineShape(arg2, arg3);
    }

    @Deprecated
    public VoxelShape getSidesShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return this.getCollisionShape(arg, arg2, arg3, ShapeContext.absent());
    }

    @Deprecated
    public VoxelShape getRayTraceShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return VoxelShapes.empty();
    }

    @Deprecated
    public int getOpacity(BlockState arg, BlockView arg2, BlockPos arg3) {
        if (arg.isOpaqueFullCube(arg2, arg3)) {
            return arg2.getMaxLightLevel();
        }
        return arg.isTranslucent(arg2, arg3) ? 0 : 1;
    }

    @Nullable
    @Deprecated
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState arg, World arg2, BlockPos arg3) {
        return null;
    }

    @Deprecated
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        return true;
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState arg, BlockView arg2, BlockPos arg3) {
        return arg.isFullCube(arg2, arg3) ? 0.2f : 1.0f;
    }

    @Deprecated
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        return 0;
    }

    @Deprecated
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return VoxelShapes.fullCube();
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.collidable ? arg.getOutlineShape(arg2, arg3) : VoxelShapes.empty();
    }

    @Deprecated
    public VoxelShape getVisualShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.getCollisionShape(arg, arg2, arg3, arg4);
    }

    @Deprecated
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        this.scheduledTick(arg, arg2, arg3, random);
    }

    @Deprecated
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
    }

    @Deprecated
    public float calcBlockBreakingDelta(BlockState arg, PlayerEntity arg2, BlockView arg3, BlockPos arg4) {
        float f = arg.getHardness(arg3, arg4);
        if (f == -1.0f) {
            return 0.0f;
        }
        int i = arg2.isUsingEffectiveTool(arg) ? 30 : 100;
        return arg2.getBlockBreakingSpeed(arg) / f / (float)i;
    }

    @Deprecated
    public void onStacksDropped(BlockState arg, World arg2, BlockPos arg3, ItemStack arg4) {
    }

    @Deprecated
    public void onBlockBreakStart(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4) {
    }

    @Deprecated
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return 0;
    }

    @Deprecated
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
    }

    @Deprecated
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
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
    public void onProjectileHit(World arg, BlockState arg2, BlockHitResult arg3, ProjectileEntity arg4) {
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

        protected AbstractBlockState(Block arg, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
            super(arg, immutableMap, mapCodec);
            Settings lv = arg.settings;
            this.luminance = lv.luminance.applyAsInt(this.asBlockState());
            this.hasSidedTransparency = arg.hasSidedTransparency(this.asBlockState());
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

        public boolean allowsSpawning(BlockView arg, BlockPos arg2, EntityType<?> arg3) {
            return this.getBlock().settings.allowsSpawningPredicate.test(this.asBlockState(), arg, arg2, arg3);
        }

        public boolean isTranslucent(BlockView arg, BlockPos arg2) {
            if (this.shapeCache != null) {
                return this.shapeCache.translucent;
            }
            return this.getBlock().isTranslucent(this.asBlockState(), arg, arg2);
        }

        public int getOpacity(BlockView arg, BlockPos arg2) {
            if (this.shapeCache != null) {
                return this.shapeCache.lightSubtracted;
            }
            return this.getBlock().getOpacity(this.asBlockState(), arg, arg2);
        }

        public VoxelShape getCullingFace(BlockView arg, BlockPos arg2, Direction arg3) {
            if (this.shapeCache != null && this.shapeCache.extrudedFaces != null) {
                return this.shapeCache.extrudedFaces[arg3.ordinal()];
            }
            return VoxelShapes.extrudeFace(this.getCullingShape(arg, arg2), arg3);
        }

        public VoxelShape getCullingShape(BlockView arg, BlockPos arg2) {
            return this.getBlock().getCullingShape(this.asBlockState(), arg, arg2);
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

        public MaterialColor getTopMaterialColor(BlockView arg, BlockPos arg2) {
            return this.materialColor;
        }

        public BlockState rotate(BlockRotation arg) {
            return this.getBlock().rotate(this.asBlockState(), arg);
        }

        public BlockState mirror(BlockMirror arg) {
            return this.getBlock().mirror(this.asBlockState(), arg);
        }

        public BlockRenderType getRenderType() {
            return this.getBlock().getRenderType(this.asBlockState());
        }

        @Environment(value=EnvType.CLIENT)
        public boolean hasEmissiveLighting(BlockView arg, BlockPos arg2) {
            return this.emissiveLightingPredicate.test(this.asBlockState(), arg, arg2);
        }

        @Environment(value=EnvType.CLIENT)
        public float getAmbientOcclusionLightLevel(BlockView arg, BlockPos arg2) {
            return this.getBlock().getAmbientOcclusionLightLevel(this.asBlockState(), arg, arg2);
        }

        public boolean isSolidBlock(BlockView arg, BlockPos arg2) {
            return this.solidBlockPredicate.test(this.asBlockState(), arg, arg2);
        }

        public boolean emitsRedstonePower() {
            return this.getBlock().emitsRedstonePower(this.asBlockState());
        }

        public int getWeakRedstonePower(BlockView arg, BlockPos arg2, Direction arg3) {
            return this.getBlock().getWeakRedstonePower(this.asBlockState(), arg, arg2, arg3);
        }

        public boolean hasComparatorOutput() {
            return this.getBlock().hasComparatorOutput(this.asBlockState());
        }

        public int getComparatorOutput(World arg, BlockPos arg2) {
            return this.getBlock().getComparatorOutput(this.asBlockState(), arg, arg2);
        }

        public float getHardness(BlockView arg, BlockPos arg2) {
            return this.hardness;
        }

        public float calcBlockBreakingDelta(PlayerEntity arg, BlockView arg2, BlockPos arg3) {
            return this.getBlock().calcBlockBreakingDelta(this.asBlockState(), arg, arg2, arg3);
        }

        public int getStrongRedstonePower(BlockView arg, BlockPos arg2, Direction arg3) {
            return this.getBlock().getStrongRedstonePower(this.asBlockState(), arg, arg2, arg3);
        }

        public PistonBehavior getPistonBehavior() {
            return this.getBlock().getPistonBehavior(this.asBlockState());
        }

        public boolean isOpaqueFullCube(BlockView arg, BlockPos arg2) {
            if (this.shapeCache != null) {
                return this.shapeCache.fullOpaque;
            }
            BlockState lv = this.asBlockState();
            if (lv.isOpaque()) {
                return Block.isShapeFullCube(lv.getCullingShape(arg, arg2));
            }
            return false;
        }

        public boolean isOpaque() {
            return this.opaque;
        }

        @Environment(value=EnvType.CLIENT)
        public boolean isSideInvisible(BlockState arg, Direction arg2) {
            return this.getBlock().isSideInvisible(this.asBlockState(), arg, arg2);
        }

        public VoxelShape getOutlineShape(BlockView arg, BlockPos arg2) {
            return this.getOutlineShape(arg, arg2, ShapeContext.absent());
        }

        public VoxelShape getOutlineShape(BlockView arg, BlockPos arg2, ShapeContext arg3) {
            return this.getBlock().getOutlineShape(this.asBlockState(), arg, arg2, arg3);
        }

        public VoxelShape getCollisionShape(BlockView arg, BlockPos arg2) {
            if (this.shapeCache != null) {
                return this.shapeCache.collisionShape;
            }
            return this.getCollisionShape(arg, arg2, ShapeContext.absent());
        }

        public VoxelShape getCollisionShape(BlockView arg, BlockPos arg2, ShapeContext arg3) {
            return this.getBlock().getCollisionShape(this.asBlockState(), arg, arg2, arg3);
        }

        public VoxelShape getSidesShape(BlockView arg, BlockPos arg2) {
            return this.getBlock().getSidesShape(this.asBlockState(), arg, arg2);
        }

        public VoxelShape getVisualShape(BlockView arg, BlockPos arg2, ShapeContext arg3) {
            return this.getBlock().getVisualShape(this.asBlockState(), arg, arg2, arg3);
        }

        public VoxelShape getRayTraceShape(BlockView arg, BlockPos arg2) {
            return this.getBlock().getRayTraceShape(this.asBlockState(), arg, arg2);
        }

        public final boolean hasSolidTopSurface(BlockView arg, BlockPos arg2, Entity arg3) {
            return this.hasSolidTopSurface(arg, arg2, arg3, Direction.UP);
        }

        public final boolean hasSolidTopSurface(BlockView arg, BlockPos arg2, Entity arg3, Direction arg4) {
            return Block.isFaceFullSquare(this.getCollisionShape(arg, arg2, ShapeContext.of(arg3)), arg4);
        }

        public Vec3d getModelOffset(BlockView arg, BlockPos arg2) {
            OffsetType lv = this.getBlock().getOffsetType();
            if (lv == OffsetType.NONE) {
                return Vec3d.ZERO;
            }
            long l = MathHelper.hashCode(arg2.getX(), 0, arg2.getZ());
            return new Vec3d(((double)((float)(l & 0xFL) / 15.0f) - 0.5) * 0.5, lv == OffsetType.XYZ ? ((double)((float)(l >> 4 & 0xFL) / 15.0f) - 1.0) * 0.2 : 0.0, ((double)((float)(l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5);
        }

        public boolean onSyncedBlockEvent(World arg, BlockPos arg2, int i, int j) {
            return this.getBlock().onSyncedBlockEvent(this.asBlockState(), arg, arg2, i, j);
        }

        public void neighborUpdate(World arg, BlockPos arg2, Block arg3, BlockPos arg4, boolean bl) {
            this.getBlock().neighborUpdate(this.asBlockState(), arg, arg2, arg3, arg4, bl);
        }

        public final void updateNeighbors(WorldAccess arg, BlockPos arg2, int i) {
            this.getBlock();
            BlockPos.Mutable lv = new BlockPos.Mutable();
            for (Direction lv2 : FACINGS) {
                lv.set(arg2, lv2);
                BlockState lv3 = arg.getBlockState(lv);
                BlockState lv4 = lv3.getStateForNeighborUpdate(lv2.getOpposite(), this.asBlockState(), arg, lv, arg2);
                Block.replaceBlock(lv3, lv4, arg, lv, i);
            }
        }

        public void prepare(WorldAccess arg, BlockPos arg2, int i) {
            this.getBlock().prepare(this.asBlockState(), arg, arg2, i);
        }

        public void onBlockAdded(World arg, BlockPos arg2, BlockState arg3, boolean bl) {
            this.getBlock().onBlockAdded(this.asBlockState(), arg, arg2, arg3, bl);
        }

        public void onStateReplaced(World arg, BlockPos arg2, BlockState arg3, boolean bl) {
            this.getBlock().onStateReplaced(this.asBlockState(), arg, arg2, arg3, bl);
        }

        public void scheduledTick(ServerWorld arg, BlockPos arg2, Random random) {
            this.getBlock().scheduledTick(this.asBlockState(), arg, arg2, random);
        }

        public void randomTick(ServerWorld arg, BlockPos arg2, Random random) {
            this.getBlock().randomTick(this.asBlockState(), arg, arg2, random);
        }

        public void onEntityCollision(World arg, BlockPos arg2, Entity arg3) {
            this.getBlock().onEntityCollision(this.asBlockState(), arg, arg2, arg3);
        }

        public void onStacksDropped(World arg, BlockPos arg2, ItemStack arg3) {
            this.getBlock().onStacksDropped(this.asBlockState(), arg, arg2, arg3);
        }

        public List<ItemStack> getDroppedStacks(LootContext.Builder arg) {
            return this.getBlock().getDroppedStacks(this.asBlockState(), arg);
        }

        public ActionResult onUse(World arg, PlayerEntity arg2, Hand arg3, BlockHitResult arg4) {
            return this.getBlock().onUse(this.asBlockState(), arg, arg4.getBlockPos(), arg2, arg3, arg4);
        }

        public void onBlockBreakStart(World arg, BlockPos arg2, PlayerEntity arg3) {
            this.getBlock().onBlockBreakStart(this.asBlockState(), arg, arg2, arg3);
        }

        public boolean shouldSuffocate(BlockView arg, BlockPos arg2) {
            return this.suffocationPredicate.test(this.asBlockState(), arg, arg2);
        }

        @Environment(value=EnvType.CLIENT)
        public boolean shouldBlockVision(BlockView arg, BlockPos arg2) {
            return this.blockVisionPredicate.test(this.asBlockState(), arg, arg2);
        }

        public BlockState getStateForNeighborUpdate(Direction arg, BlockState arg2, WorldAccess arg3, BlockPos arg4, BlockPos arg5) {
            return this.getBlock().getStateForNeighborUpdate(this.asBlockState(), arg, arg2, arg3, arg4, arg5);
        }

        public boolean canPathfindThrough(BlockView arg, BlockPos arg2, NavigationType arg3) {
            return this.getBlock().canPathfindThrough(this.asBlockState(), arg, arg2, arg3);
        }

        public boolean canReplace(ItemPlacementContext arg) {
            return this.getBlock().canReplace(this.asBlockState(), arg);
        }

        public boolean canBucketPlace(Fluid arg) {
            return this.getBlock().canBucketPlace(this.asBlockState(), arg);
        }

        public boolean canPlaceAt(WorldView arg, BlockPos arg2) {
            return this.getBlock().canPlaceAt(this.asBlockState(), arg, arg2);
        }

        public boolean shouldPostProcess(BlockView arg, BlockPos arg2) {
            return this.postProcessPredicate.test(this.asBlockState(), arg, arg2);
        }

        @Nullable
        public NamedScreenHandlerFactory createScreenHandlerFactory(World arg, BlockPos arg2) {
            return this.getBlock().createScreenHandlerFactory(this.asBlockState(), arg, arg2);
        }

        public boolean isIn(Tag<Block> arg) {
            return this.getBlock().isIn(arg);
        }

        public boolean method_27851(Tag<Block> arg, Predicate<AbstractBlockState> predicate) {
            return this.getBlock().isIn(arg) && predicate.test(this);
        }

        public boolean isOf(Block arg) {
            return this.getBlock().is(arg);
        }

        public FluidState getFluidState() {
            return this.getBlock().getFluidState(this.asBlockState());
        }

        public boolean hasRandomTicks() {
            return this.getBlock().hasRandomTicks(this.asBlockState());
        }

        @Environment(value=EnvType.CLIENT)
        public long getRenderingSeed(BlockPos arg) {
            return this.getBlock().getRenderingSeed(this.asBlockState(), arg);
        }

        public BlockSoundGroup getSoundGroup() {
            return this.getBlock().getSoundGroup(this.asBlockState());
        }

        public void onProjectileHit(World arg, BlockState arg2, BlockHitResult arg3, ProjectileEntity arg4) {
            this.getBlock().onProjectileHit(arg, arg2, arg3, arg4);
        }

        public boolean isSideSolidFullSquare(BlockView arg, BlockPos arg2, Direction arg3) {
            if (this.shapeCache != null) {
                return this.shapeCache.solidFullSquare[arg3.ordinal()];
            }
            return Block.isSideSolidFullSquare(this.asBlockState(), arg, arg2, arg3);
        }

        public boolean isFullCube(BlockView arg, BlockPos arg2) {
            if (this.shapeCache != null) {
                return this.shapeCache.isFullCube;
            }
            return Block.isShapeFullCube(this.getCollisionShape(arg, arg2));
        }

        protected abstract BlockState asBlockState();

        public boolean isToolRequired() {
            return this.toolRequired;
        }

        static final class ShapeCache {
            private static final Direction[] DIRECTIONS = Direction.values();
            protected final boolean fullOpaque;
            private final boolean translucent;
            private final int lightSubtracted;
            @Nullable
            private final VoxelShape[] extrudedFaces;
            protected final VoxelShape collisionShape;
            protected final boolean exceedsCube;
            protected final boolean[] solidFullSquare;
            protected final boolean isFullCube;

            private ShapeCache(BlockState arg2) {
                Block lv = arg2.getBlock();
                this.fullOpaque = arg2.isOpaqueFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                this.translucent = lv.isTranslucent(arg2, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                this.lightSubtracted = lv.getOpacity(arg2, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                if (!arg2.isOpaque()) {
                    this.extrudedFaces = null;
                } else {
                    this.extrudedFaces = new VoxelShape[DIRECTIONS.length];
                    VoxelShape lv2 = lv.getCullingShape(arg2, EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
                    Direction[] arrdirection = DIRECTIONS;
                    int n = arrdirection.length;
                    for (int i = 0; i < n; ++i) {
                        Direction lv3 = arrdirection[i];
                        this.extrudedFaces[lv3.ordinal()] = VoxelShapes.extrudeFace(lv2, lv3);
                    }
                }
                this.collisionShape = lv.getCollisionShape(arg2, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, ShapeContext.absent());
                this.exceedsCube = Arrays.stream(Direction.Axis.values()).anyMatch(arg -> this.collisionShape.getMin((Direction.Axis)arg) < 0.0 || this.collisionShape.getMax((Direction.Axis)arg) > 1.0);
                this.solidFullSquare = new boolean[6];
                for (Direction lv4 : DIRECTIONS) {
                    this.solidFullSquare[lv4.ordinal()] = Block.isSideSolidFullSquare(arg2, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, lv4);
                }
                this.isFullCube = Block.isShapeFullCube(arg2.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
            }
        }
    }

    public static class Settings {
        private Material material;
        private Function<BlockState, MaterialColor> materialColorFactory;
        private boolean collidable = true;
        private BlockSoundGroup soundGroup = BlockSoundGroup.STONE;
        private ToIntFunction<BlockState> luminance = arg -> 0;
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
        private TypedContextPredicate<EntityType<?>> allowsSpawningPredicate = (arg, arg2, arg3, arg4) -> arg.isSideSolidFullSquare(arg2, arg3, Direction.UP) && arg.getLuminance() < 14;
        private ContextPredicate solidBlockPredicate = (arg, arg2, arg3) -> arg.getMaterial().blocksLight() && arg.isFullCube(arg2, arg3);
        private ContextPredicate suffocationPredicate;
        private ContextPredicate blockVisionPredicate = this.suffocationPredicate = (arg, arg2, arg3) -> this.material.blocksMovement() && arg.isFullCube(arg2, arg3);
        private ContextPredicate postProcessPredicate = (arg, arg2, arg3) -> false;
        private ContextPredicate emissiveLightingPredicate = (arg, arg2, arg3) -> false;
        private boolean dynamicBounds;

        private Settings(Material arg, MaterialColor arg22) {
            this(arg, (BlockState arg2) -> arg22);
        }

        private Settings(Material arg5, Function<BlockState, MaterialColor> function) {
            this.material = arg5;
            this.materialColorFactory = function;
        }

        public static Settings of(Material arg) {
            return Settings.of(arg, arg.getColor());
        }

        public static Settings of(Material arg, DyeColor arg2) {
            return Settings.of(arg, arg2.getMaterialColor());
        }

        public static Settings of(Material arg, MaterialColor arg2) {
            return new Settings(arg, arg2);
        }

        public static Settings of(Material arg, Function<BlockState, MaterialColor> function) {
            return new Settings(arg, function);
        }

        public static Settings copy(AbstractBlock arg) {
            Settings lv = new Settings(arg.material, arg.settings.materialColorFactory);
            lv.material = arg.settings.material;
            lv.hardness = arg.settings.hardness;
            lv.resistance = arg.settings.resistance;
            lv.collidable = arg.settings.collidable;
            lv.randomTicks = arg.settings.randomTicks;
            lv.luminance = arg.settings.luminance;
            lv.materialColorFactory = arg.settings.materialColorFactory;
            lv.soundGroup = arg.settings.soundGroup;
            lv.slipperiness = arg.settings.slipperiness;
            lv.velocityMultiplier = arg.settings.velocityMultiplier;
            lv.dynamicBounds = arg.settings.dynamicBounds;
            lv.opaque = arg.settings.opaque;
            lv.isAir = arg.settings.isAir;
            lv.toolRequired = arg.settings.toolRequired;
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

        public Settings slipperiness(float f) {
            this.slipperiness = f;
            return this;
        }

        public Settings velocityMultiplier(float f) {
            this.velocityMultiplier = f;
            return this;
        }

        public Settings jumpVelocityMultiplier(float f) {
            this.jumpVelocityMultiplier = f;
            return this;
        }

        public Settings sounds(BlockSoundGroup arg) {
            this.soundGroup = arg;
            return this;
        }

        public Settings lightLevel(ToIntFunction<BlockState> toIntFunction) {
            this.luminance = toIntFunction;
            return this;
        }

        public Settings strength(float f, float g) {
            this.hardness = f;
            this.resistance = Math.max(0.0f, g);
            return this;
        }

        public Settings breakInstantly() {
            return this.strength(0.0f);
        }

        public Settings strength(float f) {
            this.strength(f, f);
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

        public Settings dropsLike(Block arg) {
            this.lootTableId = arg.getLootTableId();
            return this;
        }

        public Settings air() {
            this.isAir = true;
            return this;
        }

        public Settings allowsSpawning(TypedContextPredicate<EntityType<?>> arg) {
            this.allowsSpawningPredicate = arg;
            return this;
        }

        public Settings solidBlock(ContextPredicate arg) {
            this.solidBlockPredicate = arg;
            return this;
        }

        public Settings suffocates(ContextPredicate arg) {
            this.suffocationPredicate = arg;
            return this;
        }

        public Settings blockVision(ContextPredicate arg) {
            this.blockVisionPredicate = arg;
            return this;
        }

        public Settings postProcess(ContextPredicate arg) {
            this.postProcessPredicate = arg;
            return this;
        }

        public Settings emissiveLighting(ContextPredicate arg) {
            this.emissiveLightingPredicate = arg;
            return this;
        }

        public Settings method_29292() {
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

