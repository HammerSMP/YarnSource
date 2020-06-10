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

    public NamespaceResourceManager(ResourceType arg, String string) {
        this.type = arg;
        this.namespace = string;
    }

    public void addPack(ResourcePack arg) {
        this.packList.add(arg);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces() {
        return ImmutableSet.of((Object)this.namespace);
    }

    @Override
    public Resource getResource(Identifier arg) throws IOException {
        this.validate(arg);
        ResourcePack lv = null;
        Identifier lv2 = NamespaceResourceManager.getMetadataPath(arg);
        for (int i = this.packList.size() - 1; i >= 0; --i) {
            ResourcePack lv3 = this.packList.get(i);
            if (lv == null && lv3.contains(this.type, lv2)) {
                lv = lv3;
            }
            if (!lv3.contains(this.type, arg)) continue;
            InputStream inputStream = null;
            if (lv != null) {
                inputStream = this.open(lv2, lv);
            }
            return new ResourceImpl(lv3.getName(), arg, this.open(arg, lv3), inputStream);
        }
        throw new FileNotFoundException(arg.toString());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier arg) {
        if (!this.isPathAbsolute(arg)) {
            return false;
        }
        for (int i = this.packList.size() - 1; i >= 0; --i) {
            ResourcePack lv = this.packList.get(i);
            if (!lv.contains(this.type, arg)) continue;
            return true;
        }
        return false;
    }

    protected InputStream open(Identifier arg, ResourcePack arg2) throws IOException {
        InputStream inputStream = arg2.open(this.type, arg);
        return LOGGER.isDebugEnabled() ? new DebugInputStream(inputStream, arg, arg2.getName()) : inputStream;
    }

    private void validate(Identifier arg) throws IOException {
        if (!this.isPathAbsolute(arg)) {
            throw new IOException("Invalid relative path to resource: " + arg);
        }
    }

    private boolean isPathAbsolute(Identifier arg) {
        return !arg.getPath().contains("..");
    }

    @Override
    public List<Resource> getAllResources(Identifier arg) throws IOException {
        this.validate(arg);
        ArrayList list = Lists.newArrayList();
        Identifier lv = NamespaceResourceManager.getMetadataPath(arg);
        for (ResourcePack lv2 : this.packList) {
            if (!lv2.contains(this.type, arg)) continue;
            InputStream inputStream = lv2.contains(this.type, lv) ? this.open(lv, lv2) : null;
            list.add(new ResourceImpl(lv2.getName(), arg, this.open(arg, lv2), inputStream));
        }
        if (list.isEmpty()) {
            throw new FileNotFoundException(arg.toString());
        }
        return list;
    }

    @Override
    public Collection<Identifier> findResources(Identifier arg, Predicate<String> predicate) {
        if (Objects.equals(arg.getNamespace(), this.namespace)) {
            return this.findResources(arg.getPath(), predicate);
        }
        return ImmutableSet.of();
    }

    @Override
    public Collection<Identifier> findResources(String string, Predicate<String> predicate) {
        ArrayList list = Lists.newArrayList();
        for (ResourcePack lv : this.packList) {
            list.addAll(lv.findResources(this.type, this.namespace, string, Integer.MAX_VALUE, predicate));
        }
        Collections.sort(list);
        return list;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Stream<ResourcePack> streamResourcePacks() {
        return this.packList.stream();
    }

    static Identifier getMetadataPath(Identifier arg) {
        return new Identifier(arg.getNamespace(), arg.getPath() + ".mcmeta");
    }

    static class DebugInputStream
    extends FilterInputStream {
        private final String leakMessage;
        private boolean closed;

        public DebugInputStream(InputStream inputStream, Identifier arg, String string) {
            super(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
            this.leakMessage = "Leaked resource: '" + arg + "' loaded from pack: '" + string + "'\n" + byteArrayOutputStream;
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

