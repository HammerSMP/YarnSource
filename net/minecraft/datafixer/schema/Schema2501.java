/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class Schema2501
extends IdentifierNormalizingSchema {
    public Schema2501(int i, Schema schema) {
        super(i, schema);
    }

    private static void targetRecipeUsedField(Schema schema, Map<String, Supplier<TypeTemplate>> map, String name) {
        schema.register(map, name, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"RecipesUsed", (TypeTemplate)DSL.compoundList((TypeTemplate)TypeReferences.RECIPE.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType()))));
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map map = super.registerBlockEntities(schema);
        Schema2501.targetRecipeUsedField(schema, map, "minecraft:furnace");
        Schema2501.targetRecipeUsedField(schema, map, "minecraft:smoker");
        Schema2501.targetRecipeUsedField(schema, map, "minecraft:blast_furnace");
        return map;
    }
}

