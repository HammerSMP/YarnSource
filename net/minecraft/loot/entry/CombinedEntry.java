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
import net.minecraft.class_5341;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.EntryCombiner;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.util.JsonHelper;

public abstract class CombinedEntry
extends LootEntry {
    protected final LootEntry[] children;
    private final EntryCombiner predicate;

    protected CombinedEntry(LootEntry[] args, class_5341[] args2) {
        super(args2);
        this.children = args;
        this.predicate = this.combine(args);
    }

    @Override
    public void validate(LootTableReporter arg) {
        super.validate(arg);
        if (this.children.length == 0) {
            arg.report("Empty children list");
        }
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].validate(arg.makeChild(".entry[" + i + "]"));
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

    public static <T extends CombinedEntry> LootEntry.class_5337<T> createSerializer(final Factory<T> arg) {
        return new LootEntry.class_5337<T>(){

            @Override
            public void method_422(JsonObject jsonObject, T arg2, JsonSerializationContext jsonSerializationContext) {
                jsonObject.add("children", jsonSerializationContext.serialize((Object)((CombinedEntry)arg2).children));
            }

            @Override
            public final T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
                LootEntry[] lvs = JsonHelper.deserialize(jsonObject, "children", jsonDeserializationContext, LootEntry[].class);
                return arg.create(lvs, args);
            }

            @Override
            public /* synthetic */ LootEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
                return this.fromJson(jsonObject, jsonDeserializationContext, args);
            }
        };
    }

    @FunctionalInterface
    public static interface Factory<T extends CombinedEntry> {
        public T create(LootEntry[] var1, class_5341[] var2);
    }
}

