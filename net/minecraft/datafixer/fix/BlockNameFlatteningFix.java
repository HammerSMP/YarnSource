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
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class BlockNameFlatteningFix
extends DataFix {
    public BlockNameFlatteningFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
        Type type2 = this.getOutputSchema().getType(TypeReferences.BLOCK_NAME);
        Type type3 = DSL.named((String)TypeReferences.BLOCK_NAME.typeName(), (Type)DSL.or((Type)DSL.intType(), IdentifierNormalizingSchema.getIdentifierType()));
        Type type4 = DSL.named((String)TypeReferences.BLOCK_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType());
        if (!Objects.equals((Object)type, (Object)type3) || !Objects.equals((Object)type2, (Object)type4)) {
            throw new IllegalStateException("Expected and actual types don't match.");
        }
        return this.fixTypeEverywhere("BlockNameFlatteningFix", type3, type4, dynamicOps -> pair -> pair.mapSecond(either -> (String)either.map(BlockStateFlattening::lookupStateBlock, string -> BlockStateFlattening.lookupBlock(IdentifierNormalizingSchema.normalize(string)))));
    }
}

