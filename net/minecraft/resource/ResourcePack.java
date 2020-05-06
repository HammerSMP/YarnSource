/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.resource;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

public interface ResourcePack
extends Closeable {
    @Environment(value=EnvType.CLIENT)
    public InputStream openRoot(String var1) throws IOException;

    public InputStream open(ResourceType var1, Identifier var2) throws IOException;

    public Collection<Identifier> findResources(ResourceType var1, String var2, String var3, int var4, Predicate<String> var5);

    public boolean contains(ResourceType var1, Identifier var2);

    public Set<String> getNamespaces(ResourceType var1);

    @Nullable
    public <T> T parseMetadata(ResourceMetadataReader<T> var1) throws IOException;

    public String getName();
}

