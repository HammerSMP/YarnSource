/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.util.JsonSerializableType;
import net.minecraft.util.JsonSerializer;

public class LootPoolEntryType
extends JsonSerializableType<LootPoolEntry> {
    public LootPoolEntryType(JsonSerializer<? extends LootPoolEntry> arg) {
        super(arg);
    }
}

