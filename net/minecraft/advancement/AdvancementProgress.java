/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.JsonHelper;

public class AdvancementProgress
implements Comparable<AdvancementProgress> {
    private final Map<String, CriterionProgress> criteriaProgresses = Maps.newHashMap();
    private String[][] requirements = new String[0][];

    public void init(Map<String, AdvancementCriterion> map, String[][] strings) {
        Set<String> set = map.keySet();
        this.criteriaProgresses.entrySet().removeIf(entry -> !set.contains(entry.getKey()));
        for (String string : set) {
            if (this.criteriaProgresses.containsKey(string)) continue;
            this.criteriaProgresses.put(string, new CriterionProgress());
        }
        this.requirements = strings;
    }

    public boolean isDone() {
        if (this.requirements.length == 0) {
            return false;
        }
        for (String[] strings : this.requirements) {
            boolean bl = false;
            for (String string : strings) {
                CriterionProgress lv = this.getCriterionProgress(string);
                if (lv == null || !lv.isObtained()) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            return false;
        }
        return true;
    }

    public boolean isAnyObtained() {
        for (CriterionProgress lv : this.criteriaProgresses.values()) {
            if (!lv.isObtained()) continue;
            return true;
        }
        return false;
    }

    public boolean obtain(String string) {
        CriterionProgress lv = this.criteriaProgresses.get(string);
        if (lv != null && !lv.isObtained()) {
            lv.obtain();
            return true;
        }
        return false;
    }

    public boolean reset(String string) {
        CriterionProgress lv = this.criteriaProgresses.get(string);
        if (lv != null && lv.isObtained()) {
            lv.reset();
            return true;
        }
        return false;
    }

    public String toString() {
        return "AdvancementProgress{criteria=" + this.criteriaProgresses + ", requirements=" + Arrays.deepToString((Object[])this.requirements) + '}';
    }

    public void toPacket(PacketByteBuf arg) {
        arg.writeVarInt(this.criteriaProgresses.size());
        for (Map.Entry<String, CriterionProgress> entry : this.criteriaProgresses.entrySet()) {
            arg.writeString(entry.getKey());
            entry.getValue().toPacket(arg);
        }
    }

    public static AdvancementProgress fromPacket(PacketByteBuf arg) {
        AdvancementProgress lv = new AdvancementProgress();
        int i = arg.readVarInt();
        for (int j = 0; j < i; ++j) {
            lv.criteriaProgresses.put(arg.readString(32767), CriterionProgress.fromPacket(arg));
        }
        return lv;
    }

    @Nullable
    public CriterionProgress getCriterionProgress(String string) {
        return this.criteriaProgresses.get(string);
    }

    @Environment(value=EnvType.CLIENT)
    public float getProgressBarPercentage() {
        if (this.criteriaProgresses.isEmpty()) {
            return 0.0f;
        }
        float f = this.requirements.length;
        float g = this.countObtainedRequirements();
        return g / f;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public String getProgressBarFraction() {
        if (this.criteriaProgresses.isEmpty()) {
            return null;
        }
        int i = this.requirements.length;
        if (i <= 1) {
            return null;
        }
        int j = this.countObtainedRequirements();
        return j + "/" + i;
    }

    @Environment(value=EnvType.CLIENT)
    private int countObtainedRequirements() {
        int i = 0;
        for (String[] strings : this.requirements) {
            boolean bl = false;
            for (String string : strings) {
                CriterionProgress lv = this.getCriterionProgress(string);
                if (lv == null || !lv.isObtained()) continue;
                bl = true;
                break;
            }
            if (!bl) continue;
            ++i;
        }
        return i;
    }

    public Iterable<String> getUnobtainedCriteria() {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<String, CriterionProgress> entry : this.criteriaProgresses.entrySet()) {
            if (entry.getValue().isObtained()) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    public Iterable<String> getObtainedCriteria() {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<String, CriterionProgress> entry : this.criteriaProgresses.entrySet()) {
            if (!entry.getValue().isObtained()) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    @Nullable
    public Date getEarliestProgressObtainDate() {
        Date date = null;
        for (CriterionProgress lv : this.criteriaProgresses.values()) {
            if (!lv.isObtained() || date != null && !lv.getObtainedDate().before(date)) continue;
            date = lv.getObtainedDate();
        }
        return date;
    }

    @Override
    public int compareTo(AdvancementProgress arg) {
        Date date = this.getEarliestProgressObtainDate();
        Date date2 = arg.getEarliestProgressObtainDate();
        if (date == null && date2 != null) {
            return 1;
        }
        if (date != null && date2 == null) {
            return -1;
        }
        if (date == null && date2 == null) {
            return 0;
        }
        return date.compareTo(date2);
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((AdvancementProgress)object);
    }

    public static class Serializer
    implements JsonDeserializer<AdvancementProgress>,
    JsonSerializer<AdvancementProgress> {
        public JsonElement serialize(AdvancementProgress arg, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = new JsonObject();
            for (Map.Entry entry : arg.criteriaProgresses.entrySet()) {
                CriterionProgress lv = (CriterionProgress)entry.getValue();
                if (!lv.isObtained()) continue;
                jsonObject2.add((String)entry.getKey(), lv.toJson());
            }
            if (!jsonObject2.entrySet().isEmpty()) {
                jsonObject.add("criteria", (JsonElement)jsonObject2);
            }
            jsonObject.addProperty("done", Boolean.valueOf(arg.isDone()));
            return jsonObject;
        }

        public AdvancementProgress deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "criteria", new JsonObject());
            AdvancementProgress lv = new AdvancementProgress();
            for (Map.Entry entry : jsonObject2.entrySet()) {
                String string = (String)entry.getKey();
                lv.criteriaProgresses.put(string, CriterionProgress.obtainedAt(JsonHelper.asString((JsonElement)entry.getValue(), string)));
            }
            return lv;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((AdvancementProgress)object, type, jsonSerializationContext);
        }
    }
}

