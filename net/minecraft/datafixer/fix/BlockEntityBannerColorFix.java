/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class BlockEntityBannerColorFix
extends ChoiceFix {
    public BlockEntityBannerColorFix(Schema schema, boolean bl) {
        super(schema, bl, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
    }

    public Dynamic<?> fixBannerColor(Dynamic<?> dynamic2) {
        dynamic2 = dynamic2.update("Base", dynamic -> dynamic.createInt(15 - dynamic.asInt(0)));
        dynamic2 = dynamic2.update("Patterns", dynamic -> (Dynamic)DataFixUtils.orElse(dynamic.asStreamOpt().map(stream -> stream.map(dynamic2 -> dynamic2.update("Color", dynamic -> dynamic.createInt(15 - dynamic.asInt(0))))).map(((Dynamic)dynamic)::createList), (Object)dynamic));
        return dynamic2;
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixBannerColor);
    }
}

