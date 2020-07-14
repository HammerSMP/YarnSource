/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.types.templates.TypeTemplate
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer.schema;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema99;

public class Schema704
extends Schema {
    protected static final Map<String, String> BLOCK_RENAMES = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        hashMap.put("minecraft:furnace", "minecraft:furnace");
        hashMap.put("minecraft:lit_furnace", "minecraft:furnace");
        hashMap.put("minecraft:chest", "minecraft:chest");
        hashMap.put("minecraft:trapped_chest", "minecraft:chest");
        hashMap.put("minecraft:ender_chest", "minecraft:ender_chest");
        hashMap.put("minecraft:jukebox", "minecraft:jukebox");
        hashMap.put("minecraft:dispenser", "minecraft:dispenser");
        hashMap.put("minecraft:dropper", "minecraft:dropper");
        hashMap.put("minecraft:sign", "minecraft:sign");
        hashMap.put("minecraft:mob_spawner", "minecraft:mob_spawner");
        hashMap.put("minecraft:noteblock", "minecraft:noteblock");
        hashMap.put("minecraft:brewing_stand", "minecraft:brewing_stand");
        hashMap.put("minecraft:enhanting_table", "minecraft:enchanting_table");
        hashMap.put("minecraft:command_block", "minecraft:command_block");
        hashMap.put("minecraft:beacon", "minecraft:beacon");
        hashMap.put("minecraft:skull", "minecraft:skull");
        hashMap.put("minecraft:daylight_detector", "minecraft:daylight_detector");
        hashMap.put("minecraft:hopper", "minecraft:hopper");
        hashMap.put("minecraft:banner", "minecraft:banner");
        hashMap.put("minecraft:flower_pot", "minecraft:flower_pot");
        hashMap.put("minecraft:repeating_command_block", "minecraft:command_block");
        hashMap.put("minecraft:chain_command_block", "minecraft:command_block");
        hashMap.put("minecraft:shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:white_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:orange_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:magenta_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:light_blue_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:yellow_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:lime_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:pink_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:gray_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:silver_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:cyan_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:purple_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:blue_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:brown_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:green_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:red_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:black_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:bed", "minecraft:bed");
        hashMap.put("minecraft:light_gray_shulker_box", "minecraft:shulker_box");
        hashMap.put("minecraft:banner", "minecraft:banner");
        hashMap.put("minecraft:white_banner", "minecraft:banner");
        hashMap.put("minecraft:orange_banner", "minecraft:banner");
        hashMap.put("minecraft:magenta_banner", "minecraft:banner");
        hashMap.put("minecraft:light_blue_banner", "minecraft:banner");
        hashMap.put("minecraft:yellow_banner", "minecraft:banner");
        hashMap.put("minecraft:lime_banner", "minecraft:banner");
        hashMap.put("minecraft:pink_banner", "minecraft:banner");
        hashMap.put("minecraft:gray_banner", "minecraft:banner");
        hashMap.put("minecraft:silver_banner", "minecraft:banner");
        hashMap.put("minecraft:cyan_banner", "minecraft:banner");
        hashMap.put("minecraft:purple_banner", "minecraft:banner");
        hashMap.put("minecraft:blue_banner", "minecraft:banner");
        hashMap.put("minecraft:brown_banner", "minecraft:banner");
        hashMap.put("minecraft:green_banner", "minecraft:banner");
        hashMap.put("minecraft:red_banner", "minecraft:banner");
        hashMap.put("minecraft:black_banner", "minecraft:banner");
        hashMap.put("minecraft:standing_sign", "minecraft:sign");
        hashMap.put("minecraft:wall_sign", "minecraft:sign");
        hashMap.put("minecraft:piston_head", "minecraft:piston");
        hashMap.put("minecraft:daylight_detector_inverted", "minecraft:daylight_detector");
        hashMap.put("minecraft:unpowered_comparator", "minecraft:comparator");
        hashMap.put("minecraft:powered_comparator", "minecraft:comparator");
        hashMap.put("minecraft:wall_banner", "minecraft:banner");
        hashMap.put("minecraft:standing_banner", "minecraft:banner");
        hashMap.put("minecraft:structure_block", "minecraft:structure_block");
        hashMap.put("minecraft:end_portal", "minecraft:end_portal");
        hashMap.put("minecraft:end_gateway", "minecraft:end_gateway");
        hashMap.put("minecraft:sign", "minecraft:sign");
        hashMap.put("minecraft:shield", "minecraft:banner");
    });
    protected static final Hook.HookFunction field_5745 = new Hook.HookFunction(){

        public <T> T apply(DynamicOps<T> dynamicOps, T object) {
            return Schema99.method_5359(new Dynamic(dynamicOps, object), BLOCK_RENAMES, "ArmorStand");
        }
    };

    public Schema704(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static void method_5296(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema))));
    }

    public Type<?> getChoiceType(DSL.TypeReference typeReference, String string) {
        if (Objects.equals(typeReference.typeName(), TypeReferences.BLOCK_ENTITY.typeName())) {
            return super.getChoiceType(typeReference, IdentifierNormalizingSchema.normalize(string));
        }
        return super.getChoiceType(typeReference, string);
    }

    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        HashMap map = Maps.newHashMap();
        Schema704.method_5296(schema, map, "minecraft:furnace");
        Schema704.method_5296(schema, map, "minecraft:chest");
        schema.registerSimple((Map)map, "minecraft:ender_chest");
        schema.register((Map)map, "minecraft:jukebox", string -> DSL.optionalFields((String)"RecordItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
        Schema704.method_5296(schema, map, "minecraft:dispenser");
        Schema704.method_5296(schema, map, "minecraft:dropper");
        schema.registerSimple((Map)map, "minecraft:sign");
        schema.register((Map)map, "minecraft:mob_spawner", string -> TypeReferences.UNTAGGED_SPAWNER.in(schema));
        schema.registerSimple((Map)map, "minecraft:noteblock");
        schema.registerSimple((Map)map, "minecraft:piston");
        Schema704.method_5296(schema, map, "minecraft:brewing_stand");
        schema.registerSimple((Map)map, "minecraft:enchanting_table");
        schema.registerSimple((Map)map, "minecraft:end_portal");
        schema.registerSimple((Map)map, "minecraft:beacon");
        schema.registerSimple((Map)map, "minecraft:skull");
        schema.registerSimple((Map)map, "minecraft:daylight_detector");
        Schema704.method_5296(schema, map, "minecraft:hopper");
        schema.registerSimple((Map)map, "minecraft:comparator");
        schema.register((Map)map, "minecraft:flower_pot", string -> DSL.optionalFields((String)"Item", (TypeTemplate)DSL.or((TypeTemplate)DSL.constType((Type)DSL.intType()), (TypeTemplate)TypeReferences.ITEM_NAME.in(schema))));
        schema.registerSimple((Map)map, "minecraft:banner");
        schema.registerSimple((Map)map, "minecraft:structure_block");
        schema.registerSimple((Map)map, "minecraft:end_gateway");
        schema.registerSimple((Map)map, "minecraft:command_block");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);
        schema.registerType(false, TypeReferences.BLOCK_ENTITY, () -> DSL.taggedChoiceLazy((String)"id", IdentifierNormalizingSchema.getIdentifierType(), (Map)blockEntityTypes));
        schema.registerType(true, TypeReferences.ITEM_STACK, () -> DSL.hook((TypeTemplate)DSL.optionalFields((String)"id", (TypeTemplate)TypeReferences.ITEM_NAME.in(schema), (String)"tag", (TypeTemplate)DSL.optionalFields((String)"EntityTag", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema), (String)"BlockEntityTag", (TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema), (String)"CanDestroy", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)), (String)"CanPlaceOn", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_NAME.in(schema)))), (Hook.HookFunction)field_5745, (Hook.HookFunction)Hook.HookFunction.IDENTITY));
    }
}

