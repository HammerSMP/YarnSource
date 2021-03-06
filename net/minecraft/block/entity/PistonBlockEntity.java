/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PistonBlockEntity
extends BlockEntity
implements Tickable {
    private BlockState pushedBlock;
    private Direction facing;
    private boolean extending;
    private boolean source;
    private static final ThreadLocal<Direction> field_12205 = ThreadLocal.withInitial(() -> null);
    private float progress;
    private float lastProgress;
    private long savedWorldTime;

    public PistonBlockEntity() {
        super(BlockEntityType.PISTON);
    }

    public PistonBlockEntity(BlockState pushedBlock, Direction facing, boolean extending, boolean source) {
        this();
        this.pushedBlock = pushedBlock;
        this.facing = facing;
        this.extending = extending;
        this.source = source;
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public boolean isExtending() {
        return this.extending;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public boolean isSource() {
        return this.source;
    }

    public float getProgress(float tickDelta) {
        if (tickDelta > 1.0f) {
            tickDelta = 1.0f;
        }
        return MathHelper.lerp(tickDelta, this.lastProgress, this.progress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetX(float tickDelta) {
        return (float)this.facing.getOffsetX() * this.getAmountExtended(this.getProgress(tickDelta));
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetY(float tickDelta) {
        return (float)this.facing.getOffsetY() * this.getAmountExtended(this.getProgress(tickDelta));
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetZ(float tickDelta) {
        return (float)this.facing.getOffsetZ() * this.getAmountExtended(this.getProgress(tickDelta));
    }

    private float getAmountExtended(float progress) {
        return this.extending ? progress - 1.0f : 1.0f - progress;
    }

    private BlockState getHeadBlockState() {
        if (!this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock) {
            return (BlockState)((BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.SHORT, this.progress > 0.25f)).with(PistonHeadBlock.TYPE, this.pushedBlock.isOf(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT)).with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING));
        }
        return this.pushedBlock;
    }

    private void pushEntities(float nextProgress) {
        Direction lv = this.getMovementDirection();
        double d = nextProgress - this.progress;
        VoxelShape lv2 = this.getHeadBlockState().getCollisionShape(this.world, this.getPos());
        if (lv2.isEmpty()) {
            return;
        }
        Box lv3 = this.offsetHeadBox(lv2.getBoundingBox());
        List<Entity> list = this.world.getEntities(null, Boxes.stretch(lv3, lv, d).union(lv3));
        if (list.isEmpty()) {
            return;
        }
        List<Box> list2 = lv2.getBoundingBoxes();
        boolean bl = this.pushedBlock.isOf(Blocks.SLIME_BLOCK);
        for (Entity lv4 : list) {
            Box lv8;
            Box lv6;
            Box lv7;
            if (lv4.getPistonBehavior() == PistonBehavior.IGNORE) continue;
            if (bl) {
                if (lv4 instanceof ServerPlayerEntity) continue;
                Vec3d lv5 = lv4.getVelocity();
                double e = lv5.x;
                double g = lv5.y;
                double h = lv5.z;
                switch (lv.getAxis()) {
                    case X: {
                        e = lv.getOffsetX();
                        break;
                    }
                    case Y: {
                        g = lv.getOffsetY();
                        break;
                    }
                    case Z: {
                        h = lv.getOffsetZ();
                    }
                }
                lv4.setVelocity(e, g, h);
            }
            double i = 0.0;
            Iterator<Box> iterator = list2.iterator();
            while (!(!iterator.hasNext() || (lv7 = Boxes.stretch(this.offsetHeadBox(lv6 = iterator.next()), lv, d)).intersects(lv8 = lv4.getBoundingBox()) && (i = Math.max(i, PistonBlockEntity.getIntersectionSize(lv7, lv, lv8))) >= d)) {
            }
            if (i <= 0.0) continue;
            i = Math.min(i, d) + 0.01;
            PistonBlockEntity.method_23672(lv, lv4, i, lv);
            if (this.extending || !this.source) continue;
            this.push(lv4, lv, d);
        }
    }

    private static void method_23672(Direction arg, Entity arg2, double d, Direction arg3) {
        field_12205.set(arg);
        arg2.move(MovementType.PISTON, new Vec3d(d * (double)arg3.getOffsetX(), d * (double)arg3.getOffsetY(), d * (double)arg3.getOffsetZ()));
        field_12205.set(null);
    }

    private void method_23674(float f) {
        if (!this.isPushingHoneyBlock()) {
            return;
        }
        Direction lv = this.getMovementDirection();
        if (!lv.getAxis().isHorizontal()) {
            return;
        }
        double d = this.pushedBlock.getCollisionShape(this.world, this.pos).getMax(Direction.Axis.Y);
        Box lv2 = this.offsetHeadBox(new Box(0.0, d, 0.0, 1.0, 1.5000000999999998, 1.0));
        double e = f - this.progress;
        List<Entity> list = this.world.getEntities((Entity)null, lv2, arg2 -> PistonBlockEntity.method_23671(lv2, arg2));
        for (Entity lv3 : list) {
            PistonBlockEntity.method_23672(lv, lv3, e, lv);
        }
    }

    private static boolean method_23671(Box arg, Entity arg2) {
        return arg2.getPistonBehavior() == PistonBehavior.NORMAL && arg2.isOnGround() && arg2.getX() >= arg.minX && arg2.getX() <= arg.maxX && arg2.getZ() >= arg.minZ && arg2.getZ() <= arg.maxZ;
    }

    private boolean isPushingHoneyBlock() {
        return this.pushedBlock.isOf(Blocks.HONEY_BLOCK);
    }

    public Direction getMovementDirection() {
        return this.extending ? this.facing : this.facing.getOpposite();
    }

    private static double getIntersectionSize(Box arg, Direction arg2, Box arg3) {
        switch (arg2) {
            case EAST: {
                return arg.maxX - arg3.minX;
            }
            case WEST: {
                return arg3.maxX - arg.minX;
            }
            default: {
                return arg.maxY - arg3.minY;
            }
            case DOWN: {
                return arg3.maxY - arg.minY;
            }
            case SOUTH: {
                return arg.maxZ - arg3.minZ;
            }
            case NORTH: 
        }
        return arg3.maxZ - arg.minZ;
    }

    private Box offsetHeadBox(Box box) {
        double d = this.getAmountExtended(this.progress);
        return box.offset((double)this.pos.getX() + d * (double)this.facing.getOffsetX(), (double)this.pos.getY() + d * (double)this.facing.getOffsetY(), (double)this.pos.getZ() + d * (double)this.facing.getOffsetZ());
    }

    private void push(Entity entity, Direction direction, double amount) {
        double f;
        Direction lv3;
        double e;
        Box lv2;
        Box lv = entity.getBoundingBox();
        if (lv.intersects(lv2 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos)) && Math.abs((e = PistonBlockEntity.getIntersectionSize(lv2, lv3 = direction.getOpposite(), lv) + 0.01) - (f = PistonBlockEntity.getIntersectionSize(lv2, lv3, lv.intersection(lv2)) + 0.01)) < 0.01) {
            e = Math.min(e, amount) + 0.01;
            PistonBlockEntity.method_23672(direction, entity, e, lv3);
        }
    }

    public BlockState getPushedBlock() {
        return this.pushedBlock;
    }

    public void finish() {
        if (this.lastProgress < 1.0f && this.world != null) {
            this.lastProgress = this.progress = 1.0f;
            this.world.removeBlockEntity(this.pos);
            this.markRemoved();
            if (this.world.getBlockState(this.pos).isOf(Blocks.MOVING_PISTON)) {
                BlockState lv2;
                if (this.source) {
                    BlockState lv = Blocks.AIR.getDefaultState();
                } else {
                    lv2 = Block.postProcessState(this.pushedBlock, this.world, this.pos);
                }
                this.world.setBlockState(this.pos, lv2, 3);
                this.world.updateNeighbor(this.pos, lv2.getBlock(), this.pos);
            }
        }
    }

    @Override
    public void tick() {
        this.savedWorldTime = this.world.getTime();
        this.lastProgress = this.progress;
        if (this.lastProgress >= 1.0f) {
            this.world.removeBlockEntity(this.pos);
            this.markRemoved();
            if (this.pushedBlock != null && this.world.getBlockState(this.pos).isOf(Blocks.MOVING_PISTON)) {
                BlockState lv = Block.postProcessState(this.pushedBlock, this.world, this.pos);
                if (lv.isAir()) {
                    this.world.setBlockState(this.pos, this.pushedBlock, 84);
                    Block.replace(this.pushedBlock, lv, this.world, this.pos, 3);
                } else {
                    if (lv.contains(Properties.WATERLOGGED) && lv.get(Properties.WATERLOGGED).booleanValue()) {
                        lv = (BlockState)lv.with(Properties.WATERLOGGED, false);
                    }
                    this.world.setBlockState(this.pos, lv, 67);
                    this.world.updateNeighbor(this.pos, lv.getBlock(), this.pos);
                }
            }
            return;
        }
        float f = this.progress + 0.5f;
        this.pushEntities(f);
        this.method_23674(f);
        this.progress = f;
        if (this.progress >= 1.0f) {
            this.progress = 1.0f;
        }
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.pushedBlock = NbtHelper.toBlockState(tag.getCompound("blockState"));
        this.facing = Direction.byId(tag.getInt("facing"));
        this.lastProgress = this.progress = tag.getFloat("progress");
        this.extending = tag.getBoolean("extending");
        this.source = tag.getBoolean("source");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
        tag.putInt("facing", this.facing.getId());
        tag.putFloat("progress", this.lastProgress);
        tag.putBoolean("extending", this.extending);
        tag.putBoolean("source", this.source);
        return tag;
    }

    public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
        BlockState lv5;
        VoxelShape lv2;
        if (!this.extending && this.source) {
            VoxelShape lv = ((BlockState)this.pushedBlock.with(PistonBlock.EXTENDED, true)).getCollisionShape(world, pos);
        } else {
            lv2 = VoxelShapes.empty();
        }
        Direction lv3 = field_12205.get();
        if ((double)this.progress < 1.0 && lv3 == this.getMovementDirection()) {
            return lv2;
        }
        if (this.isSource()) {
            BlockState lv4 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, this.facing)).with(PistonHeadBlock.SHORT, this.extending != 1.0f - this.progress < 0.25f);
        } else {
            lv5 = this.pushedBlock;
        }
        float f = this.getAmountExtended(this.progress);
        double d = (float)this.facing.getOffsetX() * f;
        double e = (float)this.facing.getOffsetY() * f;
        double g = (float)this.facing.getOffsetZ() * f;
        return VoxelShapes.union(lv2, lv5.getCollisionShape(world, pos).offset(d, e, g));
    }

    public long getSavedWorldTime() {
        return this.savedWorldTime;
    }
}

