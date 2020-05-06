/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.entry;

import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootEntry;

public class GroupEntry
extends CombinedEntry {
    GroupEntry(LootEntry[] args, LootCondition[] args2) {
        super(args, args2);
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
                EntryCombiner lv = args[0];
                EntryCombiner lv2 = args[1];
                return (arg3, consumer) -> {
                    lv.expand(arg3, consumer);
                    lv2.expand(arg3, consumer);
                    return true;
                };
            }
        }
        return (arg, consumer) -> {
            for (EntryCombiner lv : args) {
                lv.expand(arg, consumer);
            }
            return true;
        };
    }
}

