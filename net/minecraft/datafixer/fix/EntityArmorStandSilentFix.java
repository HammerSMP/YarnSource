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

public class EntityArmorStandSilentFix
extends ChoiceFix {
    public EntityArmorStandSilentFix(Schema schema, boolean bl) {
        super(schema, bl, "EntityArmorStandSilentFix", TypeReferences.ENTITY, "ArmorStand");
    }

    public Dynamic<?> fixSilent(Dynamic<?> dynamic) {
        if (dynamic.get("Silent").asBoolean(false) && !dynamic.get("Marker").asBoolean(false)) {
            return dynamic.remove("Silent");
        }
        return dynamic;
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixSilent);
    }
}

