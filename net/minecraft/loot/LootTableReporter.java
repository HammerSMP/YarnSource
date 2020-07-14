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

    public LootTableReporter(LootContextType contextType, Function<Identifier, LootCondition> conditionGetter, Function<Identifier, LootTable> tableFactory) {
        this((Multimap<String, String>)HashMultimap.create(), () -> "", contextType, conditionGetter, (Set<Identifier>)ImmutableSet.of(), tableFactory, (Set<Identifier>)ImmutableSet.of());
    }

    public LootTableReporter(Multimap<String, String> messages, Supplier<String> nameFactory, LootContextType contextType, Function<Identifier, LootCondition> conditionGetter, Set<Identifier> conditions, Function<Identifier, LootTable> tableGetter, Set<Identifier> tables) {
        this.messages = messages;
        this.nameFactory = nameFactory;
        this.contextType = contextType;
        this.conditionGetter = conditionGetter;
        this.conditions = conditions;
        this.tableGetter = tableGetter;
        this.tables = tables;
    }

    private String getName() {
        if (this.name == null) {
            this.name = this.nameFactory.get();
        }
        return this.name;
    }

    public void report(String message) {
        this.messages.put((Object)this.getName(), (Object)message);
    }

    public LootTableReporter makeChild(String name) {
        return new LootTableReporter(this.messages, () -> this.getName() + name, this.contextType, this.conditionGetter, this.conditions, this.tableGetter, this.tables);
    }

    public LootTableReporter withTable(String name, Identifier id) {
        ImmutableSet immutableSet = ImmutableSet.builder().addAll(this.tables).add((Object)id).build();
        return new LootTableReporter(this.messages, () -> this.getName() + name, this.contextType, this.conditionGetter, this.conditions, this.tableGetter, (Set<Identifier>)immutableSet);
    }

    public LootTableReporter withCondition(String name, Identifier id) {
        ImmutableSet immutableSet = ImmutableSet.builder().addAll(this.conditions).add((Object)id).build();
        return new LootTableReporter(this.messages, () -> this.getName() + name, this.contextType, this.conditionGetter, (Set<Identifier>)immutableSet, this.tableGetter, this.tables);
    }

    public boolean hasTable(Identifier id) {
        return this.tables.contains(id);
    }

    public boolean hasCondition(Identifier id) {
        return this.conditions.contains(id);
    }

    public Multimap<String, String> getMessages() {
        return ImmutableMultimap.copyOf(this.messages);
    }

    public void validateContext(LootContextAware contextAware) {
        this.contextType.validate(this, contextAware);
    }

    @Nullable
    public LootTable getTable(Identifier id) {
        return this.tableGetter.apply(id);
    }

    @Nullable
    public LootCondition getCondition(Identifier id) {
        return this.conditionGetter.apply(id);
    }

    public LootTableReporter withContextType(LootContextType contextType) {
        return new LootTableReporter(this.messages, this.nameFactory, contextType, this.conditionGetter, this.conditions, this.tableGetter, this.tables);
    }
}

