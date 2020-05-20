/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractUuidFix
extends DataFix {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected DSL.TypeReference typeReference;

    public AbstractUuidFix(Schema schema, DSL.TypeReference typeReference) {
        super(schema, false);
        this.typeReference = typeReference;
    }

    protected Typed<?> updateTyped(Typed<?> typed2, String string, Function<Dynamic<?>, Dynamic<?>> function) {
        Type type = this.getInputSchema().getChoiceType(this.typeReference, string);
        Type type2 = this.getOutputSchema().getChoiceType(this.typeReference, string);
        return typed2.updateTyped(DSL.namedChoice((String)string, (Type)type), type2, typed -> typed.update(DSL.remainderFinder(), function));
    }

    protected static Optional<Dynamic<?>> updateStringUuid(Dynamic<?> dynamic, String string, String string2) {
        return AbstractUuidFix.createArrayFromStringUuid(dynamic, string).map(dynamic2 -> dynamic.remove(string).set(string2, dynamic2));
    }

    protected static Optional<Dynamic<?>> updateCompoundUuid(Dynamic<?> dynamic, String string, String string2) {
        return dynamic.get(string).result().flatMap(AbstractUuidFix::createArrayFromCompoundUuid).map(dynamic2 -> dynamic.remove(string).set(string2, dynamic2));
    }

    protected static Optional<Dynamic<?>> updateRegularMostLeast(Dynamic<?> dynamic, String string, String string2) {
        String string3 = string + "Most";
        String string4 = string + "Least";
        return AbstractUuidFix.createArrayFromMostLeastTags(dynamic, string3, string4).map(dynamic2 -> dynamic.remove(string3).remove(string4).set(string2, dynamic2));
    }

    protected static Optional<Dynamic<?>> createArrayFromStringUuid(Dynamic<?> dynamic, String string) {
        return dynamic.get(string).result().flatMap(dynamic2 -> {
            String string = dynamic2.asString(null);
            if (string != null) {
                try {
                    UUID uUID = UUID.fromString(string);
                    return AbstractUuidFix.createArray(dynamic, uUID.getMostSignificantBits(), uUID.getLeastSignificantBits());
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            return Optional.empty();
        });
    }

    protected static Optional<Dynamic<?>> createArrayFromCompoundUuid(Dynamic<?> dynamic) {
        return AbstractUuidFix.createArrayFromMostLeastTags(dynamic, "M", "L");
    }

    protected static Optional<Dynamic<?>> createArrayFromMostLeastTags(Dynamic<?> dynamic, String string, String string2) {
        long l = dynamic.get(string).asLong(0L);
        long m = dynamic.get(string2).asLong(0L);
        if (l == 0L || m == 0L) {
            return Optional.empty();
        }
        return AbstractUuidFix.createArray(dynamic, l, m);
    }

    protected static Optional<Dynamic<?>> createArray(Dynamic<?> dynamic, long l, long m) {
        return Optional.of(dynamic.createIntList(Arrays.stream(new int[]{(int)(l >> 32), (int)l, (int)(m >> 32), (int)m})));
    }
}

