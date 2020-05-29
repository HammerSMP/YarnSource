/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.DynamicLike
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.DynamicLike;
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
    public static final Properties DEFAULT_BORDER = new Properties(0.0, 0.0, 0.2, 5.0, 5, 15, 6.0E7, 0L, 0.0);

    public boolean contains(BlockPos arg) {
        return (double)(arg.getX() + 1) > this.getBoundWest() && (double)arg.getX() < this.getBoundEast() && (double)(arg.getZ() + 1) > this.getBoundNorth() && (double)arg.getZ() < this.getBoundSouth();
    }

    public boolean contains(ChunkPos arg) {
        return (double)arg.getEndX() > this.getBoundWest() && (double)arg.getStartX() < this.getBoundEast() && (double)arg.getEndZ() > this.getBoundNorth() && (double)arg.getStartZ() < this.getBoundSouth();
    }

    public boolean contains(Box arg) {
        return arg.maxX > this.getBoundWest() && arg.minX < this.getBoundEast() && arg.maxZ > this.getBoundNorth() && arg.minZ < this.getBoundSouth();
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

    public Properties write() {
        return new Properties(this);
    }

    public void load(Properties arg) {
        this.setCenter(arg.getCenterX(), arg.getCenterZ());
        this.setDamagePerBlock(arg.getDamagePerBlock());
        this.setBuffer(arg.getBuffer());
        this.setWarningBlocks(arg.getWarningBlocks());
        this.setWarningTime(arg.getWarningTime());
        if (arg.getTargetRemainingTime() > 0L) {
            this.interpolateSize(arg.getSize(), arg.getTargetSize(), arg.getTargetRemainingTime());
        } else {
            this.setSize(arg.getSize());
        }
    }

    public static class Properties {
        private final double centerX;
        private final double centerZ;
        private final double damagePerBlock;
        private final double buffer;
        private final int warningBlocks;
        private final int warningTime;
        private final double size;
        private final long targetRemainingTime;
        private final double targetSize;

        private Properties(double d, double e, double f, double g, int i, int j, double h, long l, double k) {
            this.centerX = d;
            this.centerZ = e;
            this.damagePerBlock = f;
            this.buffer = g;
            this.warningBlocks = i;
            this.warningTime = j;
            this.size = h;
            this.targetRemainingTime = l;
            this.targetSize = k;
        }

        private Properties(WorldBorder arg) {
            this.centerX = arg.getCenterX();
            this.centerZ = arg.getCenterZ();
            this.damagePerBlock = arg.getDamagePerBlock();
            this.buffer = arg.getBuffer();
            this.warningBlocks = arg.getWarningBlocks();
            this.warningTime = arg.getWarningTime();
            this.size = arg.getSize();
            this.targetRemainingTime = arg.getTargetRemainingTime();
            this.targetSize = arg.getTargetSize();
        }

        public double getCenterX() {
            return this.centerX;
        }

        public double getCenterZ() {
            return this.centerZ;
        }

        public double getDamagePerBlock() {
            return this.damagePerBlock;
        }

        public double getBuffer() {
            return this.buffer;
        }

        public int getWarningBlocks() {
            return this.warningBlocks;
        }

        public int getWarningTime() {
            return this.warningTime;
        }

        public double getSize() {
            return this.size;
        }

        public long getTargetRemainingTime() {
            return this.targetRemainingTime;
        }

        public double getTargetSize() {
            return this.targetSize;
        }

        public static Properties fromDynamic(DynamicLike<?> dynamicLike, Properties arg) {
            double d = dynamicLike.get("BorderCenterX").asDouble(arg.centerX);
            double e = dynamicLike.get("BorderCenterZ").asDouble(arg.centerZ);
            double f = dynamicLike.get("BorderSize").asDouble(arg.size);
            long l = dynamicLike.get("BorderSizeLerpTime").asLong(arg.targetRemainingTime);
            double g = dynamicLike.get("BorderSizeLerpTarget").asDouble(arg.targetSize);
            double h = dynamicLike.get("BorderSafeZone").asDouble(arg.buffer);
            double i = dynamicLike.get("BorderDamagePerBlock").asDouble(arg.damagePerBlock);
            int j = dynamicLike.get("BorderWarningBlocks").asInt(arg.warningBlocks);
            int k = dynamicLike.get("BorderWarningTime").asInt(arg.warningTime);
            return new Properties(d, e, i, h, j, k, f, l, g);
        }

        public void toTag(CompoundTag arg) {
            arg.putDouble("BorderCenterX", this.centerX);
            arg.putDouble("BorderCenterZ", this.centerZ);
            arg.putDouble("BorderSize", this.size);
            arg.putLong("BorderSizeLerpTime", this.targetRemainingTime);
            arg.putDouble("BorderSafeZone", this.buffer);
            arg.putDouble("BorderDamagePerBlock", this.damagePerBlock);
            arg.putDouble("BorderSizeLerpTarget", this.targetSize);
            arg.putDouble("BorderWarningBlocks", this.warningBlocks);
            arg.putDouble("BorderWarningTime", this.warningTime);
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

