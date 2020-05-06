/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DefaultClientResourcePack
extends DefaultResourcePack {
    private final ResourceIndex index;

    public DefaultClientResourcePack(ResourceIndex arg) {
        super("minecraft", "realms");
        this.index = arg;
    }

    @Override
    @Nullable
    protected InputStream findInputStream(ResourceType arg, Identifier arg2) {
        File file;
        if (arg == ResourceType.CLIENT_RESOURCES && (file = this.index.getResource(arg2)) != null && file.exists()) {
            try {
                return new FileInputStream(file);
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.findInputStream(arg, arg2);
    }

    @Override
    public boolean contains(ResourceType arg, Identifier arg2) {
        File file;
        if (arg == ResourceType.CLIENT_RESOURCES && (file = this.index.getResource(arg2)) != null && file.exists()) {
            return true;
        }
        return super.contains(arg, arg2);
    }

    @Override
    @Nullable
    protected InputStream getInputStream(String string) {
        File file = this.index.findFile(string);
        if (file != null && file.exists()) {
            try {
                return new FileInputStream(file);
            }
            catch (FileNotFoundException fileNotFoundException) {
                // empty catch block
            }
        }
        return super.getInputStream(string);
    }

    @Override
    public Collection<Identifier> findResources(ResourceType arg, String string, String string2, int i, Predicate<String> predicate) {
        Collection<Identifier> collection = super.findResources(arg, string, string2, i, predicate);
        collection.addAll(this.index.getFilesRecursively(string2, string, i, predicate));
        return collection;
    }
}

