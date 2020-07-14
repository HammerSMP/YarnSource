/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.AbstractUuidFix;

public class PersistentStateUuidFix
extends AbstractUuidFix {
    public PersistentStateUuidFix(Schema outputSchema) {
        super(outputSchema, TypeReferences.SAVED_DATA);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("SavedDataUUIDFix", this.getInputSchema().getType(this.typeReference), typed2 -> typed2.updateTyped(typed2.getType().findField("data"), typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("Raids", dynamic2 -> dynamic2.createList(dynamic2.asStream().map(dynamic -> dynamic.update("HeroesOfTheVillage", dynamic2 -> dynamic2.createList(dynamic2.asStream().map(dynamic -> PersistentStateUuidFix.createArrayFromMostLeastTags(dynamic, "UUIDMost", "UUIDLeast").orElseGet(() -> {
            LOGGER.warn("HeroesOfTheVillage contained invalid UUIDs.");
            return dynamic;
        }))))))))));
    }
}

