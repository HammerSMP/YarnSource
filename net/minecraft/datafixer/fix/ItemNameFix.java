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

public abstract class ItemNameFix
extends DataFix {
    private final String name;

    public ItemNameFix(Schema schema, String string) {
        super(schema, false);
        this.name = string;
    }

    public TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.ITEM_NAME.typeName(), (Type)DSL.namespacedString());
        if (!Objects.equals((Object)this.getInputSchema().getType(TypeReferences.ITEM_NAME), (Object)type)) {
            throw new IllegalStateException("item name type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this::rename));
    }

    protected abstract String rename(String var1);

    public static DataFix create(Schema schema, String string, final Function<String, String> function) {
        return new ItemNameFix(schema, string){

            @Override
            protected String rename(String string) {
                return (String)function.apply(string);
            }
        };
    }
}

