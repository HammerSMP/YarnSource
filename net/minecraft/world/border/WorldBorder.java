/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.border.WorldBorderStage;

public class WorldBorder {
    private final List<WorldBorderListener> listeners = Lists.newArrayList();
    private double damagePerBlock = 0.2;
    private double buffer = 5.0;
    private int warningTime = 15;
    private int warningBlocks = 5;
    private double centerX;
    private double centerZ;
    private int maxWorldBorderRadius = 29999984;
    private Area area = new StaticArea(6.0E7);
    public static final class_5200 field_24122 = new class_5200(0.0, 0.0, 0.2, 5.0, 5, 15, 6.0E7, 0L, 0.0);

    public boolean contains(BlockPos arg) {
        return (double)(arg.getX() + 1) > this.getBoundWest() && (double)arg.getX() < this.getBoundEast() && (double)(arg.getZ() + 1) > this.getBoundNorth() && (double)arg.getZ() < this.getBoundSouth();
    }

    public boolean contains(ChunkPos arg) {
        return (double)arg.getEndX() > this.getBoundWest() && (double)arg.getStartX() < this.getBoundEast() && (double)arg.getEndZ() > this.getBoundNorth() && (double)arg.getStartZ() < this.getBoundSouth();
    }

    public boolean contains(Box arg) {
        return arg.x2 > this.getBoundWest() && arg.x1 < this.getBoundEast() && arg.z2 > this.getBoundNorth() && arg.z1 < this.getBoundSouth();
    }

    public double getDistanceInsideBorder(Entity arg) {
        return this.getDistanceInsideBorder(arg.getX(), arg.getZ());
    }

    public VoxelShape asVoxelShape() {
        return this.area.asVoxelShape();
    }

    public double getDistanceInsideBorder(double d, double e) {
        double f = e - this.getBoundNorth();
        double g = this.getBoundSouth() - e;
        double h = d - this.getBoundWest();
        double i = this.getBoundEast() - d;
        double j = Math.min(h, i);
        j = Math.min(j, f);
        return Math.min(j, g);
    }

    @Environment(value=EnvType.CLIENT)
    public WorldBorderStage getStage() {
        return this.area.getStage();
    }

    public double getBoundWest() {
        return this.area.getBoundWest();
    }

    public double getBoundNorth() {
        return this.area.getBoundNorth();
    }

    public double getBoundEast() {
        return this.area.getBoundEast();
    }

    public double getBoundSouth() {
        return this.area.getBoundSouth();
    }

    public double getCenterX() {
        return this.centerX;
    }

    public double getCenterZ() {
        return this.centerZ;
    }

    public void setCenter(double d, double e) {
        this.centerX = d;
        this.centerZ = e;
        this.area.onCenterChanged();
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onCenterChanged(this, d, e);
        }
    }

    public double getSize() {
        return this.area.getSize();
    }

    public long getTargetRemainingTime() {
        return this.area.getTargetRemainingTime();
    }

    public double getTargetSize() {
        return this.area.getTargetSize();
    }

    public void setSize(double d) {
        this.area = new StaticArea(d);
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onSizeChange(this, d);
        }
    }

    public void interpolateSize(double d, double e, long l) {
        this.area = d == e ? new StaticArea(e) : new MovingArea(d, e, l);
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onInterpolateSize(this, d, e, l);
        }
    }

    protected List<WorldBorderListener> getListeners() {
        return Lists.newArrayList(this.listeners);
    }

    public void addListener(WorldBorderListener arg) {
        this.listeners.add(arg);
    }

    public void setMaxWorldBorderRadius(int i) {
        this.maxWorldBorderRadius = i;
        this.area.onMaxWorldBorderRadiusChanged();
    }

    public int getMaxWorldBorderRadius() {
        return this.maxWorldBorderRadius;
    }

    public double getBuffer() {
        return this.buffer;
    }

    public void setBuffer(double d) {
        this.buffer = d;
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onSafeZoneChanged(this, d);
        }
    }

    public double getDamagePerBlock() {
        return this.damagePerBlock;
    }

    public void setDamagePerBlock(double d) {
        this.damagePerBlock = d;
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onDamagePerBlockChanged(this, d);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public double getShrinkingSpeed() {
        return this.area.getShrinkingSpeed();
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public void setWarningTime(int i) {
        this.warningTime = i;
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onWarningTimeChanged(this, i);
        }
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }

    public void setWarningBlocks(int i) {
        this.warningBlocks = i;
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onWarningBlocksChanged(this, i);
        }
    }

    public void tick() {
        this.area = this.area.getAreaInstance();
    }

    public class_5200 method_27355() {
        return new class_5200(this);
    }

    public void load(class_5200 arg) {
        this.setCenter(arg.method_27356(), arg.method_27359());
        this.setDamagePerBlock(arg.method_27360());
        this.setBuffer(arg.method_27361());
        this.setWarningBlocks(arg.method_27362());
        this.setWarningTime(arg.method_27363());
        if (arg.method_27365() > 0L) {
            this.interpolateSize(arg.method_27364(), arg.method_27366(), arg.method_27365());
        } else {
            this.setSize(arg.method_27364());
        }
    }

    public static class class_5200 {
        private final double field_24123;
        private final double field_24124;
        private final double field_24125;
        private final double field_24126;
        private final int field_24127;
        private final int field_24128;
        private final double field_24129;
        private final long field_24130;
        private final double field_24131;

        private class_5200(double d, double e, double f, double g, int i, int j, double h, long l, double k) {
            this.field_24123 = d;
            this.field_24124 = e;
            this.field_24125 = f;
            this.field_24126 = g;
            this.field_24127 = i;
            this.field_24128 = j;
            this.field_24129 = h;
            this.field_24130 = l;
            this.field_24131 = k;
        }

        private class_5200(WorldBorder arg) {
            this.field_24123 = arg.getCenterX();
            this.field_24124 = arg.getCenterZ();
            this.field_24125 = arg.getDamagePerBlock();
            this.field_24126 = arg.getBuffer();
            this.field_24127 = arg.getWarningBlocks();
            this.field_24128 = arg.getWarningTime();
            this.field_24129 = arg.getSize();
            this.field_24130 = arg.getTargetRemainingTime();
            this.field_24131 = arg.getTargetSize();
        }

        public double method_27356() {
            return this.field_24123;
        }

        public double method_27359() {
            return this.field_24124;
        }

        public double method_27360() {
            return this.field_24125;
        }

        public double method_27361() {
            return this.field_24126;
        }

        public int method_27362() {
            return this.field_24127;
        }

        public int method_27363() {
            return this.field_24128;
        }

        public double method_27364() {
            return this.field_24129;
        }

        public long method_27365() {
            return this.field_24130;
        }

        public double method_27366() {
            return this.field_24131;
        }

        public static class_5200 method_27358(CompoundTag arg, class_5200 arg2) {
            double d = arg2.field_24123;
            double e = arg2.field_24124;
            double f = arg2.field_24129;
            long l = arg2.field_24130;
            double g = arg2.field_24131;
            double h = arg2.field_24126;
            double i = arg2.field_24125;
            int j = arg2.field_24127;
            int k = arg2.field_24128;
            if (arg.contains("BorderCenterX", 99)) {
                d = arg.getDouble("BorderCenterX");
            }
            if (arg.contains("BorderCenterZ", 99)) {
                e = arg.getDouble("BorderCenterZ");
            }
            if (arg.contains("BorderSize", 99)) {
                f = arg.getDouble("BorderSize");
            }
            if (arg.contains("BorderSizeLerpTime", 99)) {
                l = arg.getLong("BorderSizeLerpTime");
            }
            if (arg.contains("BorderSizeLerpTarget", 99)) {
                g = arg.getDouble("BorderSizeLerpTarget");
            }
            if (arg.contains("BorderSafeZone", 99)) {
                h = arg.getDouble("BorderSafeZone");
            }
            if (arg.contains("BorderDamagePerBlock", 99)) {
                i = arg.getDouble("BorderDamagePerBlock");
            }
            if (arg.contains("BorderWarningBlocks", 99)) {
                j = arg.getInt("BorderWarningBlocks");
            }
            if (arg.contains("BorderWarningTime", 99)) {
                k = arg.getInt("BorderWarningTime");
            }
            return new class_5200(d, e, i, h, j, k, f, l, g);
        }

        public void method_27357(CompoundTag arg) {
            arg.putDouble("BorderCenterX", this.field_24123);
            arg.putDouble("BorderCenterZ", this.field_24124);
            arg.putDouble("BorderSize", this.field_24129);
            arg.putLong("BorderSizeLerpTime", this.field_24130);
            arg.putDouble("BorderSafeZone", this.field_24126);
            arg.putDouble("BorderDamagePerBlock", this.field_24125);
            arg.putDouble("BorderSizeLerpTarget", this.field_24131);
            arg.putDouble("BorderWarningBlocks", this.field_24127);
            arg.putDouble("BorderWarningTime", this.field_24128);
        }
    }

    class StaticArea
    implements Area {
        private final double size;
        private double boundWest;
        private double boundNorth;
        private double boundEast;
        private double boundSouth;
        private VoxelShape shape;

        public StaticArea(double d) {
            this.size = d;
            this.recalculateBounds();
        }

        @Override
        public double getBoundWest() {
            return this.boundWest;
        }

        @Override
        public double getBoundEast() {
            return this.boundEast;
        }

        @Override
        public double getBoundNorth() {
            return this.boundNorth;
        }

        @Override
        public double getBoundSouth() {
            return this.boundSouth;
        }

        @Override
        public double getSize() {
            return this.size;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public WorldBorderStage getStage() {
            return WorldBorderStage.STATIONARY;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public double getShrinkingSpeed() {
            return 0.0;
        }

        @Override
        public long getTargetRemainingTime() {
            return 0L;
        }

        @Override
        public double getTargetSize() {
            return this.size;
        }

        private void recalculateBounds() {
            this.boundWest = Math.max(WorldBorder.this.getCenterX() - this.size / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
            this.boundNorth = Math.max(WorldBorder.this.getCenterZ() - this.size / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
            this.boundEast = Math.min(WorldBorder.this.getCenterX() + this.size / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
            this.boundSouth = Math.min(WorldBorder.this.getCenterZ() + this.size / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
            this.shape = VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, VoxelShapes.cuboid(Math.floor(this.getBoundWest()), Double.NEGATIVE_INFINITY, Math.floor(this.getBoundNorth()), Math.ceil(this.getBoundEast()), Double.POSITIVE_INFINITY, Math.ceil(this.getBoundSouth())), BooleanBiFunction.ONLY_FIRST);
        }

        @Override
        public void onMaxWorldBorderRadiusChanged() {
            this.recalculateBounds();
        }

        @Override
        public void onCenterChanged() {
            this.recalculateBounds();
        }

        @Override
        public Area getAreaInstance() {
            return this;
        }

        @Override
        public VoxelShape asVoxelShape() {
            return this.shape;
        }
    }

    class MovingArea
    implements Area {
        private final double oldSize;
        private final double newSize;
        private final long timeEnd;
        private final long timeStart;
        private final double timeDuration;

        private MovingArea(double d, double e, long l) {
            this.oldSize = d;
            this.newSize = e;
            this.timeDuration = l;
            this.timeStart = Util.getMeasuringTimeMs();
            this.timeEnd = this.timeStart + l;
        }

        @Override
        public double getBoundWest() {
            return Math.max(WorldBorder.this.getCenterX() - this.getSize() / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
        }

        @Override
        public double getBoundNorth() {
            return Math.max(WorldBorder.this.getCenterZ() - this.getSize() / 2.0, (double)(-WorldBorder.this.maxWorldBorderRadius));
        }

        @Override
        public double getBoundEast() {
            return Math.min(WorldBorder.this.getCenterX() + this.getSize() / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
        }

        @Override
        public double getBoundSouth() {
            return Math.min(WorldBorder.this.getCenterZ() + this.getSize() / 2.0, (double)WorldBorder.this.maxWorldBorderRadius);
        }

        @Override
        public double getSize() {
            double d = (double)(Util.getMeasuringTimeMs() - this.timeStart) / this.timeDuration;
            return d < 1.0 ? MathHelper.lerp(d, this.oldSize, this.newSize) : this.newSize;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public double getShrinkingSpeed() {
            return Math.abs(this.oldSize - this.newSize) / (double)(this.timeEnd - this.timeStart);
        }

        @Override
        public long getTargetRemainingTime() {
            return this.timeEnd - Util.getMeasuringTimeMs();
        }

        @Override
        public double getTargetSize() {
            return this.newSize;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public WorldBorderStage getStage() {
            return this.newSize < this.oldSize ? WorldBorderStage.SHRINKING : WorldBorderStage.GROWING;
        }

        @Override
        public void onCenterChanged() {
        }

        @Override
        public void onMaxWorldBorderRadiusChanged() {
        }

        @Override
        public Area getAreaInstance() {
            if (this.getTargetRemainingTime() <= 0L) {
                return new StaticArea(this.newSize);
            }
            return this;
        }

        @Override
        public VoxelShape asVoxelShape() {
            return VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, VoxelShapes.cuboid(Math.floor(this.getBoundWest()), Double.NEGATIVE_INFINITY, Math.floor(this.getBoundNorth()), Math.ceil(this.getBoundEast()), Double.POSITIVE_INFINITY, Math.ceil(this.getBoundSouth())), BooleanBiFunction.ONLY_FIRST);
        }
    }

    static interface Area {
        public double getBoundWest();

        public double getBoundEast();

        public double getBoundNorth();

        public double getBoundSouth();

        public double getSize();

        @Environment(value=EnvType.CLIENT)
        public double getShrinkingSpeed();

        public long getTargetRemainingTime();

        public double getTargetSize();

        @Environment(value=EnvType.CLIENT)
        public WorldBorderStage getStage();

        public void onMaxWorldBorderRadiusChanged();

        public void onCenterChanged();

        public Area getAreaInstance();

        public VoxelShape asVoxelShape();
    }
}

