/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class EntityHorseSaddleFix
extends ChoiceFix {
    public EntityHorseSaddleFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "EntityHorseSaddleFix", TypeReferences.ENTITY, "EntityHorse");
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
        Type type = this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"SaddleItem", (Type)type);
        Optional optional = inputType.getOptionalTyped(opticFinder2);
        Dynamic dynamic = (Dynamic)inputType.get(DSL.remainderFinder());
        if (!optional.isPresent() && dynamic.get("Saddle").asBoolean(false)) {
            Typed typed2 = (Typed)type.pointTyped(inputType.getOps()).orElseThrow(IllegalStateException::new);
            typed2 = typed2.set(opticFinder, (Object)Pair.of((Object)TypeReferences.ITEM_NAME.typeName(), (Object)"minecraft:saddle"));
            Dynamic dynamic2 = dynamic.emptyMap();
            dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
            dynamic2 = dynamic2.set("Damage", dynamic2.createShort((short)0));
            typed2 = typed2.set(DSL.remainderFinder(), (Object)dynamic2);
            dynamic.remove("Saddle");
            return inputType.set(opticFinder2, typed2).set(DSL.remainderFinder(), (Object)dynamic);
        }
        return inputType;
    }
}

