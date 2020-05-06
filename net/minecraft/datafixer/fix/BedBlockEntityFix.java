/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.List$ListType
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class BedBlockEntityFix
extends DataFix {
    public BedBlockEntityFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getOutputSchema().getType(TypeReferences.CHUNK);
        Type type2 = type.findFieldType("Level");
        Type type3 = type2.findFieldType("TileEntities");
        if (!(type3 instanceof List.ListType)) {
            throw new IllegalStateException("Tile entity type is not a list type.");
        }
        List.ListType listType = (List.ListType)type3;
        return this.method_15506(type2, listType);
    }

    private <TE> TypeRewriteRule method_15506(Type<?> type, List.ListType<TE> listType) {
        Type type2 = listType.getElement();
        OpticFinder opticFinder = DSL.fieldFinder((String)"Level", type);
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"TileEntities", listType);
        int i = 416;
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhere("InjectBedBlockEntityType", (Type)this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), (Type)this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), dynamicOps -> pair -> pair), (TypeRewriteRule)this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(TypeReferences.CHUNK), typed -> {
            Typed typed2 = typed.getTyped(opticFinder);
            Dynamic dynamic2 = (Dynamic)typed2.get(DSL.remainderFinder());
            int i = dynamic2.get("xPos").asInt(0);
            int j = dynamic2.get("zPos").asInt(0);
            ArrayList list = Lists.newArrayList((Iterable)((Iterable)typed2.getOrCreate(opticFinder2)));
            List list2 = dynamic2.get("Sections").asList(Function.identity());
            for (int k = 0; k < list2.size(); ++k) {
                Dynamic dynamic22 = (Dynamic)list2.get(k);
                int l = dynamic22.get("Y").asInt(0);
                Stream<Integer> stream = dynamic22.get("Blocks").asStream().map(dynamic -> dynamic.asInt(0));
                int m = 0;
                Iterator iterator = ((Iterable)stream::iterator).iterator();
                while (iterator.hasNext()) {
                    int n = (Integer)iterator.next();
                    if (416 == (n & 0xFF) << 4) {
                        int o = m & 0xF;
                        int p = m >> 8 & 0xF;
                        int q = m >> 4 & 0xF;
                        HashMap map = Maps.newHashMap();
                        map.put(dynamic22.createString("id"), dynamic22.createString("minecraft:bed"));
                        map.put(dynamic22.createString("x"), dynamic22.createInt(o + (i << 4)));
                        map.put(dynamic22.createString("y"), dynamic22.createInt(p + (l << 4)));
                        map.put(dynamic22.createString("z"), dynamic22.createInt(q + (j << 4)));
                        map.put(dynamic22.createString("color"), dynamic22.createShort((short)14));
                        list.add(((Optional)type2.read(dynamic22.createMap((Map)map)).getSecond()).orElseThrow(() -> new IllegalStateException("Could not parse newly created bed block entity.")));
                    }
                    ++m;
                }
            }
            if (!list.isEmpty()) {
                return typed.set(opticFinder, typed2.set(opticFinder2, (Object)list));
            }
            return typed;
        }));
    }
}

