/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class ChunkStructuresTemplateRenameFix
extends DataFix {
    private static final ImmutableMap<String, Pair<String, ImmutableMap<String, String>>> structures = ImmutableMap.builder().put((Object)"EndCity", (Object)Pair.of((Object)"ECP", (Object)ImmutableMap.builder().put((Object)"second_floor", (Object)"second_floor_1").put((Object)"third_floor", (Object)"third_floor_1").put((Object)"third_floor_c", (Object)"third_floor_2").build())).put((Object)"Mansion", (Object)Pair.of((Object)"WMP", (Object)ImmutableMap.builder().put((Object)"carpet_south", (Object)"carpet_south_1").put((Object)"carpet_west", (Object)"carpet_west_1").put((Object)"indoors_door", (Object)"indoors_door_1").put((Object)"indoors_wall", (Object)"indoors_wall_1").build())).put((Object)"Igloo", (Object)Pair.of((Object)"Iglu", (Object)ImmutableMap.builder().put((Object)"minecraft:igloo/igloo_bottom", (Object)"minecraft:igloo/bottom").put((Object)"minecraft:igloo/igloo_middle", (Object)"minecraft:igloo/middle").put((Object)"minecraft:igloo/igloo_top", (Object)"minecraft:igloo/top").build())).put((Object)"Ocean_Ruin", (Object)Pair.of((Object)"ORP", (Object)ImmutableMap.builder().put((Object)"minecraft:ruin/big_ruin1_brick", (Object)"minecraft:underwater_ruin/big_brick_1").put((Object)"minecraft:ruin/big_ruin2_brick", (Object)"minecraft:underwater_ruin/big_brick_2").put((Object)"minecraft:ruin/big_ruin3_brick", (Object)"minecraft:underwater_ruin/big_brick_3").put((Object)"minecraft:ruin/big_ruin8_brick", (Object)"minecraft:underwater_ruin/big_brick_8").put((Object)"minecraft:ruin/big_ruin1_cracked", (Object)"minecraft:underwater_ruin/big_cracked_1").put((Object)"minecraft:ruin/big_ruin2_cracked", (Object)"minecraft:underwater_ruin/big_cracked_2").put((Object)"minecraft:ruin/big_ruin3_cracked", (Object)"minecraft:underwater_ruin/big_cracked_3").put((Object)"minecraft:ruin/big_ruin8_cracked", (Object)"minecraft:underwater_ruin/big_cracked_8").put((Object)"minecraft:ruin/big_ruin1_mossy", (Object)"minecraft:underwater_ruin/big_mossy_1").put((Object)"minecraft:ruin/big_ruin2_mossy", (Object)"minecraft:underwater_ruin/big_mossy_2").put((Object)"minecraft:ruin/big_ruin3_mossy", (Object)"minecraft:underwater_ruin/big_mossy_3").put((Object)"minecraft:ruin/big_ruin8_mossy", (Object)"minecraft:underwater_ruin/big_mossy_8").put((Object)"minecraft:ruin/big_ruin_warm4", (Object)"minecraft:underwater_ruin/big_warm_4").put((Object)"minecraft:ruin/big_ruin_warm5", (Object)"minecraft:underwater_ruin/big_warm_5").put((Object)"minecraft:ruin/big_ruin_warm6", (Object)"minecraft:underwater_ruin/big_warm_6").put((Object)"minecraft:ruin/big_ruin_warm7", (Object)"minecraft:underwater_ruin/big_warm_7").put((Object)"minecraft:ruin/ruin1_brick", (Object)"minecraft:underwater_ruin/brick_1").put((Object)"minecraft:ruin/ruin2_brick", (Object)"minecraft:underwater_ruin/brick_2").put((Object)"minecraft:ruin/ruin3_brick", (Object)"minecraft:underwater_ruin/brick_3").put((Object)"minecraft:ruin/ruin4_brick", (Object)"minecraft:underwater_ruin/brick_4").put((Object)"minecraft:ruin/ruin5_brick", (Object)"minecraft:underwater_ruin/brick_5").put((Object)"minecraft:ruin/ruin6_brick", (Object)"minecraft:underwater_ruin/brick_6").put((Object)"minecraft:ruin/ruin7_brick", (Object)"minecraft:underwater_ruin/brick_7").put((Object)"minecraft:ruin/ruin8_brick", (Object)"minecraft:underwater_ruin/brick_8").put((Object)"minecraft:ruin/ruin1_cracked", (Object)"minecraft:underwater_ruin/cracked_1").put((Object)"minecraft:ruin/ruin2_cracked", (Object)"minecraft:underwater_ruin/cracked_2").put((Object)"minecraft:ruin/ruin3_cracked", (Object)"minecraft:underwater_ruin/cracked_3").put((Object)"minecraft:ruin/ruin4_cracked", (Object)"minecraft:underwater_ruin/cracked_4").put((Object)"minecraft:ruin/ruin5_cracked", (Object)"minecraft:underwater_ruin/cracked_5").put((Object)"minecraft:ruin/ruin6_cracked", (Object)"minecraft:underwater_ruin/cracked_6").put((Object)"minecraft:ruin/ruin7_cracked", (Object)"minecraft:underwater_ruin/cracked_7").put((Object)"minecraft:ruin/ruin8_cracked", (Object)"minecraft:underwater_ruin/cracked_8").put((Object)"minecraft:ruin/ruin1_mossy", (Object)"minecraft:underwater_ruin/mossy_1").put((Object)"minecraft:ruin/ruin2_mossy", (Object)"minecraft:underwater_ruin/mossy_2").put((Object)"minecraft:ruin/ruin3_mossy", (Object)"minecraft:underwater_ruin/mossy_3").put((Object)"minecraft:ruin/ruin4_mossy", (Object)"minecraft:underwater_ruin/mossy_4").put((Object)"minecraft:ruin/ruin5_mossy", (Object)"minecraft:underwater_ruin/mossy_5").put((Object)"minecraft:ruin/ruin6_mossy", (Object)"minecraft:underwater_ruin/mossy_6").put((Object)"minecraft:ruin/ruin7_mossy", (Object)"minecraft:underwater_ruin/mossy_7").put((Object)"minecraft:ruin/ruin8_mossy", (Object)"minecraft:underwater_ruin/mossy_8").put((Object)"minecraft:ruin/ruin_warm1", (Object)"minecraft:underwater_ruin/warm_1").put((Object)"minecraft:ruin/ruin_warm2", (Object)"minecraft:underwater_ruin/warm_2").put((Object)"minecraft:ruin/ruin_warm3", (Object)"minecraft:underwater_ruin/warm_3").put((Object)"minecraft:ruin/ruin_warm4", (Object)"minecraft:underwater_ruin/warm_4").put((Object)"minecraft:ruin/ruin_warm5", (Object)"minecraft:underwater_ruin/warm_5").put((Object)"minecraft:ruin/ruin_warm6", (Object)"minecraft:underwater_ruin/warm_6").put((Object)"minecraft:ruin/ruin_warm7", (Object)"minecraft:underwater_ruin/warm_7").put((Object)"minecraft:ruin/ruin_warm8", (Object)"minecraft:underwater_ruin/warm_8").put((Object)"minecraft:ruin/big_brick_1", (Object)"minecraft:underwater_ruin/big_brick_1").put((Object)"minecraft:ruin/big_brick_2", (Object)"minecraft:underwater_ruin/big_brick_2").put((Object)"minecraft:ruin/big_brick_3", (Object)"minecraft:underwater_ruin/big_brick_3").put((Object)"minecraft:ruin/big_brick_8", (Object)"minecraft:underwater_ruin/big_brick_8").put((Object)"minecraft:ruin/big_mossy_1", (Object)"minecraft:underwater_ruin/big_mossy_1").put((Object)"minecraft:ruin/big_mossy_2", (Object)"minecraft:underwater_ruin/big_mossy_2").put((Object)"minecraft:ruin/big_mossy_3", (Object)"minecraft:underwater_ruin/big_mossy_3").put((Object)"minecraft:ruin/big_mossy_8", (Object)"minecraft:underwater_ruin/big_mossy_8").put((Object)"minecraft:ruin/big_cracked_1", (Object)"minecraft:underwater_ruin/big_cracked_1").put((Object)"minecraft:ruin/big_cracked_2", (Object)"minecraft:underwater_ruin/big_cracked_2").put((Object)"minecraft:ruin/big_cracked_3", (Object)"minecraft:underwater_ruin/big_cracked_3").put((Object)"minecraft:ruin/big_cracked_8", (Object)"minecraft:underwater_ruin/big_cracked_8").put((Object)"minecraft:ruin/big_warm_4", (Object)"minecraft:underwater_ruin/big_warm_4").put((Object)"minecraft:ruin/big_warm_5", (Object)"minecraft:underwater_ruin/big_warm_5").put((Object)"minecraft:ruin/big_warm_6", (Object)"minecraft:underwater_ruin/big_warm_6").put((Object)"minecraft:ruin/big_warm_7", (Object)"minecraft:underwater_ruin/big_warm_7").build())).build();

    public ChunkStructuresTemplateRenameFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
        OpticFinder opticFinder = type.findField("Children");
        return this.fixTypeEverywhereTyped("ChunkStructuresTemplateRenameFix", type, typed -> typed.updateTyped(opticFinder, typed2 -> typed2.update(DSL.remainderFinder(), dynamic -> this.method_15671((Dynamic<?>)((Dynamic)typed.get(DSL.remainderFinder())), (Dynamic<?>)dynamic))));
    }

    private Dynamic<?> method_15671(Dynamic<?> dynamic, Dynamic<?> dynamic2) {
        Pair pair;
        String string = dynamic.get("id").asString("");
        if (structures.containsKey((Object)string) && ((String)(pair = (Pair)structures.get((Object)string)).getFirst()).equals(dynamic2.get("id").asString(""))) {
            String string2 = dynamic2.get("Template").asString("");
            dynamic2 = dynamic2.set("Template", dynamic2.createString((String)((ImmutableMap)pair.getSecond()).getOrDefault((Object)string2, (Object)string2)));
        }
        return dynamic2;
    }
}

