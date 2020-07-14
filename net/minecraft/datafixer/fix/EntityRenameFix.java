/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.TaggedChoice$TaggedChoiceType
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class EntityRenameFix
extends DataFix {
    private final String name;

    public EntityRenameFix(String name, Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
        this.name = name;
    }

    public TypeRewriteRule makeRule() {
        TaggedChoice.TaggedChoiceType taggedChoiceType = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
        TaggedChoice.TaggedChoiceType taggedChoiceType2 = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
        Type type = DSL.named((String)TypeReferences.ENTITY_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType());
        if (!Objects.equals((Object)this.getOutputSchema().getType(TypeReferences.ENTITY_NAME), (Object)type)) {
            throw new IllegalStateException("Entity name type is not what was expected.");
        }
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhere(this.name, (Type)taggedChoiceType, (Type)taggedChoiceType2, dynamicOps -> pair -> pair.mapFirst(string -> {
            String string2 = this.rename((String)string);
            Type type = (Type)taggedChoiceType.types().get(string);
            Type type2 = (Type)taggedChoiceType2.types().get(string2);
            if (!type2.equals((Object)type, true, true)) {
                throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", new Object[]{type2, type}));
            }
            return string2;
        })), (TypeRewriteRule)this.fixTypeEverywhere(this.name + " for entity name", type, dynamicOps -> pair -> pair.mapSecond(this::rename)));
    }

    protected abstract String rename(String var1);
}

