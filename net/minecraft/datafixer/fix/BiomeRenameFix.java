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
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class BiomeRenameFix
extends DataFix {
    private final String name;
    private final Map<String, String> renames;

    public BiomeRenameFix(Schema schema, boolean bl, String string, Map<String, String> map) {
        super(schema, bl);
        this.renames = map;
        this.name = string;
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.BIOME.typeName(), IdentifierNormalizingSchema.getIdentifierType());
        if (!Objects.equals((Object)type, (Object)this.getInputSchema().getType(TypeReferences.BIOME))) {
            throw new IllegalStateException("Biome type is not what was expected.");
        }
        return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(string -> this.renames.getOrDefault(string, (String)string)));
    }
}

