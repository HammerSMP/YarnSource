/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

public interface ResourceManager {
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces();

    public Resource getResource(Identifier var1) throws IOException;

    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier var1);

    public List<Resource> getAllResources(Identifier var1) throws IOException;

    public Collection<Identifier> method_29489(Identifier var1, Predicate<String> var2);

    public Collection<Identifier> findResources(String var1, Predicate<String> var2);

    @Environment(value=EnvType.CLIENT)
    public Stream<ResourcePack> method_29213();

    public static enum class_5353 implements ResourceManager
    {
        INSTANCE;


        @Override
        @Environment(value=EnvType.CLIENT)
        public Set<String> getAllNamespaces() {
            return ImmutableSet.of();
        }

        @Override
        public Resource getResource(Identifier arg) throws IOException {
            throw new FileNotFoundException(arg.toString());
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public boolean containsResource(Identifier arg) {
            return false;
        }

        @Override
        public List<Resource> getAllResources(Identifier arg) {
            return ImmutableList.of();
        }

        @Override
        public Collection<Identifier> method_29489(Identifier arg, Predicate<String> predicate) {
            return ImmutableSet.of();
        }

        @Override
        public Collection<Identifier> findResources(String string, Predicate<String> predicate) {
            return ImmutableSet.of();
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public Stream<ResourcePack> method_29213() {
            return Stream.of(new ResourcePack[0]);
        }
    }
}

