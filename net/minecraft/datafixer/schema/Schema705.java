/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer.schema;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema100;
import net.minecraft.datafixer.schema.Schema704;
import net.minecraft.datafixer.schema.Schema99;

public class Schema705
extends IdentifierNormalizingSchema {
    protected static final Hook.HookFunction field_5746 = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> dynamicOps, T object) {
            return Schema99.method_5359(new Dynamic(dynamicOps, object), Schema704.BLOCK_RENAMES, "minecraft:armor_stand");
        }
    };

    public Schema705(int i, Schema schema) {
        super(i, schema);
    }

    protected static void method_5311(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.targetItems(schema));
    }

    protected static void method_5330(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields((String)"inTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        HashMap map = Maps.newHashMap();
        schema.registerSimple((Map)map, "minecraft:area_effect_cloud");
        Schema705.method_5311(schema, map, "minecraft:armor_stand");
        schema.register((Map)map, "minecraft:arrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        Schema705.method_5311(schema, map, "minecraft:bat");
        Schema705.method_5311(schema, map, "minecraft:blaze");
        schema.registerSimple((Map)map, "minecraft:boat");
        Schema705.method_5311(schema, map, "minecraft:cave_spider");
        schema.register((Map)map, "minecraft:chest_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
        Schema705.method_5311(schema, map, "minecraft:chicken");
        schema.register((Map)map, "minecraft:commandblock_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        Schema705.method_5311(schema, map, "minecraft:cow");
        Schema705.method_5311(schema, map, "minecraft:creeper");
        schema.register((Map)map, "minecraft:donkey", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"SaddleItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        schema.registerSimple((Map)map, "minecraft:dragon_fireball");
        Schema705.method_5330(schema, map, "minecraft:egg");
        Schema705.method_5311(schema, map, "minecraft:elder_guardian");
        schema.registerSimple((Map)map, "minecraft:ender_crystal");
        Schema705.method_5311(schema, map, "minecraft:ender_dragon");
        schema.register((Map)map, "minecraft:enderman", string -> DSL.optionalFields((String)"carried", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        Schema705.method_5311(schema, map, "minecraft:endermite");
        Schema705.method_5330(schema, map, "minecraft:ender_pearl");
        schema.registerSimple((Map)map, "minecraft:eye_of_ender_signal");
        schema.register((Map)map, "minecraft:falling_block", string -> DSL.optionalFields((String)"Block", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"TileEntityData", (TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema)));
        Schema705.method_5330(schema, map, "minecraft:fireball");
        schema.register((Map)map, "minecraft:fireworks_rocket", string -> DSL.optionalFields((String)"FireworksItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        schema.register((Map)map, "minecraft:furnace_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        Schema705.method_5311(schema, map, "minecraft:ghast");
        Schema705.method_5311(schema, map, "minecraft:giant");
        Schema705.method_5311(schema, map, "minecraft:guardian");
        schema.register((Map)map, "minecraft:hopper_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
        schema.register((Map)map, "minecraft:horse", string -> DSL.optionalFields((String)"ArmorItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"SaddleItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        Schema705.method_5311(schema, map, "minecraft:husk");
        schema.register((Map)map, "minecraft:item", string -> DSL.optionalFields((String)"Item", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        schema.register((Map)map, "minecraft:item_frame", string -> DSL.optionalFields((String)"Item", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        schema.registerSimple((Map)map, "minecraft:leash_knot");
        Schema705.method_5311(schema, map, "minecraft:magma_cube");
        schema.register((Map)map, "minecraft:minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        Schema705.method_5311(schema, map, "minecraft:mooshroom");
        schema.register((Map)map, "minecraft:mule", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"SaddleItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        Schema705.method_5311(schema, map, "minecraft:ocelot");
        schema.registerSimple((Map)map, "minecraft:painting");
        schema.registerSimple((Map)map, "minecraft:parrot");
        Schema705.method_5311(schema, map, "minecraft:pig");
        Schema705.method_5311(schema, map, "minecraft:polar_bear");
        schema.register((Map)map, "minecraft:potion", string -> DSL.optionalFields((String)"Potion", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"inTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        Schema705.method_5311(schema, map, "minecraft:rabbit");
        Schema705.method_5311(schema, map, "minecraft:sheep");
        Schema705.method_5311(schema, map, "minecraft:shulker");
        schema.registerSimple((Map)map, "minecraft:shulker_bullet");
        Schema705.method_5311(schema, map, "minecraft:silverfish");
        Schema705.method_5311(schema, map, "minecraft:skeleton");
        schema.register((Map)map, "minecraft:skeleton_horse", string -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        Schema705.method_5311(schema, map, "minecraft:slime");
        Schema705.method_5330(schema, map, "minecraft:small_fireball");
        Schema705.method_5330(schema, map, "minecraft:snowball");
        Schema705.method_5311(schema, map, "minecraft:snowman");
        schema.register((Map)map, "minecraft:spawner_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (TypeTemplate)TypeReferences.UNTAGGED_SPAWNER.in(schema)));
        schema.register((Map)map, "minecraft:spectral_arrow", string -> DSL.optionalFields((String)"inTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        Schema705.method_5311(schema, map, "minecraft:spider");
        Schema705.method_5311(schema, map, "minecraft:squid");
        Schema705.method_5311(schema, map, "minecraft:stray");
        schema.registerSimple((Map)map, "minecraft:tnt");
        schema.register((Map)map, "minecraft:tnt_minecart", string -> DSL.optionalFields((String)"DisplayTile", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)));
        schema.register((Map)map, "minecraft:villager", string -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"buyB", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"sell", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)))), (TypeTemplate)Schema100.targetItems(schema)));
        Schema705.method_5311(schema, map, "minecraft:villager_golem");
        Schema705.method_5311(schema, map, "minecraft:witch");
        Schema705.method_5311(schema, map, "minecraft:wither");
        Schema705.method_5311(schema, map, "minecraft:wither_skeleton");
        Schema705.method_5330(schema, map, "minecraft:wither_skull");
        Schema705.method_5311(schema, map, "minecraft:wolf");
        Schema705.method_5330(schema, map, "minecraft:xp_bottle");
        schema.registerSimple((Map)map, "minecraft:xp_orb");
        Schema705.method_5311(schema, map, "minecraft:zombie");
        schema.register((Map)map, "minecraft:zombie_horse", string -> DSL.optionalFields((String)"SaddleItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        Schema705.method_5311(schema, map, "minecraft:zombie_pigman");
        Schema705.method_5311(schema, map, "minecraft:zombie_villager");
        schema.registerSimple((Map)map, "minecraft:evocation_fangs");
        Schema705.method_5311(schema, map, "minecraft:evocation_illager");
        schema.registerSimple((Map)map, "minecraft:illusion_illager");
        schema.register((Map)map, "minecraft:llama", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"SaddleItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"DecorItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        schema.registerSimple((Map)map, "minecraft:llama_spit");
        Schema705.method_5311(schema, map, "minecraft:vex");
        Schema705.method_5311(schema, map, "minecraft:vindication_illager");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
        super.registerTypes(schema, map, map2);
        schema.registerType(true, TypeReferences.ENTITY, () -> DSL.taggedChoiceLazy((String)"id", Schema705.method_28295(), (Map)map));
        schema.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)TypeReferences.ITEM_NAME.in(schema), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema), (String)"BlockEntityTag", (TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)))), (Hook.HookFunction)field_5746, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
    }
}

