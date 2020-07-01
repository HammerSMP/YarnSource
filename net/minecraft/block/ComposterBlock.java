/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ComposterBlock
extends Block
implements InventoryProvider {
    public static final IntProperty LEVEL = Properties.LEVEL_8;
    public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap();
    private static final VoxelShape RAY_TRACE_SHAPE = VoxelShapes.fullCube();
    private static final VoxelShape[] LEVEL_TO_COLLISION_SHAPE = Util.make(new VoxelShape[9], args -> {
        for (int i = 0; i < 8; ++i) {
            args[i] = VoxelShapes.combineAndSimplify(RAY_TRACE_SHAPE, Block.createCuboidShape(2.0, Math.max(2, 1 + i * 2), 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST);
        }
        args[8] = args[7];
    });

    public static void registerDefaultCompostableItems() {
        ITEM_TO_LEVEL_INCREASE_CHANCE.defaultReturnValue(-1.0f);
        float f = 0.3f;
        float g = 0.5f;
        float h = 0.65f;
        float i = 0.85f;
        float j = 1.0f;
        ComposterBlock.registerCompostableItem(0.3f, Items.JUNGLE_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.OAK_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.SPRUCE_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.DARK_OAK_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.ACACIA_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.BIRCH_LEAVES);
        ComposterBlock.registerCompostableItem(0.3f, Items.OAK_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.SPRUCE_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.BIRCH_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.JUNGLE_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.ACACIA_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.DARK_OAK_SAPLING);
        ComposterBlock.registerCompostableItem(0.3f, Items.BEETROOT_SEEDS);
        ComposterBlock.registerCompostableItem(0.3f, Items.DRIED_KELP);
        ComposterBlock.registerCompostableItem(0.3f, Items.GRASS);
        ComposterBlock.registerCompostableItem(0.3f, Items.KELP);
        ComposterBlock.registerCompostableItem(0.3f, Items.MELON_SEEDS);
        ComposterBlock.registerCompostableItem(0.3f, Items.PUMPKIN_SEEDS);
        ComposterBlock.registerCompostableItem(0.3f, Items.SEAGRASS);
        ComposterBlock.registerCompostableItem(0.3f, Items.SWEET_BERRIES);
        ComposterBlock.registerCompostableItem(0.3f, Items.WHEAT_SEEDS);
        ComposterBlock.registerCompostableItem(0.5f, Items.DRIED_KELP_BLOCK);
        ComposterBlock.registerCompostableItem(0.5f, Items.TALL_GRASS);
        ComposterBlock.registerCompostableItem(0.5f, Items.CACTUS);
        ComposterBlock.registerCompostableItem(0.5f, Items.SUGAR_CANE);
        ComposterBlock.registerCompostableItem(0.5f, Items.VINE);
        ComposterBlock.registerCompostableItem(0.5f, Items.NETHER_SPROUTS);
        ComposterBlock.registerCompostableItem(0.5f, Items.WEEPING_VINES);
        ComposterBlock.registerCompostableItem(0.5f, Items.TWISTING_VINES);
        ComposterBlock.registerCompostableItem(0.5f, Items.MELON_SLICE);
        ComposterBlock.registerCompostableItem(0.65f, Items.SEA_PICKLE);
        ComposterBlock.registerCompostableItem(0.65f, Items.LILY_PAD);
        ComposterBlock.registerCompostableItem(0.65f, Items.PUMPKIN);
        ComposterBlock.registerCompostableItem(0.65f, Items.CARVED_PUMPKIN);
        ComposterBlock.registerCompostableItem(0.65f, Items.MELON);
        ComposterBlock.registerCompostableItem(0.65f, Items.APPLE);
        ComposterBlock.registerCompostableItem(0.65f, Items.BEETROOT);
        ComposterBlock.registerCompostableItem(0.65f, Items.CARROT);
        ComposterBlock.registerCompostableItem(0.65f, Items.COCOA_BEANS);
        ComposterBlock.registerCompostableItem(0.65f, Items.POTATO);
        ComposterBlock.registerCompostableItem(0.65f, Items.WHEAT);
        ComposterBlock.registerCompostableItem(0.65f, Items.BROWN_MUSHROOM);
        ComposterBlock.registerCompostableItem(0.65f, Items.RED_MUSHROOM);
        ComposterBlock.registerCompostableItem(0.65f, Items.MUSHROOM_STEM);
        ComposterBlock.registerCompostableItem(0.65f, Items.CRIMSON_FUNGUS);
        ComposterBlock.registerCompostableItem(0.65f, Items.WARPED_FUNGUS);
        ComposterBlock.registerCompostableItem(0.65f, Items.NETHER_WART);
        ComposterBlock.registerCompostableItem(0.65f, Items.CRIMSON_ROOTS);
        ComposterBlock.registerCompostableItem(0.65f, Items.WARPED_ROOTS);
        ComposterBlock.registerCompostableItem(0.65f, Items.SHROOMLIGHT);
        ComposterBlock.registerCompostableItem(0.65f, Items.DANDELION);
        ComposterBlock.registerCompostableItem(0.65f, Items.POPPY);
        ComposterBlock.registerCompostableItem(0.65f, Items.BLUE_ORCHID);
        ComposterBlock.registerCompostableItem(0.65f, Items.ALLIUM);
        ComposterBlock.registerCompostableItem(0.65f, Items.AZURE_BLUET);
        ComposterBlock.registerCompostableItem(0.65f, Items.RED_TULIP);
        ComposterBlock.registerCompostableItem(0.65f, Items.ORANGE_TULIP);
        ComposterBlock.registerCompostableItem(0.65f, Items.WHITE_TULIP);
        ComposterBlock.registerCompostableItem(0.65f, Items.PINK_TULIP);
        ComposterBlock.registerCompostableItem(0.65f, Items.OXEYE_DAISY);
        ComposterBlock.registerCompostableItem(0.65f, Items.CORNFLOWER);
        ComposterBlock.registerCompostableItem(0.65f, Items.LILY_OF_THE_VALLEY);
        ComposterBlock.registerCompostableItem(0.65f, Items.WITHER_ROSE);
        ComposterBlock.registerCompostableItem(0.65f, Items.FERN);
        ComposterBlock.registerCompostableItem(0.65f, Items.SUNFLOWER);
        ComposterBlock.registerCompostableItem(0.65f, Items.LILAC);
        ComposterBlock.registerCompostableItem(0.65f, Items.ROSE_BUSH);
        ComposterBlock.registerCompostableItem(0.65f, Items.PEONY);
        ComposterBlock.registerCompostableItem(0.65f, Items.LARGE_FERN);
        ComposterBlock.registerCompostableItem(0.85f, Items.HAY_BLOCK);
        ComposterBlock.registerCompostableItem(0.85f, Items.BROWN_MUSHROOM_BLOCK);
        ComposterBlock.registerCompostableItem(0.85f, Items.RED_MUSHROOM_BLOCK);
        ComposterBlock.registerCompostableItem(0.85f, Items.NETHER_WART_BLOCK);
        ComposterBlock.registerCompostableItem(0.85f, Items.WARPED_WART_BLOCK);
        ComposterBlock.registerCompostableItem(0.85f, Items.BREAD);
        ComposterBlock.registerCompostableItem(0.85f, Items.BAKED_POTATO);
        ComposterBlock.registerCompostableItem(0.85f, Items.COOKIE);
        ComposterBlock.registerCompostableItem(1.0f, Items.CAKE);
        ComposterBlock.registerCompostableItem(1.0f, Items.PUMPKIN_PIE);
    }

    private static void registerCompostableItem(float f, ItemConvertible arg) {
        ITEM_TO_LEVEL_INCREASE_CHANCE.put((Object)arg.asItem(), f);
    }

    public ComposterBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LEVEL, 0));
    }

    @Environment(value=EnvType.CLIENT)
    public static void playEffects(World arg, BlockPos arg2, boolean bl) {
        BlockState lv = arg.getBlockState(arg2);
        arg.playSound(arg2.getX(), (double)arg2.getY(), (double)arg2.getZ(), bl ? SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS : SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        double d = lv.getOutlineShape(arg, arg2).getEndingCoord(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
        double e = 0.13125f;
        double f = 0.7375f;
        Random random = arg.getRandom();
        for (int i = 0; i < 10; ++i) {
            double g = random.nextGaussian() * 0.02;
            double h = random.nextGaussian() * 0.02;
            double j = random.nextGaussian() * 0.02;
            arg.addParticle(ParticleTypes.COMPOSTER, (double)arg2.getX() + (double)0.13125f + (double)0.7375f * (double)random.nextFloat(), (double)arg2.getY() + d + (double)random.nextFloat() * (1.0 - d), (double)arg2.getZ() + (double)0.13125f + (double)0.7375f * (double)random.nextFloat(), g, h, j);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return LEVEL_TO_COLLISION_SHAPE[arg.get(LEVEL)];
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState arg, BlockView arg2, BlockPos arg3) {
        return RAY_TRACE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return LEVEL_TO_COLLISION_SHAPE[0];
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg.get(LEVEL) == 7) {
            arg2.getBlockTickScheduler().schedule(arg3, arg.getBlock(), 20);
        }
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        int i = arg.get(LEVEL);
        ItemStack lv = arg4.getStackInHand(arg5);
        if (i < 8 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey((Object)lv.getItem())) {
            if (i < 7 && !arg2.isClient) {
                BlockState lv2 = ComposterBlock.addToComposter(arg, arg2, arg3, lv);
                arg2.syncWorldEvent(1500, arg3, arg != lv2 ? 1 : 0);
                if (!arg4.abilities.creativeMode) {
                    lv.decrement(1);
                }
            }
            return ActionResult.success(arg2.isClient);
        }
        if (i == 8) {
            ComposterBlock.emptyFullComposter(arg, arg2, arg3);
            return ActionResult.success(arg2.isClient);
        }
        return ActionResult.PASS;
    }

    public static BlockState compost(BlockState arg, ServerWorld arg2, ItemStack arg3, BlockPos arg4) {
        int i = arg.get(LEVEL);
        if (i < 7 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey((Object)arg3.getItem())) {
            BlockState lv = ComposterBlock.addToComposter(arg, arg2, arg4, arg3);
            arg3.decrement(1);
            return lv;
        }
        return arg;
    }

    public static BlockState emptyFullComposter(BlockState arg, World arg2, BlockPos arg3) {
        if (!arg2.isClient) {
            float f = 0.7f;
            double d = (double)(arg2.random.nextFloat() * 0.7f) + (double)0.15f;
            double e = (double)(arg2.random.nextFloat() * 0.7f) + 0.06000000238418579 + 0.6;
            double g = (double)(arg2.random.nextFloat() * 0.7f) + (double)0.15f;
            ItemEntity lv = new ItemEntity(arg2, (double)arg3.getX() + d, (double)arg3.getY() + e, (double)arg3.getZ() + g, new ItemStack(Items.BONE_MEAL));
            lv.setToDefaultPickupDelay();
            arg2.spawnEntity(lv);
        }
        BlockState lv2 = ComposterBlock.emptyComposter(arg, arg2, arg3);
        arg2.playSound(null, arg3, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return lv2;
    }

    private static BlockState emptyComposter(BlockState arg, WorldAccess arg2, BlockPos arg3) {
        BlockState lv = (BlockState)arg.with(LEVEL, 0);
        arg2.setBlockState(arg3, lv, 3);
        return lv;
    }

    private static BlockState addToComposter(BlockState arg, WorldAccess arg2, BlockPos arg3, ItemStack arg4) {
        int i = arg.get(LEVEL);
        float f = ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat((Object)arg4.getItem());
        if (i == 0 && f > 0.0f || arg2.getRandom().nextDouble() < (double)f) {
            int j = i + 1;
            BlockState lv = (BlockState)arg.with(LEVEL, j);
            arg2.setBlockState(arg3, lv, 3);
            if (j == 7) {
                arg2.getBlockTickScheduler().schedule(arg3, arg.getBlock(), 20);
            }
            return lv;
        }
        return arg;
    }

    @Override
    public void scheduledTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg.get(LEVEL) == 7) {
            arg2.setBlockState(arg3, (BlockState)arg.cycle(LEVEL), 3);
            arg2.playSound(null, arg3, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState arg) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState arg, World arg2, BlockPos arg3) {
        return arg.get(LEVEL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(LEVEL);
    }

    @Override
    public boolean canPathfindThrough(BlockState arg, BlockView arg2, BlockPos arg3, NavigationType arg4) {
        return false;
    }

    @Override
    public SidedInventory getInventory(BlockState arg, WorldAccess arg2, BlockPos arg3) {
        int i = arg.get(LEVEL);
        if (i == 8) {
            return new FullComposterInventory(arg, arg2, arg3, new ItemStack(Items.BONE_MEAL));
        }
        if (i < 7) {
            return new ComposterInventory(arg, arg2, arg3);
        }
        return new DummyInventory();
    }

    static class ComposterInventory
    extends SimpleInventory
    implements SidedInventory {
        private final BlockState state;
        private final WorldAccess world;
        private final BlockPos pos;
        private boolean dirty;

        public ComposterInventory(BlockState arg, WorldAccess arg2, BlockPos arg3) {
            super(1);
            this.state = arg;
            this.world = arg2;
            this.pos = arg3;
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }

        @Override
        public int[] getAvailableSlots(Direction arg) {
            int[] arrn;
            if (arg == Direction.UP) {
                int[] arrn2 = new int[1];
                arrn = arrn2;
                arrn2[0] = 0;
            } else {
                arrn = new int[]{};
            }
            return arrn;
        }

        @Override
        public boolean canInsert(int i, ItemStack arg, @Nullable Direction arg2) {
            return !this.dirty && arg2 == Direction.UP && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey((Object)arg.getItem());
        }

        @Override
        public boolean canExtract(int i, ItemStack arg, Direction arg2) {
            return false;
        }

        @Override
        public void markDirty() {
            ItemStack lv = this.getStack(0);
            if (!lv.isEmpty()) {
                this.dirty = true;
                BlockState lv2 = ComposterBlock.addToComposter(this.state, this.world, this.pos, lv);
                this.world.syncWorldEvent(1500, this.pos, lv2 != this.state ? 1 : 0);
                this.removeStack(0);
            }
        }
    }

    static class FullComposterInventory
    extends SimpleInventory
    implements SidedInventory {
        private final BlockState state;
        private final WorldAccess world;
        private final BlockPos pos;
        private boolean dirty;

        public FullComposterInventory(BlockState arg, WorldAccess arg2, BlockPos arg3, ItemStack arg4) {
            super(arg4);
            this.state = arg;
            this.world = arg2;
            this.pos = arg3;
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }

        @Override
        public int[] getAvailableSlots(Direction arg) {
            int[] arrn;
            if (arg == Direction.DOWN) {
                int[] arrn2 = new int[1];
                arrn = arrn2;
                arrn2[0] = 0;
            } else {
                arrn = new int[]{};
            }
            return arrn;
        }

        @Override
        public boolean canInsert(int i, ItemStack arg, @Nullable Direction arg2) {
            return false;
        }

        @Override
        public boolean canExtract(int i, ItemStack arg, Direction arg2) {
            return !this.dirty && arg2 == Direction.DOWN && arg.getItem() == Items.BONE_MEAL;
        }

        @Override
        public void markDirty() {
            ComposterBlock.emptyComposter(this.state, this.world, this.pos);
            this.dirty = true;
        }
    }

    static class DummyInventory
    extends SimpleInventory
    implements SidedInventory {
        public DummyInventory() {
            super(0);
        }

        @Override
        public int[] getAvailableSlots(Direction arg) {
            return new int[0];
        }

        @Override
        public boolean canInsert(int i, ItemStack arg, @Nullable Direction arg2) {
            return false;
        }

        @Override
        public boolean canExtract(int i, ItemStack arg, Direction arg2) {
            return false;
        }
    }
}

