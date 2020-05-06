/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema100;

public class Schema1470
extends IdentifierNormalizingSchema {
    public Schema1470(int i, Schema schema) {
        super(i, schema);
    }

    protected static void method_5280(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.targetItems(schema));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map map = super.registerEntities(schema);
        Schema1470.method_5280(schema, map, "minecraft:turtle");
        Schema1470.method_5280(schema, map, "minecraft:cod_mob");
        Schema1470.method_5280(schema, map, "minecraft:tropical_fish");
        Schema1470.method_5280(schema, map, "minecraft:salmon_mob");
        Schema1470.method_5280(schema, map, "minecraft:puffer_fish");
        Schema1470.method_5280(schema, map, "minecraft:phantom");
        Schema1470.method_5280(schema, map, "minecraft:dolphin");
        Schema1470.method_5280(schema, map, "minecraft:drowned");
        schema.register(map, "minecraft:trident", string -> DSL.optionalFields((String)"inBlockState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema)));
        return map;
    }
}

