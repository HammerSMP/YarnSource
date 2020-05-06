/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.loot.entry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public abstract class CombinedEntry
extends LootEntry {
    protected final LootEntry[] children;
    private final EntryCombiner predicate;

    protected CombinedEntry(LootEntry[] args, LootCondition[] args2) {
        super(args2);
        this.children = args;
        this.predicate = this.combine(args);
    }

    @Override
    public void check(LootTableReporter arg) {
        super.check(arg);
        if (this.children.length == 0) {
            arg.report("Empty children list");
        }
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].check(arg.makeChild(".entry[" + i + "]"));
        }
    }

    protected abstract EntryCombiner combine(EntryCombiner[] var1);

    @Override
    public final boolean expand(LootContext arg, Consumer<LootChoice> consumer) {
        if (!this.test(arg)) {
            return false;
        }
        return this.predicate.expand(arg, consumer);
    }

    public static <T extends CombinedEntry> Serializer<T> createSerializer(Identifier arg, Class<T> arg2, final Factory<T> arg3) {
        return new Serializer<T>(arg, arg2){

            @Override
            protected T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootEntry[] args, LootCondition[] args2) {
                return arg3.create(args, args2);
            }
        };
    }

    public static abstract class Serializer<T extends CombinedEntry>
    extends LootEntry.Serializer<T> {
        public Serializer(Identifier arg, Class<T> arg2) {
            super(arg, arg2);
        }

        @Override
        public void toJson(JsonObject jsonObject, T arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("children", jsonSerializationContext.serialize((Object)((CombinedEntry)arg).children));
        }

        @Override
        public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            LootEntry[] lvs = JsonHelper.deserialize(jsonObject, "children", jsonDeserializationContext, LootEntry[].class);
            return this.fromJson(jsonObject, jsonDeserializationContext, lvs, args);
        }

        protected abstract T fromJson(JsonObject var1, JsonDeserializationContext var2, LootEntry[] var3, LootCondition[] var4);

        @Override
        public /* synthetic */ LootEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }

    @FunctionalInterface
    public static interface Factory<T extends CombinedEntry> {
        public T create(LootEntry[] var1, LootCondition[] var2);
    }
}

