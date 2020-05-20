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

public class EntityElderGuardianSplitFix
extends EntitySimpleTransformFix {
    public EntityElderGuardianSplitFix(Schema schema, boolean bl) {
        super("EntityElderGuardianSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Dynamic<?>> transform(String string, Dynamic<?> dynamic) {
        return Pair.of((Object)(Objects.equals(string, "Guardian") && dynamic.get("Elder").asBoolean(false) ? "ElderGuardian" : string), dynamic);
    }
}

