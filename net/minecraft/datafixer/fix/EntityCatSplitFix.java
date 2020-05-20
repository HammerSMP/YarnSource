/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.fix.EntitySimpleTransformFix;

public class EntityCatSplitFix
extends EntitySimpleTransformFix {
    public EntityCatSplitFix(Schema schema, boolean bl) {
        super("EntityCatSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Dynamic<?>> transform(String string, Dynamic<?> dynamic) {
        if (Objects.equals("minecraft:ocelot", string)) {
            int i = dynamic.get("CatType").asInt(0);
            if (i == 0) {
                String string2 = dynamic.get("Owner").asString("");
                String string3 = dynamic.get("OwnerUUID").asString("");
                if (string2.length() > 0 || string3.length() > 0) {
                    dynamic.set("Trusting", dynamic.createBoolean(true));
                }
            } else if (i > 0 && i < 4) {
                dynamic = dynamic.set("CatType", dynamic.createInt(i));
                dynamic = dynamic.set("OwnerUUID", dynamic.createString(dynamic.get("OwnerUUID").asString("")));
                return Pair.of((Object)"minecraft:cat", (Object)dynamic);
            }
        }
        return Pair.of((Object)string, dynamic);
    }
}

