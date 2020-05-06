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
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class DynamicEntry
extends LeafEntry {
    public static final Identifier instance = new Identifier("dynamic");
    private final Identifier name;

    private DynamicEntry(Identifier arg, int i, int j, LootCondition[] args, LootFunction[] args2) {
        super(i, j, args, args2);
        this.name = arg;
    }

    @Override
    public void drop(Consumer<ItemStack> consumer, LootContext arg) {
        arg.drop(this.name, consumer);
    }

    public static LeafEntry.Builder<?> builder(Identifier arg) {
        return DynamicEntry.builder((int i, int j, LootCondition[] args, LootFunction[] args2) -> new DynamicEntry(arg, i, j, args, args2));
    }

    public static class Serializer
    extends LeafEntry.Serializer<DynamicEntry> {
        public Serializer() {
            super(new Identifier("dynamic"), DynamicEntry.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, DynamicEntry arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            jsonObject.addProperty("name", arg.name.toString());
        }

        @Override
        protected DynamicEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "name"));
            return new DynamicEntry(lv, i, j, args, args2);
        }

        @Override
        protected /* synthetic */ LeafEntry fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, LootCondition[] args, LootFunction[] args2) {
            return this.fromJson(jsonObject, jsonDeserializationContext, i, j, args, args2);
        }
    }
}

