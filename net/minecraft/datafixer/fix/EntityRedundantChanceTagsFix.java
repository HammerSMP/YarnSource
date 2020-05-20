/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class EntityRedundantChanceTagsFix
extends DataFix {
    public EntityRedundantChanceTagsFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(TypeReferences.ENTITY), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Dynamic dynamic2 = dynamic;
            if (Objects.equals((Object)dynamic.get("HandDropChances"), Optional.of(dynamic.createList(Stream.generate(() -> dynamic2.createFloat(0.0f)).limit(2L))))) {
                dynamic = dynamic.remove("HandDropChances");
            }
            if (Objects.equals((Object)dynamic.get("ArmorDropChances"), Optional.of(dynamic.createList(Stream.generate(() -> dynamic2.createFloat(0.0f)).limit(4L))))) {
                dynamic = dynamic.remove("ArmorDropChances");
            }
            return dynamic;
        }));
    }
}

