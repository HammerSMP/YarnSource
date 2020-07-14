/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class EntityShulkerColorFix
extends ChoiceFix {
    public EntityShulkerColorFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "EntityShulkerColorFix", TypeReferences.ENTITY, "minecraft:shulker");
    }

    public Dynamic<?> fixShulkerColor(Dynamic<?> dynamic) {
        if (!dynamic.get("Color").map(Dynamic::asNumber).result().isPresent()) {
            return dynamic.set("Color", dynamic.createByte((byte)10));
        }
        return dynamic;
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), this::fixShulkerColor);
    }
}

