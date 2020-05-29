/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.entry;

import net.minecraft.class_5338;
import net.minecraft.class_5341;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootEntries;
import net.minecraft.loot.entry.LootEntry;

public class GroupEntry
extends CombinedEntry {
    GroupEntry(LootEntry[] args, class_5341[] args2) {
        super(args, args2);
    }

    @Override
    public class_5338 method_29318() {
        return LootEntries.SEQUENCE;
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

