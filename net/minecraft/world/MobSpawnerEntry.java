/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.WeightedPicker;

public class MobSpawnerEntry
extends WeightedPicker.Entry {
    private final CompoundTag entityTag;

    public MobSpawnerEntry() {
        super(1);
        this.entityTag = new CompoundTag();
        this.entityTag.putString("id", "minecraft:pig");
    }

    public MobSpawnerEntry(CompoundTag tag) {
        this(tag.contains("Weight", 99) ? tag.getInt("Weight") : 1, tag.getCompound("Entity"));
    }

    public MobSpawnerEntry(int weight, CompoundTag entityTag) {
        super(weight);
        this.entityTag = entityTag;
        Identifier lv = Identifier.tryParse(entityTag.getString("id"));
        if (lv != null) {
            entityTag.putString("id", lv.toString());
        } else {
            entityTag.putString("id", "minecraft:pig");
        }
    }

    public CompoundTag serialize() {
        CompoundTag lv = new CompoundTag();
        lv.put("Entity", this.entityTag);
        lv.putInt("Weight", this.weight);
        return lv;
    }

    public CompoundTag getEntityTag() {
        return this.entityTag;
    }
}

