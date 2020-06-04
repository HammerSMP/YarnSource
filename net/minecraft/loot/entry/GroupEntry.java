/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;

public class GroupEntry
extends CombinedEntry {
    GroupEntry(LootPoolEntry[] args, LootCondition[] args2) {
        super(args, args2);
    }

    @Override
    public LootPoolEntryType method_29318() {
        return LootPoolEntryTypes.SEQUENCE;
    }

    @Override
    protected EntryCombiner combine(EntryCombiner[] args) {
        switch (args.length) {
            case 0: {
                return ALWAYS_TRUE;
            }
            case 1: {
                return args[0];
            }
            case 2: {
                return args[0].and(args[1]);
            }
        }
        return (arg, consumer) -> {
            for (EntryCombiner lv : args) {
                if (lv.expand(arg, consumer)) continue;
                return false;
            }
            return true;
        };
    }
}

