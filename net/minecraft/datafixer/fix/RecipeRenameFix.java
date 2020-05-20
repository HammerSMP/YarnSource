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

public class RecipeRenameFix
extends DataFix {
    private final String name;
    private final Function<String, String> renamer;

    public RecipeRenameFix(Schema schema, boolean bl, String string, Function<String, String> function) {
        super(schema, bl);
        this.name = string;
        this.renamer = function;
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.RECIPE.typeName(), IdentifierNormalizingSchema.method_28295());
        if (!Objects.equals((Object)type, (Object)this.getInputSchema().getType(TypeReferences.RECIPE))) {
            throw new IllegalStateException("Recipe type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this.renamer));
    }
}

