/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class class_5429
extends ChoiceFix {
    public class_5429(Schema schema, boolean bl) {
        super(schema, bl, "Remove Golem Gossip Fix", TypeReferences.ENTITY, "minecraft:villager");
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), class_5429::method_30326);
    }

    private static Dynamic<?> method_30326(Dynamic<?> dynamic) {
        return dynamic.update("Gossips", dynamic22 -> dynamic.createList(dynamic22.asStream().filter(dynamic -> !dynamic.get("Type").asString("").equals("golem"))));
    }
}

