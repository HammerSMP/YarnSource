/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.types.templates.List$ListType
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.stream.LongStream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.math.MathHelper;

public class BitStorageAlignFix
extends DataFix {
    public BitStorageAlignFix(Schema outputSchema) {
        super(outputSchema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type type2 = type.findFieldType("Level");
        OpticFinder opticFinder = DSL.fieldFinder((String)"Level", (Type)type2);
        OpticFinder opticFinder2 = opticFinder.type().findField("Sections");
        Type type3 = ((List.ListType)opticFinder2.type()).getElement();
        OpticFinder opticFinder3 = DSL.typeFinder((Type)type3);
        Type type4 = DSL.named((String)TypeReferences.BLOCK_STATE.typeName(), (Type)DSL.remainderType());
        OpticFinder opticFinder4 = DSL.fieldFinder((String)"Palette", (Type)DSL.list((Type)type4));
        return this.fixTypeEverywhereTyped("BitStorageAlignFix", type, this.getOutputSchema().getType(TypeReferences.CHUNK), typed2 -> typed2.updateTyped(opticFinder, typed -> this.method_27775(BitStorageAlignFix.method_27774(opticFinder2, opticFinder3, opticFinder4, typed))));
    }

    private Typed<?> method_27775(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("Heightmaps", dynamic2 -> dynamic2.updateMapValues(pair -> pair.mapSecond(dynamic2 -> BitStorageAlignFix.method_27772(dynamic, dynamic2, 256, 9)))));
    }

    private static Typed<?> method_27774(OpticFinder<?> opticFinder, OpticFinder<?> opticFinder2, OpticFinder<List<Pair<String, Dynamic<?>>>> opticFinder3, Typed<?> typed) {
        return typed.updateTyped(opticFinder, typed2 -> typed2.updateTyped(opticFinder2, typed -> {
            int i = typed.getOptional(opticFinder3).map(list -> Math.max(4, DataFixUtils.ceillog2((int)list.size()))).orElse(0);
            if (i == 0 || MathHelper.isPowerOfTwo(i)) {
                return typed;
            }
            return typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("BlockStates", dynamic2 -> BitStorageAlignFix.method_27772(dynamic, dynamic2, 4096, i)));
        }));
    }

    private static Dynamic<?> method_27772(Dynamic<?> dynamic, Dynamic<?> dynamic2, int i, int j) {
        long[] ls = dynamic2.asLongStream().toArray();
        long[] ms = BitStorageAlignFix.method_27288(i, j, ls);
        return dynamic.createLongList(LongStream.of(ms));
    }

    public static long[] method_27288(int i, int j, long[] ls) {
        int k = ls.length;
        if (k == 0) {
            return ls;
        }
        long l = (1L << j) - 1L;
        int m = 64 / j;
        int n = (i + m - 1) / m;
        long[] ms = new long[n];
        int o = 0;
        int p = 0;
        long q = 0L;
        int r = 0;
        long s = ls[0];
        long t = k > 1 ? ls[1] : 0L;
        for (int u = 0; u < i; ++u) {
            long ab;
            int v = u * j;
            int w = v >> 6;
            int x = (u + 1) * j - 1 >> 6;
            int y = v ^ w << 6;
            if (w != r) {
                s = t;
                t = w + 1 < k ? ls[w + 1] : 0L;
                r = w;
            }
            if (w == x) {
                long z = s >>> y & l;
            } else {
                int aa = 64 - y;
                ab = (s >>> y | t << aa) & l;
            }
            int ac = p + j;
            if (ac >= 64) {
                ms[o++] = q;
                q = ab;
                p = j;
                continue;
            }
            q |= ab << p;
            p = ac;
        }
        if (q != 0L) {
            ms[o] = q;
        }
        return ms;
    }
}

