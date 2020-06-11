/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SetAttributesLootFunction
extends ConditionalLootFunction {
    private final List<Attribute> attributes;

    private SetAttributesLootFunction(LootCondition[] args, List<Attribute> list) {
        super(args);
        this.attributes = ImmutableList.copyOf(list);
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_ATTRIBUTES;
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        Random random = arg2.getRandom();
        for (Attribute lv : this.attributes) {
            UUID uUID = lv.id;
            if (uUID == null) {
                uUID = UUID.randomUUID();
            }
            EquipmentSlot lv2 = Util.getRandom(lv.slots, random);
            arg.addAttributeModifier(lv.attribute, new EntityAttributeModifier(uUID, lv.name, (double)lv.amountRange.nextFloat(random), lv.operation), lv2);
        }
        return arg;
    }

    static class Attribute {
        private final String name;
        private final EntityAttribute attribute;
        private final EntityAttributeModifier.Operation operation;
        private final UniformLootTableRange amountRange;
        @Nullable
        private final UUID id;
        private final EquipmentSlot[] slots;

        private Attribute(String string, EntityAttribute arg, EntityAttributeModifier.Operation arg2, UniformLootTableRange arg3, EquipmentSlot[] args, @Nullable UUID uUID) {
            this.name = string;
            this.attribute = arg;
            this.operation = arg2;
            this.amountRange = arg3;
            this.id = uUID;
            this.slots = args;
        }

        public JsonObject serialize(JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", this.name);
            jsonObject.addProperty("attribute", Registry.ATTRIBUTE.getId(this.attribute).toString());
            jsonObject.addProperty("operation", Attribute.getName(this.operation));
            jsonObject.add("amount", jsonSerializationContext.serialize((Object)this.amountRange));
            if (this.id != null) {
                jsonObject.addProperty("id", this.id.toString());
            }
            if (this.slots.length == 1) {
                jsonObject.addProperty("slot", this.slots[0].getName());
            } else {
                JsonArray jsonArray = new JsonArray();
                for (EquipmentSlot lv : this.slots) {
                    jsonArray.add((JsonElement)new JsonPrimitive(lv.getName()));
                }
                jsonObject.add("slot", (JsonElement)jsonArray);
            }
            return jsonObject;
        }

        /*
         * WARNING - void declaration
         */
        public static Attribute deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            void lvs3;
            String string = JsonHelper.getString(jsonObject, "name");
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "attribute"));
            EntityAttribute lv2 = Registry.ATTRIBUTE.get(lv);
            if (lv2 == null) {
                throw new JsonSyntaxException("Unknown attribute: " + lv);
            }
            EntityAttributeModifier.Operation lv3 = Attribute.fromName(JsonHelper.getString(jsonObject, "operation"));
            UniformLootTableRange lv4 = JsonHelper.deserialize(jsonObject, "amount", jsonDeserializationContext, UniformLootTableRange.class);
            UUID uUID = null;
            if (JsonHelper.hasString(jsonObject, "slot")) {
                EquipmentSlot[] lvs = new EquipmentSlot[]{EquipmentSlot.byName(JsonHelper.getString(jsonObject, "slot"))};
            } else if (JsonHelper.hasArray(jsonObject, "slot")) {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "slot");
                EquipmentSlot[] lvs2 = new EquipmentSlot[jsonArray.size()];
                int i = 0;
                for (JsonElement jsonElement : jsonArray) {
                    lvs2[i++] = EquipmentSlot.byName(JsonHelper.asString(jsonElement, "slot"));
                }
                if (lvs2.length == 0) {
                    throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                }
            } else {
                throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
            }
            if (jsonObject.has("id")) {
                String string2 = JsonHelper.getString(jsonObject, "id");
                try {
                    uUID = UUID.fromString(string2);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    throw new JsonSyntaxException("Invalid attribute modifier id '" + string2 + "' (must be UUID format, with dashes)");
                }
            }
            return new Attribute(string, lv2, lv3, lv4, (EquipmentSlot[])lvs3, uUID);
        }

        private static String getName(EntityAttributeModifier.Operation arg) {
            switch (arg) {
                case ADDITION: {
                    return "addition";
                }
                case MULTIPLY_BASE: {
                    return "multiply_base";
                }
                case MULTIPLY_TOTAL: {
                    return "multiply_total";
                }
            }
            throw new IllegalArgumentException("Unknown operation " + (Object)((Object)arg));
        }

        private static EntityAttributeModifier.Operation fromName(String string) {
            switch (string) {
                case "addition": {
                    return EntityAttributeModifier.Operation.ADDITION;
                }
                case "multiply_base": {
                    return EntityAttributeModifier.Operation.MULTIPLY_BASE;
                }
                case "multiply_total": {
                    return EntityAttributeModifier.Operation.MULTIPLY_TOTAL;
                }
            }
            throw new JsonSyntaxException("Unknown attribute modifier operation " + string);
        }
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<SetAttributesLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetAttributesLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            JsonArray jsonArray = new JsonArray();
            for (Attribute lv : arg.attributes) {
                jsonArray.add((JsonElement)lv.serialize(jsonSerializationContext));
            }
            jsonObject.add("modifiers", (JsonElement)jsonArray);
        }

        @Override
        public SetAttributesLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "modifiers");
            ArrayList list = Lists.newArrayListWithExpectedSize((int)jsonArray.size());
            for (JsonElement jsonElement : jsonArray) {
                list.add(Attribute.deserialize(JsonHelper.asObject(jsonElement, "modifier"), jsonDeserializationContext));
            }
            if (list.isEmpty()) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            }
            return new SetAttributesLootFunction(args, list);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }
}

