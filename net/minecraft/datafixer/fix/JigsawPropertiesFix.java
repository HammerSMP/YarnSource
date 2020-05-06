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

public class JigsawPropertiesFix
extends ChoiceFix {
    public JigsawPropertiesFix(Schema schema, boolean bl) {
        super(schema, bl, "JigsawPropertiesFix", TypeReferences.BLOCK_ENTITY, "minecraft:jigsaw");
    }

    private static Dynamic<?> renameProperties(Dynamic<?> dynamic) {
        String string = dynamic.get("attachement_type").asString("minecraft:empty");
        String string2 = dynamic.get("target_pool").asString("minecraft:empty");
        return dynamic.set("name", dynamic.createString(string)).set("target", dynamic.createString(string)).remove("attachement_type").set("pool", dynamic.createString(string2)).remove("target_pool");
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), JigsawPropertiesFix::renameProperties);
    }
}

