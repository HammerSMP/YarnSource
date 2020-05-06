/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public abstract class BlockEntity {
    private static final Logger LOGGER = LogManager.getLogger();
    private final BlockEntityType<?> type;
    @Nullable
    protected World world;
    protected BlockPos pos = BlockPos.ORIGIN;
    protected boolean removed;
    @Nullable
    private BlockState cachedState;
    private boolean invalid;

    public BlockEntity(BlockEntityType<?> arg) {
        this.type = arg;
    }

    @Nullable
    public World getWorld() {
        return this.world;
    }

    public void setLocation(World arg, BlockPos arg2) {
        this.world = arg;
        this.pos = arg2.toImmutable();
    }

    public boolean hasWorld() {
        return this.world != null;
    }

    public void fromTag(BlockState arg, CompoundTag arg2) {
        this.pos = new BlockPos(arg2.getInt("x"), arg2.getInt("y"), arg2.getInt("z"));
    }

    public CompoundTag toTag(CompoundTag arg) {
        return this.writeIdentifyingData(arg);
    }

    private CompoundTag writeIdentifyingData(CompoundTag arg) {
        Identifier lv = BlockEntityType.getId(this.getType());
        if (lv == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        }
        arg.putString("id", lv.toString());
        arg.putInt("x", this.pos.getX());
        arg.putInt("y", this.pos.getY());
        arg.putInt("z", this.pos.getZ());
        return arg;
    }

    @Nullable
    public static BlockEntity createFromTag(BlockState arg2, CompoundTag arg22) {
        String string = arg22.getString("id");
        return Registry.BLOCK_ENTITY_TYPE.getOrEmpty(new Identifier(string)).map(arg -> {
            try {
                return arg.instantiate();
            }
            catch (Throwable throwable) {
                LOGGER.error("Failed to create block entity {}", (Object)string, (Object)throwable);
                return null;
            }
        }).map(arg3 -> {
            try {
                arg3.fromTag(arg2, arg22);
                return arg3;
            }
            catch (Throwable throwable) {
                LOGGER.error("Failed to load data for block entity {}", (Object)string, (Object)throwable);
                return null;
            }
        }).orElseGet(() -> {
            LOGGER.warn("Skipping BlockEntity with id {}", (Object)string);
            return null;
        });
    }

    public void markDirty() {
        if (this.world != null) {
            this.cachedState = this.world.getBlockState(this.pos);
            this.world.markDirty(this.pos, this);
            if (!this.cachedState.isAir()) {
                this.world.updateComparators(this.pos, this.cachedState.getBlock());
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public double getSquaredRenderDistance() {
        return 64.0;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getCachedState() {
        if (this.cachedState == null) {
            this.cachedState = this.world.getBlockState(this.pos);
        }
        return this.cachedState;
    }

    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return null;
    }

    public CompoundTag toInitialChunkDataTag() {
        return this.writeIdentifyingData(new CompoundTag());
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public void markRemoved() {
        this.removed = true;
    }

    public void cancelRemoval() {
        this.removed = false;
    }

    public boolean onSyncedBlockEvent(int i, int j) {
        return false;
    }

    public void resetBlock() {
        this.cachedState = null;
    }

    public void populateCrashReport(CrashReportSection arg) {
        arg.add("Name", () -> Registry.BLOCK_ENTITY_TYPE.getId(this.getType()) + " // " + this.getClass().getCanonicalName());
        if (this.world == null) {
            return;
        }
        CrashReportSection.addBlockInfo(arg, this.pos, this.getCachedState());
        CrashReportSection.addBlockInfo(arg, this.pos, this.world.getBlockState(this.pos));
    }

    public void setPos(BlockPos arg) {
        this.pos = arg.toImmutable();
    }

    public boolean copyItemDataRequiresOperator() {
        return false;
    }

    public void applyRotation(BlockRotation arg) {
    }

    public void applyMirror(BlockMirror arg) {
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    public void markInvalid() {
        if (this.invalid) {
            return;
        }
        this.invalid = true;
        LOGGER.warn("Block entity invalid: {} @ {}", new Supplier[]{() -> Registry.BLOCK_ENTITY_TYPE.getId(this.getType()), this::getPos});
    }
}

