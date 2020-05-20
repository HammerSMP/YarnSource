/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class VillagerFollowRangeFix
extends ChoiceFix {
    public VillagerFollowRangeFix(Schema schema) {
        super(schema, false, "Villager Follow Range Fix", TypeReferences.ENTITY, "minecraft:villager");
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), VillagerFollowRangeFix::method_27914);
    }

    private static Dynamic<?> method_27914(Dynamic<?> dynamic) {
        return dynamic.update("Attributes", dynamic22 -> dynamic.createList(dynamic22.asStream().map(dynamic -> {
            if (!dynamic.get("Name").asString().orElse("").equals("generic.follow_range") || ((Number)dynamic.get("Base").asNumber().orElse(0)).doubleValue() != 16.0) {
                return dynamic;
            }
            return dynamic.set("Base", dynamic.createDouble(48.0));
        })));
    }
}

