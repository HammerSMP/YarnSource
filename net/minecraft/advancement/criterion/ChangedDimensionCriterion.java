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

    public void trigger(ServerPlayerEntity player, RegistryKey<World> from, RegistryKey<World> to) {
        this.test(player, arg3 -> arg3.matches(from, to));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        @Nullable
        private final RegistryKey<World> from;
        @Nullable
        private final RegistryKey<World> to;

        public Conditions(EntityPredicate.Extended player, @Nullable RegistryKey<World> from, @Nullable RegistryKey<World> to) {
            super(ID, player);
            this.from = from;
            this.to = to;
        }

        public static Conditions to(RegistryKey<World> to) {
            return new Conditions(EntityPredicate.Extended.EMPTY, null, to);
        }

        public boolean matches(RegistryKey<World> from, RegistryKey<World> to) {
            if (this.from != null && this.from != from) {
                return false;
            }
            return this.to == null || this.to == to;
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
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

