/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import net.minecraft.datafixer.fix.EntityTransformFix;

public abstract class EntitySimpleTransformFix
extends EntityTransformFix {
    public EntitySimpleTransformFix(String string, Schema schema, boolean bl) {
        super(string, schema, bl);
    }

    @Override
    protected Pair<String, Typed<?>> transform(String string, Typed<?> typed) {
        Pair<String, Dynamic<?>> pair = this.transform(string, (Dynamic)typed.getOrCreate(DSL.remainderFinder()));
        return Pair.of((Object)pair.getFirst(), (Object)typed.set(DSL.remainderFinder(), pair.getSecond()));
    }

    protected abstract Pair<String, Dynamic<?>> transform(String var1, Dynamic<?> var2);
}
