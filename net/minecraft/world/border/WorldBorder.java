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

    public boolean contains(BlockPos pos) {
        return (double)(pos.getX() + 1) > this.getBoundWest() && (double)pos.getX() < this.getBoundEast() && (double)(pos.getZ() + 1) > this.getBoundNorth() && (double)pos.getZ() < this.getBoundSouth();
    }

    public boolean contains(ChunkPos pos) {
        return (double)pos.getEndX() > this.getBoundWest() && (double)pos.getStartX() < this.getBoundEast() && (double)pos.getEndZ() > this.getBoundNorth() && (double)pos.getStartZ() < this.getBoundSouth();
    }

    public boolean contains(Box box) {
        return box.maxX > this.getBoundWest() && box.minX < this.getBoundEast() && box.maxZ > this.getBoundNorth() && box.minZ < this.getBoundSouth();
    }

    public double getDistanceInsideBorder(Entity entity) {
        return this.getDistanceInsideBorder(entity.getX(), entity.getZ());
    }

    public VoxelShape asVoxelShape() {
        return this.area.asVoxelShape();
    }

    public double getDistanceInsideBorder(double x, double z) {
        double f = z - this.getBoundNorth();
        double g = this.getBoundSouth() - z;
        double h = x - this.getBoundWest();
        double i = this.getBoundEast() - x;
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

    public void setCenter(double x, double z) {
        this.centerX = x;
        this.centerZ = z;
        this.area.onCenterChanged();
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onCenterChanged(this, x, z);
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

    public void setSize(double size) {
        this.area = new StaticArea(size);
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onSizeChange(this, size);
        }
    }

    public void interpolateSize(double fromSize, double toSize, long time) {
        this.area = fromSize == toSize ? new StaticArea(toSize) : new MovingArea(fromSize, toSize, time);
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onInterpolateSize(this, fromSize, toSize, time);
        }
    }

    protected List<WorldBorderListener> getListeners() {
        return Lists.newArrayList(this.listeners);
    }

    public void addListener(WorldBorderListener listener) {
        this.listeners.add(listener);
    }

    public void setMaxWorldBorderRadius(int radius) {
        this.maxWorldBorderRadius = radius;
        this.area.onMaxWorldBorderRadiusChanged();
    }

    public int getMaxWorldBorderRadius() {
        return this.maxWorldBorderRadius;
    }

    public double getBuffer() {
        return this.buffer;
    }

    public void setBuffer(double buffer) {
        this.buffer = buffer;
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onSafeZoneChanged(this, buffer);
        }
    }

    public double getDamagePerBlock() {
        return this.damagePerBlock;
    }

    public void setDamagePerBlock(double damagePerBlock) {
        this.damagePerBlock = damagePerBlock;
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onDamagePerBlockChanged(this, damagePerBlock);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public double getShrinkingSpeed() {
        return this.area.getShrinkingSpeed();
    }

    public int getWarningTime() {
        return this.warningTime;
    }

    public void setWarningTime(int warningTime) {
        this.warningTime = warningTime;
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onWarningTimeChanged(this, warningTime);
        }
    }

    public int getWarningBlocks() {
        return this.warningBlocks;
    }

    public void setWarningBlocks(int warningBlocks) {
        this.warningBlocks = warningBlocks;
        for (WorldBorderListener lv : this.getListeners()) {
            lv.onWarningBlocksChanged(this, warningBlocks);
        }
    }

    public void tick() {
        this.area = this.area.getAreaInstance();
    }

    public Properties write() {
        return new Properties(this);
    }

    public void load(Properties properties) {
        this.setCenter(properties.getCenterX(), properties.getCenterZ());
        this.setDamagePerBlock(properties.getDamagePerBlock());
        this.setBuffer(properties.getBuffer());
        this.setWarningBlocks(properties.getWarningBlocks());
        this.setWarningTime(properties.getWarningTime());
        if (properties.getTargetRemainingTime() > 0L) {
            this.interpolateSize(properties.getSize(), properties.getTargetSize(), properties.getTargetRemainingTime());
        } else {
            this.setSize(properties.getSize());
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

        private Properties(double centerX, double centerZ, double damagePerBlock, double buffer, int warningBlocks, int warningTime, double size, long targetRemainingTime, double targetSize) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.damagePerBlock = damagePerBlock;
            this.buffer = buffer;
            this.warningBlocks = warningBlocks;
            this.warningTime = warningTime;
            this.size = size;
            this.targetRemainingTime = targetRemainingTime;
            this.targetSize = targetSize;
        }

        private Properties(WorldBorder worldBorder) {
            this.centerX = worldBorder.getCenterX();
            this.centerZ = worldBorder.getCenterZ();
            this.damagePerBlock = worldBorder.getDamagePerBlock();
            this.buffer = worldBorder.getBuffer();
            this.warningBlocks = worldBorder.getWarningBlocks();
            this.warningTime = worldBorder.getWarningTime();
            this.size = worldBorder.getSize();
            this.targetRemainingTime = worldBorder.getTargetRemainingTime();
            this.targetSize = worldBorder.getTargetSize();
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

        public void toTag(CompoundTag tag) {
            tag.putDouble("BorderCenterX", this.centerX);
            tag.putDouble("BorderCenterZ", this.centerZ);
            tag.putDouble("BorderSize", this.size);
            tag.putLong("BorderSizeLerpTime", this.targetRemainingTime);
            tag.putDouble("BorderSafeZone", this.buffer);
            tag.putDouble("BorderDamagePerBlock", this.damagePerBlock);
            tag.putDouble("BorderSizeLerpTarget", this.targetSize);
            tag.putDouble("BorderWarningBlocks", this.warningBlocks);
            tag.putDouble("BorderWarningTime", this.warningTime);
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

        private MovingArea(double oldSize, double newSize, long duration) {
            this.oldSize = oldSize;
            this.newSize = newSize;
            this.timeDuration = duration;
            this.timeStart = Util.getMeasuringTimeMs();
            this.timeEnd = this.timeStart + duration;
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

