/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.EntityTransformFix;

public class EntityHorseSplitFix
extends EntityTransformFix {
    public EntityHorseSplitFix(Schema schema, boolean bl) {
        super("EntityHorseSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Typed<?>> transform(String string, Typed<?> typed) {
        Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
        if (Objects.equals("EntityHorse", string)) {
            String string6;
            int i = dynamic.get("Type").asInt(0);
            switch (i) {
                default: {
                    String string2 = "Horse";
                    break;
                }
                case 1: {
                    String string3 = "Donkey";
                    break;
                }
                case 2: {
                    String string4 = "Mule";
                    break;
                }
                case 3: {
                    String string5 = "ZombieHorse";
                    break;
                }
                case 4: {
                    string6 = "SkeletonHorse";
                }
            }
            dynamic.remove("Type");
            Type type = (Type)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string6);
            return Pair.of((Object)string6, (Object)((Pair)typed.write().flatMap(((Type)type)::readTyped).result().orElseThrow(() -> new IllegalStateException("Could not parse the new horse"))).getFirst());
        }
        return Pair.of((Object)string, typed);
    }
}

