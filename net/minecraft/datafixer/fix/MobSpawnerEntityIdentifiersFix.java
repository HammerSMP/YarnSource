/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class MobSpawnerEntityIdentifiersFix
extends DataFix {
    public MobSpawnerEntityIdentifiersFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private Dynamic<?> fixSpawner(Dynamic<?> dynamic2) {
        Optional optional2;
        if (!"MobSpawner".equals(dynamic2.get("id").asString(""))) {
            return dynamic2;
        }
        Optional optional = dynamic2.get("EntityId").asString().result();
        if (optional.isPresent()) {
            Dynamic dynamic22 = (Dynamic)DataFixUtils.orElse((Optional)dynamic2.get("SpawnData").result(), (Object)dynamic2.emptyMap());
            dynamic22 = dynamic22.set("id", dynamic22.createString(((String)optional.get()).isEmpty() ? "Pig" : (String)optional.get()));
            dynamic2 = dynamic2.set("SpawnData", dynamic22);
            dynamic2 = dynamic2.remove("EntityId");
        }
        if ((optional2 = dynamic2.get("SpawnPotentials").asStreamOpt().result()).isPresent()) {
            dynamic2 = dynamic2.set("SpawnPotentials", dynamic2.createList(((Stream)optional2.get()).map(dynamic -> {
                Optional optional = dynamic.get("Type").asString().result();
                if (optional.isPresent()) {
                    Dynamic dynamic2 = ((Dynamic)DataFixUtils.orElse((Optional)dynamic.get("Properties").result(), (Object)dynamic.emptyMap())).set("id", dynamic.createString((String)optional.get()));
                    return dynamic.set("Entity", dynamic2).remove("Type").remove("Properties");
                }
                return dynamic;
            })));
        }
        return dynamic2;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getOutputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
        return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(TypeReferences.UNTAGGED_SPAWNER), type, typed -> {
            Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
            DataResult dataResult = type.readTyped(this.fixSpawner(dynamic = dynamic.set("id", dynamic.createString("MobSpawner"))));
            if (!dataResult.result().isPresent()) {
                return typed;
            }
            return (Typed)((Pair)dataResult.result().get()).getFirst();
        });
    }
}

