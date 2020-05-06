/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.datafixer.schema;

import com.google.common.collect.ImmutableMap;
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

    private static void targetRecipeUsedField(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"RecipesUsed", (TypeTemplate)DSL.compoundList((TypeTemplate)TypeReferences.RECIPE.in(schema), (TypeTemplate)DSL.constType((Type)DSL.intType()))));
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map map = super.registerBlockEntities(schema);
        Schema2501.targetRecipeUsedField(schema, map, "minecraft:furnace");
        Schema2501.targetRecipeUsedField(schema, map, "minecraft:smoker");
        Schema2501.targetRecipeUsedField(schema, map, "minecraft:blast_furnace");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        ImmutableMap map3 = ImmutableMap.builder().put((Object)"default", DSL::remainder).put((Object)"largeBiomes", DSL::remainder).put((Object)"amplified", DSL::remainder).put((Object)"customized", DSL::remainder).put((Object)"debug_all_block_states", DSL::remainder).put((Object)"default_1_1", DSL::remainder).put((Object)"flat", () -> DSL.optionalFields((String)"biome", (TypeTemplate)TypeReferences.BIOME.in(schema), (String)"layers", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"block", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema))))).put((Object)"buffet", () -> DSL.optionalFields((String)"biome_source", (TypeTemplate)DSL.optionalFields((String)"options", (TypeTemplate)DSL.optionalFields((String)"biomes", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BIOME.in(schema)))), (String)"chunk_generator", (TypeTemplate)DSL.optionalFields((String)"options", (TypeTemplate)DSL.optionalFields((String)"default_block", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"default_fluid", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema))))).build();
        schema.registerType(false, TypeReferences.CHUNK_GENERATOR_SETTINGS, () -> Schema2501.method_25934((Map)map3));
    }

    private static /* synthetic */ TypeTemplate method_25934(Map map) {
        return DSL.taggedChoiceLazy((String)"levelType", (Type)DSL.string(), (Map)map);
    }
}

