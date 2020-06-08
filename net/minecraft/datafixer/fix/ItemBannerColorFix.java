/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemBannerColorFix
extends DataFix {
    public ItemBannerColorFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
        OpticFinder opticFinder2 = type.findField("tag");
        OpticFinder opticFinder3 = opticFinder2.type().findField("BlockEntityTag");
        return this.fixTypeEverywhereTyped("ItemBannerColorFix", type, typed -> {
            Optional optional = typed.getOptional(opticFinder);
            if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:banner")) {
                Typed typed2;
                Optional optional3;
                Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
                Optional optional2 = typed.getOptionalTyped(opticFinder2);
                if (optional2.isPresent() && (optional3 = (typed2 = (Typed)optional2.get()).getOptionalTyped(opticFinder3)).isPresent()) {
                    Typed typed3 = (Typed)optional3.get();
                    Dynamic dynamic2 = (Dynamic)typed2.get(DSL.remainderFinder());
                    Dynamic dynamic3 = (Dynamic)typed3.getOrCreate(DSL.remainderFinder());
                    if (dynamic3.get("Base").asNumber().result().isPresent()) {
                        Dynamic dynamic4;
                        dynamic = dynamic.set("Damage", dynamic.createShort((short)(dynamic3.get("Base").asInt(0) & 0xF)));
                        Optional optional4 = dynamic2.get("display").result();
                        if (optional4.isPresent() && Objects.equals((Object)(dynamic4 = (Dynamic)optional4.get()), (Object)dynamic4.emptyMap().merge(dynamic4.createString("Lore"), dynamic4.createList(Stream.of(dynamic4.createString("(+NBT")))))) {
                            return typed.set(DSL.remainderFinder(), (Object)dynamic);
                        }
                        dynamic3.remove("Base");
                        return typed.set(DSL.remainderFinder(), (Object)dynamic).set(opticFinder2, typed2.set(opticFinder3, typed3.set(DSL.remainderFinder(), (Object)dynamic3)));
                    }
                }
                return typed.set(DSL.remainderFinder(), (Object)dynamic);
            }
            return typed;
        });
    }
}

