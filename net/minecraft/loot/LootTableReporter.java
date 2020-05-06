/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nullable
 */
package net.minecraft.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;

public class LootTableReporter {
    private final Multimap<String, String> messages;
    private final Supplier<String> nameFactory;
    private final LootContextType contextType;
    private final Function<Identifier, LootCondition> conditionGetter;
    private final Set<Identifier> conditions;
    private final Function<Identifier, LootTable> tableGetter;
    private final Set<Identifier> tables;
    private String name;

    public LootTableReporter(LootContextType arg, Function<Identifier, LootCondition> function, Function<Identifier, LootTable> function2) {
        this((Multimap<String, String>)HashMultimap.create(), () -> "", arg, function, (Set<Identifier>)ImmutableSet.of(), function2, (Set<Identifier>)ImmutableSet.of());
    }

    public LootTableReporter(Multimap<String, String> multimap, Supplier<String> supplier, LootContextType arg, Function<Identifier, LootCondition> function, Set<Identifier> set, Function<Identifier, LootTable> function2, Set<Identifier> set2) {
        this.messages = multimap;
        this.nameFactory = supplier;
        this.contextType = arg;
        this.conditionGetter = function;
        this.conditions = set;
        this.tableGetter = function2;
        this.tables = set2;
    }

    private String getName() {
        if (this.name == null) {
            this.name = this.nameFactory.get();
        }
        return this.name;
    }

    public void report(String string) {
        this.messages.put((Object)this.getName(), (Object)string);
    }

    public LootTableReporter makeChild(String string) {
        return new LootTableReporter(this.messages, () -> this.getName() + string, this.contextType, this.conditionGetter, this.conditions, this.tableGetter, this.tables);
    }

    public LootTableReporter withTable(String string, Identifier arg) {
        ImmutableSet immutableSet = ImmutableSet.builder().addAll(this.tables).add((Object)arg).build();
        return new LootTableReporter(this.messages, () -> this.getName() + string, this.contextType, this.conditionGetter, this.conditions, this.tableGetter, (Set<Identifier>)immutableSet);
    }

    public LootTableReporter withCondition(String string, Identifier arg) {
        ImmutableSet immutableSet = ImmutableSet.builder().addAll(this.conditions).add((Object)arg).build();
        return new LootTableReporter(this.messages, () -> this.getName() + string, this.contextType, this.conditionGetter, (Set<Identifier>)immutableSet, this.tableGetter, this.tables);
    }

    public boolean hasTable(Identifier arg) {
        return this.tables.contains(arg);
    }

    public boolean hasCondition(Identifier arg) {
        return this.conditions.contains(arg);
    }

    public Multimap<String, String> getMessages() {
        return ImmutableMultimap.copyOf(this.messages);
    }

    public void checkContext(LootContextAware arg) {
        this.contextType.check(this, arg);
    }

    @Nullable
    public LootTable getTable(Identifier arg) {
        return this.tableGetter.apply(arg);
    }

    @Nullable
    public LootCondition getCondition(Identifier arg) {
        return this.conditionGetter.apply(arg);
    }

    public LootTableReporter withContextType(LootContextType arg) {
        return new LootTableReporter(this.messages, this.nameFactory, arg, this.conditionGetter, this.conditions, this.tableGetter, this.tables);
    }
}

