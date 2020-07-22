/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamespaceResourceManager
implements ResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final List<ResourcePack> packList = Lists.newArrayList();
    private final ResourceType type;
    private final String namespace;

    public NamespaceResourceManager(ResourceType type, String namespace) {
        this.type = type;
        this.namespace = namespace;
    }

    public void addPack(ResourcePack pack) {
        this.packList.add(pack);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces() {
        return ImmutableSet.of((Object)this.namespace);
    }

    @Override
    public Resource getResource(Identifier id) throws IOException {
        this.validate(id);
        ResourcePack lv = null;
        Identifier lv2 = NamespaceResourceManager.getMetadataPath(id);
        for (int i = this.packList.size() - 1; i >= 0; --i) {
            ResourcePack lv3 = this.packList.get(i);
            if (lv == null && lv3.contains(this.type, lv2)) {
                lv = lv3;
            }
            if (!lv3.contains(this.type, id)) continue;
            InputStream inputStream = null;
            if (lv != null) {
                inputStream = this.open(lv2, lv);
            }
            return new ResourceImpl(lv3.getName(), id, this.open(id, lv3), inputStream);
        }
        throw new FileNotFoundException(id.toString());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier id) {
        if (!this.isPathAbsolute(id)) {
            return false;
        }
        for (int i = this.packList.size() - 1; i >= 0; --i) {
            ResourcePack lv = this.packList.get(i);
            if (!lv.contains(this.type, id)) continue;
            return true;
        }
        return false;
    }

    protected InputStream open(Identifier id, ResourcePack pack) throws IOException {
        InputStream inputStream = pack.open(this.type, id);
        return LOGGER.isDebugEnabled() ? new DebugInputStream(inputStream, id, pack.getName()) : inputStream;
    }

    private void validate(Identifier id) throws IOException {
        if (!this.isPathAbsolute(id)) {
            throw new IOException("Invalid relative path to resource: " + id);
        }
    }

    private boolean isPathAbsolute(Identifier id) {
        return !id.getPath().contains("..");
    }

    @Override
    public List<Resource> getAllResources(Identifier id) throws IOException {
        this.validate(id);
        ArrayList list = Lists.newArrayList();
        Identifier lv = NamespaceResourceManager.getMetadataPath(id);
        for (ResourcePack lv2 : this.packList) {
            if (!lv2.contains(this.type, id)) continue;
            InputStream inputStream = lv2.contains(this.type, lv) ? this.open(lv, lv2) : null;
            list.add(new ResourceImpl(lv2.getName(), id, this.open(id, lv2), inputStream));
        }
        if (list.isEmpty()) {
            throw new FileNotFoundException(id.toString());
        }
        return list;
    }

    @Override
    public Collection<Identifier> findResources(Identifier resourceType, Predicate<String> predicate) {
        if (Objects.equals(resourceType.getNamespace(), this.namespace)) {
            return this.findResources(resourceType.getPath(), predicate);
        }
        return ImmutableSet.of();
    }

    @Override
    public Collection<Identifier> findResources(String resourceType, Predicate<String> pathPredicate) {
        ArrayList list = Lists.newArrayList();
        for (ResourcePack lv : this.packList) {
            list.addAll(lv.findResources(this.type, this.namespace, resourceType, Integer.MAX_VALUE, pathPredicate));
        }
        Collections.sort(list);
        return list;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Stream<ResourcePack> streamResourcePacks() {
        return this.packList.stream();
    }

    static Identifier getMetadataPath(Identifier id) {
        return new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
    }

    static class DebugInputStream
    extends FilterInputStream {
        private final String leakMessage;
        private boolean closed;

        public DebugInputStream(InputStream parent, Identifier id, String packName) {
            super(parent);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
            this.leakMessage = "Leaked resource: '" + id + "' loaded from pack: '" + packName + "'\n" + byteArrayOutputStream;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.closed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.closed) {
                LOGGER.warn(this.leakMessage);
            }
            super.finalize();
        }
    }
}

