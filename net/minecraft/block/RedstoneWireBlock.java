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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
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
    private final BlockState dotShape;
    private boolean wiresGivePower = true;

    public RedstoneWireBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(WIRE_CONNECTION_NORTH, WireConnection.NONE)).with(WIRE_CONNECTION_EAST, WireConnection.NONE)).with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)).with(WIRE_CONNECTION_WEST, WireConnection.NONE)).with(POWER, 0));
        this.dotShape = (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.SIDE)).with(WIRE_CONNECTION_EAST, WireConnection.SIDE)).with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE)).with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.field_24416.get(state.with(POWER, 0));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.method_27840(ctx.getWorld(), this.dotShape, ctx.getBlockPos());
    }

    private BlockState method_27840(BlockView world, BlockState state, BlockPos pos) {
        boolean bl7;
        boolean bl = RedstoneWireBlock.isNotConnected(state);
        state = this.method_27843(world, (BlockState)this.getDefaultState().with(POWER, state.get(POWER)), pos);
        if (bl && RedstoneWireBlock.isNotConnected(state)) {
            return state;
        }
        boolean bl2 = state.get(WIRE_CONNECTION_NORTH).isConnected();
        boolean bl3 = state.get(WIRE_CONNECTION_SOUTH).isConnected();
        boolean bl4 = state.get(WIRE_CONNECTION_EAST).isConnected();
        boolean bl5 = state.get(WIRE_CONNECTION_WEST).isConnected();
        boolean bl6 = !bl2 && !bl3;
        boolean bl8 = bl7 = !bl4 && !bl5;
        if (!bl5 && bl6) {
            state = (BlockState)state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
        }
        if (!bl4 && bl6) {
            state = (BlockState)state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
        }
        if (!bl2 && bl7) {
            state = (BlockState)state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
        }
        if (!bl3 && bl7) {
            state = (BlockState)state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
        }
        return state;
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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == Direction.DOWN) {
            return state;
        }
        if (direction == Direction.UP) {
            return this.method_27840(world, state, pos);
        }
        WireConnection lv = this.getRenderConnectionType(world, pos, direction);
        if (lv.isConnected() == ((WireConnection)state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() && !RedstoneWireBlock.isFullyConnected(state)) {
            return (BlockState)state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), lv);
        }
        return this.method_27840(world, (BlockState)((BlockState)this.dotShape.with(POWER, state.get(POWER))).with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), lv), pos);
    }

    private static boolean isFullyConnected(BlockState state) {
        return state.get(WIRE_CONNECTION_NORTH).isConnected() && state.get(WIRE_CONNECTION_SOUTH).isConnected() && state.get(WIRE_CONNECTION_EAST).isConnected() && state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.get(WIRE_CONNECTION_NORTH).isConnected() && !state.get(WIRE_CONNECTION_SOUTH).isConnected() && !state.get(WIRE_CONNECTION_EAST).isConnected() && !state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    @Override
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int j) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            WireConnection lv3 = (WireConnection)state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(lv2));
            if (lv3 == WireConnection.NONE || world.getBlockState(lv.set(pos, lv2)).isOf(this)) continue;
            lv.move(Direction.DOWN);
            BlockState lv4 = world.getBlockState(lv);
            if (!lv4.isOf(Blocks.OBSERVER)) {
                BlockPos lv5 = lv.offset(lv2.getOpposite());
                BlockState lv6 = lv4.getStateForNeighborUpdate(lv2.getOpposite(), world.getBlockState(lv5), world, lv, lv5);
                RedstoneWireBlock.replace(lv4, lv6, world, lv, flags, j);
            }
            lv.set(pos, lv2).move(Direction.UP);
            BlockState lv7 = world.getBlockState(lv);
            if (lv7.isOf(Blocks.OBSERVER)) continue;
            BlockPos lv8 = lv.offset(lv2.getOpposite());
            BlockState lv9 = lv7.getStateForNeighborUpdate(lv2.getOpposite(), world.getBlockState(lv8), world, lv, lv8);
            RedstoneWireBlock.replace(lv7, lv9, world, lv, flags, j);
        }
    }

    private WireConnection getRenderConnectionType(BlockView arg, BlockPos arg2, Direction arg3) {
        return this.method_27841(arg, arg2, arg3, !arg.getBlockState(arg2.up()).isSolidBlock(arg, arg2));
    }

    private WireConnection method_27841(BlockView arg, BlockPos arg2, Direction arg3, boolean bl) {
        boolean bl2;
        BlockPos lv = arg2.offset(arg3);
        BlockState lv2 = arg.getBlockState(lv);
        if (bl && (bl2 = this.canRunOnTop(arg, lv, lv2)) && RedstoneWireBlock.connectsTo(arg.getBlockState(lv.up()))) {
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
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos lv = pos.down();
        BlockState lv2 = world.getBlockState(lv);
        return this.canRunOnTop(world, lv, lv2);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    private void update(World world, BlockPos pos, BlockState state) {
        int i = this.method_27842(world, pos);
        if (state.get(POWER) != i) {
            if (world.getBlockState(pos) == state) {
                world.setBlockState(pos, (BlockState)state.with(POWER, i), 2);
            }
            HashSet set = Sets.newHashSet();
            set.add(pos);
            for (Direction lv : Direction.values()) {
                set.add(pos.offset(lv));
            }
            for (BlockPos lv2 : set) {
                world.updateNeighborsAlways(lv2, this);
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

    private int increasePower(BlockState state) {
        return state.isOf(this) ? state.get(POWER) : 0;
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (!world.getBlockState(pos).isOf(this)) {
            return;
        }
        world.updateNeighborsAlways(pos, this);
        for (Direction lv : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(lv), this);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock()) || world.isClient) {
            return;
        }
        this.update(world, pos, state);
        for (Direction lv : Direction.Type.VERTICAL) {
            world.updateNeighborsAlways(pos.offset(lv), this);
        }
        this.method_27844(world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, moved);
        if (world.isClient) {
            return;
        }
        for (Direction lv : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(lv), this);
        }
        this.update(world, pos, state);
        this.method_27844(world, pos);
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
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (world.isClient) {
            return;
        }
        if (state.canPlaceAt(world, pos)) {
            this.update(world, pos, state);
        } else {
            RedstoneWireBlock.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!this.wiresGivePower) {
            return 0;
        }
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!this.wiresGivePower || direction == Direction.DOWN) {
            return 0;
        }
        int i = state.get(POWER);
        if (i == 0) {
            return 0;
        }
        if (direction == Direction.UP || ((WireConnection)this.method_27840(world, state, pos).get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction.getOpposite()))).isConnected()) {
            return i;
        }
        return 0;
    }

    protected static boolean connectsTo(BlockState state) {
        return RedstoneWireBlock.connectsTo(state, null);
    }

    protected static boolean connectsTo(BlockState state, @Nullable Direction dir) {
        if (state.isOf(Blocks.REDSTONE_WIRE)) {
            return true;
        }
        if (state.isOf(Blocks.REPEATER)) {
            Direction lv = state.get(RepeaterBlock.FACING);
            return lv == dir || lv.getOpposite() == dir;
        }
        if (state.isOf(Blocks.OBSERVER)) {
            return dir == state.get(ObserverBlock.FACING);
        }
        return state.emitsRedstonePower() && dir != null;
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return this.wiresGivePower;
    }

    @Environment(value=EnvType.CLIENT)
    public static int getWireColor(int powerLevel) {
        Vector3f lv = field_24466[powerLevel];
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
        double d = 0.5 + (double)(0.4375f * (float)arg4.getOffsetX()) + (double)(j * (float)arg5.getOffsetX());
        double e = 0.5 + (double)(0.4375f * (float)arg4.getOffsetY()) + (double)(j * (float)arg5.getOffsetY());
        double k = 0.5 + (double)(0.4375f * (float)arg4.getOffsetZ()) + (double)(j * (float)arg5.getOffsetZ());
        arg.addParticle(new DustParticleEffect(arg3.getX(), arg3.getY(), arg3.getZ(), 1.0f), (double)arg2.getX() + d, (double)arg2.getY() + e, (double)arg2.getZ() + k, 0.0, 0.0, 0.0);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i = state.get(POWER);
        if (i == 0) {
            return;
        }
        block4: for (Direction lv : Direction.Type.HORIZONTAL) {
            WireConnection lv2 = (WireConnection)state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(lv));
            switch (lv2) {
                case UP: {
                    this.method_27936(world, random, pos, field_24466[i], lv, Direction.UP, -0.5f, 0.5f);
                }
                case SIDE: {
                    this.method_27936(world, random, pos, field_24466[i], Direction.DOWN, lv, 0.0f, 0.5f);
                    continue block4;
                }
            }
            this.method_27936(world, random, pos, field_24466[i], Direction.DOWN, lv, 0.0f, 0.3f);
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH))).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST))).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH))).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_EAST))).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH));
            }
        }
        return state;
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)state.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            }
        }
        return super.mirror(state, mirror);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, POWER);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.abilities.allowModifyWorld) {
            return ActionResult.PASS;
        }
        if (RedstoneWireBlock.isFullyConnected(state) || RedstoneWireBlock.isNotConnected(state)) {
            BlockState lv = RedstoneWireBlock.isFullyConnected(state) ? this.getDefaultState() : this.dotShape;
            lv = (BlockState)lv.with(POWER, state.get(POWER));
            if ((lv = this.method_27840(world, lv, pos)) != state) {
                world.setBlockState(pos, lv, 3);
                this.method_28482(world, pos, state, lv);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private void method_28482(World arg, BlockPos arg2, BlockState arg3, BlockState arg4) {
        for (Direction lv : Direction.Type.HORIZONTAL) {
            BlockPos lv2 = arg2.offset(lv);
            if (((WireConnection)arg3.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(lv))).isConnected() == ((WireConnection)arg4.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(lv))).isConnected() || !arg.getBlockState(lv2).isSolidBlock(arg, lv2)) continue;
            arg.updateNeighborsExcept(lv2, arg4.getBlock(), lv.getOpposite());
        }
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

