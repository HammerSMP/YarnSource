/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class RedstoneWireBlock
extends Block {
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
    public static final IntProperty POWER = Properties.POWER;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, WIRE_CONNECTION_NORTH, (Object)Direction.EAST, WIRE_CONNECTION_EAST, (Object)Direction.SOUTH, WIRE_CONNECTION_SOUTH, (Object)Direction.WEST, WIRE_CONNECTION_WEST));
    private static final VoxelShape field_24413 = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0);
    private static final Map<Direction, VoxelShape> field_24414 = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0), (Object)Direction.SOUTH, (Object)Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0), (Object)Direction.EAST, (Object)Block.createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0), (Object)Direction.WEST, (Object)Block.createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)));
    private static final Map<Direction, VoxelShape> field_24415 = Maps.newEnumMap((Map)ImmutableMap.of((Object)Direction.NORTH, (Object)VoxelShapes.union(field_24414.get(Direction.NORTH), Block.createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)), (Object)Direction.SOUTH, (Object)VoxelShapes.union(field_24414.get(Direction.SOUTH), Block.createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)), (Object)Direction.EAST, (Object)VoxelShapes.union(field_24414.get(Direction.EAST), Block.createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)), (Object)Direction.WEST, (Object)VoxelShapes.union(field_24414.get(Direction.WEST), Block.createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))));
    private final Map<BlockState, VoxelShape> field_24416 = Maps.newHashMap();
    private static final Vector3f[] field_24466 = new Vector3f[16];
    private boolean wiresGivePower = true;

    public RedstoneWireBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WIRE_CONNECTION_NORTH, WireConnection.NONE)).with(WIRE_CONNECTION_EAST, WireConnection.NONE)).with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)).with(WIRE_CONNECTION_WEST, WireConnection.NONE)).with(POWER, 0));
        for (BlockState lv : this.getStateManager().getStates()) {
            if (lv.get(POWER) != 0) continue;
            this.field_24416.put(lv, this.method_27845(lv));
        }
    }

    private VoxelShape method_27845(BlockState arg) {
        VoxelShape lv = field_24413;
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            WireConnection lv3 = (WireConnection)arg.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(lv2));
            if (lv3 == WireConnection.SIDE) {
                lv = VoxelShapes.union(lv, field_24414.get(lv2));
                continue;
            }
            if (lv3 != WireConnection.UP) continue;
            lv = VoxelShapes.union(lv, field_24415.get(lv2));
        }
        return lv;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState arg, BlockView arg2, BlockPos arg3, ShapeContext arg4) {
        return this.field_24416.get(arg.with(POWER, 0));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext arg) {
        return this.method_27840(arg.getWorld(), this.getDefaultState(), arg.getBlockPos());
    }

    private BlockState method_27840(BlockView arg, BlockState arg2, BlockPos arg3) {
        boolean bl6;
        arg2 = this.method_27843(arg, (BlockState)this.getDefaultState().with(POWER, arg2.get(POWER)), arg3);
        boolean bl = arg2.get(WIRE_CONNECTION_NORTH).isConnected();
        boolean bl2 = arg2.get(WIRE_CONNECTION_SOUTH).isConnected();
        boolean bl3 = arg2.get(WIRE_CONNECTION_EAST).isConnected();
        boolean bl4 = arg2.get(WIRE_CONNECTION_WEST).isConnected();
        boolean bl5 = !bl && !bl2;
        boolean bl7 = bl6 = !bl3 && !bl4;
        if (!bl4 && bl5) {
            arg2 = (BlockState)arg2.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
        }
        if (!bl3 && bl5) {
            arg2 = (BlockState)arg2.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
        }
        if (!bl && bl6) {
            arg2 = (BlockState)arg2.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
        }
        if (!bl2 && bl6) {
            arg2 = (BlockState)arg2.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
        }
        return arg2;
    }

    private BlockState method_27843(BlockView arg, BlockState arg2, BlockPos arg3) {
        boolean bl = !arg.getBlockState(arg3.up()).isSolidBlock(arg, arg3);
        for (Direction lv : Direction.Type.HORIZONTAL) {
            if (((WireConnection)arg2.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(lv))).isConnected()) continue;
            WireConnection lv2 = this.method_27841(arg, arg3, lv, bl);
            arg2 = (BlockState)arg2.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(lv), lv2);
        }
        return arg2;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState arg, Direction arg2, BlockState arg3, WorldAccess arg4, BlockPos arg5, BlockPos arg6) {
        if (arg2 == Direction.DOWN) {
            return arg;
        }
        if (arg2 == Direction.UP) {
            return this.method_27840(arg4, arg, arg5);
        }
        WireConnection lv = this.getRenderConnectionType(arg4, arg5, arg2);
        if (lv.isConnected() == ((WireConnection)arg.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(arg2))).isConnected() && !RedstoneWireBlock.method_27846(arg)) {
            return (BlockState)arg.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(arg2), lv);
        }
        return this.method_27840(arg4, (BlockState)((BlockState)this.getDefaultState().with(POWER, arg.get(POWER))).with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(arg2), lv), arg5);
    }

    private static boolean method_27846(BlockState arg) {
        return arg.get(WIRE_CONNECTION_NORTH).isConnected() && arg.get(WIRE_CONNECTION_SOUTH).isConnected() && arg.get(WIRE_CONNECTION_EAST).isConnected() && arg.get(WIRE_CONNECTION_WEST).isConnected();
    }

    @Override
    public void prepare(BlockState arg, WorldAccess arg2, BlockPos arg3, int i) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            WireConnection lv3 = (WireConnection)arg.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(lv2));
            if (lv3 == WireConnection.NONE || arg2.getBlockState(lv.set(arg3, lv2)).isOf(this)) continue;
            lv.move(Direction.DOWN);
            BlockState lv4 = arg2.getBlockState(lv);
            if (!lv4.isOf(Blocks.OBSERVER)) {
                BlockPos lv5 = lv.offset(lv2.getOpposite());
                BlockState lv6 = lv4.getStateForNeighborUpdate(lv2.getOpposite(), arg2.getBlockState(lv5), arg2, lv, lv5);
                RedstoneWireBlock.replaceBlock(lv4, lv6, arg2, lv, i);
            }
            lv.set(arg3, lv2).move(Direction.UP);
            BlockState lv7 = arg2.getBlockState(lv);
            if (lv7.isOf(Blocks.OBSERVER)) continue;
            BlockPos lv8 = lv.offset(lv2.getOpposite());
            BlockState lv9 = lv7.getStateForNeighborUpdate(lv2.getOpposite(), arg2.getBlockState(lv8), arg2, lv, lv8);
            RedstoneWireBlock.replaceBlock(lv7, lv9, arg2, lv, i);
        }
    }

    private WireConnection getRenderConnectionType(BlockView arg, BlockPos arg2, Direction arg3) {
        return this.method_27841(arg, arg2, arg3, !arg.getBlockState(arg2.up()).isSolidBlock(arg, arg2));
    }

    private WireConnection method_27841(BlockView arg, BlockPos arg2, Direction arg3, boolean bl) {
        boolean bl2;
        BlockPos lv = arg2.offset(arg3);
        BlockState lv2 = arg.getBlockState(lv);
        if (bl && (bl2 = this.method_27937(arg, lv, lv2)) && RedstoneWireBlock.connectsTo(arg.getBlockState(lv.up()))) {
            if (lv2.isSideSolidFullSquare(arg, lv, arg3.getOpposite())) {
                return WireConnection.UP;
            }
            return WireConnection.SIDE;
        }
        if (RedstoneWireBlock.connectsTo(lv2, arg3) || !lv2.isSolidBlock(arg, lv) && RedstoneWireBlock.connectsTo(arg.getBlockState(lv.down()))) {
            return WireConnection.SIDE;
        }
        return WireConnection.NONE;
    }

    @Override
    public boolean canPlaceAt(BlockState arg, WorldView arg2, BlockPos arg3) {
        BlockPos lv = arg3.down();
        BlockState lv2 = arg2.getBlockState(lv);
        return this.method_27937(arg2, lv, lv2);
    }

    private boolean method_27937(BlockView arg, BlockPos arg2, BlockState arg3) {
        return arg3.isSideSolidFullSquare(arg, arg2, Direction.UP) || arg3.isOf(Blocks.HOPPER);
    }

    private void update(World arg, BlockPos arg2, BlockState arg3) {
        int i = this.method_27842(arg, arg2);
        if (arg3.get(POWER) != i) {
            if (arg.getBlockState(arg2) == arg3) {
                arg.setBlockState(arg2, (BlockState)arg3.with(POWER, i), 2);
            }
            HashSet set = Sets.newHashSet();
            set.add(arg2);
            for (Direction lv : Direction.values()) {
                set.add(arg2.offset(lv));
            }
            for (BlockPos lv2 : set) {
                arg.updateNeighborsAlways(lv2, this);
            }
        }
    }

    private int method_27842(World arg, BlockPos arg2) {
        this.wiresGivePower = false;
        int i = arg.getReceivedRedstonePower(arg2);
        this.wiresGivePower = true;
        int j = 0;
        if (i < 15) {
            for (Direction lv : Direction.Type.HORIZONTAL) {
                BlockPos lv2 = arg2.offset(lv);
                BlockState lv3 = arg.getBlockState(lv2);
                j = Math.max(j, this.increasePower(lv3));
                BlockPos lv4 = arg2.up();
                if (lv3.isSolidBlock(arg, lv2) && !arg.getBlockState(lv4).isSolidBlock(arg, lv4)) {
                    j = Math.max(j, this.increasePower(arg.getBlockState(lv2.up())));
                    continue;
                }
                if (lv3.isSolidBlock(arg, lv2)) continue;
                j = Math.max(j, this.increasePower(arg.getBlockState(lv2.down())));
            }
        }
        return Math.max(i, j - 1);
    }

    private int increasePower(BlockState arg) {
        return arg.isOf(this) ? arg.get(POWER) : 0;
    }

    private void updateNeighbors(World arg, BlockPos arg2) {
        if (!arg.getBlockState(arg2).isOf(this)) {
            return;
        }
        arg.updateNeighborsAlways(arg2, this);
        for (Direction lv : Direction.values()) {
            arg.updateNeighborsAlways(arg2.offset(lv), this);
        }
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg4.isOf(arg.getBlock()) || arg2.isClient) {
            return;
        }
        this.update(arg2, arg3, arg);
        for (Direction lv : Direction.Type.VERTICAL) {
            arg2.updateNeighborsAlways(arg3.offset(lv), this);
        }
        this.method_27844(arg2, arg3);
    }

    @Override
    public void onStateReplaced(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (bl || arg.isOf(arg4.getBlock())) {
            return;
        }
        super.onStateReplaced(arg, arg2, arg3, arg4, bl);
        if (arg2.isClient) {
            return;
        }
        for (Direction lv : Direction.values()) {
            arg2.updateNeighborsAlways(arg3.offset(lv), this);
        }
        this.update(arg2, arg3, arg);
        this.method_27844(arg2, arg3);
    }

    private void method_27844(World arg, BlockPos arg2) {
        for (Direction lv : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(arg, arg2.offset(lv));
        }
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            BlockPos lv3 = arg2.offset(lv2);
            if (arg.getBlockState(lv3).isSolidBlock(arg, lv3)) {
                this.updateNeighbors(arg, lv3.up());
                continue;
            }
            this.updateNeighbors(arg, lv3.down());
        }
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg2.isClient) {
            return;
        }
        if (arg.canPlaceAt(arg2, arg3)) {
            this.update(arg2, arg3, arg);
        } else {
            RedstoneWireBlock.dropStacks(arg, arg2, arg3);
            arg2.removeBlock(arg3, false);
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (!this.wiresGivePower) {
            return 0;
        }
        return arg.getWeakRedstonePower(arg2, arg3, arg4);
    }

    @Override
    public int getWeakRedstonePower(BlockState arg, BlockView arg2, BlockPos arg3, Direction arg4) {
        if (!this.wiresGivePower || arg4 == Direction.DOWN) {
            return 0;
        }
        int i = arg.get(POWER);
        if (i == 0) {
            return 0;
        }
        if (arg4 == Direction.UP || ((WireConnection)this.method_27840(arg2, arg, arg3).get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(arg4.getOpposite()))).isConnected()) {
            return i;
        }
        return 0;
    }

    protected static boolean connectsTo(BlockState arg) {
        return RedstoneWireBlock.connectsTo(arg, null);
    }

    protected static boolean connectsTo(BlockState arg, @Nullable Direction arg2) {
        if (arg.isOf(Blocks.REDSTONE_WIRE)) {
            return true;
        }
        if (arg.isOf(Blocks.REPEATER)) {
            Direction lv = arg.get(RepeaterBlock.FACING);
            return lv == arg2 || lv.getOpposite() == arg2;
        }
        if (arg.isOf(Blocks.OBSERVER)) {
            return arg2 == arg.get(ObserverBlock.FACING);
        }
        return arg.emitsRedstonePower() && arg2 != null;
    }

    @Override
    public boolean emitsRedstonePower(BlockState arg) {
        return this.wiresGivePower;
    }

    @Environment(value=EnvType.CLIENT)
    public static int getWireColor(int i) {
        Vector3f lv = field_24466[i];
        return MathHelper.packRgb(lv.getX(), lv.getY(), lv.getZ());
    }

    @Environment(value=EnvType.CLIENT)
    private void method_27936(World arg, Random random, BlockPos arg2, Vector3f arg3, Direction arg4, Direction arg5, float f, float g) {
        float h = g - f;
        if (random.nextFloat() >= 0.2f * h) {
            return;
        }
        float i = 0.4375f;
        float j = f + h * random.nextFloat();
        float k = 0.5f + 0.4375f * (float)arg4.getOffsetX() + j * (float)arg5.getOffsetX();
        float l = 0.5f + 0.4375f * (float)arg4.getOffsetY() + j * (float)arg5.getOffsetY();
        float m = 0.5f + 0.4375f * (float)arg4.getOffsetZ() + j * (float)arg5.getOffsetZ();
        arg.addParticle(new DustParticleEffect(arg3.getX(), arg3.getY(), arg3.getZ(), 1.0f), (float)arg2.getX() + k, (float)arg2.getY() + l, (float)arg2.getZ() + m, 0.0, 0.0, 0.0);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState arg, World arg2, BlockPos arg3, Random random) {
        int i = arg.get(POWER);
        if (i == 0) {
            return;
        }
        block4: for (Direction lv : Direction.Type.HORIZONTAL) {
            WireConnection lv2 = (WireConnection)arg.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(lv));
            switch (lv2) {
                case UP: {
                    this.method_27936(arg2, random, arg3, field_24466[i], lv, Direction.UP, -0.5f, 0.5f);
                }
                case SIDE: {
                    this.method_27936(arg2, random, arg3, field_24466[i], Direction.DOWN, lv, 0.0f, 0.5f);
                    continue block4;
                }
            }
            this.method_27936(arg2, random, arg3, field_24466[i], Direction.DOWN, lv, 0.0f, 0.3f);
        }
    }

    @Override
    public BlockState rotate(BlockState arg, BlockRotation arg2) {
        switch (arg2) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(WIRE_CONNECTION_NORTH, arg.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_EAST, arg.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_SOUTH, arg.get(WIRE_CONNECTION_NORTH))).with(WIRE_CONNECTION_WEST, arg.get(WIRE_CONNECTION_EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(WIRE_CONNECTION_NORTH, arg.get(WIRE_CONNECTION_EAST))).with(WIRE_CONNECTION_EAST, arg.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_SOUTH, arg.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_WEST, arg.get(WIRE_CONNECTION_NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)arg.with(WIRE_CONNECTION_NORTH, arg.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_EAST, arg.get(WIRE_CONNECTION_NORTH))).with(WIRE_CONNECTION_SOUTH, arg.get(WIRE_CONNECTION_EAST))).with(WIRE_CONNECTION_WEST, arg.get(WIRE_CONNECTION_SOUTH));
            }
        }
        return arg;
    }

    @Override
    public BlockState mirror(BlockState arg, BlockMirror arg2) {
        switch (arg2) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)arg.with(WIRE_CONNECTION_NORTH, arg.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_SOUTH, arg.get(WIRE_CONNECTION_NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)arg.with(WIRE_CONNECTION_EAST, arg.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_WEST, arg.get(WIRE_CONNECTION_EAST));
            }
        }
        return super.mirror(arg, arg2);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWER);
    }

    static {
        for (int i = 0; i <= 15; ++i) {
            float f;
            float g = f * 0.6f + ((f = (float)i / 15.0f) > 0.0f ? 0.4f : 0.3f);
            float h = MathHelper.clamp(f * f * 0.7f - 0.5f, 0.0f, 1.0f);
            float j = MathHelper.clamp(f * f * 0.6f - 0.7f, 0.0f, 1.0f);
            RedstoneWireBlock.field_24466[i] = new Vector3f(g, h, j);
        }
    }
}

