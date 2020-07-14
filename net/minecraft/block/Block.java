/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block
extends AbstractBlock
implements ItemConvertible {
    protected static final Logger LOGGER = LogManager.getLogger();
    public static final IdList<BlockState> STATE_IDS = new IdList();
    private static final LoadingCache<VoxelShape, Boolean> FULL_CUBE_SHAPE_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build((CacheLoader)new CacheLoader<VoxelShape, Boolean>(){

        public Boolean load(VoxelShape arg) {
            return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), arg, BooleanBiFunction.NOT_SAME);
        }

        public /* synthetic */ Object load(Object shape) throws Exception {
            return this.load((VoxelShape)shape);
        }
    });
    protected final StateManager<Block, BlockState> stateManager;
    private BlockState defaultState;
    @Nullable
    private String translationKey;
    @Nullable
    private Item cachedItem;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<NeighborGroup>> FACE_CULL_MAP = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<NeighborGroup>(2048, 0.25f){

            protected void rehash(int i) {
            }
        };
        object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
        return object2ByteLinkedOpenHashMap;
    });

    public static int getRawIdFromState(@Nullable BlockState state) {
        if (state == null) {
            return 0;
        }
        int i = STATE_IDS.getRawId(state);
        return i == -1 ? 0 : i;
    }

    public static BlockState getStateFromRawId(int stateId) {
        BlockState lv = STATE_IDS.get(stateId);
        return lv == null ? Blocks.AIR.getDefaultState() : lv;
    }

    public static Block getBlockFromItem(@Nullable Item item) {
        if (item instanceof BlockItem) {
            return ((BlockItem)item).getBlock();
        }
        return Blocks.AIR;
    }

    public static BlockState pushEntitiesUpBeforeBlockChange(BlockState from, BlockState to, World world, BlockPos pos) {
        VoxelShape lv = VoxelShapes.combine(from.getCollisionShape(world, pos), to.getCollisionShape(world, pos), BooleanBiFunction.ONLY_SECOND).offset(pos.getX(), pos.getY(), pos.getZ());
        List<Entity> list = world.getEntities(null, lv.getBoundingBox());
        for (Entity lv2 : list) {
            double d = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, lv2.getBoundingBox().offset(0.0, 1.0, 0.0), Stream.of(lv), -1.0);
            lv2.requestTeleport(lv2.getX(), lv2.getY() + 1.0 + d, lv2.getZ());
        }
        return to;
    }

    public static VoxelShape createCuboidShape(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        return VoxelShapes.cuboid(xMin / 16.0, yMin / 16.0, zMin / 16.0, xMax / 16.0, yMax / 16.0, zMax / 16.0);
    }

    public boolean isIn(Tag<Block> tag) {
        return tag.contains(this);
    }

    public boolean is(Block block) {
        return this == block;
    }

    public static BlockState postProcessState(BlockState state, WorldAccess world, BlockPos pos) {
        BlockState lv = state;
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (Direction lv3 : FACINGS) {
            lv2.set(pos, lv3);
            lv = lv.getStateForNeighborUpdate(lv3, world.getBlockState(lv2), world, pos, lv2);
        }
        return lv;
    }

    public static void replace(BlockState state, BlockState newState, WorldAccess world, BlockPos pos, int flags) {
        Block.replace(state, newState, world, pos, flags, 512);
    }

    public static void replace(BlockState state, BlockState newState, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
        if (newState != state) {
            if (newState.isAir()) {
                if (!world.isClient()) {
                    world.breakBlock(pos, (flags & 0x20) == 0, null, maxUpdateDepth);
                }
            } else {
                world.setBlockState(pos, newState, flags & 0xFFFFFFDF, maxUpdateDepth);
            }
        }
    }

    public Block(AbstractBlock.Settings arg) {
        super(arg);
        StateManager.Builder<Block, BlockState> lv = new StateManager.Builder<Block, BlockState>(this);
        this.appendProperties(lv);
        this.stateManager = lv.build(Block::getDefaultState, BlockState::new);
        this.setDefaultState(this.stateManager.getDefaultState());
    }

    public static boolean cannotConnect(Block block) {
        return block instanceof LeavesBlock || block == Blocks.BARRIER || block == Blocks.CARVED_PUMPKIN || block == Blocks.JACK_O_LANTERN || block == Blocks.MELON || block == Blocks.PUMPKIN || block.isIn(BlockTags.SHULKER_BOXES);
    }

    public boolean hasRandomTicks(BlockState state) {
        return this.randomTicks;
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction facing) {
        BlockPos lv = pos.offset(facing);
        BlockState lv2 = world.getBlockState(lv);
        if (state.isSideInvisible(lv2, facing)) {
            return false;
        }
        if (lv2.isOpaque()) {
            NeighborGroup lv3 = new NeighborGroup(state, lv2, facing);
            Object2ByteLinkedOpenHashMap<NeighborGroup> object2ByteLinkedOpenHashMap = FACE_CULL_MAP.get();
            byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst((Object)lv3);
            if (b != 127) {
                return b != 0;
            }
            VoxelShape lv4 = state.getCullingFace(world, pos, facing);
            VoxelShape lv5 = lv2.getCullingFace(world, lv, facing.getOpposite());
            boolean bl = VoxelShapes.matchesAnywhere(lv4, lv5, BooleanBiFunction.ONLY_FIRST);
            if (object2ByteLinkedOpenHashMap.size() == 2048) {
                object2ByteLinkedOpenHashMap.removeLastByte();
            }
            object2ByteLinkedOpenHashMap.putAndMoveToFirst((Object)lv3, (byte)(bl ? 1 : 0));
            return bl;
        }
        return true;
    }

    public static boolean hasTopRim(BlockView world, BlockPos pos) {
        return world.getBlockState(pos).isSideSolid(world, pos, Direction.UP, SideShapeType.RIGID);
    }

    public static boolean sideCoversSmallSquare(WorldView world, BlockPos pos, Direction side) {
        BlockState lv = world.getBlockState(pos);
        if (side == Direction.DOWN && lv.isIn(BlockTags.UNSTABLE_BOTTOM_CENTER)) {
            return false;
        }
        return lv.isSideSolid(world, pos, side, SideShapeType.CENTER);
    }

    public static boolean isFaceFullSquare(VoxelShape shape, Direction side) {
        VoxelShape lv = shape.getFace(side);
        return Block.isShapeFullCube(lv);
    }

    public static boolean isShapeFullCube(VoxelShape shape) {
        return (Boolean)FULL_CUBE_SHAPE_CACHE.getUnchecked((Object)shape);
    }

    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return !Block.isShapeFullCube(state.getOutlineShape(world, pos)) && state.getFluidState().isEmpty();
    }

    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
    }

    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
    }

    public static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity) {
        LootContext.Builder lv = new LootContext.Builder(world).random(world.random).parameter(LootContextParameters.POSITION, pos).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);
        return state.getDroppedStacks(lv);
    }

    public static List<ItemStack> getDroppedStacks(BlockState state, ServerWorld world, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack stack) {
        LootContext.Builder lv = new LootContext.Builder(world).random(world.random).parameter(LootContextParameters.POSITION, pos).parameter(LootContextParameters.TOOL, stack).optionalParameter(LootContextParameters.THIS_ENTITY, entity).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity);
        return state.getDroppedStacks(lv);
    }

    public static void dropStacks(BlockState state, World world, BlockPos pos) {
        if (world instanceof ServerWorld) {
            Block.getDroppedStacks(state, (ServerWorld)world, pos, null).forEach(stack -> Block.dropStack(world, pos, stack));
            state.onStacksDropped((ServerWorld)world, pos, ItemStack.EMPTY);
        }
    }

    public static void dropStacks(BlockState state, WorldAccess arg2, BlockPos pos, @Nullable BlockEntity blockEntity) {
        if (arg2 instanceof ServerWorld) {
            Block.getDroppedStacks(state, (ServerWorld)arg2, pos, blockEntity).forEach(stack -> Block.dropStack((ServerWorld)arg2, pos, stack));
            state.onStacksDropped((ServerWorld)arg2, pos, ItemStack.EMPTY);
        }
    }

    public static void dropStacks(BlockState state, World world, BlockPos pos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack stack) {
        if (world instanceof ServerWorld) {
            Block.getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, stack).forEach(arg3 -> Block.dropStack(world, pos, arg3));
            state.onStacksDropped((ServerWorld)world, pos, stack);
        }
    }

    public static void dropStack(World world, BlockPos pos, ItemStack stack) {
        if (world.isClient || stack.isEmpty() || !world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            return;
        }
        float f = 0.5f;
        double d = (double)(world.random.nextFloat() * 0.5f) + 0.25;
        double e = (double)(world.random.nextFloat() * 0.5f) + 0.25;
        double g = (double)(world.random.nextFloat() * 0.5f) + 0.25;
        ItemEntity lv = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, stack);
        lv.setToDefaultPickupDelay();
        world.spawnEntity(lv);
    }

    protected void dropExperience(ServerWorld arg, BlockPos pos, int size) {
        if (arg.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            while (size > 0) {
                int j = ExperienceOrbEntity.roundToOrbSize(size);
                size -= j;
                arg.spawnEntity(new ExperienceOrbEntity(arg, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, j));
            }
        }
    }

    public float getBlastResistance() {
        return this.resistance;
    }

    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
    }

    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState();
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005f);
        Block.dropStacks(state, world, pos, blockEntity, player, stack);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    }

    public boolean canMobSpawnInside() {
        return !this.material.isSolid() && !this.material.isLiquid();
    }

    @Environment(value=EnvType.CLIENT)
    public MutableText getName() {
        return new TranslatableText(this.getTranslationKey());
    }

    public String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("block", Registry.BLOCK.getId(this));
        }
        return this.translationKey;
    }

    public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance) {
        entity.handleFallDamage(distance, 1.0f);
    }

    public void onEntityLand(BlockView world, Entity entity) {
        entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0));
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }

    public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
        list.add(new ItemStack(this));
    }

    public float getSlipperiness() {
        return this.slipperiness;
    }

    public float getVelocityMultiplier() {
        return this.velocityMultiplier;
    }

    public float getJumpVelocityMultiplier() {
        return this.jumpVelocityMultiplier;
    }

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        world.syncWorldEvent(player, 2001, pos, Block.getRawIdFromState(state));
        if (this.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinBrain.onGuardedBlockBroken(player, false);
        }
    }

    public void rainTick(World world, BlockPos pos) {
    }

    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    }

    public StateManager<Block, BlockState> getStateManager() {
        return this.stateManager;
    }

    protected final void setDefaultState(BlockState state) {
        this.defaultState = state;
    }

    public final BlockState getDefaultState() {
        return this.defaultState;
    }

    public BlockSoundGroup getSoundGroup(BlockState state) {
        return this.soundGroup;
    }

    @Override
    public Item asItem() {
        if (this.cachedItem == null) {
            this.cachedItem = Item.fromBlock(this);
        }
        return this.cachedItem;
    }

    public boolean hasDynamicBounds() {
        return this.dynamicBounds;
    }

    public String toString() {
        return "Block{" + Registry.BLOCK.getId(this) + "}";
    }

    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
    }

    @Override
    protected Block asBlock() {
        return this;
    }

    public static final class NeighborGroup {
        private final BlockState self;
        private final BlockState other;
        private final Direction facing;

        public NeighborGroup(BlockState self, BlockState other, Direction facing) {
            this.self = self;
            this.other = other;
            this.facing = facing;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof NeighborGroup)) {
                return false;
            }
            NeighborGroup lv = (NeighborGroup)o;
            return this.self == lv.self && this.other == lv.other && this.facing == lv.facing;
        }

        public int hashCode() {
            int i = this.self.hashCode();
            i = 31 * i + this.other.hashCode();
            i = 31 * i + this.facing.hashCode();
            return i;
        }
    }
}

