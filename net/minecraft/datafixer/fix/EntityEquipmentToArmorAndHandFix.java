/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class EntityEquipmentToArmorAndHandFix
extends DataFix {
    public EntityEquipmentToArmorAndHandFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        return this.fixEquipment(this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK));
    }

    private <IS> TypeRewriteRule fixEquipment(Type<IS> type) {
        Type type2 = DSL.and((Type)DSL.optional((Type)DSL.field((String)"Equipment", (Type)DSL.list(type))), (Type)DSL.remainderType());
        Type type3 = DSL.and((Type)DSL.optional((Type)DSL.field((String)"ArmorItems", (Type)DSL.list(type))), (Type)DSL.optional((Type)DSL.field((String)"HandItems", (Type)DSL.list(type))), (Type)DSL.remainderType());
        OpticFinder opticFinder = DSL.typeFinder((Type)type2);
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"Equipment", (Type)DSL.list(type));
        return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(TypeReferences.ENTITY), this.getOutputSchema().getType(TypeReferences.ENTITY), typed -> {
            Either either = Either.right((Object)DSL.unit());
            Either either2 = Either.right((Object)DSL.unit());
            Dynamic dynamic = (Dynamic)typed.getOrCreate(DSL.remainderFinder());
            Optional optional = typed.getOptional(opticFinder2);
            if (optional.isPresent()) {
                List list = (List)optional.get();
                Object object = ((Optional)type.read(dynamic.emptyMap()).getSecond()).orElseThrow(() -> new IllegalStateException("Could not parse newly created empty itemstack."));
                if (!list.isEmpty()) {
                    either = Either.left((Object)Lists.newArrayList((Object[])new Object[]{list.get(0), object}));
                }
                if (list.size() > 1) {
                    ArrayList list2 = Lists.newArrayList((Object[])new Object[]{object, object, object, object});
                    for (int i = 1; i < Math.min(list.size(), 5); ++i) {
                        list2.set(i - 1, list.get(i));
                    }
                    either2 = Either.left((Object)list2);
                }
            }
            Dynamic dynamic2 = dynamic;
            Optional optional2 = dynamic.get("DropChances").asStreamOpt();
            if (optional2.isPresent()) {
                Iterator iterator = Stream.concat((Stream)optional2.get(), Stream.generate(() -> dynamic2.createInt(0))).iterator();
                float f = ((Dynamic)iterator.next()).asFloat(0.0f);
                if (!dynamic.get("HandDropChances").get().isPresent()) {
                    Dynamic dynamic3 = dynamic.emptyMap().merge(dynamic.createFloat(f)).merge(dynamic.createFloat(0.0f));
                    dynamic = dynamic.set("HandDropChances", dynamic3);
                }
                if (!dynamic.get("ArmorDropChances").get().isPresent()) {
                    Dynamic dynamic4 = dynamic.emptyMap().merge(dynamic.createFloat(((Dynamic)iterator.next()).asFloat(0.0f))).merge(dynamic.createFloat(((Dynamic)iterator.next()).asFloat(0.0f))).merge(dynamic.createFloat(((Dynamic)iterator.next()).asFloat(0.0f))).merge(dynamic.createFloat(((Dynamic)iterator.next()).asFloat(0.0f)));
                    dynamic = dynamic.set("ArmorDropChances", dynamic4);
                }
                dynamic = dynamic.remove("DropChances");
            }
            return typed.set(opticFinder, type3, (Object)Pair.of((Object)either, (Object)Pair.of((Object)either2, (Object)dynamic)));
        });
    }
}

