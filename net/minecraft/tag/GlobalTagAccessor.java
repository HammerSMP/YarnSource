/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5394;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

public class GlobalTagAccessor<T> {
    private final TagContainer<T> emptyContainer = new TagContainer(arg -> Optional.empty(), "", "");
    private TagContainer<T> currentContainer = this.emptyContainer;
    private final List<CachedTag<T>> tags = Lists.newArrayList();

    public Tag.Identified<T> get(String string) {
        CachedTag lv = new CachedTag(new Identifier(string));
        this.tags.add(lv);
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public void markReady() {
        this.currentContainer = this.emptyContainer;
        class_5394 lv = class_5394.method_29898();
        this.tags.forEach(arg22 -> arg22.updateContainer(arg2 -> lv));
    }

    public void setContainer(TagContainer<T> arg) {
        this.currentContainer = arg;
        this.tags.forEach(arg2 -> arg2.updateContainer(arg::get));
    }

    public TagContainer<T> getContainer() {
        return this.currentContainer;
    }

    public List<CachedTag<T>> method_29902() {
        return this.tags;
    }

    public Set<Identifier> method_29224(TagContainer<T> arg) {
        Set set = this.tags.stream().map(CachedTag::getId).collect(Collectors.toSet());
        ImmutableSet immutableSet = ImmutableSet.copyOf(arg.getKeys());
        return Sets.difference(set, (Set)immutableSet);
    }

    public static class CachedTag<T>
    implements Tag.Identified<T> {
        @Nullable
        private Tag<T> currentTag;
        protected final Identifier id;

        private CachedTag(Identifier arg) {
            this.id = arg;
        }

        @Override
        public Identifier getId() {
            return this.id;
        }

        private Tag<T> get() {
            if (this.currentTag == null) {
                throw new IllegalStateException("Tag " + this.id + " used before it was bound");
            }
            return this.currentTag;
        }

        void updateContainer(Function<Identifier, Tag<T>> function) {
            this.currentTag = function.apply(this.id);
        }

        @Override
        public boolean contains(T object) {
            return this.get().contains(object);
        }

        @Override
        public List<T> values() {
            return this.get().values();
        }
    }
}

