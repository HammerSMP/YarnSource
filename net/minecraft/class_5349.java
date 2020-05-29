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
package net.minecraft;

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

public class class_5349
implements ResourceReloadListener {
    private static final Logger field_25326 = LogManager.getLogger();
    private static final int field_25327 = "functions/".length();
    private static final int field_25328 = ".mcfunction".length();
    private volatile Map<Identifier, CommandFunction> field_25329 = ImmutableMap.of();
    private final TagContainer<CommandFunction> field_25330 = new TagContainer(this::method_29456, "tags/functions", "function");
    private final int field_25331;
    private final CommandDispatcher<ServerCommandSource> field_25332;

    public Optional<CommandFunction> method_29456(Identifier arg) {
        return Optional.ofNullable(this.field_25329.get(arg));
    }

    public Map<Identifier, CommandFunction> method_29447() {
        return this.field_25329;
    }

    public TagContainer<CommandFunction> method_29458() {
        return this.field_25330;
    }

    public Tag<CommandFunction> method_29459(Identifier arg) {
        return this.field_25330.getOrCreate(arg);
    }

    public class_5349(int i, CommandDispatcher<ServerCommandSource> commandDispatcher) {
        this.field_25331 = i;
        this.field_25332 = commandDispatcher;
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer arg, ResourceManager arg2, Profiler arg3, Profiler arg4, Executor executor, Executor executor2) {
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = this.field_25330.prepareReload(arg2, executor);
        CompletionStage completableFuture2 = CompletableFuture.supplyAsync(() -> arg2.findResources("functions", string -> string.endsWith(".mcfunction")), executor).thenCompose(collection -> {
            HashMap map = Maps.newHashMap();
            ServerCommandSource lv = new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, this.field_25331, "", LiteralText.EMPTY, null, null);
            for (Identifier lv2 : collection) {
                String string = lv2.getPath();
                Identifier lv3 = new Identifier(lv2.getNamespace(), string.substring(field_25327, string.length() - field_25328));
                map.put(lv3, CompletableFuture.supplyAsync(() -> {
                    List<String> list = class_5349.method_29450(arg2, lv2);
                    return CommandFunction.create(lv3, this.field_25332, lv, list);
                }, executor));
            }
            CompletableFuture[] completableFutures = map.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf(completableFutures).handle((arg, throwable) -> map);
        });
        return ((CompletableFuture)((CompletableFuture)completableFuture.thenCombine(completableFuture2, Pair::of)).thenCompose(arg::whenPrepared)).thenAcceptAsync(pair -> {
            Map map = (Map)pair.getSecond();
            ImmutableMap.Builder builder = ImmutableMap.builder();
            map.forEach((arg, completableFuture) -> ((CompletableFuture)completableFuture.handle((arg2, throwable) -> {
                if (throwable != null) {
                    field_25326.error("Failed to load function {}", arg, throwable);
                } else {
                    builder.put(arg, arg2);
                }
                return null;
            })).join());
            this.field_25329 = builder.build();
            this.field_25330.applyReload((Map)pair.getFirst());
        }, executor2);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<String> method_29450(ResourceManager arg, Identifier arg2) {
        try (Resource lv = arg.getResource(arg2);){
            List list = IOUtils.readLines((InputStream)lv.getInputStream(), (Charset)StandardCharsets.UTF_8);
            return list;
        }
        catch (IOException iOException) {
            throw new CompletionException(iOException);
        }
    }
}

