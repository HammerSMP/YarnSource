/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_5394;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractTagProvider<T>
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator root;
    protected final Registry<T> registry;
    private final Map<Identifier, Tag.Builder> tagBuilders = Maps.newLinkedHashMap();

    protected AbstractTagProvider(DataGenerator arg, Registry<T> arg2) {
        this.root = arg;
        this.registry = arg2;
    }

    protected abstract void configure();

    @Override
    public void run(DataCache arg4) {
        this.tagBuilders.clear();
        this.configure();
        class_5394 lv = class_5394.method_29898();
        Function<Identifier, Tag> function = arg2 -> this.tagBuilders.containsKey(arg2) ? lv : null;
        Function<Identifier, Object> function2 = arg -> this.registry.getOrEmpty((Identifier)arg).orElse(null);
        this.tagBuilders.forEach((arg2, arg3) -> {
            List list = arg3.streamUnresolvedEntries(function, function2).collect(Collectors.toList());
            if (!list.isEmpty()) {
                throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", arg2, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
            }
            JsonObject jsonObject = arg3.toJson();
            Path path = this.getOutput((Identifier)arg2);
            try {
                String string = GSON.toJson((JsonElement)jsonObject);
                String string2 = SHA1.hashUnencodedChars((CharSequence)string).toString();
                if (!Objects.equals(arg4.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
                    Files.createDirectories(path.getParent(), new FileAttribute[0]);
                    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                        bufferedWriter.write(string);
                    }
                }
                arg4.updateSha1(path, string2);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't save tags to {}", (Object)path, (Object)iOException);
            }
        });
    }

    protected abstract Path getOutput(Identifier var1);

    protected ObjectBuilder<T> getOrCreateTagBuilder(Tag.Identified<T> arg) {
        Tag.Builder lv = this.method_27169(arg);
        return new ObjectBuilder(lv, this.registry, "vanilla");
    }

    protected Tag.Builder method_27169(Tag.Identified<T> arg2) {
        return this.tagBuilders.computeIfAbsent(arg2.getId(), arg -> new Tag.Builder());
    }

    public static class ObjectBuilder<T> {
        private final Tag.Builder field_23960;
        private final Registry<T> field_23961;
        private final String field_23962;

        private ObjectBuilder(Tag.Builder arg, Registry<T> arg2, String string) {
            this.field_23960 = arg;
            this.field_23961 = arg2;
            this.field_23962 = string;
        }

        public ObjectBuilder<T> add(T object) {
            this.field_23960.add(this.field_23961.getId(object), this.field_23962);
            return this;
        }

        public ObjectBuilder<T> addTag(Tag.Identified<T> arg) {
            this.field_23960.addTag(arg.getId(), this.field_23962);
            return this;
        }

        @SafeVarargs
        public final ObjectBuilder<T> add(T ... objects) {
            Stream.of(objects).map(this.field_23961::getId).forEach(arg -> this.field_23960.add((Identifier)arg, this.field_23962));
            return this;
        }
    }
}

