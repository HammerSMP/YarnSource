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

    public MobSpawnerEntry(CompoundTag arg) {
        this(arg.contains("Weight", 99) ? arg.getInt("Weight") : 1, arg.getCompound("Entity"));
    }

    public MobSpawnerEntry(int i, CompoundTag arg) {
        super(i);
        this.entityTag = arg;
    }

    public CompoundTag serialize() {
        CompoundTag lv = new CompoundTag();
        if (!this.entityTag.contains("id", 8)) {
            this.entityTag.putString("id", "minecraft:pig");
        } else if (!this.entityTag.getString("id").contains(":")) {
            this.entityTag.putString("id", new Identifier(this.entityTag.getString("id")).toString());
        }
        lv.put("Entity", this.entityTag);
        lv.putInt("Weight", this.weight);
        return lv;
    }

    public CompoundTag getEntityTag() {
        return this.entityTag;
    }
}

