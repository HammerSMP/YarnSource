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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;

public class RequiredTagList<T> {
    private TagGroup<T> group = TagGroup.createEmpty();
    private final List<TagWrapper<T>> tags = Lists.newArrayList();
    private final Function<TagManager, TagGroup<T>> groupGetter;

    public RequiredTagList(Function<TagManager, TagGroup<T>> managerGetter) {
        this.groupGetter = managerGetter;
    }

    public Tag.Identified<T> add(String id) {
        TagWrapper lv = new TagWrapper(new Identifier(id));
        this.tags.add(lv);
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public void clearAllTags() {
        this.group = TagGroup.createEmpty();
        SetTag lv = SetTag.empty();
        this.tags.forEach(tag -> tag.updateDelegate(id -> lv));
    }

    public void updateTagManager(TagManager manager) {
        TagGroup lv = this.groupGetter.apply(manager);
        this.group = lv;
        this.tags.forEach(tag -> tag.updateDelegate(lv::getTag));
    }

    public TagGroup<T> getGroup() {
        return this.group;
    }

    public List<? extends Tag<T>> getTags() {
        return this.tags;
    }

    public Set<Identifier> getMissingTags(TagManager manager) {
        TagGroup<T> lv = this.groupGetter.apply(manager);
        Set set = this.tags.stream().map(TagWrapper::getId).collect(Collectors.toSet());
        ImmutableSet immutableSet = ImmutableSet.copyOf(lv.getTagIds());
        return Sets.difference(set, (Set)immutableSet);
    }

    static class TagWrapper<T>
    implements Tag.Identified<T> {
        @Nullable
        private Tag<T> delegate;
        protected final Identifier id;

        private TagWrapper(Identifier id) {
            this.id = id;
        }

        @Override
        public Identifier getId() {
            return this.id;
        }

        private Tag<T> get() {
            if (this.delegate == null) {
                throw new IllegalStateException("Tag " + this.id + " used before it was bound");
            }
            return this.delegate;
        }

        void updateDelegate(Function<Identifier, Tag<T>> tagFactory) {
            this.delegate = tagFactory.apply(this.id);
        }

        @Override
        public boolean contains(T entry) {
            return this.get().contains(entry);
        }

        @Override
        public List<T> values() {
            return this.get().values();
        }
    }
}

