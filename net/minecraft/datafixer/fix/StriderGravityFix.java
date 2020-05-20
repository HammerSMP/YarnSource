/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class StriderGravityFix
extends ChoiceFix {
    public StriderGravityFix(Schema schema, boolean bl) {
        super(schema, bl, "StriderGravityFix", TypeReferences.ENTITY, "minecraft:strider");
    }

    public Dynamic<?> updateNoGravityTag(Dynamic<?> dynamic) {
        if (dynamic.get("NoGravity").asBoolean(false)) {
            return dynamic.set("NoGravity", dynamic.createBoolean(false));
        }
        return dynamic;
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::updateNoGravityTag);
    }
}

