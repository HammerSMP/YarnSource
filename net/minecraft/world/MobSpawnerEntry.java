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
        Identifier lv = Identifier.tryParse(arg.getString("id"));
        if (lv != null) {
            arg.putString("id", lv.toString());
        } else {
            arg.putString("id", "minecraft:pig");
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

