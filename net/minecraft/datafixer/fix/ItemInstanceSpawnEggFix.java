/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemInstanceSpawnEggFix
extends DataFix {
    private static final Map<String, String> ENTITY_SPAWN_EGGS = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        hashMap.put("minecraft:bat", "minecraft:bat_spawn_egg");
        hashMap.put("minecraft:blaze", "minecraft:blaze_spawn_egg");
        hashMap.put("minecraft:cave_spider", "minecraft:cave_spider_spawn_egg");
        hashMap.put("minecraft:chicken", "minecraft:chicken_spawn_egg");
        hashMap.put("minecraft:cow", "minecraft:cow_spawn_egg");
        hashMap.put("minecraft:creeper", "minecraft:creeper_spawn_egg");
        hashMap.put("minecraft:donkey", "minecraft:donkey_spawn_egg");
        hashMap.put("minecraft:elder_guardian", "minecraft:elder_guardian_spawn_egg");
        hashMap.put("minecraft:enderman", "minecraft:enderman_spawn_egg");
        hashMap.put("minecraft:endermite", "minecraft:endermite_spawn_egg");
        hashMap.put("minecraft:evocation_illager", "minecraft:evocation_illager_spawn_egg");
        hashMap.put("minecraft:ghast", "minecraft:ghast_spawn_egg");
        hashMap.put("minecraft:guardian", "minecraft:guardian_spawn_egg");
        hashMap.put("minecraft:horse", "minecraft:horse_spawn_egg");
        hashMap.put("minecraft:husk", "minecraft:husk_spawn_egg");
        hashMap.put("minecraft:llama", "minecraft:llama_spawn_egg");
        hashMap.put("minecraft:magma_cube", "minecraft:magma_cube_spawn_egg");
        hashMap.put("minecraft:mooshroom", "minecraft:mooshroom_spawn_egg");
        hashMap.put("minecraft:mule", "minecraft:mule_spawn_egg");
        hashMap.put("minecraft:ocelot", "minecraft:ocelot_spawn_egg");
        hashMap.put("minecraft:pufferfish", "minecraft:pufferfish_spawn_egg");
        hashMap.put("minecraft:parrot", "minecraft:parrot_spawn_egg");
        hashMap.put("minecraft:pig", "minecraft:pig_spawn_egg");
        hashMap.put("minecraft:polar_bear", "minecraft:polar_bear_spawn_egg");
        hashMap.put("minecraft:rabbit", "minecraft:rabbit_spawn_egg");
        hashMap.put("minecraft:sheep", "minecraft:sheep_spawn_egg");
        hashMap.put("minecraft:shulker", "minecraft:shulker_spawn_egg");
        hashMap.put("minecraft:silverfish", "minecraft:silverfish_spawn_egg");
        hashMap.put("minecraft:skeleton", "minecraft:skeleton_spawn_egg");
        hashMap.put("minecraft:skeleton_horse", "minecraft:skeleton_horse_spawn_egg");
        hashMap.put("minecraft:slime", "minecraft:slime_spawn_egg");
        hashMap.put("minecraft:spider", "minecraft:spider_spawn_egg");
        hashMap.put("minecraft:squid", "minecraft:squid_spawn_egg");
        hashMap.put("minecraft:stray", "minecraft:stray_spawn_egg");
        hashMap.put("minecraft:turtle", "minecraft:turtle_spawn_egg");
        hashMap.put("minecraft:vex", "minecraft:vex_spawn_egg");
        hashMap.put("minecraft:villager", "minecraft:villager_spawn_egg");
        hashMap.put("minecraft:vindication_illager", "minecraft:vindication_illager_spawn_egg");
        hashMap.put("minecraft:witch", "minecraft:witch_spawn_egg");
        hashMap.put("minecraft:wither_skeleton", "minecraft:wither_skeleton_spawn_egg");
        hashMap.put("minecraft:wolf", "minecraft:wolf_spawn_egg");
        hashMap.put("minecraft:zombie", "minecraft:zombie_spawn_egg");
        hashMap.put("minecraft:zombie_horse", "minecraft:zombie_horse_spawn_egg");
        hashMap.put("minecraft:zombie_pigman", "minecraft:zombie_pigman_spawn_egg");
        hashMap.put("minecraft:zombie_villager", "minecraft:zombie_villager_spawn_egg");
    });

    public ItemInstanceSpawnEggFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.method_28295()));
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"id", IdentifierNormalizingSchema.method_28295());
        OpticFinder opticFinder3 = type.findField("tag");
        OpticFinder opticFinder4 = opticFinder3.type().findField("EntityTag");
        return this.fixTypeEverywhereTyped("ItemInstanceSpawnEggFix", type, typed -> {
            Typed typed2;
            Typed typed3;
            Optional optional2;
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:spawn_egg") && (optional2 = (typed3 = (typed2 = typed.getOrCreateTyped(opticFinder3)).getOrCreateTyped(opticFinder4)).getOptional(opticFinder2)).isPresent()) {
                return typed.set(opticFinder, (Object)Pair.of((Object)TypeReferences.ITEM_NAME.typeName(), (Object)ENTITY_SPAWN_EGGS.getOrDefault(optional2.get(), "minecraft:pig_spawn_egg")));
            }
            return typed;
        });
    }
}

