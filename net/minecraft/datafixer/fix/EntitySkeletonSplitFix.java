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
    public EntitySkeletonSplitFix(Schema outputSchema, boolean changesType) {
        super("EntitySkeletonSplitFix", outputSchema, changesType);
    }

    @Override
    protected Pair<String, Dynamic<?>> transform(String choice, Dynamic<?> dynamic) {
        if (Objects.equals(choice, "Skeleton")) {
            int i = dynamic.get("SkeletonType").asInt(0);
            if (i == 1) {
                choice = "WitherSkeleton";
            } else if (i == 2) {
                choice = "Stray";
            }
        }
        return Pair.of((Object)choice, dynamic);
    }
}

