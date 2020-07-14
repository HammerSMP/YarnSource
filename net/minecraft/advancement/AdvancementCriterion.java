/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancement;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class AdvancementCriterion {
    private final CriterionConditions conditions;

    public AdvancementCriterion(CriterionConditions conditions) {
        this.conditions = conditions;
    }

    public AdvancementCriterion() {
        this.conditions = null;
    }

    public void toPacket(PacketByteBuf buf) {
    }

    public static AdvancementCriterion fromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        Identifier lv = new Identifier(JsonHelper.getString(obj, "trigger"));
        Criterion lv2 = Criteria.getById(lv);
        if (lv2 == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + lv);
        }
        Object lv3 = lv2.conditionsFromJson(JsonHelper.getObject(obj, "conditions", new JsonObject()), predicateDeserializer);
        return new AdvancementCriterion((CriterionConditions)lv3);
    }

    public static AdvancementCriterion fromPacket(PacketByteBuf buf) {
        return new AdvancementCriterion();
    }

    public static Map<String, AdvancementCriterion> criteriaFromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry entry : obj.entrySet()) {
            map.put(entry.getKey(), AdvancementCriterion.fromJson(JsonHelper.asObject((JsonElement)entry.getValue(), "criterion"), predicateDeserializer));
        }
        return map;
    }

    public static Map<String, AdvancementCriterion> criteriaFromPacket(PacketByteBuf buf) {
        HashMap map = Maps.newHashMap();
        int i = buf.readVarInt();
        for (int j = 0; j < i; ++j) {
            map.put(buf.readString(32767), AdvancementCriterion.fromPacket(buf));
        }
        return map;
    }

    public static void criteriaToPacket(Map<String, AdvancementCriterion> criteria, PacketByteBuf buf) {
        buf.writeVarInt(criteria.size());
        for (Map.Entry<String, AdvancementCriterion> entry : criteria.entrySet()) {
            buf.writeString(entry.getKey());
            entry.getValue().toPacket(buf);
        }
    }

    @Nullable
    public CriterionConditions getConditions() {
        return this.conditions;
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("trigger", this.conditions.getId().toString());
        JsonObject jsonObject2 = this.conditions.toJson(AdvancementEntityPredicateSerializer.INSTANCE);
        if (jsonObject2.size() != 0) {
            jsonObject.add("conditions", (JsonElement)jsonObject2);
        }
        return jsonObject;
    }
}

