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

        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((VoxelShape)object);
        }
    });
    private static final VoxelShape SOLID_MEDIUM_SQUARE_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST);
    private static final VoxelShape SOLID_SMALL_SQUARE_SHAPE = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);
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

    public static int getRawIdFromState(@Nullable BlockState arg) {
        if (arg == null) {
            return 0;
        }
        int i = STATE_IDS.getId(arg);
        return i == -1 ? 0 : i;
    }

    public static BlockState getStateFromRawId(int i) {
        BlockState lv = STATE_IDS.get(i);
        return lv == null ? Blocks.AIR.getDefaultState() : lv;
    }

    public static Block getBlockFromItem(@Nullable Item arg) {
        if (arg instanceof BlockItem) {
            return ((BlockItem)arg).getBlock();
        }
        return Blocks.AIR;
    }

    public static BlockState pushEntitiesUpBeforeBlockChange(BlockState arg, BlockState arg2, World arg3, BlockPos arg4) {
        VoxelShape lv = VoxelShapes.combine(arg.getCollisionShape(arg3, arg4), arg2.getCollisionShape(arg3, arg4), BooleanBiFunction.ONLY_SECOND).offset(arg4.getX(), arg4.getY(), arg4.getZ());
        List<Entity> list = arg3.getEntities(null, lv.getBoundingBox());
        for (Entity lv2 : list) {
            double d = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, lv2.getBoundingBox().offset(0.0, 1.0, 0.0), Stream.of(lv), -1.0);
            lv2.requestTeleport(lv2.getX(), lv2.getY() + 1.0 + d, lv2.getZ());
        }
        return arg2;
    }

    public static VoxelShape createCuboidShape(double d, double e, double f, double g, double h, double i) {
        return VoxelShapes.cuboid(d / 16.0, e / 16.0, f / 16.0, g / 16.0, h / 16.0, i / 16.0);
    }

    public boolean isIn(Tag<Block> arg) {
        return arg.contains(this);
    }

    public boolean is(Block arg) {
        return this == arg;
    }

    public static BlockState postProcessState(BlockState arg, WorldAccess arg2, BlockPos arg3) {
        BlockState lv = arg;
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        for (Direction lv3 : FACINGS) {
            lv2.set(arg3, lv3);
            lv = lv.getStateForNeighborUpdate(lv3, arg2.getBlockState(lv2), arg2, arg3, lv2);
        }
        return lv;
    }

    public static void method_30094(BlockState arg, BlockState arg2, WorldAccess arg3, BlockPos arg4, int i) {
        Block.replaceBlock(arg, arg2, arg3, arg4, i, 512);
    }

    public static void replaceBlock(BlockState arg, BlockState arg2, WorldAccess arg3, BlockPos arg4, int i, int j) {
        if (arg2 != arg) {
            if (arg2.isAir()) {
                if (!arg3.isClient()) {
                    arg3.method_30093(arg4, (i & 0x20) == 0, null, j);
                }
            } else {
                arg3.method_30092(arg4, arg2, i & 0xFFFFFFDF, j);
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

    public static boolean cannotConnect(Block arg) {
        return arg instanceof LeavesBlock || arg == Blocks.BARRIER || arg == Blocks.CARVED_PUMPKIN || arg == Blocks.JACK_O_LANTERN || arg == Blocks.MELON || arg == Blocks.PUMPKIN || arg.isIn(BlockTags.SHULKER_BOXES);
    }

    public boolean hasRandomTicks(BlockState arg) {
        return this.randomTicks;
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean shouldDrawSide(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        BlockPos lv = arg3.offset(arg4);
        BlockState lv2 = arg2.getBlockState(lv);
        if (arg.isSideInvisible(lv2, arg4)) {
            return false;
        }
        if (lv2.isOpaque()) {
            NeighborGroup lv3 = new NeighborGroup(arg, lv2, arg4);
            Object2ByteLinkedOpenHashMap<NeighborGroup> object2ByteLinkedOpenHashMap = FACE_CULL_MAP.get();
            byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst((Object)lv3);
            if (b != 127) {
                return b != 0;
            }
            VoxelShape lv4 = arg.getCullingFace(arg2, arg3, arg4);
            VoxelShape lv5 = lv2.getCullingFace(arg2, lv, arg4.getOpposite());
            boolean bl = VoxelShapes.matchesAnywhere(lv4, lv5, BooleanBiFunction.ONLY_FIRST);
            if (object2ByteLinkedOpenHashMap.size() == 2048) {
                object2ByteLinkedOpenHashMap.removeLastByte();
            }
            object2ByteLinkedOpenHashMap.putAndMoveToFirst((Object)lv3, (byte)(bl ? 1 : 0));
            return bl;
        }
        return true;
    }

    public static boolean hasTopRim(BlockView arg, BlockPos arg2) {
        BlockState lv = arg.getBlockState(arg2);
        return lv.isFullCube(arg, arg2) && lv.isSideSolidFullSquare(arg, arg2, Direction.UP) || !VoxelShapes.matchesAnywhere(lv.getSidesShape(arg, arg2).getFace(Direction.UP), SOLID_MEDIUM_SQUARE_SHAPE, BooleanBiFunction.ONLY_SECOND);
    }

    public static boolean sideCoversSmallSquare(WorldView arg, BlockPos arg2, Direction arg3) {
        BlockState lv = arg.getBlockState(arg2);
        if (arg3 == Direction.DOWN && lv.isIn(BlockTags.UNSTABLE_BOTTOM_CENTER)) {
            return false;
        }
        return !VoxelShapes.matchesAnywhere(lv.getSidesShape(arg, arg2).getFace(arg3), SOLID_SMALL_SQUARE_SHAPE, BooleanBiFunction.ONLY_SECOND);
    }

    public static boolean isSideSolidFullSquare(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        return Block.isFaceFullSquare(arg.getSidesShape(arg2, arg3), arg4);
    }

    public static boolean isFaceFullSquare(VoxelShape arg, Direction arg2) {
        VoxelShape lv = arg.getFace(arg2);
        return Block.isShapeFullCube(lv);
    }

    public static boolean isShapeFullCube(VoxelShape arg) {
        return (Boolean)FULL_CUBE_SHAPE_CACHE.getUnchecked((Object)arg);
    }

    public boolean isTranslucent(BlockState arg, BlockView arg2, BlockPos arg3) {
        return !Block.isShapeFullCube(arg.getOutlineShape(arg2, arg3)) && arg.getFluidState().isEmpty();
    }

    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
    }

    public void onBroken(WorldAccess arg, BlockPos arg2, BlockState arg3) {
    }

    public static List<ItemStack> getDroppedStacks(BlockState arg, ServerWorld arg2, BlockPos arg3, @Nullable BlockEntity arg4) {
        LootContext.Builder lv = new LootContext.Builder(arg2).random(arg2.random).parameter(LootContextParameters.POSITION, arg3).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, arg4);
        return arg.getDroppedStacks(lv);
    }

    public static List<ItemStack> getDroppedStacks(BlockState arg, ServerWorld arg2, BlockPos arg3, @Nullable BlockEntity arg4, @Nullable Entity arg5, ItemStack arg6) {
        LootContext.Builder lv = new LootContext.Builder(arg2).random(arg2.random).parameter(LootContextParameters.POSITION, arg3).parameter(LootContextParameters.TOOL, arg6).optionalParameter(LootContextParameters.THIS_ENTITY, arg5).optionalParameter(LootContextParameters.BLOCK_ENTITY, arg4);
        return arg.getDroppedStacks(lv);
    }

    public static void dropStacks(BlockState arg, World arg2, BlockPos arg32) {
        if (arg2 instanceof ServerWorld) {
            Block.getDroppedStacks(arg, (ServerWorld)arg2, arg32, null).forEach(arg3 -> Block.dropStack(arg2, arg32, arg3));
        }
        arg.onStacksDropped(arg2, arg32, ItemStack.EMPTY);
    }

    public static void dropStacks(BlockState arg, World arg2, BlockPos arg32, @Nullable BlockEntity arg4) {
        if (arg2 instanceof ServerWorld) {
            Block.getDroppedStacks(arg, (ServerWorld)arg2, arg32, arg4).forEach(arg3 -> Block.dropStack(arg2, arg32, arg3));
        }
        arg.onStacksDropped(arg2, arg32, ItemStack.EMPTY);
    }

    public static void dropStacks(BlockState arg, World arg2, BlockPos arg32, @Nullable BlockEntity arg4, Entity arg5, ItemStack arg6) {
        if (arg2 instanceof ServerWorld) {
            Block.getDroppedStacks(arg, (ServerWorld)arg2, arg32, arg4, arg5, arg6).forEach(arg3 -> Block.dropStack(arg2, arg32, arg3));
        }
        arg.onStacksDropped(arg2, arg32, arg6);
    }

    public static void dropStack(World arg, BlockPos arg2, ItemStack arg3) {
        if (arg.isClient || arg3.isEmpty() || !arg.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            return;
        }
        float f = 0.5f;
        double d = (double)(arg.random.nextFloat() * 0.5f) + 0.25;
        double e = (double)(arg.random.nextFloat() * 0.5f) + 0.25;
        double g = (double)(arg.random.nextFloat() * 0.5f) + 0.25;
        ItemEntity lv = new ItemEntity(arg, (double)arg2.getX() + d, (double)arg2.getY() + e, (double)arg2.getZ() + g, arg3);
        lv.setToDefaultPickupDelay();
        arg.spawnEntity(lv);
    }

    protected void dropExperience(World arg, BlockPos arg2, int i) {
        if (!arg.isClient && arg.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            while (i > 0) {
                int j = ExperienceOrbEntity.roundToOrbSize(i);
                i -= j;
                arg.spawnEntity(new ExperienceOrbEntity(arg, (double)arg2.getX() + 0.5, (double)arg2.getY() + 0.5, (double)arg2.getZ() + 0.5, j));
            }
        }
    }

    public float getBlastResistance() {
        return this.resistance;
    }

    public void onDestroyedByExplosion(World arg, BlockPos arg2, Explosion arg3) {
    }

    public void onSteppedOn(World arg, BlockPos arg2, Entity arg3) {
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return this.getDefaultState();
    }

    public void afterBreak(World arg, PlayerEntity arg2, BlockPos arg3, BlockState arg4, @Nullable BlockEntity arg5, ItemStack arg6) {
        arg2.incrementStat(Stats.MINED.getOrCreateStat(this));
        arg2.addExhaustion(0.005f);
        Block.dropStacks(arg4, arg, arg3, arg5, arg2, arg6);
    }

    public void onPlaced(World arg, BlockPos arg2, BlockState arg3, @Nullable LivingEntity arg4, ItemStack arg5) {
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

    public void onLandedUpon(World arg, BlockPos arg2, Entity arg3, float f) {
        arg3.handleFallDamage(f, 1.0f);
    }

    public void onEntityLand(BlockView arg, Entity arg2) {
        arg2.setVelocity(arg2.getVelocity().multiply(1.0, 0.0, 1.0));
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return new ItemStack(this);
    }

    public void addStacksForDisplay(ItemGroup arg, DefaultedList<ItemStack> arg2) {
        arg2.add(new ItemStack(this));
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

    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        arg.syncWorldEvent(arg4, 2001, arg2, Block.getRawIdFromState(arg3));
        if (this.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinBrain.onGoldBlockBroken(arg4, false);
        }
    }

    public void rainTick(World arg, BlockPos arg2) {
    }

    public boolean shouldDropItemsOnExplosion(Explosion arg) {
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
    }

    public StateManager<Block, BlockState> getStateManager() {
        return this.stateManager;
    }

    protected final void setDefaultState(BlockState arg) {
        this.defaultState = arg;
    }

    public final BlockState getDefaultState() {
        return this.defaultState;
    }

    public BlockSoundGroup getSoundGroup(BlockState arg) {
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
    public void buildTooltip(ItemStack arg, @Nullable BlockView arg2, List<Text> list, TooltipContext arg3) {
    }

    @Override
    protected Block asBlock() {
        return this;
    }

    public static final class NeighborGroup {
        private final BlockState self;
        private final BlockState other;
        private final Direction facing;

        public NeighborGroup(BlockState arg, BlockState arg2, Direction arg3) {
            this.self = arg;
            this.other = arg2;
            this.facing = arg3;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof NeighborGroup)) {
                return false;
            }
            NeighborGroup lv = (NeighborGroup)object;
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

