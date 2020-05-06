/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.util.Identifier;

public class IdentifierNormalizingSchema
extends Schema {
    public IdentifierNormalizingSchema(int i, Schema schema) {
        super(i, schema);
    }

    public static String normalize(String string) {
        Identifier lv = Identifier.tryParse(string);
        if (lv != null) {
            return lv.toString();
        }
        return string;
    }

    public Type<?> getChoiceType(DSL.TypeReference typeReference, String string) {
        return super.getChoiceType(typeReference, IdentifierNormalizingSchema.normalize(string));
    }
}

