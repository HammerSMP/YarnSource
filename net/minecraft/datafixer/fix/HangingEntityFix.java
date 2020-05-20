/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class HangingEntityFix
extends DataFix {
    private static final int[][] OFFSETS = new int[][]{{0, 0, 1}, {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}};

    public HangingEntityFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private Dynamic<?> fixDecorationPosition(Dynamic<?> dynamic, boolean bl, boolean bl2) {
        if ((bl || bl2) && !dynamic.get("Facing").asNumber().result().isPresent()) {
            int j;
            if (dynamic.get("Direction").asNumber().result().isPresent()) {
                int i = dynamic.get("Direction").asByte((byte)0) % OFFSETS.length;
                int[] is = OFFSETS[i];
                dynamic = dynamic.set("TileX", dynamic.createInt(dynamic.get("TileX").asInt(0) + is[0]));
                dynamic = dynamic.set("TileY", dynamic.createInt(dynamic.get("TileY").asInt(0) + is[1]));
                dynamic = dynamic.set("TileZ", dynamic.createInt(dynamic.get("TileZ").asInt(0) + is[2]));
                dynamic = dynamic.remove("Direction");
                if (bl2 && dynamic.get("ItemRotation").asNumber().result().isPresent()) {
                    dynamic = dynamic.set("ItemRotation", dynamic.createByte((byte)(dynamic.get("ItemRotation").asByte((byte)0) * 2)));
                }
            } else {
                j = dynamic.get("Dir").asByte((byte)0) % OFFSETS.length;
                dynamic = dynamic.remove("Dir");
            }
            dynamic = dynamic.set("Facing", dynamic.createByte((byte)j));
        }
        return dynamic;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "Painting");
        OpticFinder opticFinder = DSL.namedChoice((String)"Painting", (Type)type);
        Type type2 = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "ItemFrame");
        OpticFinder opticFinder2 = DSL.namedChoice((String)"ItemFrame", (Type)type2);
        Type type3 = this.getInputSchema().getType(TypeReferences.ENTITY);
        TypeRewriteRule typeRewriteRule = this.fixTypeEverywhereTyped("EntityPaintingFix", type3, typed2 -> typed2.updateTyped(opticFinder, type, typed -> typed.update(DSL.remainderFinder(), dynamic -> this.fixDecorationPosition((Dynamic<?>)dynamic, true, false))));
        TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped("EntityItemFrameFix", type3, typed2 -> typed2.updateTyped(opticFinder2, type2, typed -> typed.update(DSL.remainderFinder(), dynamic -> this.fixDecorationPosition((Dynamic<?>)dynamic, false, true))));
        return TypeRewriteRule.seq((TypeRewriteRule)typeRewriteRule, (TypeRewriteRule)typeRewriteRule2);
    }
}

