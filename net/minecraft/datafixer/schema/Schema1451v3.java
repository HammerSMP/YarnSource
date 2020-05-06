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

public class Schema1451v3
extends IdentifierNormalizingSchema {
    public Schema1451v3(int i, Schema schema) {
        super(i, schema);
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map map = super.registerEntities(schema);
        schema.registerSimple(map, "minecraft:egg");
        schema.registerSimple(map, "minecraft:ender_pearl");
        schema.registerSimple(map, "minecraft:fireball");
        schema.register(map, "minecraft:potion", string -> DSL.optionalFields((String)"Potion", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple(map, "minecraft:small_fireball");
        schema.registerSimple(map, "minecraft:snowball");
        schema.registerSimple(map, "minecraft:wither_skull");
        schema.registerSimple(map, "minecraft:xp_bottle");
        schema.register(map, "minecraft:arrow", () -> DSL.optionalFields((String)"inBlockState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema)));
        schema.register(map, "minecraft:enderman", () -> DSL.optionalFields((String)"carriedBlockState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        schema.register(map, "minecraft:falling_block", () -> DSL.optionalFields((String)"BlockState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema), (String)"TileEntityData", (TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema)));
        schema.register(map, "minecraft:spectral_arrow", () -> DSL.optionalFields((String)"inBlockState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema)));
        schema.register(map, "minecraft:chest_minecart", () -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
        schema.register(map, "minecraft:commandblock_minecart", () -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema)));
        schema.register(map, "minecraft:furnace_minecart", () -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema)));
        schema.register(map, "minecraft:hopper_minecart", () -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
        schema.register(map, "minecraft:minecart", () -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema)));
        schema.register(map, "minecraft:spawner_minecart", () -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema), (TypeTemplate)TypeReferences.UNTAGGED_SPAWNER.in(schema)));
        schema.register(map, "minecraft:tnt_minecart", () -> DSL.optionalFields((String)"DisplayState", (TypeTemplate)TypeReferences.BLOCK_STATE.in(schema)));
        return map;
    }
}

