/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.util.Supplier
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ProfilingResourceReloader;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class ReloadableResourceManagerImpl
implements ReloadableResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, NamespaceResourceManager> namespaceManagers = Maps.newHashMap();
    private final List<ResourceReloadListener> listeners = Lists.newArrayList();
    private final List<ResourceReloadListener> initialListeners = Lists.newArrayList();
    private final Set<String> namespaces = Sets.newLinkedHashSet();
    private final List<ResourcePack> field_25145 = Lists.newArrayList();
    private final ResourceType type;

    public ReloadableResourceManagerImpl(ResourceType arg) {
        this.type = arg;
    }

    public void addPack(ResourcePack arg) {
        this.field_25145.add(arg);
        for (String string : arg.getNamespaces(this.type)) {
            this.namespaces.add(string);
            NamespaceResourceManager lv = this.namespaceManagers.get(string);
            if (lv == null) {
                lv = new NamespaceResourceManager(this.type, string);
                this.namespaceManagers.put(string, lv);
            }
            lv.addPack(arg);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces() {
        return this.namespaces;
    }

    @Override
    public Resource getResource(Identifier arg) throws IOException {
        ResourceManager lv = this.namespaceManagers.get(arg.getNamespace());
        if (lv != null) {
            return lv.getResource(arg);
        }
        throw new FileNotFoundException(arg.toString());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier arg) {
        ResourceManager lv = this.namespaceManagers.get(arg.getNamespace());
        if (lv != null) {
            return lv.containsResource(arg);
        }
        return false;
    }

    @Override
    public List<Resource> getAllResources(Identifier arg) throws IOException {
        ResourceManager lv = this.namespaceManagers.get(arg.getNamespace());
        if (lv != null) {
            return lv.getAllResources(arg);
        }
        throw new FileNotFoundException(arg.toString());
    }

    @Override
    public Collection<Identifier> findResources(Identifier arg, Predicate<String> predicate) {
        ResourceManager lv = this.namespaceManagers.get(arg.getNamespace());
        if (lv != null) {
            return lv.findResources(arg.getPath(), predicate);
        }
        return ImmutableSet.of();
    }

    @Override
    public Collection<Identifier> findResources(String string, Predicate<String> predicate) {
        HashSet set = Sets.newHashSet();
        for (NamespaceResourceManager lv : this.namespaceManagers.values()) {
            set.addAll(lv.findResources(string, predicate));
        }
        ArrayList list = Lists.newArrayList((Iterable)set);
        Collections.sort(list);
        return list;
    }

    private void clear() {
        this.namespaceManagers.clear();
        this.namespaces.clear();
        this.field_25145.forEach(ResourcePack::close);
        this.field_25145.clear();
    }

    @Override
    public void close() {
        this.clear();
    }

    @Override
    public void registerListener(ResourceReloadListener arg) {
        this.listeners.add(arg);
        this.initialListeners.add(arg);
    }

    protected ResourceReloadMonitor beginReloadInner(Executor executor, Executor executor2, List<ResourceReloadListener> list, CompletableFuture<Unit> completableFuture) {
        ResourceReloader<Void> lv2;
        if (LOGGER.isDebugEnabled()) {
            ProfilingResourceReloader lv = new ProfilingResourceReloader(this, Lists.newArrayList(list), executor, executor2, completableFuture);
        } else {
            lv2 = ResourceReloader.create(this, Lists.newArrayList(list), executor, executor2, completableFuture);
        }
        this.initialListeners.clear();
        return lv2;
    }

    @Override
    public ResourceReloadMonitor beginMonitoredReload(Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture, List<ResourcePack> list) {
        this.clear();
        LOGGER.info("Reloading ResourceManager: {}", new Supplier[]{() -> list.stream().map(ResourcePack::getName).collect(Collectors.joining(", "))});
        for (ResourcePack lv : list) {
            try {
                this.addPack(lv);
            }
            catch (Exception exception) {
                LOGGER.error("Failed to add resource pack {}", (Object)lv.getName(), (Object)exception);
                return new FailedResourceReloadMonitor(new PackAdditionFailedException(lv, (Throwable)exception));
            }
        }
        return this.beginReloadInner(executor, executor2, this.listeners, completableFuture);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Stream<ResourcePack> method_29213() {
        return this.field_25145.stream();
    }

    static class FailedResourceReloadMonitor
    implements ResourceReloadMonitor {
        private final PackAdditionFailedException exception;
        private final CompletableFuture<Unit> future;

        public FailedResourceReloadMonitor(PackAdditionFailedException arg) {
            this.exception = arg;
            this.future = new CompletableFuture();
            this.future.completeExceptionally(arg);
        }

        @Override
        public CompletableFuture<Unit> whenComplete() {
            return this.future;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public float getProgress() {
            return 0.0f;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public boolean isPrepareStageComplete() {
            return false;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public boolean isApplyStageComplete() {
            return true;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void throwExceptions() {
            throw this.exception;
        }
    }

    public static class PackAdditionFailedException
    extends RuntimeException {
        private final ResourcePack pack;

        public PackAdditionFailedException(ResourcePack arg, Throwable throwable) {
            super(arg.getName(), throwable);
            this.pack = arg;
        }

        @Environment(value=EnvType.CLIENT)
        public ResourcePack getPack() {
            return this.pack;
        }
    }
}

