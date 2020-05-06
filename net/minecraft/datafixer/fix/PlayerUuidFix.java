/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.AbstractUuidFix;
import net.minecraft.datafixer.fix.EntityUuidFix;

public class PlayerUuidFix
extends AbstractUuidFix {
    public PlayerUuidFix(Schema schema) {
        super(schema, TypeReferences.PLAYER);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("PlayerUUIDFix", this.getInputSchema().getType(this.typeReference), typed2 -> {
            OpticFinder opticFinder = typed2.getType().findField("RootVehicle");
            return typed2.updateTyped(opticFinder, opticFinder.type(), typed -> typed.update(DSL.remainderFinder(), dynamic -> PlayerUuidFix.updateRegularMostLeast(dynamic, "Attach", "Attach").orElse((Dynamic<?>)dynamic))).update(DSL.remainderFinder(), dynamic -> EntityUuidFix.updateSelfUuid(EntityUuidFix.updateLiving(dynamic)));
        });
    }
}

