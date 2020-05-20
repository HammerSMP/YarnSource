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
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.shorts.ShortArrayList
 *  it.unimi.dsi.fastutil.shorts.ShortList
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
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class ChunkToProtoChunkFix
extends DataFix {
    public ChunkToProtoChunkFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
        Type type3 = type.findFieldType("Level");
        Type type4 = type2.findFieldType("Level");
        Type type5 = type3.findFieldType("TileTicks");
        OpticFinder opticFinder = DSL.fieldFinder((String)"Level", (Type)type3);
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"TileTicks", (Type)type5);
        return TypeRewriteRule.seq((TypeRewriteRule)this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", type, this.getOutputSchema().getType(TypeReferences.CHUNK), typed -> typed.updateTyped(opticFinder, type4, typed2 -> {
            Dynamic dynamic4;
            Optional optional = typed2.getOptionalTyped(opticFinder2).flatMap(typed -> typed.write().result()).flatMap(dynamic -> dynamic.asStreamOpt().result());
            Dynamic dynamic2 = (Dynamic)typed2.get(DSL.remainderFinder());
            boolean bl = dynamic2.get("TerrainPopulated").asBoolean(false) && (!dynamic2.get("LightPopulated").asNumber().result().isPresent() || dynamic2.get("LightPopulated").asBoolean(false));
            dynamic2 = dynamic2.set("Status", dynamic2.createString(bl ? "mobs_spawned" : "empty"));
            dynamic2 = dynamic2.set("hasLegacyStructureData", dynamic2.createBoolean(true));
            if (bl) {
                Optional optional2 = dynamic2.get("Biomes").asByteBufferOpt().result();
                if (optional2.isPresent()) {
                    ByteBuffer byteBuffer = (ByteBuffer)optional2.get();
                    int[] is = new int[256];
                    for (int i2 = 0; i2 < is.length; ++i2) {
                        if (i2 >= byteBuffer.capacity()) continue;
                        is[i2] = byteBuffer.get(i2) & 0xFF;
                    }
                    dynamic2 = dynamic2.set("Biomes", dynamic2.createIntList(Arrays.stream(is)));
                }
                Dynamic dynamic22 = dynamic2;
                List list = IntStream.range(0, 16).mapToObj(i -> new ShortArrayList()).collect(Collectors.toList());
                if (optional.isPresent()) {
                    ((Stream)optional.get()).forEach(dynamic -> {
                        int i = dynamic.get("x").asInt(0);
                        int j = dynamic.get("y").asInt(0);
                        int k = dynamic.get("z").asInt(0);
                        short s = ChunkToProtoChunkFix.method_15675(i, j, k);
                        ((ShortList)list.get(j >> 4)).add(s);
                    });
                    dynamic2 = dynamic2.set("ToBeTicked", dynamic2.createList(list.stream().map(shortList -> dynamic22.createList(shortList.stream().map(((Dynamic)dynamic22)::createShort)))));
                }
                Dynamic dynamic3 = (Dynamic)DataFixUtils.orElse((Optional)typed2.set(DSL.remainderFinder(), (Object)dynamic2).write().result(), (Object)dynamic2);
            } else {
                dynamic4 = dynamic2;
            }
            return (Typed)((Pair)type4.readTyped(dynamic4).result().orElseThrow(() -> new IllegalStateException("Could not read the new chunk"))).getFirst();
        })), (TypeRewriteRule)this.writeAndRead("Structure biome inject", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE)));
    }

    private static short method_15675(int i, int j, int k) {
        return (short)(i & 0xF | (j & 0xF) << 4 | (k & 0xF) << 8);
    }
}

