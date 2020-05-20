/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class EntityHorseSaddleFix
extends ChoiceFix {
    public EntityHorseSaddleFix(Schema schema, boolean bl) {
        super(schema, bl, "EntityHorseSaddleFix", TypeReferences.ENTITY, "EntityHorse");
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)TypeReferences.ITEM_NAME.typeName(), (Type)DSL.namespacedString()));
        Type type = this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"SaddleItem", (Type)type);
        Optional optional = typed.getOptionalTyped(opticFinder2);
        Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
        if (!optional.isPresent() && dynamic.get("Saddle").asBoolean(false)) {
            Typed typed2 = (Typed)type.pointTyped(typed.getOps()).orElseThrow(IllegalStateException::new);
            typed2 = typed2.set(opticFinder, (Object)Pair.of((Object)TypeReferences.ITEM_NAME.typeName(), (Object)"minecraft:saddle"));
            Dynamic dynamic2 = dynamic.emptyMap();
            dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
            dynamic2 = dynamic2.set("Damage", dynamic2.createShort((short)0));
            typed2 = typed2.set(DSL.remainderFinder(), (Object)dynamic2);
            dynamic.remove("Saddle");
            return typed.set(opticFinder2, typed2).set(DSL.remainderFinder(), (Object)dynamic);
        }
        return typed;
    }
}
