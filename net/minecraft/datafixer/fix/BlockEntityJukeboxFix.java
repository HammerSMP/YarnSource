/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;
import net.minecraft.datafixer.fix.ItemIdFix;
import net.minecraft.datafixer.fix.ItemInstanceTheFlatteningFix;

public class BlockEntityJukeboxFix
extends ChoiceFix {
    public BlockEntityJukeboxFix(Schema schema, boolean bl) {
        super(schema, bl, "BlockEntityJukeboxFix", TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        Type type = this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
        Type type2 = type.findFieldType("RecordItem");
        OpticFinder opticFinder = DSL.fieldFinder((String)"RecordItem", (Type)type2);
        Dynamic dynamic = (Dynamic)typed.get(DSL.remainderFinder());
        int i = dynamic.get("Record").asInt(0);
        if (i > 0) {
            dynamic.remove("Record");
            String string = ItemInstanceTheFlatteningFix.getItem(ItemIdFix.fromId(i), 0);
            if (string != null) {
                Dynamic dynamic2 = dynamic.emptyMap();
                dynamic2 = dynamic2.set("id", dynamic2.createString(string));
                dynamic2 = dynamic2.set("Count", dynamic2.createByte((byte)1));
                return typed.set(opticFinder, (Typed)((Optional)type2.readTyped(dynamic2).getSecond()).orElseThrow(() -> new IllegalStateException("Could not create record item stack."))).set(DSL.remainderFinder(), (Object)dynamic);
            }
        }
        return typed;
    }
}
