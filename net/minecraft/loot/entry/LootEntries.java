/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.loot.entry;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.GroupEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.entry.SequenceEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.ArrayUtils;

public class LootEntries {
    private static final Map<Identifier, LootEntry.Serializer<?>> idSerializers = Maps.newHashMap();
    private static final Map<Class<?>, LootEntry.Serializer<?>> classSerializers = Maps.newHashMap();

    private static void register(LootEntry.Serializer<?> arg) {
        idSerializers.put(arg.getIdentifier(), arg);
        classSerializers.put(arg.getType(), arg);
    }

    static {
        LootEntries.register(CombinedEntry.createSerializer(new Identifier("alternatives"), AlternativeEntry.class, AlternativeEntry::new));
        LootEntries.register(CombinedEntry.createSerializer(new Identifier("sequence"), SequenceEntry.class, SequenceEntry::new));
        LootEntries.register(CombinedEntry.createSerializer(new Identifier("group"), GroupEntry.class, GroupEntry::new));
        LootEntries.register(new EmptyEntry.Serializer());
        LootEntries.register(new ItemEntry.Serializer());
        LootEntries.register(new LootTableEntry.Serializer());
        LootEntries.register(new DynamicEntry.Serializer());
        LootEntries.register(new TagEntry.Serializer());
    }

    public static class Serializer
    implements JsonDeserializer<LootEntry>,
    JsonSerializer<LootEntry> {
        public LootEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entry");
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "type"));
            LootEntry.Serializer lv2 = (LootEntry.Serializer)idSerializers.get(lv);
            if (lv2 == null) {
                throw new JsonParseException("Unknown item type: " + lv);
            }
            LootCondition[] lvs = JsonHelper.deserialize(jsonObject, "conditions", new LootCondition[0], jsonDeserializationContext, LootCondition[].class);
            return lv2.fromJson(jsonObject, jsonDeserializationContext, lvs);
        }

        public JsonElement serialize(LootEntry arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            LootEntry.Serializer<LootEntry> lv = Serializer.getSerializer(arg.getClass());
            jsonObject.addProperty("type", lv.getIdentifier().toString());
            if (!ArrayUtils.isEmpty((Object[])arg.conditions)) {
                jsonObject.add("conditions", jsonSerializationContext.serialize((Object)arg.conditions));
            }
            lv.toJson(jsonObject, arg, jsonSerializationContext);
            return jsonObject;
        }

        private static LootEntry.Serializer<LootEntry> getSerializer(Class<?> arg) {
            LootEntry.Serializer lv = (LootEntry.Serializer)classSerializers.get(arg);
            if (lv == null) {
                throw new JsonParseException("Unknown item type: " + arg);
            }
            return lv;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((LootEntry)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

