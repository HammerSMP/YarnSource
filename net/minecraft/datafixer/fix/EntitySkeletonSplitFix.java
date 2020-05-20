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

public class EntitySkeletonSplitFix
extends EntitySimpleTransformFix {
    public EntitySkeletonSplitFix(Schema schema, boolean bl) {
        super("EntitySkeletonSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Dynamic<?>> transform(String string, Dynamic<?> dynamic) {
        if (Objects.equals(string, "Skeleton")) {
            int i = dynamic.get("SkeletonType").asInt(0);
            if (i == 1) {
                string = "WitherSkeleton";
            } else if (i == 2) {
                string = "Stray";
            }
        }
        return Pair.of((Object)string, dynamic);
    }
}

