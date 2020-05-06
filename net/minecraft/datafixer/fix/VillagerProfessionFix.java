/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class VillagerProfessionFix
extends ChoiceFix {
    public VillagerProfessionFix(Schema schema, String string) {
        super(schema, false, "Villager profession data fix (" + string + ")", TypeReferences.ENTITY, string);
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
        return typed.set(DSL.remainderFinder(), (Object)dynamic.remove("Profession").remove("Career").remove("CareerLevel").set("VillagerData", dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("type"), (Object)dynamic.createString("minecraft:plains"), (Object)dynamic.createString("profession"), (Object)dynamic.createString(VillagerProfessionFix.convertProfessionId(dynamic.get("Profession").asInt(0), dynamic.get("Career").asInt(0))), (Object)dynamic.createString("level"), (Object)DataFixUtils.orElse((Optional)dynamic.get("CareerLevel").get(), (Object)dynamic.createInt(1))))));
    }

    private static String convertProfessionId(int i, int j) {
        if (i == 0) {
            if (j == 2) {
                return "minecraft:fisherman";
            }
            if (j == 3) {
                return "minecraft:shepherd";
            }
            if (j == 4) {
                return "minecraft:fletcher";
            }
            return "minecraft:farmer";
        }
        if (i == 1) {
            if (j == 2) {
                return "minecraft:cartographer";
            }
            return "minecraft:librarian";
        }
        if (i == 2) {
            return "minecraft:cleric";
        }
        if (i == 3) {
            if (j == 2) {
                return "minecraft:weaponsmith";
            }
            if (j == 3) {
                return "minecraft:toolsmith";
            }
            return "minecraft:armorer";
        }
        if (i == 4) {
            if (j == 2) {
                return "minecraft:leatherworker";
            }
            return "minecraft:butcher";
        }
        if (i == 5) {
            return "minecraft:nitwit";
        }
        return "minecraft:none";
    }
}

