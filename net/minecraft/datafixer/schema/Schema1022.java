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

public class Schema1022
extends Schema {
    public Schema1022(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);
        schema.registerType(false, TypeReferences.RECIPE, () -> DSL.constType(IdentifierNormalizingSchema.getIdentifierType()));
        schema.registerType(false, TypeReferences.PLAYER, () -> DSL.optionalFields((String)"RootVehicle", (TypeTemplate)DSL.optionalFields((String)"Entity", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema)), (String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"EnderItems", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (TypeTemplate)DSL.optionalFields((String)"ShoulderEntityLeft", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema), (String)"ShoulderEntityRight", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema), (String)"recipeBook", (TypeTemplate)DSL.optionalFields((String)"recipes", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.RECIPE.in(schema)), (String)"toBeDisplayed", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.RECIPE.in(schema))))));
        schema.registerType(false, TypeReferences.HOTBAR, () -> DSL.compoundList((TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
    }
}

