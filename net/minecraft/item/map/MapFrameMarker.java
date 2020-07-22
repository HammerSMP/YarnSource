/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item.map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

public class MapFrameMarker {
    private final BlockPos pos;
    private final int rotation;
    private final int entityId;

    public MapFrameMarker(BlockPos pos, int rotation, int entityId) {
        this.pos = pos;
        this.rotation = rotation;
        this.entityId = entityId;
    }

    public static MapFrameMarker fromTag(CompoundTag tag) {
        BlockPos lv = NbtHelper.toBlockPos(tag.getCompound("Pos"));
        int i = tag.getInt("Rotation");
        int j = tag.getInt("EntityId");
        return new MapFrameMarker(lv, i, j);
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        lv.put("Pos", NbtHelper.fromBlockPos(this.pos));
        lv.putInt("Rotation", this.rotation);
        lv.putInt("EntityId", this.entityId);
        return lv;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public String getKey() {
        return MapFrameMarker.getKey(this.pos);
    }

    public static String getKey(BlockPos pos) {
        return "frame-" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }
}

