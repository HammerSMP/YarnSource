/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 */
package net.minecraft.loot.context;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.context.LootContextParameter;

public class LootContextType {
    private final Set<LootContextParameter<?>> required;
    private final Set<LootContextParameter<?>> allowed;

    private LootContextType(Set<LootContextParameter<?>> set, Set<LootContextParameter<?>> set2) {
        this.required = ImmutableSet.copyOf(set);
        this.allowed = ImmutableSet.copyOf((Collection)Sets.union(set, set2));
    }

    public Set<LootContextParameter<?>> getRequired() {
        return this.required;
    }

    public Set<LootContextParameter<?>> getAllowed() {
        return this.allowed;
    }

    public String toString() {
        return "[" + Joiner.on((String)", ").join(this.allowed.stream().map(arg -> (this.required.contains(arg) ? "!" : "") + arg.getIdentifier()).iterator()) + "]";
    }

    public void validate(LootTableReporter arg, LootContextAware arg2) {
        Set<LootContextParameter<?>> set = arg2.getRequiredParameters();
        Sets.SetView set2 = Sets.difference(set, this.allowed);
        if (!set2.isEmpty()) {
            arg.report("Parameters " + (Object)set2 + " are not provided in this context");
        }
    }

    public static class Builder {
        private final Set<LootContextParameter<?>> required = Sets.newIdentityHashSet();
        private final Set<LootContextParameter<?>> allowed = Sets.newIdentityHashSet();

        public Builder require(LootContextParameter<?> arg) {
            if (this.allowed.contains(arg)) {
                throw new IllegalArgumentException("Parameter " + arg.getIdentifier() + " is already optional");
            }
            this.required.add(arg);
            return this;
        }

        public Builder allow(LootContextParameter<?> arg) {
            if (this.required.contains(arg)) {
                throw new IllegalArgumentException("Parameter " + arg.getIdentifier() + " is already required");
            }
            this.allowed.add(arg);
            return this;
        }

        public LootContextType build() {
            return new LootContextType(this.required, this.allowed);
        }
    }
}

