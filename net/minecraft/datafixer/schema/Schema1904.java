/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema100;

public class Schema1904
extends IdentifierNormalizingSchema {
    public Schema1904(int i, Schema schema) {
        super(i, schema);
    }

    protected static void method_16050(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.targetItems(schema));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map map = super.registerEntities(schema);
        Schema1904.method_16050(schema, map, "minecraft:cat");
        return map;
    }
}

