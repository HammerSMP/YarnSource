/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class ItemNameFix
extends DataFix {
    private final String name;

    public ItemNameFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    public TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType());
        if (!Objects.equals((Object)this.getInputSchema().getType(TypeReferences.ITEM_NAME), (Object)type)) {
            throw new IllegalStateException("item name type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this::rename));
    }

    protected abstract String rename(String var1);

    public static DataFix create(Schema outputSchema, String name, final Function<String, String> rename) {
        return new ItemNameFix(outputSchema, name){

            @Override
            protected String rename(String input) {
                return (String)rename.apply(input);
            }
        };
    }
}

