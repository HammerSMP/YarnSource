/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class VillagerProfessionFix
extends ChoiceFix {
    public VillagerProfessionFix(Schema outputSchema, String entity) {
        super(outputSchema, false, "Villager profession data fix (" + entity + ")", TypeReferences.ENTITY, entity);
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        Dynamic dynamic = (Dynamic)inputType.get(DSL.remainderFinder());
        return inputType.set(DSL.remainderFinder(), (Object)dynamic.remove("Profession").remove("Career").remove("CareerLevel").set("VillagerData", dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:plains"), (Object)dynamic.createString("profession"), (Object)dynamic.createString(VillagerProfessionFix.convertProfessionId(dynamic.get("Profession").asInt(0), dynamic.get("Career").asInt(0))), (Object)dynamic.createString("level"), (Object)DataFixUtils.orElse((Optional)dynamic.get("CareerLevel").result(), (Object)dynamic.createInt(1))))));
    }

    private static String convertProfessionId(int professionId, int careerId) {
        if (professionId == 0) {
            if (careerId == 2) {
                return "minecraft:fisherman";
            }
            if (careerId == 3) {
                return "minecraft:shepherd";
            }
            if (careerId == 4) {
                return "minecraft:fletcher";
            }
            return "minecraft:farmer";
        }
        if (professionId == 1) {
            if (careerId == 2) {
                return "minecraft:cartographer";
            }
            return "minecraft:librarian";
        }
        if (professionId == 2) {
            return "minecraft:cleric";
        }
        if (professionId == 3) {
            if (careerId == 2) {
                return "minecraft:weaponsmith";
            }
            if (careerId == 3) {
                return "minecraft:toolsmith";
            }
            return "minecraft:armorer";
        }
        if (professionId == 4) {
            if (careerId == 2) {
                return "minecraft:leatherworker";
            }
            return "minecraft:butcher";
        }
        if (professionId == 5) {
            return "minecraft:nitwit";
        }
        return "minecraft:none";
    }
}

