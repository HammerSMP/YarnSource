/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.class_5454;
import net.minecraft.class_5459;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldAccess;

public class AreaHelper {
    private static final AbstractBlock.ContextPredicate field_25883 = (arg, arg2, arg3) -> arg.isOf(Blocks.OBSIDIAN);
    private final WorldAccess world;
    private final Direction.Axis axis;
    private final Direction negativeDir;
    private int foundPortalBlocks;
    @Nullable
    private BlockPos lowerCorner;
    private int height;
    private int width;

    public static Optional<AreaHelper> method_30485(WorldAccess arg2, BlockPos arg22, Direction.Axis arg3) {
        return AreaHelper.method_30486(arg2, arg22, arg -> arg.isValid() && arg.foundPortalBlocks == 0, arg3);
    }

    public static Optional<AreaHelper> method_30486(WorldAccess arg, BlockPos arg2, Predicate<AreaHelper> predicate, Direction.Axis arg3) {
        Optional<AreaHelper> optional = Optional.of(new AreaHelper(arg, arg2, arg3)).filter(predicate);
        if (optional.isPresent()) {
            return optional;
        }
        Direction.Axis lv = arg3 == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        return Optional.of(new AreaHelper(arg, arg2, lv)).filter(predicate);
    }

    public AreaHelper(WorldAccess arg, BlockPos arg2, Direction.Axis arg3) {
        this.world = arg;
        this.axis = arg3;
        this.negativeDir = arg3 == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
        this.lowerCorner = this.method_30492(arg2);
        if (this.lowerCorner == null) {
            this.lowerCorner = arg2;
            this.width = 1;
            this.height = 1;
        } else {
            this.width = this.method_30495();
            if (this.width > 0) {
                this.height = this.method_30496();
            }
        }
    }

    @Nullable
    private BlockPos method_30492(BlockPos arg) {
        int i = Math.max(0, arg.getY() - 21);
        while (arg.getY() > i && AreaHelper.validStateInsidePortal(this.world.getBlockState(arg.down()))) {
            arg = arg.down();
        }
        Direction lv = this.negativeDir.getOpposite();
        int j = this.method_30493(arg, lv) - 1;
        if (j < 0) {
            return null;
        }
        return arg.offset(lv, j);
    }

    private int method_30495() {
        int i = this.method_30493(this.lowerCorner, this.negativeDir);
        if (i < 2 || i > 21) {
            return 0;
        }
        return i;
    }

    private int method_30493(BlockPos arg, Direction arg2) {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int i = 0; i <= 21; ++i) {
            lv.set(arg).move(arg2, i);
            BlockState lv2 = this.world.getBlockState(lv);
            if (!AreaHelper.validStateInsidePortal(lv2)) {
                if (!field_25883.test(lv2, this.world, lv)) break;
                return i;
            }
            BlockState lv3 = this.world.getBlockState(lv.move(Direction.DOWN));
            if (!field_25883.test(lv3, this.world, lv)) break;
        }
        return 0;
    }

    private int method_30496() {
        BlockPos.Mutable lv = new BlockPos.Mutable();
        int i = this.method_30490(lv);
        if (i < 3 || i > 21 || !this.method_30491(lv, i)) {
            return 0;
        }
        return i;
    }

    private boolean method_30491(BlockPos.Mutable arg, int i) {
        for (int j = 0; j < this.width; ++j) {
            BlockPos.Mutable lv = arg.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, j);
            if (field_25883.test(this.world.getBlockState(lv), this.world, lv)) continue;
            return false;
        }
        return true;
    }

    private int method_30490(BlockPos.Mutable arg) {
        for (int i = 0; i < 21; ++i) {
            arg.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, -1);
            if (!field_25883.test(this.world.getBlockState(arg), this.world, arg)) {
                return i;
            }
            arg.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, this.width);
            if (!field_25883.test(this.world.getBlockState(arg), this.world, arg)) {
                return i;
            }
            for (int j = 0; j < this.width; ++j) {
                arg.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, j);
                BlockState lv = this.world.getBlockState(arg);
                if (!AreaHelper.validStateInsidePortal(lv)) {
                    return i;
                }
                if (!lv.isOf(Blocks.NETHER_PORTAL)) continue;
                ++this.foundPortalBlocks;
            }
        }
        return 21;
    }

    private static boolean validStateInsidePortal(BlockState arg) {
        return arg.isAir() || arg.isIn(BlockTags.FIRE) || arg.isOf(Blocks.NETHER_PORTAL);
    }

    public boolean isValid() {
        return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
    }

    public void createPortal() {
        BlockState lv = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis);
        BlockPos.iterate(this.lowerCorner, this.lowerCorner.offset(Direction.UP, this.height - 1).offset(this.negativeDir, this.width - 1)).forEach(arg2 -> this.world.setBlockState((BlockPos)arg2, lv, 18));
    }

    public boolean wasAlreadyValid() {
        return this.isValid() && this.foundPortalBlocks == this.width * this.height;
    }

    public static Vec3d method_30494(class_5459.class_5460 arg, Direction.Axis arg2, Vec3d arg3, EntityDimensions arg4) {
        double j;
        double h;
        double d = (double)arg.field_25937 - (double)arg4.width;
        double e = (double)arg.field_25938 - (double)arg4.height;
        BlockPos lv = arg.field_25936;
        if (d > 0.0) {
            float f = (float)lv.method_30558(arg2) + arg4.width / 2.0f;
            double g = MathHelper.clamp(MathHelper.getLerpProgress(arg3.getComponentAlongAxis(arg2) - (double)f, 0.0, d), 0.0, 1.0);
        } else {
            h = 0.5;
        }
        if (e > 0.0) {
            Direction.Axis lv2 = Direction.Axis.Y;
            double i = MathHelper.clamp(MathHelper.getLerpProgress(arg3.getComponentAlongAxis(lv2) - (double)lv.method_30558(lv2), 0.0, e), 0.0, 1.0);
        } else {
            j = 0.0;
        }
        Direction.Axis lv3 = arg2 == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        double k = arg3.getComponentAlongAxis(lv3) - ((double)lv.method_30558(lv3) + 0.5);
        return new Vec3d(h, j, k);
    }

    public static class_5454 method_30484(ServerWorld arg, class_5459.class_5460 arg2, Direction.Axis arg3, Vec3d arg4, EntityDimensions arg5, Vec3d arg6, float f, float g) {
        BlockPos lv = arg2.field_25936;
        BlockState lv2 = arg.getBlockState(lv);
        Direction.Axis lv3 = lv2.get(Properties.HORIZONTAL_AXIS);
        double d = arg2.field_25937;
        double e = arg2.field_25938;
        int i = arg3 == lv3 ? 0 : 90;
        Vec3d lv4 = arg3 == lv3 ? arg6 : new Vec3d(arg6.z, arg6.y, -arg6.x);
        double h = (double)arg5.width / 2.0 + (d - (double)arg5.width) * arg4.getX();
        double j = (e - (double)arg5.height) * arg4.getY();
        double k = 0.5 + arg4.getZ();
        boolean bl = lv3 == Direction.Axis.X;
        Vec3d lv5 = new Vec3d((double)lv.getX() + (bl ? h : k), (double)lv.getY() + j, (double)lv.getZ() + (bl ? k : h));
        return new class_5454(lv5, lv4, f + (float)i, g);
    }
}

