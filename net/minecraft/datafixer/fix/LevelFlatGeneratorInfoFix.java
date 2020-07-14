/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Splitter
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Dynamic
 *  org.apache.commons.lang3.math.NumberUtils
 */
package net.minecraft.datafixer.fix;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import net.minecraft.datafixer.fix.EntityBlockStateFix;
import org.apache.commons.lang3.math.NumberUtils;

public class LevelFlatGeneratorInfoFix
extends DataFix {
    private static final Splitter SPLIT_ON_SEMICOLON = Splitter.on((char)';').limit(5);
    private static final Splitter SPLIT_ON_COMMA = Splitter.on((char)',');
    private static final Splitter SPLIT_ON_LOWER_X = Splitter.on((char)'x').limit(2);
    private static final Splitter SPLIT_ON_ASTERISK = Splitter.on((char)'*').limit(2);
    private static final Splitter SPLIT_ON_COLON = Splitter.on((char)':').limit(3);

    public LevelFlatGeneratorInfoFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(TypeReferences.LEVEL), typed -> typed.update(DSL.remainderFinder(), this::fixGeneratorOptions));
    }

    private Dynamic<?> fixGeneratorOptions(Dynamic<?> dynamic2) {
        if (dynamic2.get("generatorName").asString("").equalsIgnoreCase("flat")) {
            return dynamic2.update("generatorOptions", dynamic -> (Dynamic)DataFixUtils.orElse((Optional)dynamic.asString().map(this::fixFlatGeneratorOptions).map(((Dynamic)dynamic)::createString).result(), (Object)dynamic));
        }
        return dynamic2;
    }

    @VisibleForTesting
    String fixFlatGeneratorOptions(String generatorOptions) {
        String string4;
        int j;
        if (generatorOptions.isEmpty()) {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
        }
        Iterator iterator = SPLIT_ON_SEMICOLON.split((CharSequence)generatorOptions).iterator();
        String string2 = (String)iterator.next();
        if (iterator.hasNext()) {
            int i = NumberUtils.toInt((String)string2, (int)0);
            String string3 = (String)iterator.next();
        } else {
            j = 0;
            string4 = string2;
        }
        if (j < 0 || j > 3) {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
        }
        StringBuilder stringBuilder = new StringBuilder();
        Splitter splitter = j < 3 ? SPLIT_ON_LOWER_X : SPLIT_ON_ASTERISK;
        stringBuilder.append(StreamSupport.stream(SPLIT_ON_COMMA.split((CharSequence)string4).spliterator(), false).map(string -> {
            String string3;
            int k;
            List list = splitter.splitToList((CharSequence)string);
            if (list.size() == 2) {
                int j = NumberUtils.toInt((String)((String)list.get(0)));
                String string2 = (String)list.get(1);
            } else {
                k = 1;
                string3 = (String)list.get(0);
            }
            List list2 = SPLIT_ON_COLON.splitToList((CharSequence)string3);
            int l = ((String)list2.get(0)).equals("minecraft") ? 1 : 0;
            String string4 = (String)list2.get(l);
            int m = j == 3 ? EntityBlockStateFix.getNumericalBlockId("minecraft:" + string4) : NumberUtils.toInt((String)string4, (int)0);
            int n = l + 1;
            int o = list2.size() > n ? NumberUtils.toInt((String)((String)list2.get(n)), (int)0) : 0;
            return (k == 1 ? "" : k + "*") + BlockStateFlattening.lookupState(m << 4 | o).get("Name").asString("");
        }).collect(Collectors.joining(",")));
        while (iterator.hasNext()) {
            stringBuilder.append(';').append((String)iterator.next());
        }
        return stringBuilder.toString();
    }
}

