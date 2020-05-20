/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.LoadingCache
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class NetherPortalBlock
extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public NetherPortalBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        switch (arg.get(AXIS)) {
            case Z: {
                return Z_SHAPE;
            }
        }
        return X_SHAPE;
    }

    @Override
    public void randomTick(BlockState arg, ServerWorld arg2, BlockPos arg3, Random random) {
        if (arg2.getDimension().method_28537() && arg2.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && random.nextInt(2000) < arg2.getDifficulty().getId()) {
            ZombifiedPiglinEntity lv;
            while (arg2.getBlockState(arg3).isOf(this)) {
                arg3 = arg3.down();
            }
            if (arg2.getBlockState(arg3).allowsSpawning(arg2, arg3, EntityType.ZOMBIFIED_PIGLIN) && (lv = EntityType.ZOMBIFIED_PIGLIN.spawn(arg2, null, null, null, arg3.up(), SpawnReason.STRUCTURE, false, false)) != null) {
                lv.netherPortalCooldown = lv.getDefaultNetherPortalCooldown();
            }
        }
    }

    public static boolean createPortalAt(WorldAccess arg, BlockPos arg2) {
        AreaHelper lv = NetherPortalBlock.createAreaHelper(arg, arg2);
        if (lv != null) {
            lv.createPortal();
            return true;
        }
        return false;
    }

    @Nullable
    public static AreaHelper createAreaHelper(WorldAccess arg, BlockPos arg2) {
        AreaHelper lv = new AreaHelper(arg, arg2, Direction.Axis.X);
        if (lv.isValid() && lv.foundPortalBlocks == 0) {
            return lv;
        }
        AreaHelper lv2 = new AreaHelper(arg, arg2, Direction.Axis.Z);
        if (lv2.isValid() && lv2.foundPortalBlocks == 0) {
            return lv2;
        }
        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        boolean bl;
        Direction.Axis lv = arg2.getAxis();
        Direction.Axis lv2 = arg.get(AXIS);
        boolean bl2 = bl = lv2 != lv && lv.isHorizontal();
        if (bl || arg3.isOf(this) || new AreaHelper(arg4, arg5, lv2).wasAlreadyValid()) {
            return super.getStateForNeighborUpdate(arg, arg2, arg3, arg4, arg5, arg6);
        }
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public void onEntityCollision(BlockState arg, World arg2, BlockPos arg3, Entity arg4) {
        if (!arg4.hasVehicle() && !arg4.hasPassengers() && arg4.canUsePortals()) {
            arg4.setInNetherPortal(arg3);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        if (random.nextInt(100) == 0) {
            arg2.playSound((double)arg3.getX() + 0.5, (double)arg3.getY() + 0.5, (double)arg3.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int i = 0; i < 4; ++i) {
            double d = (double)arg3.getX() + (double)random.nextFloat();
            double e = (double)arg3.getY() + (double)random.nextFloat();
            double f = (double)arg3.getZ() + (double)random.nextFloat();
            double g = ((double)random.nextFloat() - 0.5) * 0.5;
            double h = ((double)random.nextFloat() - 0.5) * 0.5;
            double j = ((double)random.nextFloat() - 0.5) * 0.5;
            int k = random.nextInt(2) * 2 - 1;
            if (arg2.getBlockState(arg3.west()).isOf(this) || arg2.getBlockState(arg3.east()).isOf(this)) {
                f = (double)arg3.getZ() + 0.5 + 0.25 * (double)k;
                j = random.nextFloat() * 2.0f * (float)k;
            } else {
                d = (double)arg3.getX() + 0.5 + 0.25 * (double)k;
                g = random.nextFloat() * 2.0f * (float)k;
            }
            arg2.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, j);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView arg, BlockPos arg2, BlockState arg3) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch (arg.get(AXIS)) {
                    case X: {
                        return (BlockState)arg.with(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)arg.with(AXIS, Direction.Axis.X);
                    }
                }
                return arg;
            }
        }
        return arg;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(AXIS);
    }

    public static BlockPattern.Result findPortal(WorldAccess arg, BlockPos arg2) {
        Direction.Axis lv = Direction.Axis.Z;
        AreaHelper lv2 = new AreaHelper(arg, arg2, Direction.Axis.X);
        LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(arg, true);
        if (!lv2.isValid()) {
            lv = Direction.Axis.X;
            lv2 = new AreaHelper(arg, arg2, Direction.Axis.Z);
        }
        if (!lv2.isValid()) {
            return new BlockPattern.Result(arg2, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
        }
        int[] is = new int[Direction.AxisDirection.values().length];
        Direction lv3 = lv2.negativeDir.rotateYCounterclockwise();
        BlockPos lv4 = lv2.lowerCorner.up(lv2.getHeight() - 1);
        for (Direction.AxisDirection lv5 : Direction.AxisDirection.values()) {
            BlockPattern.Result lv6 = new BlockPattern.Result(lv3.getDirection() == lv5 ? lv4 : lv4.offset(lv2.negativeDir, lv2.getWidth() - 1), Direction.get(lv5, lv), Direction.UP, loadingCache, lv2.getWidth(), lv2.getHeight(), 1);
            for (int i = 0; i < lv2.getWidth(); ++i) {
                for (int j = 0; j < lv2.getHeight(); ++j) {
                    CachedBlockPosition lv7 = lv6.translate(i, j, 1);
                    if (lv7.getBlockState().isAir()) continue;
                    int n = lv5.ordinal();
                    is[n] = is[n] + 1;
                }
            }
        }
        Direction.AxisDirection lv8 = Direction.AxisDirection.POSITIVE;
        for (Direction.AxisDirection lv9 : Direction.AxisDirection.values()) {
            if (is[lv9.ordinal()] >= is[lv8.ordinal()]) continue;
            lv8 = lv9;
        }
        return new BlockPattern.Result(lv3.getDirection() == lv8 ? lv4 : lv4.offset(lv2.negativeDir, lv2.getWidth() - 1), Direction.get(lv8, lv), Direction.UP, loadingCache, lv2.getWidth(), lv2.getHeight(), 1);
    }

    public static class AreaHelper {
        private final WorldAccess world;
        private final Direction.Axis axis;
        private final Direction negativeDir;
        private final Direction positiveDir;
        private int foundPortalBlocks;
        @Nullable
        private BlockPos lowerCorner;
        private int height;
        private int width;

        public AreaHelper(WorldAccess arg, BlockPos arg2, Direction.Axis arg3) {
            this.world = arg;
            this.axis = arg3;
            if (arg3 == Direction.Axis.X) {
                this.positiveDir = Direction.EAST;
                this.negativeDir = Direction.WEST;
            } else {
                this.positiveDir = Direction.NORTH;
                this.negativeDir = Direction.SOUTH;
            }
            BlockPos lv = arg2;
            while (arg2.getY() > lv.getY() - 21 && arg2.getY() > 0 && this.validStateInsidePortal(arg.getBlockState(arg2.down()))) {
                arg2 = arg2.down();
            }
            int i = this.distanceToPortalEdge(arg2, this.positiveDir) - 1;
            if (i >= 0) {
                this.lowerCorner = arg2.offset(this.positiveDir, i);
                this.width = this.distanceToPortalEdge(this.lowerCorner, this.negativeDir);
                if (this.width < 2 || this.width > 21) {
                    this.lowerCorner = null;
                    this.width = 0;
                }
            }
            if (this.lowerCorner != null) {
                this.height = this.findHeight();
            }
        }

        protected int distanceToPortalEdge(BlockPos arg, Direction arg2) {
            BlockPos lv;
            int i;
            for (i = 0; i < 22 && this.validStateInsidePortal(this.world.getBlockState(lv = arg.offset(arg2, i))) && this.world.getBlockState(lv.down()).isOf(Blocks.OBSIDIAN); ++i) {
            }
            if (this.world.getBlockState(arg.offset(arg2, i)).isOf(Blocks.OBSIDIAN)) {
                return i;
            }
            return 0;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }

        protected int findHeight() {
            this.height = 0;
            block0 : while (this.height < 21) {
                for (int i = 0; i < this.width; ++i) {
                    BlockPos lv = this.lowerCorner.offset(this.negativeDir, i).up(this.height);
                    BlockState lv2 = this.world.getBlockState(lv);
                    if (!this.validStateInsidePortal(lv2)) break block0;
                    if (lv2.isOf(Blocks.NETHER_PORTAL)) {
                        ++this.foundPortalBlocks;
                    }
                    if (i == 0 ? !this.world.getBlockState(lv.offset(this.positiveDir)).isOf(Blocks.OBSIDIAN) : i == this.width - 1 && !this.world.getBlockState(lv.offset(this.negativeDir)).isOf(Blocks.OBSIDIAN)) break block0;
                }
                ++this.height;
            }
            for (int j = 0; j < this.width; ++j) {
                if (this.world.getBlockState(this.lowerCorner.offset(this.negativeDir, j).up(this.height)).isOf(Blocks.OBSIDIAN)) continue;
                this.height = 0;
                break;
            }
            if (this.height > 21 || this.height < 3) {
                this.lowerCorner = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
            return this.height;
        }

        protected boolean validStateInsidePortal(BlockState arg) {
            return arg.isAir() || arg.isIn(BlockTags.FIRE) || arg.isOf(Blocks.NETHER_PORTAL);
        }

        public boolean isValid() {
            return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void createPortal() {
            for (int i = 0; i < this.width; ++i) {
                BlockPos lv = this.lowerCorner.offset(this.negativeDir, i);
                for (int j = 0; j < this.height; ++j) {
                    this.world.setBlockState(lv.up(j), (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(AXIS, this.axis), 18);
                }
            }
        }

        private boolean portalAlreadyExisted() {
            return this.foundPortalBlocks >= this.width * this.height;
        }

        public boolean wasAlreadyValid() {
            return this.isValid() && this.portalAlreadyExisted();
        }
    }
}

