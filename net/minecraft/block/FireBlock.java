/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TntBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class FireBlock
extends AbstractFireBlock {
    public static final IntProperty AGE = Properties.AGE_15;
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty UP = ConnectingBlock.UP;
    private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(Util.toMap());
    private final Object2IntMap<Block> burnChances = new Object2IntOpenHashMap();
    private final Object2IntMap<Block> spreadChances = new Object2IntOpenHashMap();

    public FireBlock(AbstractBlock.Settings arg) {
        super(arg, 1.0f);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(UP, false));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (this.canPlaceAt(arg, arg4, arg5)) {
            return this.method_24855(arg4, arg5, arg.get(AGE));
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        VoxelShape lv = VoxelShapes.empty();
        if (arg.get(UP).booleanValue()) {
            lv = field_22497;
        }
        if (arg.get(WEST).booleanValue()) {
            lv = VoxelShapes.union(lv, field_22499);
        }
        if (arg.get(EAST).booleanValue()) {
            lv = VoxelShapes.union(lv, field_22500);
        }
        if (arg.get(NORTH).booleanValue()) {
            lv = VoxelShapes.union(lv, field_22501);
        }
        if (arg.get(SOUTH).booleanValue()) {
            lv = VoxelShapes.union(lv, field_22502);
        }
        if (lv == VoxelShapes.empty()) {
            return field_22498;
        }
        return lv;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return this.getStateForPosition(arg.getWorld(), arg.getBlockPos());
    }

    protected BlockState getStateForPosition(BlockView arg, BlockPos arg2) {
        BlockPos lv = arg2.down();
        BlockState lv2 = arg.getBlockState(lv);
        if (this.isFlammable(lv2) || lv2.isSideSolidFullSquare(arg, lv, Direction.UP)) {
            return this.getDefaultState();
        }
        BlockState lv3 = this.getDefaultState();
        for (Direction lv4 : Direction.values()) {
            BooleanProperty lv5 = DIRECTION_PROPERTIES.get(lv4);
            if (lv5 == null) continue;
            lv3 = (BlockState)lv3.with(lv5, this.isFlammable(arg.getBlockState(arg2.offset(lv4))));
        }
        return lv3;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.down();
        return arg2.getBlockState(lv).isSideSolidFullSquare(arg2, lv, Direction.UP) || this.areBlocksAroundFlammable(arg2, arg3);
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        boolean bl2;
        arg2.getBlockTickScheduler().schedule(arg3, this, FireBlock.method_26155(arg2.random));
        if (!arg2.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }
        if (!arg.canPlaceAt(arg2, arg3)) {
            arg2.removeBlock(arg3, false);
        }
        BlockState lv = arg2.getBlockState(arg3.down());
        boolean bl = lv.isIn(arg2.getDimension().getInfiniburnBlocks());
        int i = arg.get(AGE);
        if (!bl && arg2.isRaining() && this.isRainingAround(arg2, arg3) && random.nextFloat() < 0.2f + (float)i * 0.03f) {
            arg2.removeBlock(arg3, false);
            return;
        }
        int j = Math.min(15, i + random.nextInt(3) / 2);
        if (i != j) {
            arg = (BlockState)arg.with(AGE, j);
            arg2.setBlockState(arg3, arg, 4);
        }
        if (!bl) {
            if (!this.areBlocksAroundFlammable(arg2, arg3)) {
                BlockPos lv2 = arg3.down();
                if (!arg2.getBlockState(lv2).isSideSolidFullSquare(arg2, lv2, Direction.UP) || i > 3) {
                    arg2.removeBlock(arg3, false);
                }
                return;
            }
            if (i == 15 && random.nextInt(4) == 0 && !this.isFlammable(arg2.getBlockState(arg3.down()))) {
                arg2.removeBlock(arg3, false);
                return;
            }
        }
        int k = (bl2 = arg2.hasHighHumidity(arg3)) ? -50 : 0;
        this.trySpreadingFire(arg2, arg3.east(), 300 + k, random, i);
        this.trySpreadingFire(arg2, arg3.west(), 300 + k, random, i);
        this.trySpreadingFire(arg2, arg3.down(), 250 + k, random, i);
        this.trySpreadingFire(arg2, arg3.up(), 250 + k, random, i);
        this.trySpreadingFire(arg2, arg3.north(), 300 + k, random, i);
        this.trySpreadingFire(arg2, arg3.south(), 300 + k, random, i);
        BlockPos.Mutable lv3 = new BlockPos.Mutable();
        for (int l = -1; l <= 1; ++l) {
            for (int m = -1; m <= 1; ++m) {
                for (int n = -1; n <= 4; ++n) {
                    if (l == 0 && n == 0 && m == 0) continue;
                    int o = 100;
                    if (n > 1) {
                        o += (n - 1) * 100;
                    }
                    lv3.set(arg3, l, n, m);
                    int p = this.getBurnChance(arg2, lv3);
                    if (p <= 0) continue;
                    int q = (p + 40 + arg2.getDifficulty().getId() * 7) / (i + 30);
                    if (bl2) {
                        q /= 2;
                    }
                    if (q <= 0 || random.nextInt(o) > q || arg2.isRaining() && this.isRainingAround(arg2, lv3)) continue;
                    int r = Math.min(15, i + random.nextInt(5) / 4);
                    arg2.setBlockState(lv3, this.method_24855(arg2, lv3, r), 3);
                }
            }
        }
    }

    protected boolean isRainingAround(World arg, BlockPos arg2) {
        return arg.hasRain(arg2) || arg.hasRain(arg2.west()) || arg.hasRain(arg2.east()) || arg.hasRain(arg2.north()) || arg.hasRain(arg2.south());
    }

    private int getSpreadChance(BlockState arg) {
        if (arg.contains(Properties.WATERLOGGED) && arg.get(Properties.WATERLOGGED).booleanValue()) {
            return 0;
        }
        return this.spreadChances.getInt((Object)arg.getBlock());
    }

    private int getBurnChance(BlockState arg) {
        if (arg.contains(Properties.WATERLOGGED) && arg.get(Properties.WATERLOGGED).booleanValue()) {
            return 0;
        }
        return this.burnChances.getInt((Object)arg.getBlock());
    }

    private void trySpreadingFire(World arg, BlockPos arg2, int i, Random random, int j) {
        int k = this.getSpreadChance(arg.getBlockState(arg2));
        if (random.nextInt(i) < k) {
            BlockState lv = arg.getBlockState(arg2);
            if (random.nextInt(j + 10) < 5 && !arg.hasRain(arg2)) {
                int l = Math.min(j + random.nextInt(5) / 4, 15);
                arg.setBlockState(arg2, this.method_24855(arg, arg2, l), 3);
            } else {
                arg.removeBlock(arg2, false);
            }
            Block lv2 = lv.getBlock();
            if (lv2 instanceof TntBlock) {
                TntBlock cfr_ignored_0 = (TntBlock)lv2;
                TntBlock.primeTnt(arg, arg2);
            }
        }
    }

    private BlockState method_24855(WorldAccess arg, BlockPos arg2, int i) {
        BlockState lv = FireBlock.getState(arg, arg2);
        if (lv.isOf(Blocks.FIRE)) {
            return (BlockState)lv.with(AGE, i);
        }
        return lv;
    }

    private boolean areBlocksAroundFlammable(BlockView arg, BlockPos arg2) {
        for (Direction lv : Direction.values()) {
            if (!this.isFlammable(arg.getBlockState(arg2.offset(lv)))) continue;
            return true;
        }
        return false;
    }

    private int getBurnChance(WorldView arg, BlockPos arg2) {
        if (!arg.isAir(arg2)) {
            return 0;
        }
        int i = 0;
        for (Direction lv : Direction.values()) {
            BlockState lv2 = arg.getBlockState(arg2.offset(lv));
            i = Math.max(this.getBurnChance(lv2), i);
        }
        return i;
    }

    @Override
    protected boolean isFlammable(BlockState arg) {
        return this.getBurnChance(arg) > 0;
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        super.onBlockAdded(arg, arg2, arg3, arg4, bl);
        arg2.getBlockTickScheduler().schedule(arg3, this, FireBlock.method_26155(arg2.random));
    }

    private static int method_26155(Random random) {
        return 30 + random.nextInt(10);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AGE, NORTH, EAST, SOUTH, WEST, UP);
    }

    private void registerFlammableBlock(Block arg, int i, int j) {
        this.burnChances.put((Object)arg, i);
        this.spreadChances.put((Object)arg, j);
    }

    public static void registerDefaultFlammables() {
        FireBlock lv = (FireBlock)Blocks.FIRE;
        lv.registerFlammableBlock(Blocks.OAK_PLANKS, 5, 20);
        lv.registerFlammableBlock(Blocks.SPRUCE_PLANKS, 5, 20);
        lv.registerFlammableBlock(Blocks.BIRCH_PLANKS, 5, 20);
        lv.registerFlammableBlock(Blocks.JUNGLE_PLANKS, 5, 20);
        lv.registerFlammableBlock(Blocks.ACACIA_PLANKS, 5, 20);
        lv.registerFlammableBlock(Blocks.DARK_OAK_PLANKS, 5, 20);
        lv.registerFlammableBlock(Blocks.OAK_SLAB, 5, 20);
        lv.registerFlammableBlock(Blocks.SPRUCE_SLAB, 5, 20);
        lv.registerFlammableBlock(Blocks.BIRCH_SLAB, 5, 20);
        lv.registerFlammableBlock(Blocks.JUNGLE_SLAB, 5, 20);
        lv.registerFlammableBlock(Blocks.ACACIA_SLAB, 5, 20);
        lv.registerFlammableBlock(Blocks.DARK_OAK_SLAB, 5, 20);
        lv.registerFlammableBlock(Blocks.OAK_FENCE_GATE, 5, 20);
        lv.registerFlammableBlock(Blocks.SPRUCE_FENCE_GATE, 5, 20);
        lv.registerFlammableBlock(Blocks.BIRCH_FENCE_GATE, 5, 20);
        lv.registerFlammableBlock(Blocks.JUNGLE_FENCE_GATE, 5, 20);
        lv.registerFlammableBlock(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
        lv.registerFlammableBlock(Blocks.ACACIA_FENCE_GATE, 5, 20);
        lv.registerFlammableBlock(Blocks.OAK_FENCE, 5, 20);
        lv.registerFlammableBlock(Blocks.SPRUCE_FENCE, 5, 20);
        lv.registerFlammableBlock(Blocks.BIRCH_FENCE, 5, 20);
        lv.registerFlammableBlock(Blocks.JUNGLE_FENCE, 5, 20);
        lv.registerFlammableBlock(Blocks.DARK_OAK_FENCE, 5, 20);
        lv.registerFlammableBlock(Blocks.ACACIA_FENCE, 5, 20);
        lv.registerFlammableBlock(Blocks.OAK_STAIRS, 5, 20);
        lv.registerFlammableBlock(Blocks.BIRCH_STAIRS, 5, 20);
        lv.registerFlammableBlock(Blocks.SPRUCE_STAIRS, 5, 20);
        lv.registerFlammableBlock(Blocks.JUNGLE_STAIRS, 5, 20);
        lv.registerFlammableBlock(Blocks.ACACIA_STAIRS, 5, 20);
        lv.registerFlammableBlock(Blocks.DARK_OAK_STAIRS, 5, 20);
        lv.registerFlammableBlock(Blocks.OAK_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.SPRUCE_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.BIRCH_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.JUNGLE_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.ACACIA_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.DARK_OAK_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_OAK_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_OAK_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.OAK_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.SPRUCE_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.BIRCH_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.JUNGLE_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.ACACIA_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.DARK_OAK_WOOD, 5, 5);
        lv.registerFlammableBlock(Blocks.OAK_LEAVES, 30, 60);
        lv.registerFlammableBlock(Blocks.SPRUCE_LEAVES, 30, 60);
        lv.registerFlammableBlock(Blocks.BIRCH_LEAVES, 30, 60);
        lv.registerFlammableBlock(Blocks.JUNGLE_LEAVES, 30, 60);
        lv.registerFlammableBlock(Blocks.ACACIA_LEAVES, 30, 60);
        lv.registerFlammableBlock(Blocks.DARK_OAK_LEAVES, 30, 60);
        lv.registerFlammableBlock(Blocks.BOOKSHELF, 30, 20);
        lv.registerFlammableBlock(Blocks.TNT, 15, 100);
        lv.registerFlammableBlock(Blocks.GRASS, 60, 100);
        lv.registerFlammableBlock(Blocks.FERN, 60, 100);
        lv.registerFlammableBlock(Blocks.DEAD_BUSH, 60, 100);
        lv.registerFlammableBlock(Blocks.SUNFLOWER, 60, 100);
        lv.registerFlammableBlock(Blocks.LILAC, 60, 100);
        lv.registerFlammableBlock(Blocks.ROSE_BUSH, 60, 100);
        lv.registerFlammableBlock(Blocks.PEONY, 60, 100);
        lv.registerFlammableBlock(Blocks.TALL_GRASS, 60, 100);
        lv.registerFlammableBlock(Blocks.LARGE_FERN, 60, 100);
        lv.registerFlammableBlock(Blocks.DANDELION, 60, 100);
        lv.registerFlammableBlock(Blocks.POPPY, 60, 100);
        lv.registerFlammableBlock(Blocks.BLUE_ORCHID, 60, 100);
        lv.registerFlammableBlock(Blocks.ALLIUM, 60, 100);
        lv.registerFlammableBlock(Blocks.AZURE_BLUET, 60, 100);
        lv.registerFlammableBlock(Blocks.RED_TULIP, 60, 100);
        lv.registerFlammableBlock(Blocks.ORANGE_TULIP, 60, 100);
        lv.registerFlammableBlock(Blocks.WHITE_TULIP, 60, 100);
        lv.registerFlammableBlock(Blocks.PINK_TULIP, 60, 100);
        lv.registerFlammableBlock(Blocks.OXEYE_DAISY, 60, 100);
        lv.registerFlammableBlock(Blocks.CORNFLOWER, 60, 100);
        lv.registerFlammableBlock(Blocks.LILY_OF_THE_VALLEY, 60, 100);
        lv.registerFlammableBlock(Blocks.WITHER_ROSE, 60, 100);
        lv.registerFlammableBlock(Blocks.WHITE_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.ORANGE_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.MAGENTA_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.LIGHT_BLUE_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.YELLOW_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.LIME_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.PINK_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.GRAY_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.LIGHT_GRAY_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.CYAN_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.PURPLE_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.BLUE_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.BROWN_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.GREEN_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.RED_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.BLACK_WOOL, 30, 60);
        lv.registerFlammableBlock(Blocks.VINE, 15, 100);
        lv.registerFlammableBlock(Blocks.COAL_BLOCK, 5, 5);
        lv.registerFlammableBlock(Blocks.HAY_BLOCK, 60, 20);
        lv.registerFlammableBlock(Blocks.TARGET, 15, 20);
        lv.registerFlammableBlock(Blocks.WHITE_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.ORANGE_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.MAGENTA_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.LIGHT_BLUE_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.YELLOW_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.LIME_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.PINK_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.GRAY_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.LIGHT_GRAY_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.CYAN_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.PURPLE_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.BLUE_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.BROWN_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.GREEN_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.RED_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.BLACK_CARPET, 60, 20);
        lv.registerFlammableBlock(Blocks.DRIED_KELP_BLOCK, 30, 60);
        lv.registerFlammableBlock(Blocks.BAMBOO, 60, 60);
        lv.registerFlammableBlock(Blocks.SCAFFOLDING, 60, 60);
        lv.registerFlammableBlock(Blocks.LECTERN, 30, 20);
        lv.registerFlammableBlock(Blocks.COMPOSTER, 5, 20);
        lv.registerFlammableBlock(Blocks.SWEET_BERRY_BUSH, 60, 100);
        lv.registerFlammableBlock(Blocks.BEEHIVE, 5, 20);
        lv.registerFlammableBlock(Blocks.BEE_NEST, 30, 20);
    }
}

