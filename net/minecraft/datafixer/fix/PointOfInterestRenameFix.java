/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public abstract class PointOfInterestRenameFix
extends DataFix {
    public PointOfInterestRenameFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.POI_CHUNK.typeName(), (Type)DSL.remainderType());
        if (!Objects.equals((Object)type, (Object)this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
            throw new IllegalStateException("Poi type is not what was expected.");
        }
        return this.fixTypeEverywhere("POI rename", type, dynamicOps -> pair -> pair.mapSecond(this::method_23299));
    }

    private <T> Dynamic<T> method_23299(Dynamic<T> dynamic2) {
        return dynamic2.update("Sections", dynamic -> dynamic.updateMapValues(pair -> pair.mapSecond(dynamic2 -> dynamic2.update("Records", dynamic -> (Dynamic)DataFixUtils.orElse(this.method_23304((Dynamic)dynamic), (Object)dynamic)))));
    }

    private <T> Optional<Dynamic<T>> method_23304(Dynamic<T> dynamic) {
        return dynamic.asStreamOpt().map(stream -> dynamic.createList(stream.map(dynamic2 -> dynamic2.update("type", dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.asString().map(this::rename).map(((Dynamic)dynamic)::createString).result(), (Object)dynamic))))).result();
    }

    protected abstract String rename(String var1);
}

