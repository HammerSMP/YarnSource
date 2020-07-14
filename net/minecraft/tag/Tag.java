/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public interface Tag<T> {
    public static <T> Codec<Tag<T>> codec(Supplier<TagGroup<T>> groupGetter) {
        return Identifier.CODEC.flatXmap(arg -> Optional.ofNullable(((TagGroup)groupGetter.get()).getTag((Identifier)arg)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown tag: " + arg))), arg -> Optional.ofNullable(((TagGroup)groupGetter.get()).getUncheckedTagId(arg)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown tag: " + arg))));
    }

    public boolean contains(T var1);

    public List<T> values();

    default public T getRandom(Random random) {
        List<T> list = this.values();
        return list.get(random.nextInt(list.size()));
    }

    public static <T> Tag<T> of(Set<T> values) {
        return SetTag.of(values);
    }

    public static interface Identified<T>
    extends Tag<T> {
        public Identifier getId();
    }

    public static class TagEntry
    implements Entry {
        private final Identifier id;

        public TagEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            Tag<T> lv = tagGetter.apply(this.id);
            if (lv == null) {
                return false;
            }
            lv.values().forEach(collector);
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            json.add("#" + this.id);
        }

        public String toString() {
            return "#" + this.id;
        }
    }

    public static class ObjectEntry
    implements Entry {
        private final Identifier id;

        public ObjectEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            T object = objectGetter.apply(this.id);
            if (object == null) {
                return false;
            }
            collector.accept(object);
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            json.add(this.id.toString());
        }

        public String toString() {
            return this.id.toString();
        }
    }

    public static interface Entry {
        public <T> boolean resolve(Function<Identifier, Tag<T>> var1, Function<Identifier, T> var2, Consumer<T> var3);

        public void addToJson(JsonArray var1);
    }

    public static class Builder {
        private final List<TrackedEntry> entries = Lists.newArrayList();

        public static Builder create() {
            return new Builder();
        }

        public Builder add(TrackedEntry trackedEntry) {
            this.entries.add(trackedEntry);
            return this;
        }

        public Builder add(Entry entry, String source) {
            return this.add(new TrackedEntry(entry, source));
        }

        public Builder add(Identifier id, String source) {
            return this.add(new ObjectEntry(id), source);
        }

        public Builder addTag(Identifier id, String source) {
            return this.add(new TagEntry(id), source);
        }

        public <T> Optional<Tag<T>> build(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (TrackedEntry lv : this.entries) {
                if (lv.getEntry().resolve(tagGetter, objectGetter, ((ImmutableSet.Builder)builder)::add)) continue;
                return Optional.empty();
            }
            return Optional.of(Tag.of(builder.build()));
        }

        public Stream<TrackedEntry> streamEntries() {
            return this.entries.stream();
        }

        public <T> Stream<TrackedEntry> streamUnresolvedEntries(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            return this.streamEntries().filter(arg -> !arg.getEntry().resolve(tagGetter, objectGetter, object -> {}));
        }

        public Builder read(JsonObject json, String source) {
            JsonArray jsonArray = JsonHelper.getArray(json, "values");
            ArrayList list = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                String string2 = JsonHelper.asString(jsonElement, "value");
                if (string2.startsWith("#")) {
                    list.add(new TagEntry(new Identifier(string2.substring(1))));
                    continue;
                }
                list.add(new ObjectEntry(new Identifier(string2)));
            }
            if (JsonHelper.getBoolean(json, "replace", false)) {
                this.entries.clear();
            }
            list.forEach(arg -> this.entries.add(new TrackedEntry((Entry)arg, source)));
            return this;
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            for (TrackedEntry lv : this.entries) {
                lv.getEntry().addToJson(jsonArray);
            }
            jsonObject.addProperty("replace", Boolean.valueOf(false));
            jsonObject.add("values", (JsonElement)jsonArray);
            return jsonObject;
        }
    }

    public static class TrackedEntry {
        private final Entry entry;
        private final String source;

        private TrackedEntry(Entry entry, String source) {
            this.entry = entry;
            this.source = source;
        }

        public Entry getEntry() {
            return this.entry;
        }

        public String toString() {
            return this.entry.toString() + " (from " + this.source + ")";
        }
    }
}

