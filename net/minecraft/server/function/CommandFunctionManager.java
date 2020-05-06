/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.CommandDispatcher
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandFunctionManager
implements SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier TICK_FUNCTION = new Identifier("tick");
    private static final Identifier LOAD_FUNCTION = new Identifier("load");
    public static final int PATH_PREFIX_LENGTH = "functions/".length();
    public static final int EXTENSION_LENGTH = ".mcfunction".length();
    private final MinecraftServer server;
    private final Map<Identifier, CommandFunction> idMap = Maps.newHashMap();
    private boolean executing;
    private final ArrayDeque<Entry> chain = new ArrayDeque();
    private final List<Entry> pending = Lists.newArrayList();
    private final TagContainer<CommandFunction> tags = new TagContainer(this::getFunction, "tags/functions", "function");
    private final List<CommandFunction> tickFunctions = Lists.newArrayList();
    private boolean needToRunLoadFunctions;

    public CommandFunctionManager(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
    }

    public Optional<CommandFunction> getFunction(Identifier arg) {
        return Optional.ofNullable(this.idMap.get(arg));
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public int getMaxCommandChainLength() {
        return this.server.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
    }

    public Map<Identifier, CommandFunction> getFunctions() {
        return this.idMap;
    }

    public CommandDispatcher<ServerCommandSource> getDispatcher() {
        return this.server.getCommandManager().getDispatcher();
    }

    public void tick() {
        this.server.getProfiler().push(TICK_FUNCTION::toString);
        for (CommandFunction lv : this.tickFunctions) {
            this.execute(lv, this.getTaggedFunctionSource());
        }
        this.server.getProfiler().pop();
        if (this.needToRunLoadFunctions) {
            this.needToRunLoadFunctions = false;
            List<CommandFunction> collection = this.getTags().getOrCreate(LOAD_FUNCTION).values();
            this.server.getProfiler().push(LOAD_FUNCTION::toString);
            for (CommandFunction lv2 : collection) {
                this.execute(lv2, this.getTaggedFunctionSource());
            }
            this.server.getProfiler().pop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int execute(CommandFunction arg, ServerCommandSource arg2) {
        int i = this.getMaxCommandChainLength();
        if (this.executing) {
            if (this.chain.size() + this.pending.size() < i) {
                this.pending.add(new Entry(this, arg2, new CommandFunction.FunctionElement(arg)));
            }
            return 0;
        }
        try {
            this.executing = true;
            int j = 0;
            CommandFunction.Element[] lvs = arg.getElements();
            for (int k = lvs.length - 1; k >= 0; --k) {
                this.chain.push(new Entry(this, arg2, lvs[k]));
            }
            while (!this.chain.isEmpty()) {
                try {
                    Entry lv = this.chain.removeFirst();
                    this.server.getProfiler().push(lv::toString);
                    lv.execute(this.chain, i);
                    if (!this.pending.isEmpty()) {
                        Lists.reverse(this.pending).forEach(this.chain::addFirst);
                        this.pending.clear();
                    }
                }
                finally {
                    this.server.getProfiler().pop();
                }
                if (++j < i) continue;
                int n = j;
                return n;
            }
            int n = j;
            return n;
        }
        finally {
            this.chain.clear();
            this.pending.clear();
            this.executing = false;
        }
    }

    @Override
    public void apply(ResourceManager arg) {
        this.idMap.clear();
        this.tickFunctions.clear();
        Collection<Identifier> collection = arg.findResources("functions", string -> string.endsWith(".mcfunction"));
        ArrayList list2 = Lists.newArrayList();
        for (Identifier lv : collection) {
            String string2 = lv.getPath();
            Identifier lv2 = new Identifier(lv.getNamespace(), string2.substring(PATH_PREFIX_LENGTH, string2.length() - EXTENSION_LENGTH));
            list2.add(((CompletableFuture)CompletableFuture.supplyAsync(() -> CommandFunctionManager.readLines(arg, lv), ResourceImpl.RESOURCE_IO_EXECUTOR).thenApplyAsync(list -> CommandFunction.create(lv2, this, list), this.server.getWorkerExecutor())).handle((arg2, throwable) -> this.load((CommandFunction)arg2, (Throwable)throwable, lv)));
        }
        CompletableFuture.allOf(list2.toArray(new CompletableFuture[0])).join();
        if (!this.idMap.isEmpty()) {
            LOGGER.info("Loaded {} custom command functions", (Object)this.idMap.size());
        }
        this.tags.applyReload(this.tags.prepareReload(arg, this.server.getWorkerExecutor()).join());
        this.tickFunctions.addAll(this.tags.getOrCreate(TICK_FUNCTION).values());
        this.needToRunLoadFunctions = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private CommandFunction load(CommandFunction arg, @Nullable Throwable throwable, Identifier arg2) {
        if (throwable != null) {
            LOGGER.error("Couldn't load function at {}", (Object)arg2, (Object)throwable);
            return null;
        }
        Map<Identifier, CommandFunction> map = this.idMap;
        synchronized (map) {
            this.idMap.put(arg.getId(), arg);
        }
        return arg;
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

    public ServerCommandSource getTaggedFunctionSource() {
        return this.server.getCommandSource().withLevel(2).withSilent();
    }

    public ServerCommandSource getCommandFunctionSource() {
        return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, this.server.getFunctionPermissionLevel(), "", LiteralText.EMPTY, this.server, null);
    }

    public TagContainer<CommandFunction> getTags() {
        return this.tags;
    }

    public static class Entry {
        private final CommandFunctionManager manager;
        private final ServerCommandSource source;
        private final CommandFunction.Element element;

        public Entry(CommandFunctionManager arg, ServerCommandSource arg2, CommandFunction.Element arg3) {
            this.manager = arg;
            this.source = arg2;
            this.element = arg3;
        }

        public void execute(ArrayDeque<Entry> arrayDeque, int i) {
            try {
                this.element.execute(this.manager, this.source, arrayDeque, i);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }

        public String toString() {
            return this.element.toString();
        }
    }
}

