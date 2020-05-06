/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.util.Identifier;

public abstract class AbstractCriterionConditions
implements CriterionConditions {
    private final Identifier id;
    private final EntityPredicate.Extended playerPredicate;

    public AbstractCriterionConditions(Identifier arg, EntityPredicate.Extended arg2) {
        this.id = arg;
        this.playerPredicate = arg2;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    protected EntityPredicate.Extended getPlayerPredicate() {
        return this.playerPredicate;
    }

    @Override
    public JsonObject toJson(AdvancementEntityPredicateSerializer arg) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("player", this.playerPredicate.toJson(arg));
        return jsonObject;
    }

    public String toString() {
        return "AbstractCriterionInstance{criterion=" + this.id + '}';
    }
}

