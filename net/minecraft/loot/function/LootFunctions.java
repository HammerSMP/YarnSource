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
 *  com.google.gson.JsonSyntaxException
 */
package net.minecraft.loot.function;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.CopyStateFunction;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.EnchantWithLevelsLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.FillPlayerHeadLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetAttributesLootFunction;
import net.minecraft.loot.function.SetContentsLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.function.SetLootTableLootFunction;
import net.minecraft.loot.function.SetLoreLootFunction;
import net.minecraft.loot.function.SetNameLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class LootFunctions {
    private static final Map<Identifier, LootFunction.Factory<?>> byId = Maps.newHashMap();
    private static final Map<Class<? extends LootFunction>, LootFunction.Factory<?>> byClass = Maps.newHashMap();
    public static final BiFunction<ItemStack, LootContext, ItemStack> NOOP = (arg, arg2) -> arg;

    public static <T extends LootFunction> void register(LootFunction.Factory<? extends T> arg) {
        Identifier lv = arg.getId();
        Class<T> lv2 = arg.getFunctionClass();
        if (byId.containsKey(lv)) {
            throw new IllegalArgumentException("Can't re-register item function name " + lv);
        }
        if (byClass.containsKey(lv2)) {
            throw new IllegalArgumentException("Can't re-register item function class " + lv2.getName());
        }
        byId.put(lv, arg);
        byClass.put(lv2, arg);
    }

    public static LootFunction.Factory<?> get(Identifier arg) {
        LootFunction.Factory<?> lv = byId.get(arg);
        if (lv == null) {
            throw new IllegalArgumentException("Unknown loot item function '" + arg + "'");
        }
        return lv;
    }

    public static <T extends LootFunction> LootFunction.Factory<T> getFactory(T arg) {
        LootFunction.Factory<?> lv = byClass.get(arg.getClass());
        if (lv == null) {
            throw new IllegalArgumentException("Unknown loot item function " + arg);
        }
        return lv;
    }

    public static BiFunction<ItemStack, LootContext, ItemStack> join(BiFunction<ItemStack, LootContext, ItemStack>[] biFunctions) {
        switch (biFunctions.length) {
            case 0: {
                return NOOP;
            }
            case 1: {
                return biFunctions[0];
            }
            case 2: {
                BiFunction<ItemStack, LootContext, ItemStack> biFunction = biFunctions[0];
                BiFunction<ItemStack, LootContext, ItemStack> biFunction2 = biFunctions[1];
                return (arg, arg2) -> (ItemStack)biFunction2.apply((ItemStack)biFunction.apply((ItemStack)arg, (LootContext)arg2), (LootContext)arg2);
            }
        }
        return (arg, arg2) -> {
            for (BiFunction biFunction : biFunctions) {
                arg = (ItemStack)biFunction.apply(arg, arg2);
            }
            return arg;
        };
    }

    static {
        LootFunctions.register(new SetCountLootFunction.Factory());
        LootFunctions.register(new EnchantWithLevelsLootFunction.Factory());
        LootFunctions.register(new EnchantRandomlyLootFunction.Factory());
        LootFunctions.register(new SetNbtLootFunction.Builder());
        LootFunctions.register(new FurnaceSmeltLootFunction.Factory());
        LootFunctions.register(new LootingEnchantLootFunction.Factory());
        LootFunctions.register(new SetDamageLootFunction.Factory());
        LootFunctions.register(new SetAttributesLootFunction.Factory());
        LootFunctions.register(new SetNameLootFunction.Factory());
        LootFunctions.register(new ExplorationMapLootFunction.Factory());
        LootFunctions.register(new SetStewEffectLootFunction.Factory());
        LootFunctions.register(new CopyNameLootFunction.Factory());
        LootFunctions.register(new SetContentsLootFunction.Factory());
        LootFunctions.register(new LimitCountLootFunction.Factory());
        LootFunctions.register(new ApplyBonusLootFunction.Factory());
        LootFunctions.register(new SetLootTableLootFunction.Factory());
        LootFunctions.register(new ExplosionDecayLootFunction.Factory());
        LootFunctions.register(new SetLoreLootFunction.Factory());
        LootFunctions.register(new FillPlayerHeadLootFunction.Factory());
        LootFunctions.register(new CopyNbtLootFunction.Factory());
        LootFunctions.register(new CopyStateFunction.Factory());
    }

    public static class Factory
    implements JsonDeserializer<LootFunction>,
    JsonSerializer<LootFunction> {
        /*
         * WARNING - void declaration
         */
        public LootFunction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            void lv3;
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "function");
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "function"));
            try {
                LootFunction.Factory<?> lv2 = LootFunctions.get(lv);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw new JsonSyntaxException("Unknown function '" + lv + "'");
            }
            return lv3.fromJson(jsonObject, jsonDeserializationContext);
        }

        public JsonElement serialize(LootFunction arg, Type type, JsonSerializationContext jsonSerializationContext) {
            LootFunction.Factory<LootFunction> lv = LootFunctions.getFactory(arg);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("function", lv.getId().toString());
            lv.toJson(jsonObject, arg, jsonSerializationContext);
            return jsonObject;
        }

        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.serialize((LootFunction)object, type, jsonSerializationContext);
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }
}

