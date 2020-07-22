/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class FurnaceRecipesFix
extends DataFix {
    public FurnaceRecipesFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    protected TypeRewriteRule makeRule() {
        return this.updateBlockEntities(this.getOutputSchema().getTypeRaw(TypeReferences.RECIPE));
    }

    private <R> TypeRewriteRule updateBlockEntities(Type<R> type) {
        Type type2 = DSL.and((Type)DSL.optional((Type)DSL.field((String)"RecipesUsed", (Type)DSL.and((Type)DSL.compoundList(type, (Type)DSL.intType()), (Type)DSL.remainderType()))), (Type)DSL.remainderType());
        OpticFinder opticFinder = DSL.namedChoice((String)"minecraft:furnace", (Type)this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:furnace"));
        OpticFinder opticFinder2 = DSL.namedChoice((String)"minecraft:blast_furnace", (Type)this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:blast_furnace"));
        OpticFinder opticFinder3 = DSL.namedChoice((String)"minecraft:smoker", (Type)this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:smoker"));
        Type type3 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:furnace");
        Type type4 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:blast_furnace");
        Type type5 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:smoker");
        Type type6 = this.getInputSchema().getType(TypeReferences.BLOCK_ENTITY);
        Type type7 = this.getOutputSchema().getType(TypeReferences.BLOCK_ENTITY);
        return this.fixTypeEverywhereTyped("FurnaceRecipesFix", type6, type7, typed2 -> typed2.updateTyped(opticFinder, type3, typed -> this.updateBlockEntityData(type, (Type)type2, (Typed<?>)typed)).updateTyped(opticFinder2, type4, typed -> this.updateBlockEntityData(type, (Type)type2, (Typed<?>)typed)).updateTyped(opticFinder3, type5, typed -> this.updateBlockEntityData(type, (Type)type2, (Typed<?>)typed)));
    }

    private <R> Typed<?> updateBlockEntityData(Type<R> type, Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>> type2, Typed<?> typed) {
        Dynamic dynamic2 = (Dynamic)typed.getOrCreate(DSL.remainderFinder());
        int i = dynamic2.get("RecipesUsedSize").asInt(0);
        dynamic2 = dynamic2.remove("RecipesUsedSize");
        ArrayList list = Lists.newArrayList();
        for (int j = 0; j < i; ++j) {
            String string = "RecipeLocation" + j;
            String string2 = "RecipeAmount" + j;
            Optional optional = dynamic2.get(string).result();
            int k = dynamic2.get(string2).asInt(0);
            if (k > 0) {
                optional.ifPresent(dynamic -> {
                    Optional optional = type.read(dynamic).result();
                    optional.ifPresent(pair -> list.add(Pair.of((Object)pair.getFirst(), (Object)k)));
                });
            }
            dynamic2 = dynamic2.remove(string).remove(string2);
        }
        return typed.set(DSL.remainderFinder(), type2, (Object)Pair.of((Object)Either.left((Object)Pair.of((Object)list, (Object)dynamic2.emptyMap())), (Object)dynamic2));
    }
}

