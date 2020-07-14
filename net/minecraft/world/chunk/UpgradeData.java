/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.EightWayDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final UpgradeData NO_UPGRADE_DATA = new UpgradeData();
    private static final EightWayDirection[] EIGHT_WAYS = EightWayDirection.values();
    private final EnumSet<EightWayDirection> sidesToUpgrade = EnumSet.noneOf(EightWayDirection.class);
    private final int[][] centerIndicesToUpgrade = new int[16][];
    private static final Map<Block, Logic> BLOCK_TO_LOGIC = new IdentityHashMap<Block, Logic>();
    private static final Set<Logic> CALLBACK_LOGICS = Sets.newHashSet();

    private UpgradeData() {
    }

    public UpgradeData(CompoundTag tag) {
        this();
        if (tag.contains("Indices", 10)) {
            CompoundTag lv = tag.getCompound("Indices");
            for (int i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
                String string = String.valueOf(i);
                if (!lv.contains(string, 11)) continue;
                this.centerIndicesToUpgrade[i] = lv.getIntArray(string);
            }
        }
        int j = tag.getInt("Sides");
        for (EightWayDirection lv2 : EightWayDirection.values()) {
            if ((j & 1 << lv2.ordinal()) == 0) continue;
            this.sidesToUpgrade.add(lv2);
        }
    }

    public void upgrade(WorldChunk chunk) {
        this.upgradeCenter(chunk);
        for (EightWayDirection lv : EIGHT_WAYS) {
            UpgradeData.upgradeSide(chunk, lv);
        }
        World lv2 = chunk.getWorld();
        CALLBACK_LOGICS.forEach(arg2 -> arg2.postUpdate(lv2));
    }

    private static void upgradeSide(WorldChunk chunk, EightWayDirection side) {
        World lv = chunk.getWorld();
        if (!chunk.getUpgradeData().sidesToUpgrade.remove((Object)side)) {
            return;
        }
        Set<Direction> set = side.getDirections();
        boolean i = false;
        int j = 15;
        boolean bl = set.contains(Direction.EAST);
        boolean bl2 = set.contains(Direction.WEST);
        boolean bl3 = set.contains(Direction.SOUTH);
        boolean bl4 = set.contains(Direction.NORTH);
        boolean bl5 = set.size() == 1;
        ChunkPos lv2 = chunk.getPos();
        int k = lv2.getStartX() + (bl5 && (bl4 || bl3) ? 1 : (bl2 ? 0 : 15));
        int l = lv2.getStartX() + (bl5 && (bl4 || bl3) ? 14 : (bl2 ? 0 : 15));
        int m = lv2.getStartZ() + (bl5 && (bl || bl2) ? 1 : (bl4 ? 0 : 15));
        int n = lv2.getStartZ() + (bl5 && (bl || bl2) ? 14 : (bl4 ? 0 : 15));
        Direction[] lvs = Direction.values();
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (BlockPos lv4 : BlockPos.iterate(k, 0, m, l, lv.getHeight() - 1, n)) {
            BlockState lv5;
            BlockState lv6 = lv5 = lv.getBlockState(lv4);
            for (Direction lv7 : lvs) {
                lv3.set(lv4, lv7);
                lv6 = UpgradeData.applyAdjacentBlock(lv6, lv7, lv, lv4, lv3);
            }
            Block.replace(lv5, lv6, lv, lv4, 18);
        }
    }

    private static BlockState applyAdjacentBlock(BlockState oldState, Direction dir, WorldAccess world, BlockPos currentPos, BlockPos otherPos) {
        return BLOCK_TO_LOGIC.getOrDefault(oldState.getBlock(), BuiltinLogic.DEFAULT).getUpdatedState(oldState, dir, world.getBlockState(otherPos), world, currentPos, otherPos);
    }

    private void upgradeCenter(WorldChunk chunk) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        BlockPos.Mutable lv2 = new BlockPos.Mutable();
        ChunkPos lv3 = chunk.getPos();
        World lv4 = chunk.getWorld();
        for (int i = 0; i < 16; ++i) {
            ChunkSection lv5 = chunk.getSectionArray()[i];
            int[] is = this.centerIndicesToUpgrade[i];
            this.centerIndicesToUpgrade[i] = null;
            if (lv5 == null || is == null || is.length <= 0) continue;
            Direction[] lvs = Direction.values();
            PalettedContainer<BlockState> lv6 = lv5.getContainer();
            for (int j : is) {
                BlockState lv7;
                int k = j & 0xF;
                int l = j >> 8 & 0xF;
                int m = j >> 4 & 0xF;
                lv.set(lv3.getStartX() + k, (i << 4) + l, lv3.getStartZ() + m);
                BlockState lv8 = lv7 = lv6.get(j);
                for (Direction lv9 : lvs) {
                    lv2.set(lv, lv9);
                    if (lv.getX() >> 4 != lv3.x || lv.getZ() >> 4 != lv3.z) continue;
                    lv8 = UpgradeData.applyAdjacentBlock(lv8, lv9, lv4, lv, lv2);
                }
                Block.replace(lv7, lv8, lv4, lv, 18);
            }
        }
        for (int n = 0; n < this.centerIndicesToUpgrade.length; ++n) {
            if (this.centerIndicesToUpgrade[n] != null) {
                LOGGER.warn("Discarding update data for section {} for chunk ({} {})", (Object)n, (Object)lv3.x, (Object)lv3.z);
            }
            this.centerIndicesToUpgrade[n] = null;
        }
    }

    public boolean isDone() {
        for (int[] is : this.centerIndicesToUpgrade) {
            if (is == null) continue;
            return false;
        }
        return this.sidesToUpgrade.isEmpty();
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        CompoundTag lv2 = new CompoundTag();
        for (int i = 0; i < this.centerIndicesToUpgrade.length; ++i) {
            String string = String.valueOf(i);
            if (this.centerIndicesToUpgrade[i] == null || this.centerIndicesToUpgrade[i].length == 0) continue;
            lv2.putIntArray(string, this.centerIndicesToUpgrade[i]);
        }
        if (!lv2.isEmpty()) {
            lv.put("Indices", lv2);
        }
        int j = 0;
        for (EightWayDirection lv3 : this.sidesToUpgrade) {
            j |= 1 << lv3.ordinal();
        }
        lv.putByte("Sides", (byte)j);
        return lv;
    }

    static enum BuiltinLogic implements Logic
    {
        BLACKLIST(new Block[]{Blocks.OBSERVER, Blocks.NETHER_PORTAL, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.DRAGON_EGG, Blocks.GRAVEL, Blocks.SAND, Blocks.RED_SAND, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN}){

            @Override
            public BlockState getUpdatedState(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
                return arg;
            }
        }
        ,
        DEFAULT(new Block[0]){

            @Override
            public BlockState getUpdatedState(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
                return arg.getStateForNeighborUpdate(arg2, arg4.getBlockState(arg6), arg4, arg5, arg6);
            }
        }
        ,
        CHEST(new Block[]{Blocks.CHEST, Blocks.TRAPPED_CHEST}){

            @Override
            public BlockState getUpdatedState(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
                if (arg3.isOf(arg.getBlock()) && arg2.getAxis().isHorizontal() && arg.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE && arg3.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) {
                    Direction lv = arg.get(ChestBlock.FACING);
                    if (arg2.getAxis() != lv.getAxis() && lv == arg3.get(ChestBlock.FACING)) {
                        ChestType lv2 = arg2 == lv.rotateYClockwise() ? ChestType.LEFT : ChestType.RIGHT;
                        arg4.setBlockState(arg6, (BlockState)arg3.with(ChestBlock.CHEST_TYPE, lv2.getOpposite()), 18);
                        if (lv == Direction.NORTH || lv == Direction.EAST) {
                            BlockEntity lv3 = arg4.getBlockEntity(arg5);
                            BlockEntity lv4 = arg4.getBlockEntity(arg6);
                            if (lv3 instanceof ChestBlockEntity && lv4 instanceof ChestBlockEntity) {
                                ChestBlockEntity.copyInventory((ChestBlockEntity)lv3, (ChestBlockEntity)lv4);
                            }
                        }
                        return (BlockState)arg.with(ChestBlock.CHEST_TYPE, lv2);
                    }
                }
                return arg;
            }
        }
        ,
        LEAVES(true, new Block[]{Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES}){
            private final ThreadLocal<List<ObjectSet<BlockPos>>> distanceToPositions = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity((int)7));

            @Override
            public BlockState getUpdatedState(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
                BlockState lv = arg.getStateForNeighborUpdate(arg2, arg4.getBlockState(arg6), arg4, arg5, arg6);
                if (arg != lv) {
                    int i = lv.get(Properties.DISTANCE_1_7);
                    List<ObjectSet<BlockPos>> list = this.distanceToPositions.get();
                    if (list.isEmpty()) {
                        for (int j = 0; j < 7; ++j) {
                            list.add((ObjectSet<BlockPos>)new ObjectOpenHashSet());
                        }
                    }
                    list.get(i).add((Object)arg5.toImmutable());
                }
                return arg;
            }

            @Override
            public void postUpdate(WorldAccess world) {
                BlockPos.Mutable lv = new BlockPos.Mutable();
                List<ObjectSet<BlockPos>> list = this.distanceToPositions.get();
                for (int i = 2; i < list.size(); ++i) {
                    int j = i - 1;
                    ObjectSet<BlockPos> objectSet = list.get(j);
                    ObjectSet<BlockPos> objectSet2 = list.get(i);
                    for (BlockPos lv2 : objectSet) {
                        BlockState lv3 = world.getBlockState(lv2);
                        if (lv3.get(Properties.DISTANCE_1_7) < j) continue;
                        world.setBlockState(lv2, (BlockState)lv3.with(Properties.DISTANCE_1_7, j), 18);
                        if (i == 7) continue;
                        for (Direction lv4 : DIRECTIONS) {
                            lv.set(lv2, lv4);
                            BlockState lv5 = world.getBlockState(lv);
                            if (!lv5.contains(Properties.DISTANCE_1_7) || lv3.get(Properties.DISTANCE_1_7) <= i) continue;
                            objectSet2.add((Object)lv.toImmutable());
                        }
                    }
                }
                list.clear();
            }
        }
        ,
        STEM_BLOCK(new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM}){

            @Override
            public BlockState getUpdatedState(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
                GourdBlock lv;
                if (arg.get(StemBlock.AGE) == 7 && arg3.isOf(lv = ((StemBlock)arg.getBlock()).getGourdBlock())) {
                    return (BlockState)lv.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, arg2);
                }
                return arg;
            }
        };

        public static final Direction[] DIRECTIONS;

        private BuiltinLogic(Block ... blocks) {
            this(false, blocks);
        }

        private BuiltinLogic(boolean bl, Block ... args) {
            for (Block lv : args) {
                BLOCK_TO_LOGIC.put(lv, this);
            }
            if (bl) {
                CALLBACK_LOGICS.add(this);
            }
        }

        static {
            DIRECTIONS = Direction.values();
        }
    }

    public static interface Logic {
        public BlockState getUpdatedState(BlockState var1, Direction var2, BlockState var3, WorldAccess var4, BlockPos var5, BlockPos var6);

        default public void postUpdate(WorldAccess world) {
        }
    }
}

