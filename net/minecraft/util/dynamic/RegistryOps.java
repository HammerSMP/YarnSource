/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.util.dynamic;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_5455;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.ForwardingDynamicOps;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryOps<T>
extends ForwardingDynamicOps<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceManager resourceManager;
    private final class_5455 registryTracker;
    private final Map<RegistryKey<? extends Registry<?>>, ValueHolder<?>> valueHolders = Maps.newIdentityHashMap();

    public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, class_5455 registryTracker) {
        return new RegistryOps<T>(delegate, resourceManager, registryTracker);
    }

    private RegistryOps(DynamicOps<T> delegate, ResourceManager resourceManager, class_5455 registryTracker) {
        super(delegate);
        this.resourceManager = resourceManager;
        this.registryTracker = registryTracker;
    }

    protected <E> DataResult<Pair<java.util.function.Supplier<E>, T>> decodeOrId(T object, RegistryKey<? extends Registry<E>> arg, MapCodec<E> mapCodec) {
        Optional optional = this.registryTracker.method_30527(arg);
        if (!optional.isPresent()) {
            return DataResult.error((String)("Unknown registry: " + arg));
        }
        MutableRegistry lv = optional.get();
        DataResult dataResult = Identifier.CODEC.decode(this.delegate, object);
        if (!dataResult.result().isPresent()) {
            return SimpleRegistry.method_30516(arg, mapCodec).codec().decode(this.delegate, object).map(pair2 -> pair2.mapFirst(pair -> {
                lv.add((RegistryKey)pair.getFirst(), pair.getSecond());
                lv.markLoaded((RegistryKey)pair.getFirst());
                return ((Pair)pair)::getSecond;
            }));
        }
        Pair pair = (Pair)dataResult.result().get();
        Identifier lv2 = (Identifier)pair.getFirst();
        return this.readSupplier(arg, lv, mapCodec, lv2).map(supplier -> Pair.of((Object)supplier, (Object)pair.getSecond()));
    }

    public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> registryRef, MapCodec<E> mapCodec) {
        Identifier lv = registryRef.getValue();
        Collection<Identifier> collection = this.resourceManager.findResources(lv, string -> string.endsWith(".json"));
        DataResult dataResult = DataResult.success(registry, (Lifecycle)Lifecycle.stable());
        for (Identifier lv2 : collection) {
            String string2 = lv2.getPath();
            if (!string2.endsWith(".json")) {
                LOGGER.warn("Skipping resource {} since it is not a json file", (Object)lv2);
                continue;
            }
            if (!string2.startsWith(lv.getPath() + "/")) {
                LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", (Object)lv2);
                continue;
            }
            String string22 = string2.substring(0, string2.length() - ".json".length()).substring(lv.getPath().length() + 1);
            int i = string22.indexOf(47);
            if (i < 0) {
                LOGGER.warn("Skipping resource {} since it does not have a namespace", (Object)lv2);
                continue;
            }
            String string3 = string22.substring(0, i);
            String string4 = string22.substring(i + 1);
            Identifier lv3 = new Identifier(string3, string4);
            dataResult = dataResult.flatMap(arg3 -> this.readSupplier(registryRef, (MutableRegistry)arg3, mapCodec, lv3).map(supplier -> arg3));
        }
        return dataResult.setPartial(registry);
    }

    private <E> DataResult<java.util.function.Supplier<E>> readSupplier(RegistryKey<? extends Registry<E>> registryRef, MutableRegistry<E> registry, MapCodec<E> mapCodec, Identifier elementId) {
        RegistryKey lv = RegistryKey.of(registryRef, elementId);
        Object object2 = registry.get(lv);
        if (object2 != null) {
            return DataResult.success(() -> object2, (Lifecycle)Lifecycle.stable());
        }
        ValueHolder<E> lv2 = this.getValueHolder(registryRef);
        DataResult dataResult = (DataResult)((ValueHolder)lv2).values.get(lv);
        if (dataResult != null) {
            return dataResult;
        }
        Supplier supplier = Suppliers.memoize(() -> {
            Object object = registry.get(lv);
            if (object == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + lv);
            }
            return object;
        });
        ((ValueHolder)lv2).values.put(lv, DataResult.success((Object)supplier));
        DataResult dataResult2 = this.readElement(registryRef, lv, mapCodec);
        dataResult2.result().ifPresent(object -> registry.add(lv, object));
        DataResult dataResult3 = dataResult2.map(object -> () -> object);
        ((ValueHolder)lv2).values.put(lv, dataResult3);
        return dataResult3;
    }

    /*
     * Exception decompiling
     */
    private <E> DataResult<E> readElement(RegistryKey<? extends Registry<E>> registryRef, RegistryKey<E> elementRef, MapCodec<E> mapCodec) {
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

    private <E> ValueHolder<E> getValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
        return this.valueHolders.computeIfAbsent(registryRef, arg -> new ValueHolder());
    }

    static final class ValueHolder<E> {
        private final Map<RegistryKey<E>, DataResult<java.util.function.Supplier<E>>> values = Maps.newIdentityHashMap();

        private ValueHolder() {
        }
    }
}

