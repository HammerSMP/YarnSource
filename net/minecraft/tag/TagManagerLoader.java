/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 */
package net.minecraft.tag;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

public class TagManagerLoader
implements ResourceReloadListener {
    private final TagGroupLoader<Block> blocks = new TagGroupLoader(Registry.BLOCK::getOrEmpty, "tags/blocks", "block");
    private final TagGroupLoader<Item> items = new TagGroupLoader(Registry.ITEM::getOrEmpty, "tags/items", "item");
    private final TagGroupLoader<Fluid> fluids = new TagGroupLoader(Registry.FLUID::getOrEmpty, "tags/fluids", "fluid");
    private final TagGroupLoader<EntityType<?>> entityTypes = new TagGroupLoader(Registry.ENTITY_TYPE::getOrEmpty, "tags/entity_types", "entity_type");
    private TagManager tagManager = TagManager.EMPTY;

    public TagManager getTagManager() {
        return this.tagManager;
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = this.blocks.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture2 = this.items.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture3 = this.fluids.prepareReload(manager, prepareExecutor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture4 = this.entityTypes.prepareReload(manager, prepareExecutor);
        return ((CompletableFuture)CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3, completableFuture4).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(void_ -> {
            TagGroup<EntityType<?>> lv4;
            TagGroup<Fluid> lv3;
            TagGroup<Item> lv2;
            TagGroup<Block> lv = this.blocks.applyReload((Map)completableFuture.join());
            TagManager lv5 = TagManager.create(lv, lv2 = this.items.applyReload((Map)completableFuture2.join()), lv3 = this.fluids.applyReload((Map)completableFuture3.join()), lv4 = this.entityTypes.applyReload((Map)completableFuture4.join()));
            Multimap<Identifier, Identifier> multimap = RequiredTagListRegistry.getMissingTags(lv5);
            if (!multimap.isEmpty()) {
                throw new IllegalStateException("Missing required tags: " + multimap.entries().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(",")));
            }
            ServerTagManagerHolder.setTagManager(lv5);
            this.tagManager = lv5;
        }, applyExecutor);
    }
}

