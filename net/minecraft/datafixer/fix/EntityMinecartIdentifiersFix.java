/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class EntityMinecartIdentifiersFix
extends DataFix {
    private static final List<String> MINECARTS = Lists.newArrayList((Object[])new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace"});

    public EntityMinecartIdentifiersFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType taggedChoiceType = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
        TaggedChoice.TaggedChoiceType taggedChoiceType2 = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
        return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", (Type)taggedChoiceType, (Type)taggedChoiceType2, dynamicOps -> pair -> {
            if (Objects.equals(pair.getFirst(), "Minecart")) {
                String string2;
                Typed typed = (Typed)taggedChoiceType.point(dynamicOps, (Object)"Minecart", pair.getSecond()).orElseThrow(IllegalStateException::new);
                Dynamic dynamic2 = (Dynamic)typed.getOrCreate(DSL.remainderFinder());
                int i = dynamic2.get("Type").asInt(0);
                if (i > 0 && i < MINECARTS.size()) {
                    String string = MINECARTS.get(i);
                } else {
                    string2 = "MinecartRideable";
                }
                return Pair.of((Object)string2, typed.write().map(dynamic -> ((Type)taggedChoiceType2.types().get(string2)).read(dynamic)).result().orElseThrow(() -> new IllegalStateException("Could not read the new minecart.")));
            }
            return pair;
        });
    }
}

