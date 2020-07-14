/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.List$ListType
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class VillagerTradeFix
extends ChoiceFix {
    public VillagerTradeFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "Villager trade fix", TypeReferences.ENTITY, "minecraft:villager");
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        OpticFinder opticFinder = inputType.getType().findField("Offers");
        OpticFinder opticFinder2 = opticFinder.type().findField("Recipes");
        Type type = opticFinder2.type();
        if (!(type instanceof List.ListType)) {
            throw new IllegalStateException("Recipes are expected to be a list.");
        }
        List.ListType listType = (List.ListType)type;
        Type type2 = listType.getElement();
        OpticFinder opticFinder3 = DSL.typeFinder((Type)type2);
        OpticFinder opticFinder4 = type2.findField("buy");
        OpticFinder opticFinder5 = type2.findField("buyB");
        OpticFinder opticFinder6 = type2.findField("sell");
        OpticFinder opticFinder7 = DSL.fieldFinder((String)"id", (Type)DSL.named((String)TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
        Function<Typed, Typed> function = typed -> this.fixPumpkinTrade((OpticFinder<Pair<String, String>>)opticFinder7, (Typed<?>)typed);
        return inputType.updateTyped(opticFinder, typed -> typed.updateTyped(opticFinder2, typed2 -> typed2.updateTyped(opticFinder3, typed -> typed.updateTyped(opticFinder4, function).updateTyped(opticFinder5, function).updateTyped(opticFinder6, function))));
    }

    private Typed<?> fixPumpkinTrade(OpticFinder<Pair<String, String>> opticFinder, Typed<?> typed) {
        return typed.update(opticFinder, pair -> pair.mapSecond(string -> Objects.equals(string, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : string));
    }
}

