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

    private LootContextType(Set<LootContextParameter<?>> required, Set<LootContextParameter<?>> allowed) {
        this.required = ImmutableSet.copyOf(required);
        this.allowed = ImmutableSet.copyOf((Collection)Sets.union(required, allowed));
    }

    public Set<LootContextParameter<?>> getRequired() {
        return this.required;
    }

    public Set<LootContextParameter<?>> getAllowed() {
        return this.allowed;
    }

    public String toString() {
        return "[" + Joiner.on((String)", ").join(this.allowed.stream().map(parameter -> (this.required.contains(parameter) ? "!" : "") + parameter.getIdentifier()).iterator()) + "]";
    }

    public void validate(LootTableReporter reporter, LootContextAware parameterConsumer) {
        Set<LootContextParameter<?>> set = parameterConsumer.getRequiredParameters();
        Sets.SetView set2 = Sets.difference(set, this.allowed);
        if (!set2.isEmpty()) {
            reporter.report("Parameters " + (Object)set2 + " are not provided in this context");
        }
    }

    public static class Builder {
        private final Set<LootContextParameter<?>> required = Sets.newIdentityHashSet();
        private final Set<LootContextParameter<?>> allowed = Sets.newIdentityHashSet();

        public Builder require(LootContextParameter<?> parameter) {
            if (this.allowed.contains(parameter)) {
                throw new IllegalArgumentException("Parameter " + parameter.getIdentifier() + " is already optional");
            }
            this.required.add(parameter);
            return this;
        }

        public Builder allow(LootContextParameter<?> parameter) {
            if (this.required.contains(parameter)) {
                throw new IllegalArgumentException("Parameter " + parameter.getIdentifier() + " is already required");
            }
            this.allowed.add(parameter);
            return this;
        }

        public LootContextType build() {
            return new LootContextType(this.required, this.allowed);
        }
    }
}

