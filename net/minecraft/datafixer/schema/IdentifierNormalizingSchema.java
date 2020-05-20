/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Const$PrimitiveType
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.PrimitiveCodec
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Const;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.util.Identifier;

public class IdentifierNormalizingSchema
extends Schema {
    public static final PrimitiveCodec<String> field_24652 = new PrimitiveCodec<String>(){

        public <T> DataResult<String> read(DynamicOps<T> dynamicOps, T object) {
            return dynamicOps.getStringValue(object).map(IdentifierNormalizingSchema::normalize);
        }

        public <T> T write(DynamicOps<T> dynamicOps, String string) {
            return (T)dynamicOps.createString(string);
        }

        public String toString() {
            return "NamespacedString";
        }

        public /* synthetic */ Object write(DynamicOps dynamicOps, Object object) {
            return this.write(dynamicOps, (String)object);
        }
    };
    private static final Type<String> field_24653 = new Const.PrimitiveType(field_24652);

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

    public static Type<String> method_28295() {
        return field_24653;
    }

    public Type<?> getChoiceType(DSL.TypeReference typeReference, String string) {
        return super.getChoiceType(typeReference, IdentifierNormalizingSchema.normalize(string));
    }
}

