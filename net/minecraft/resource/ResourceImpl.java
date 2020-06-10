/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.resource;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;

public class ResourceImpl
implements Resource {
    private final String packName;
    private final Identifier id;
    private final InputStream inputStream;
    private final InputStream metaInputStream;
    @Environment(value=EnvType.CLIENT)
    private boolean readMetadata;
    @Environment(value=EnvType.CLIENT)
    private JsonObject metadata;

    public ResourceImpl(String string, Identifier arg, InputStream inputStream, @Nullable InputStream inputStream2) {
        this.packName = string;
        this.id = arg;
        this.inputStream = inputStream;
        this.metaInputStream = inputStream2;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Identifier getId() {
        return this.id;
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasMetadata() {
        return this.metaInputStream != null;
    }

    @Override
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public <T> T getMetadata(ResourceMetadataReader<T> arg) {
        if (!this.hasMetadata()) {
            return null;
        }
        if (this.metadata == null && !this.readMetadata) {
            this.readMetadata = true;
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(this.metaInputStream, StandardCharsets.UTF_8));
                this.metadata = JsonHelper.deserialize(bufferedReader);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(bufferedReader);
                throw throwable;
            }
            IOUtils.closeQuietly((Reader)bufferedReader);
        }
        if (this.metadata == null) {
            return null;
        }
        String string = arg.getKey();
        return this.metadata.has(string) ? (T)arg.fromJson(JsonHelper.getObject(this.metadata, string)) : null;
    }

    @Override
    public String getResourcePackName() {
        return this.packName;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ResourceImpl)) {
            return false;
        }
        ResourceImpl lv = (ResourceImpl)object;
        if (this.id != null ? !this.id.equals(lv.id) : lv.id != null) {
            return false;
        }
        return !(this.packName != null ? !this.packName.equals(lv.packName) : lv.packName != null);
    }

    public int hashCode() {
        int i = this.packName != null ? this.packName.hashCode() : 0;
        i = 31 * i + (this.id != null ? this.id.hashCode() : 0);
        return i;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
        if (this.metaInputStream != null) {
            this.metaInputStream.close();
        }
    }
}

