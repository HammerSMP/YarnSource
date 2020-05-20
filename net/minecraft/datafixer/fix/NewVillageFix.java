/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.CompoundList$CompoundListType
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class NewVillageFix
extends DataFix {
    public NewVillageFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        CompoundList.CompoundListType compoundListType = DSL.compoundList((Type)DSL.string(), (Type)this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE));
        OpticFinder opticFinder = compoundListType.finder();
        return this.method_17334(compoundListType);
    }

    private <SF> TypeRewriteRule method_17334(CompoundList.CompoundListType<String, SF> compoundListType) {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type type2 = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
        OpticFinder opticFinder = type.findField("Level");
        OpticFinder opticFinder2 = opticFinder.type().findField("Structures");
        OpticFinder opticFinder3 = opticFinder2.type().findField("Starts");
        OpticFinder opticFinder4 = compoundListType.finder();
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhereTyped("NewVillageFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.updateTyped(opticFinder2, typed2 -> typed2.updateTyped(opticFinder3, typed -> typed.update(opticFinder4, list -> list.stream().filter(pair -> !Objects.equals(pair.getFirst(), "Village")).map(pair -> pair.mapFirst(string -> string.equals("New_Village") ? "Village" : string)).collect(Collectors.toList()))).update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("References", dynamic -> {
            Optional optional = dynamic.get("New_Village").result();
            return ((Dynamic)DataFixUtils.orElse(optional.map(dynamic2 -> dynamic.remove("New_Village").set("Village", dynamic2)), (Object)dynamic)).remove("Village");
        }))))), (TypeRewriteRule)this.fixTypeEverywhereTyped("NewVillageStartFix", type2, typed -> typed.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("id", dynamic -> Objects.equals(IdentifierNormalizingSchema.normalize(dynamic.asString("")), "minecraft:new_village") ? dynamic.createString("minecraft:village") : dynamic))));
    }
}

