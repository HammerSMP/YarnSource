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
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class class_5301
extends IdentifierNormalizingSchema {
    public class_5301(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        schema.registerType(false, TypeReferences.CHUNK_GENERATOR_SETTINGS, () -> DSL.fields((String)"dimensions", (TypeTemplate)DSL.compoundList((TypeTemplate)DSL.constType(class_5301.method_28295()), (TypeTemplate)DSL.fields((String)"generator", (TypeTemplate)DSL.taggedChoiceLazy((String)"type", (Type)DSL.string(), (Map)ImmutableMap.of((Object)"minecraft:debug", DSL::remainder, (Object)"minecraft:flat", () -> DSL.optionalFields((String)"settings", (TypeTemplate)DSL.optionalFields((String)"biome", (TypeTemplate)TypeReferences.BIOME.in(schema), (String)"layers", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"block", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema))))), (Object)"minecraft:noise", () -> DSL.optionalFields((String)"biome_source", (TypeTemplate)DSL.taggedChoiceLazy((String)"type", (Type)DSL.string(), (Map)ImmutableMap.of((Object)"minecraft:fixed", () -> DSL.fields((String)"biome", (TypeTemplate)TypeReferences.BIOME.in(schema)), (Object)"minecraft:multi_noise", () -> DSL.list((TypeTemplate)DSL.fields((String)"biome", (TypeTemplate)TypeReferences.BIOME.in(schema))), (Object)"minecraft:checkerboard", () -> DSL.fields((String)"biomes", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BIOME.in(schema))), (Object)"minecraft:vanilla_layered", DSL::remainder, (Object)"minecraft:the_end", DSL::remainder)), (String)"settings", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.string()), (TypeTemplate)DSL.optionalFields((String)"default_block", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"default_fluid", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema))))))))));
    }
}

