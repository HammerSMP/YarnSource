/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class ChangedDimensionCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("changed_dimension");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        RegistryKey<World> lv = jsonObject.has("from") ? RegistryKey.of(Registry.DIMENSION, new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
        RegistryKey<World> lv2 = jsonObject.has("to") ? RegistryKey.of(Registry.DIMENSION, new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
        return new Conditions(arg, lv, lv2);
    }

    public void trigger(ServerPlayerEntity arg, RegistryKey<World> arg2, RegistryKey<World> arg32) {
        this.test(arg, arg3 -> arg3.matches(arg2, arg32));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended arg, AdvancementEntityPredicateDeserializer arg2) {
        return this.conditionsFromJson(jsonObject, arg, arg2);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        @Nullable
        private final RegistryKey<World> from;
        @Nullable
        private final RegistryKey<World> to;

        public Conditions(EntityPredicate.Extended arg, @Nullable RegistryKey<World> arg2, @Nullable RegistryKey<World> arg3) {
            super(ID, arg);
            this.from = arg2;
            this.to = arg3;
        }

        public static Conditions to(RegistryKey<World> arg) {
            return new Conditions(EntityPredicate.Extended.EMPTY, null, arg);
        }

        public boolean matches(RegistryKey<World> arg, RegistryKey<World> arg2) {
            if (this.from != null && this.from != arg) {
                return false;
            }
            return this.to == null || this.to == arg2;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
            JsonObject jsonObject = super.toJson(arg);
            if (this.from != null) {
                jsonObject.addProperty("from", this.from.getValue().toString());
            }
            if (this.to != null) {
                jsonObject.addProperty("to", this.to.getValue().toString());
            }
            return jsonObject;
        }
    }
}

