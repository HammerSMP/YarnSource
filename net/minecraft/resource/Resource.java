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
import java.io.InputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

public interface Resource
extends Closeable {
    @Environment(value=EnvType.CLIENT)
    public Identifier getId();

    public InputStream getInputStream();

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public <T> T getMetadata(ResourceMetadataReader<T> var1);

    public String getResourcePackName();
}

