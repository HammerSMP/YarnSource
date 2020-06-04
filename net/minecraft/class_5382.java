/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_5379;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_5382<T>
extends class_5379<T> {
    private static final Logger field_25509 = LogManager.getLogger();
    private final ResourceManager field_25510;
    private final DimensionTracker field_25511;
    private final Map<RegistryKey<? extends Registry<?>>, class_5383<?>> field_25512 = Maps.newIdentityHashMap();

    public static <T> class_5382<T> method_29753(DynamicOps<T> dynamicOps, ResourceManager arg, DimensionTracker arg2) {
        return new class_5382<T>(dynamicOps, arg, arg2);
    }

    private class_5382(DynamicOps<T> dynamicOps, ResourceManager arg, DimensionTracker arg2) {
        super(dynamicOps);
        this.field_25510 = arg;
        this.field_25511 = arg2;
    }

    protected <E> DataResult<Pair<java.util.function.Supplier<E>, T>> method_29759(T object, RegistryKey<Registry<E>> arg, Codec<E> codec) {
        DataResult dataResult = Identifier.field_25139.decode(this.field_25503, object);
        if (!dataResult.result().isPresent()) {
            return codec.decode(this.field_25503, object).map(pair -> pair.mapFirst(object -> () -> object));
        }
        Optional<MutableRegistry<E>> optional = this.field_25511.method_29726(arg);
        if (!optional.isPresent()) {
            return DataResult.error((String)("Unknown registry: " + arg));
        }
        Pair pair2 = (Pair)dataResult.result().get();
        Identifier lv = (Identifier)pair2.getFirst();
        return this.method_29763(arg, optional.get(), codec, lv).map(supplier -> Pair.of((Object)supplier, (Object)pair2.getSecond()));
    }

    public <E> DataResult<SimpleRegistry<E>> method_29755(SimpleRegistry<E> arg, RegistryKey<Registry<E>> arg2, Codec<E> codec) {
        Identifier lv = arg2.getValue();
        Collection<Identifier> collection = this.field_25510.method_29489(lv, string -> string.endsWith(".json"));
        DataResult dataResult = DataResult.success(arg, (Lifecycle)Lifecycle.stable());
        for (Identifier lv2 : collection) {
            String string2 = lv2.getPath();
            if (!string2.endsWith(".json")) {
                field_25509.warn("Skipping resource {} since it is not a json file", (Object)lv2);
                continue;
            }
            if (!string2.startsWith(lv.getPath() + "/")) {
                field_25509.warn("Skipping resource {} since it does not have a registry name prefix", (Object)lv2);
                continue;
            }
            String string22 = string2.substring(0, string2.length() - ".json".length()).substring(lv.getPath().length() + 1);
            int i = string22.indexOf(47);
            if (i < 0) {
                field_25509.warn("Skipping resource {} since it does not have a namespace", (Object)lv2);
                continue;
            }
            String string3 = string22.substring(0, i);
            String string4 = string22.substring(i + 1);
            Identifier lv3 = new Identifier(string3, string4);
            dataResult = dataResult.flatMap(arg3 -> this.method_29763(arg2, (MutableRegistry)arg3, codec, lv3).map(supplier -> arg3));
        }
        return dataResult.setPartial(arg);
    }

    private <E> DataResult<java.util.function.Supplier<E>> method_29763(RegistryKey<Registry<E>> arg, MutableRegistry<E> arg2, Codec<E> codec, Identifier arg3) {
        RegistryKey lv = RegistryKey.of(arg, arg3);
        Object object2 = arg2.get(lv);
        if (object2 != null) {
            return DataResult.success(() -> object2, (Lifecycle)Lifecycle.stable());
        }
        class_5383<E> lv2 = this.method_29761(arg);
        DataResult dataResult = (DataResult)((class_5383)lv2).field_25513.get(lv);
        if (dataResult != null) {
            return dataResult;
        }
        Supplier supplier = Suppliers.memoize(() -> {
            Object object = arg2.get(lv);
            if (object == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + lv);
            }
            return object;
        });
        ((class_5383)lv2).field_25513.put(lv, DataResult.success((Object)supplier));
        DataResult<E> dataResult2 = this.method_29764(arg, lv, codec);
        dataResult2.result().ifPresent(object -> arg2.add(lv, object));
        DataResult dataResult3 = dataResult2.map(object -> () -> object);
        ((class_5383)lv2).field_25513.put(lv, dataResult3);
        return dataResult3;
    }

    /*
     * Exception decompiling
     */
    private <E> DataResult<E> method_29764(RegistryKey<Registry<E>> arg, RegistryKey<E> arg2, Codec<E> codec) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 5[TRYBLOCK]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:428)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:477)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:779)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:251)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:185)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:463)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1001)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:888)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:252)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:134)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    private <E> class_5383<E> method_29761(RegistryKey<Registry<E>> arg2) {
        return this.field_25512.computeIfAbsent(arg2, arg -> new class_5383());
    }

    static final class class_5383<E> {
        private final Map<RegistryKey<E>, DataResult<java.util.function.Supplier<E>>> field_25513 = Maps.newIdentityHashMap();

        private class_5383() {
        }
    }
}

