/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanMap
 *  it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectMap
 *  it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
 */
package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class FlowableFluid
extends Fluid {
    public static final BooleanProperty FALLING = Properties.FALLING;
    public static final IntProperty LEVEL = Properties.LEVEL_1_8;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> field_15901 = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200){

            protected void rehash(int i) {
            }
        };
        object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
        return object2ByteLinkedOpenHashMap;
    });
    private final Map<FluidState, VoxelShape> shapeCache = Maps.newIdentityHashMap();

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> arg) {
        arg.add(FALLING);
    }

    @Override
    public Vec3d getVelocity(BlockView arg, BlockPos arg2, FluidState arg3) {
        double d = 0.0;
        double e = 0.0;
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            lv.set(arg2, lv2);
            FluidState lv3 = arg.getFluidState(lv);
            if (!this.method_15748(lv3)) continue;
            float f = lv3.getHeight();
            float g = 0.0f;
            if (f == 0.0f) {
                Vec3i lv4;
                FluidState lv5;
                if (!arg.getBlockState(lv).getMaterial().blocksMovement() && this.method_15748(lv5 = arg.getFluidState((BlockPos)(lv4 = lv.down()))) && (f = lv5.getHeight()) > 0.0f) {
                    g = arg3.getHeight() - (f - 0.8888889f);
                }
            } else if (f > 0.0f) {
                g = arg3.getHeight() - f;
            }
            if (g == 0.0f) continue;
            d += (double)((float)lv2.getOffsetX() * g);
            e += (double)((float)lv2.getOffsetZ() * g);
        }
        Vec3d lv6 = new Vec3d(d, 0.0, e);
        if (arg3.get(FALLING).booleanValue()) {
            for (Direction lv7 : Direction.Type.HORIZONTAL) {
                lv.set(arg2, lv7);
                if (!this.method_15749(arg, lv, lv7) && !this.method_15749(arg, lv.up(), lv7)) continue;
                lv6 = lv6.normalize().add(0.0, -6.0, 0.0);
                break;
            }
        }
        return lv6.normalize();
    }

    private boolean method_15748(FluidState arg) {
        return arg.isEmpty() || arg.getFluid().matchesType(this);
    }

    protected boolean method_15749(BlockView arg, BlockPos arg2, Direction arg3) {
        BlockState lv = arg.getBlockState(arg2);
        FluidState lv2 = arg.getFluidState(arg2);
        if (lv2.getFluid().matchesType(this)) {
            return false;
        }
        if (arg3 == Direction.UP) {
            return true;
        }
        if (lv.getMaterial() == Material.ICE) {
            return false;
        }
        return lv.isSideSolidFullSquare(arg, arg2, arg3);
    }

    protected void tryFlow(WorldAccess arg, BlockPos arg2, FluidState arg3) {
        if (arg3.isEmpty()) {
            return;
        }
        BlockState lv = arg.getBlockState(arg2);
        BlockPos lv2 = arg2.down();
        BlockState lv3 = arg.getBlockState(lv2);
        FluidState lv4 = this.getUpdatedState(arg, lv2, lv3);
        if (this.canFlow(arg, arg2, lv, Direction.DOWN, lv2, lv3, arg.getFluidState(lv2), lv4.getFluid())) {
            this.flow(arg, lv2, lv3, Direction.DOWN, lv4);
            if (this.method_15740(arg, arg2) >= 3) {
                this.method_15744(arg, arg2, arg3, lv);
            }
        } else if (arg3.isStill() || !this.method_15736(arg, lv4.getFluid(), arg2, lv, lv2, lv3)) {
            this.method_15744(arg, arg2, arg3, lv);
        }
    }

    private void method_15744(WorldAccess arg, BlockPos arg2, FluidState arg3, BlockState arg4) {
        int i = arg3.getLevel() - this.getLevelDecreasePerBlock(arg);
        if (arg3.get(FALLING).booleanValue()) {
            i = 7;
        }
        if (i <= 0) {
            return;
        }
        Map<Direction, FluidState> map = this.getSpread(arg, arg2, arg4);
        for (Map.Entry<Direction, FluidState> entry : map.entrySet()) {
            BlockState lv4;
            Direction lv = entry.getKey();
            FluidState lv2 = entry.getValue();
            BlockPos lv3 = arg2.offset(lv);
            if (!this.canFlow(arg, arg2, arg4, lv, lv3, lv4 = arg.getBlockState(lv3), arg.getFluidState(lv3), lv2.getFluid())) continue;
            this.flow(arg, lv3, lv4, lv, lv2);
        }
    }

    protected FluidState getUpdatedState(WorldView arg, BlockPos arg2, BlockState arg3) {
        BlockPos lv7;
        BlockState lv8;
        FluidState lv9;
        int i = 0;
        int j = 0;
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BlockPos lv2 = arg2.offset(lv);
            BlockState lv3 = arg.getBlockState(lv2);
            FluidState lv4 = lv3.getFluidState();
            if (!lv4.getFluid().matchesType(this) || !this.receivesFlow(lv, arg, arg2, arg3, lv2, lv3)) continue;
            if (lv4.isStill()) {
                ++j;
            }
            i = Math.max(i, lv4.getLevel());
        }
        if (this.isInfinite() && j >= 2) {
            BlockState lv5 = arg.getBlockState(arg2.down());
            FluidState lv6 = lv5.getFluidState();
            if (lv5.getMaterial().isSolid() || this.isMatchingAndStill(lv6)) {
                return this.getStill(false);
            }
        }
        if (!(lv9 = (lv8 = arg.getBlockState(lv7 = arg2.up())).getFluidState()).isEmpty() && lv9.getFluid().matchesType(this) && this.receivesFlow(Direction.UP, arg, arg2, arg3, lv7, lv8)) {
            return this.getFlowing(8, true);
        }
        int k = i - this.getLevelDecreasePerBlock(arg);
        if (k <= 0) {
            return Fluids.EMPTY.getDefaultState();
        }
        return this.getFlowing(k, false);
    }

    private boolean receivesFlow(Direction arg, BlockView arg2, BlockPos arg3, BlockState arg4, BlockPos arg5, BlockState arg6) {
        VoxelShape lv4;
        VoxelShape lv3;
        boolean bl;
        Object lv2;
        Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap2;
        if (arg4.getBlock().hasDynamicBounds() || arg6.getBlock().hasDynamicBounds()) {
            Object object2ByteLinkedOpenHashMap = null;
        } else {
            object2ByteLinkedOpenHashMap2 = field_15901.get();
        }
        if (object2ByteLinkedOpenHashMap2 != null) {
            Block.NeighborGroup lv = new Block.NeighborGroup(arg4, arg6, arg);
            byte b = object2ByteLinkedOpenHashMap2.getAndMoveToFirst((Object)lv);
            if (b != 127) {
                return b != 0;
            }
        } else {
            lv2 = null;
        }
        boolean bl2 = bl = !VoxelShapes.adjacentSidesCoverSquare(lv3 = arg4.getCollisionShape(arg2, arg3), lv4 = arg6.getCollisionShape(arg2, arg5), arg);
        if (object2ByteLinkedOpenHashMap2 != null) {
            if (object2ByteLinkedOpenHashMap2.size() == 200) {
                object2ByteLinkedOpenHashMap2.removeLastByte();
            }
            object2ByteLinkedOpenHashMap2.putAndMoveToFirst(lv2, (byte)(bl ? 1 : 0));
        }
        return bl;
    }

    public abstract Fluid getFlowing();

    public FluidState getFlowing(int i, boolean bl) {
        return (FluidState)((FluidState)this.getFlowing().getDefaultState().with(LEVEL, i)).with(FALLING, bl);
    }

    public abstract Fluid getStill();

    public FluidState getStill(boolean bl) {
        return (FluidState)this.getStill().getDefaultState().with(FALLING, bl);
    }

    protected abstract boolean isInfinite();

    protected void flow(WorldAccess arg, BlockPos arg2, BlockState arg3, Direction arg4, FluidState arg5) {
        if (arg3.getBlock() instanceof FluidFillable) {
            ((FluidFillable)((Object)arg3.getBlock())).tryFillWithFluid(arg, arg2, arg3, arg5);
        } else {
            if (!arg3.isAir()) {
                this.beforeBreakingBlock(arg, arg2, arg3);
            }
            arg.setBlockState(arg2, arg5.getBlockState(), 3);
        }
    }

    protected abstract void beforeBreakingBlock(WorldAccess var1, BlockPos var2, BlockState var3);

    private static short method_15747(BlockPos arg, BlockPos arg2) {
        int i = arg2.getX() - arg.getX();
        int j = arg2.getZ() - arg.getZ();
        return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
    }

    protected int method_15742(WorldView arg, BlockPos arg2, int i2, Direction arg3, BlockState arg4, BlockPos arg5, Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap, Short2BooleanMap short2BooleanMap) {
        int j = 1000;
        for (Direction lv : Direction.Type.HORIZONTAL) {
            int k;
            if (lv == arg3) continue;
            BlockPos lv2 = arg2.offset(lv);
            short s = FlowableFluid.method_15747(arg5, lv2);
            Pair pair = (Pair)short2ObjectMap.computeIfAbsent(s, i -> {
                BlockState lv = arg.getBlockState(lv2);
                return Pair.of((Object)lv, (Object)lv.getFluidState());
            });
            BlockState lv3 = (BlockState)pair.getFirst();
            FluidState lv4 = (FluidState)pair.getSecond();
            if (!this.canFlowThrough(arg, this.getFlowing(), arg2, arg4, lv, lv2, lv3, lv4)) continue;
            boolean bl = short2BooleanMap.computeIfAbsent(s, i -> {
                BlockPos lv = lv2.down();
                BlockState lv2 = arg.getBlockState(lv);
                return this.method_15736(arg, this.getFlowing(), lv2, lv3, lv, lv2);
            });
            if (bl) {
                return i2;
            }
            if (i2 >= this.getFlowSpeed(arg) || (k = this.method_15742(arg, lv2, i2 + 1, lv.getOpposite(), lv3, arg5, short2ObjectMap, short2BooleanMap)) >= j) continue;
            j = k;
        }
        return j;
    }

    private boolean method_15736(BlockView arg, Fluid arg2, BlockPos arg3, BlockState arg4, BlockPos arg5, BlockState arg6) {
        if (!this.receivesFlow(Direction.DOWN, arg, arg3, arg4, arg5, arg6)) {
            return false;
        }
        if (arg6.getFluidState().getFluid().matchesType(this)) {
            return true;
        }
        return this.canFill(arg, arg5, arg6, arg2);
    }

    private boolean canFlowThrough(BlockView arg, Fluid arg2, BlockPos arg3, BlockState arg4, Direction arg5, BlockPos arg6, BlockState arg7, FluidState arg8) {
        return !this.isMatchingAndStill(arg8) && this.receivesFlow(arg5, arg, arg3, arg4, arg6, arg7) && this.canFill(arg, arg6, arg7, arg2);
    }

    private boolean isMatchingAndStill(FluidState arg) {
        return arg.getFluid().matchesType(this) && arg.isStill();
    }

    protected abstract int getFlowSpeed(WorldView var1);

    private int method_15740(WorldView arg, BlockPos arg2) {
        int i = 0;
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BlockPos lv2 = arg2.offset(lv);
            FluidState lv3 = arg.getFluidState(lv2);
            if (!this.isMatchingAndStill(lv3)) continue;
            ++i;
        }
        return i;
    }

    protected Map<Direction, FluidState> getSpread(WorldView arg, BlockPos arg2, BlockState arg3) {
        int i2 = 1000;
        EnumMap map = Maps.newEnumMap(Direction.class);
        Short2ObjectOpenHashMap short2ObjectMap = new Short2ObjectOpenHashMap();
        Short2BooleanOpenHashMap short2BooleanMap = new Short2BooleanOpenHashMap();
        for (Direction lv : Direction.Type.HORIZONTAL) {
            int k;
            BlockPos lv2 = arg2.offset(lv);
            short s = FlowableFluid.method_15747(arg2, lv2);
            Pair pair = (Pair)short2ObjectMap.computeIfAbsent(s, i -> {
                BlockState lv = arg.getBlockState(lv2);
                return Pair.of((Object)lv, (Object)lv.getFluidState());
            });
            BlockState lv3 = (BlockState)pair.getFirst();
            FluidState lv4 = (FluidState)pair.getSecond();
            FluidState lv5 = this.getUpdatedState(arg, lv2, lv3);
            if (!this.canFlowThrough(arg, lv5.getFluid(), arg2, arg3, lv, lv2, lv3, lv4)) continue;
            BlockPos lv6 = lv2.down();
            boolean bl = short2BooleanMap.computeIfAbsent(s, i -> {
                BlockState lv = arg.getBlockState(lv6);
                return this.method_15736(arg, this.getFlowing(), lv2, lv3, lv6, lv);
            });
            if (bl) {
                boolean j = false;
            } else {
                k = this.method_15742(arg, lv2, 1, lv.getOpposite(), lv3, arg2, (Short2ObjectMap<Pair<BlockState, FluidState>>)short2ObjectMap, (Short2BooleanMap)short2BooleanMap);
            }
            if (k < i2) {
                map.clear();
            }
            if (k > i2) continue;
            map.put(lv, lv5);
            i2 = k;
        }
        return map;
    }

    private boolean canFill(BlockView arg, BlockPos arg2, BlockState arg3, Fluid arg4) {
        Block lv = arg3.getBlock();
        if (lv instanceof FluidFillable) {
            return ((FluidFillable)((Object)lv)).canFillWithFluid(arg, arg2, arg3, arg4);
        }
        if (lv instanceof DoorBlock || lv.isIn(BlockTags.SIGNS) || lv == Blocks.LADDER || lv == Blocks.SUGAR_CANE || lv == Blocks.BUBBLE_COLUMN) {
            return false;
        }
        Material lv2 = arg3.getMaterial();
        if (lv2 == Material.PORTAL || lv2 == Material.STRUCTURE_VOID || lv2 == Material.UNDERWATER_PLANT || lv2 == Material.REPLACEABLE_UNDERWATER_PLANT) {
            return false;
        }
        return !lv2.blocksMovement();
    }

    protected boolean canFlow(BlockView arg, BlockPos arg2, BlockState arg3, Direction arg4, BlockPos arg5, BlockState arg6, FluidState arg7, Fluid arg8) {
        return arg7.canBeReplacedWith(arg, arg5, arg8, arg4) && this.receivesFlow(arg4, arg, arg2, arg3, arg5, arg6) && this.canFill(arg, arg5, arg6, arg8);
    }

    protected abstract int getLevelDecreasePerBlock(WorldView var1);

    protected int getNextTickDelay(World arg, BlockPos arg2, FluidState arg3, FluidState arg4) {
        return this.getTickRate(arg);
    }

    @Override
    public void onScheduledTick(World arg, BlockPos arg2, FluidState arg3) {
        if (!arg3.isStill()) {
            FluidState lv = this.getUpdatedState(arg, arg2, arg.getBlockState(arg2));
            int i = this.getNextTickDelay(arg, arg2, arg3, lv);
            if (lv.isEmpty()) {
                arg3 = lv;
                arg.setBlockState(arg2, Blocks.AIR.getDefaultState(), 3);
            } else if (!lv.equals(arg3)) {
                arg3 = lv;
                BlockState lv2 = arg3.getBlockState();
                arg.setBlockState(arg2, lv2, 2);
                arg.getFluidTickScheduler().schedule(arg2, arg3.getFluid(), i);
                arg.updateNeighborsAlways(arg2, lv2.getBlock());
            }
        }
        this.tryFlow(arg, arg2, arg3);
    }

    protected static int method_15741(FluidState arg) {
        if (arg.isStill()) {
            return 0;
        }
        return 8 - Math.min(arg.getLevel(), 8) + (arg.get(FALLING) != false ? 8 : 0);
    }

    private static boolean isFluidAboveEqual(FluidState arg, BlockView arg2, BlockPos arg3) {
        return arg.getFluid().matchesType(arg2.getFluidState(arg3.up()).getFluid());
    }

    @Override
    public float getHeight(FluidState arg, BlockView arg2, BlockPos arg3) {
        if (FlowableFluid.isFluidAboveEqual(arg, arg2, arg3)) {
            return 1.0f;
        }
        return arg.getHeight();
    }

    @Override
    public float getHeight(FluidState arg) {
        return (float)arg.getLevel() / 9.0f;
    }

    @Override
    public VoxelShape getShape(FluidState arg, BlockView arg2, BlockPos arg32) {
        if (arg.getLevel() == 9 && FlowableFluid.isFluidAboveEqual(arg, arg2, arg32)) {
            return VoxelShapes.fullCube();
        }
        return this.shapeCache.computeIfAbsent(arg, arg3 -> VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, arg3.getHeight(arg2, arg32), 1.0));
    }
}

