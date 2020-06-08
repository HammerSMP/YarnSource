/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.loot.entry;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativeEntry
extends CombinedEntry {
    AlternativeEntry(LootPoolEntry[] args, LootCondition[] args2) {
        super(args, args2);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntryTypes.ALTERNATIVES;
    }

    @Override
    protected EntryCombiner combine(EntryCombiner[] args) {
        switch (args.length) {
            case 0: {
                return ALWAYS_FALSE;
            }
            case 1: {
                return args[0];
            }
            case 2: {
                return args[0].or(args[1]);
            }
        }
        return (arg, consumer) -> {
            for (EntryCombiner lv : args) {
                if (!lv.expand(arg, consumer)) continue;
                return true;
            }
            return false;
        };
    }

    @Override
    public void validate(LootTableReporter arg) {
        super.validate(arg);
        for (int i = 0; i < this.children.length - 1; ++i) {
            if (!ArrayUtils.isEmpty((Object[])this.children[i].conditions)) continue;
            arg.report("Unreachable entry!");
        }
    }

    public static Builder builder(LootPoolEntry.Builder<?> ... args) {
        return new Builder(args);
    }

    public static class Builder
    extends LootPoolEntry.Builder<Builder> {
        private final List<LootPoolEntry> children = Lists.newArrayList();

        public Builder(LootPoolEntry.Builder<?> ... args) {
            for (LootPoolEntry.Builder<?> lv : args) {
                this.children.add(lv.build());
            }
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public Builder alternatively(LootPoolEntry.Builder<?> arg) {
            this.children.add(arg.build());
            return this;
        }

        @Override
        public LootPoolEntry build() {
            return new AlternativeEntry(this.children.toArray(new LootPoolEntry[0]), this.getConditions());
        }

        @Override
        protected /* synthetic */ LootPoolEntry.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

