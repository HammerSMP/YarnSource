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
import net.minecraft.loot.entry.LootEntry;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativeEntry
extends CombinedEntry {
    AlternativeEntry(LootEntry[] args, LootCondition[] args2) {
        super(args, args2);
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
    public void check(LootTableReporter arg) {
        super.check(arg);
        for (int i = 0; i < this.children.length - 1; ++i) {
            if (!ArrayUtils.isEmpty((Object[])this.children[i].conditions)) continue;
            arg.report("Unreachable entry!");
        }
    }

    public static Builder builder(LootEntry.Builder<?> ... args) {
        return new Builder(args);
    }

    public static class Builder
    extends LootEntry.Builder<Builder> {
        private final List<LootEntry> children = Lists.newArrayList();

        public Builder(LootEntry.Builder<?> ... args) {
            for (LootEntry.Builder<?> lv : args) {
                this.children.add(lv.build());
            }
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public Builder withChild(LootEntry.Builder<?> arg) {
            this.children.add(arg.build());
            return this;
        }

        @Override
        public LootEntry build() {
            return new AlternativeEntry(this.children.toArray(new LootEntry[0]), this.getConditions());
        }

        @Override
        protected /* synthetic */ LootEntry.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

