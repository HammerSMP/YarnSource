/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReferenceLootCondition
implements LootCondition {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Identifier id;

    public ReferenceLootCondition(Identifier arg) {
        this.id = arg;
    }

    @Override
    public void validate(LootTableReporter arg) {
        if (arg.hasCondition(this.id)) {
            arg.report("Condition " + this.id + " is recursively called");
            return;
        }
        LootCondition.super.validate(arg);
        LootCondition lv = arg.getCondition(this.id);
        if (lv == null) {
            arg.report("Unknown condition table called " + this.id);
        } else {
            lv.validate(arg.withTable(".{" + this.id + "}", this.id));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean test(LootContext arg) {
        LootCondition lv = arg.getCondition(this.id);
        if (arg.addCondition(lv)) {
            try {
                boolean bl = lv.test(arg);
                return bl;
            }
            finally {
                arg.removeCondition(lv);
            }
        }
        LOGGER.warn("Detected infinite loop in loot tables");
        return false;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((LootContext)object);
    }

    public static class Factory
    extends LootCondition.Factory<ReferenceLootCondition> {
        protected Factory() {
            super(new Identifier("reference"), ReferenceLootCondition.class);
        }

        @Override
        public void toJson(JsonObject jsonObject, ReferenceLootCondition arg, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("name", arg.id.toString());
        }

        @Override
        public ReferenceLootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Identifier lv = new Identifier(JsonHelper.getString(jsonObject, "name"));
            return new ReferenceLootCondition(lv);
        }

        @Override
        public /* synthetic */ LootCondition fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return this.fromJson(jsonObject, jsonDeserializationContext);
        }
    }
}

