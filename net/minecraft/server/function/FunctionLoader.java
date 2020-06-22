/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.datafixers.util.Pair
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionLoader
implements ResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int PATH_PREFIX_LENGTH = "functions/".length();
    private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
    private volatile Map<Identifier, CommandFunction> functions = ImmutableMap.of();
    private final TagContainer<CommandFunction> tags = new TagContainer(this::get, "tags/functions", "function");
    private final int level;
    private final CommandDispatcher<ServerCommandSource> commandDispatcher;

    public Optional<CommandFunction> get(Identifier arg) {
        return Optional.ofNullable(this.functions.get(arg));
    }

    public Map<Identifier, CommandFunction> getFunctions() {
        return this.functions;
    }

    public TagContainer<CommandFunction> getTags() {
        return this.tags;
    }

    public Tag<CommandFunction> getOrCreateTag(Identifier arg) {
        return this.tags.getOrCreate(arg);
    }

    public FunctionLoader(int i, CommandDispatcher<ServerCommandSource> commandDispatcher) {
        this.level = i;
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer arg, ResourceManager arg2, Profiler arg3, Profiler arg4, Executor executor, Executor executor2) {
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = this.tags.prepareReload(arg2, executor);
        CompletionStage completableFuture2 = CompletableFuture.supplyAsync(() -> arg2.findResources("functions", string -> string.endsWith(".mcfunction")), executor).thenCompose(collection -> {
            HashMap map = Maps.newHashMap();
            ServerCommandSource lv = new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, this.level, "", LiteralText.EMPTY, null, null);
            for (Identifier lv2 : collection) {
                String string = lv2.getPath();
                Identifier lv3 = new Identifier(lv2.getNamespace(), string.substring(PATH_PREFIX_LENGTH, string.length() - PATH_SUFFIX_LENGTH));
                map.put(lv3, CompletableFuture.supplyAsync(() -> {
                    List<String> list = FunctionLoader.readLines(arg2, lv2);
                    return CommandFunction.create(lv3, this.commandDispatcher, lv, list);
                }, executor));
            }
            CompletableFuture[] completableFutures = map.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf(completableFutures).handle((void_, throwable) -> map);
        });
        return ((CompletableFuture)((CompletableFuture)completableFuture.thenCombine(completableFuture2, Pair::of)).thenCompose(arg::whenPrepared)).thenAcceptAsync(pair -> {
            Map map = (Map)pair.getSecond();
            ImmutableMap.Builder builder = ImmutableMap.builder();
            map.forEach((arg, completableFuture) -> ((CompletableFuture)completableFuture.handle((arg2, throwable) -> {
                if (throwable != null) {
                    LOGGER.error("Failed to load function {}", arg, throwable);
                } else {
                    builder.put(arg, arg2);
                }
                return null;
            })).join());
            this.functions = builder.build();
            this.tags.applyReload((Map)pair.getFirst());
        }, executor2);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<String> readLines(ResourceManager arg, Identifier arg2) {
        try (Resource lv = arg.getResource(arg2);){
            List list = IOUtils.readLines((InputStream)lv.getInputStream(), (Charset)StandardCharsets.UTF_8);
            return list;
        }
        catch (IOException iOException) {
            throw new CompletionException(iOException);
        }
    }
}

