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

    public AdvancementCriterion(CriterionConditions arg) {
        this.conditions = arg;
    }

    public AdvancementCriterion() {
        this.conditions = null;
    }

    public void toPacket(PacketByteBuf arg) {
    }

    public static AdvancementCriterion fromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer arg) {
        Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "trigger"));
        Criterion lv2 = Criteria.getById(lv);
        if (lv2 == null) {
            throw new JsonSyntaxException("Invalid criterion trigger: " + lv);
        }
        Object lv3 = lv2.conditionsFromJson(JsonHelper.getObject(jsonObject, "conditions", new JsonObject()), arg);
        return new AdvancementCriterion((CriterionConditions)lv3);
    }

    public static AdvancementCriterion fromPacket(PacketByteBuf arg) {
        return new AdvancementCriterion();
    }

    public static Map<String, AdvancementCriterion> criteriaFromJson(JsonObject jsonObject, AdvancementEntityPredicateDeserializer arg) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), AdvancementCriterion.fromJson(JsonHelper.asObject((JsonElement)entry.getValue(), "criterion"), arg));
        }
        return map;
    }

    public static Map<String, AdvancementCriterion> criteriaFromPacket(PacketByteBuf arg) {
        HashMap map = Maps.newHashMap();
        int i = arg.readVarInt();
        for (int j = 0; j < i; ++j) {
            map.put(arg.readString(32767), AdvancementCriterion.fromPacket(arg));
        }
        return map;
    }

    public static void criteriaToPacket(Map<String, AdvancementCriterion> map, PacketByteBuf arg) {
        arg.writeVarInt(map.size());
        for (Map.Entry<String, AdvancementCriterion> entry : map.entrySet()) {
            arg.writeString(entry.getKey());
            entry.getValue().toPacket(arg);
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

