/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.TypeReferences;

public class IglooMetadataRemovalFix
extends DataFix {
    public IglooMetadataRemovalFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
        Type type2 = this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
        return this.writeFixAndRead("IglooMetadataRemovalFix", type, type2, IglooMetadataRemovalFix::removeMetadata);
    }

    private static <T> Dynamic<T> removeMetadata(Dynamic<T> dynamic) {
        boolean bl = dynamic.get("Children").asStreamOpt().map(stream -> stream.allMatch(IglooMetadataRemovalFix::isIgloo)).orElse(false);
        if (bl) {
            return dynamic.set("id", dynamic.createString("Igloo")).remove("Children");
        }
        return dynamic.update("Children", IglooMetadataRemovalFix::removeIgloos);
    }

    private static <T> Dynamic<T> removeIgloos(Dynamic<T> dynamic) {
        return dynamic.asStreamOpt().map(stream -> stream.filter(dynamic -> !IglooMetadataRemovalFix.isIgloo(dynamic))).map(dynamic::createList).orElse(dynamic);
    }

    private static boolean isIgloo(Dynamic<?> dynamic) {
        return dynamic.get("id").asString("").equals("Iglu");
    }
}

